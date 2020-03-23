package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Periodicidad;
import com.prosesol.api.rest.models.entity.Servicio;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.IPeriodicidadService;
import com.prosesol.api.rest.services.IServicioService;
import com.prosesol.api.rest.utils.CalcularFecha;
import com.prosesol.api.rest.utils.GenerarClave;
import com.prosesol.api.rest.utils.Paises;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/afiliados")
public class AfiliadoController {

    protected static final Log logger = LogFactory.getLog(AfiliadoController.class);
    
    
    @Value("${app.clave}")
    private String clave;

    @Value("${afiliado.servicio.total.id}")
    private Long idTotal;

    @Autowired
    private CalcularFecha calcularFechas;

    @Autowired
    private IAfiliadoService afiliadoService;

    @Autowired
    private IServicioService servicioService;

    @Autowired
    private GenerarClave generarClave;

    @Autowired
    private IPeriodicidadService periodicidadService;
    

    @RequestMapping(value = "/servicio")
    public String seleccionarServicio(Model model) {
        List<Servicio> servicios = servicioService.findAll();

        model.addAttribute("servicios", servicios);
        return "afiliados/servicio";
    }

    @RequestMapping(value = "/servicio/{id}")
    public String getIdServicio(@PathVariable("id") Long id, Model model,
                                RedirectAttributes redirect) {

        Servicio servicio = servicioService.findById(id);
        Afiliado afiliado = new Afiliado();

        afiliado.setServicio(servicio);
        if (servicio == null) {

            redirect.addFlashAttribute("error", "Debes seleccionar un servicio");
            return "redirect:/afiliados/servicio";
        }
        
        model.addAttribute("afiliado", afiliado);
        model.addAttribute("servicio", servicio);

        return "afiliados/crear";
    }

    @RequestMapping(value = "/crear", method = RequestMethod.POST)
    public String guardar(Afiliado afiliado,
                          RedirectAttributes redirect,
                          SessionStatus status) {

        String mensajeFlash = null;
        Date date = new Date();
        Double saldoAcumulado = 0.0;
        Integer corte = 0;
        String periodo = "MENSUAL";

        try {

            Afiliado buscarAfiliadoExistente = afiliadoService.findByRfc(afiliado.getRfc());
            Servicio servicio = servicioService.findById(afiliado.getServicio().getId());

            Periodicidad periodicidad = periodicidadService.getPeriodicidadByNombrePeriodo(periodo);

            if (buscarAfiliadoExistente != null) {
                logger.error("Error afiliado ya se encuentra registrado");
                redirect.addFlashAttribute("error", "El Afiliado ya se encuentra registrado");

                return "redirect:/afiliados/servicio/" + afiliado.getServicio().getId();
            }

            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            LocalDate fechaNacimiento = afiliado.getFechaNacimiento().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            int fNacimiento = fechaNacimiento.getYear();

            int rangoEdad = currentYear - fNacimiento;
            if (rangoEdad >= 55 && servicio.getId() == idTotal) {
                redirect.addFlashAttribute("error", "A partir de los 55 años, no puede contratar este servicio");
                return "redirect:/afiliados/servicio/" + afiliado.getServicio().getId();
            }

            if (afiliado.getId() != null) {

                if (afiliado.getIsBeneficiario().equals(true)) {
                    afiliado.setIsBeneficiario(true);
                } else {
                    afiliado.setIsBeneficiario(false);
                }

                mensajeFlash = "Registro editado con éxito";

            } else {
                saldoAcumulado = servicio.getCostoTitular() + servicio.getInscripcionTitular();
                afiliado.setIsInscripcion(true);
                afiliado.setSaldoAcumulado(saldoAcumulado);
                afiliado.setSaldoCorte(saldoAcumulado);
                afiliado.setIsBeneficiario(false);
                afiliado.setFechaAlta(date);
                afiliado.setClave(generarClave.getClaveAfiliado(clave));

                DateFormat formatoFecha = new SimpleDateFormat("dd");
                String dia = formatoFecha.format(afiliado.getFechaAlta());
                corte = Integer.parseInt(dia);
                Date fechaCorte = calcularFechas.calcularFechas(periodo, corte);
                afiliado.setFechaCorte(fechaCorte);
                afiliado.setPeriodicidad(periodicidad);
                mensajeFlash = "Registro creado con éxito";
            }
            afiliado.setEstatus(3);
            logger.info(mensajeFlash);
            afiliadoService.save(afiliado);

            status.setComplete();

        } catch (DataIntegrityViolationException e) {
            logger.error("Error al momento de ejecutar el proceso: " + e);
            redirect.addFlashAttribute("error", "El RFC ya existe en la base de datos ");

            return "redirect:/afiliados/servicio/" + afiliado.getServicio().getId();
        } catch (Exception e) {
            logger.error("Error al momento de ejecutar el proceso: " + e);

            redirect.addFlashAttribute("error", "Ocurrió un error al momento de insertar el Afiliado");

            return "redirect:/afiliados/servicio/" + afiliado.getServicio().getId();
        }

        mensajeFlash = "id del afiliado creado es: " + afiliado.getId();
        logger.info(mensajeFlash);
        redirect.addFlashAttribute("success", "Afiliado creado con éxito");
        return "redirect:/afiliados/bienvenido/" + afiliado.getId();
    }

    @RequestMapping(value = "/bienvenido/{id}")
    public String mostrar(@PathVariable("id") Long id,
                          Model model, RedirectAttributes redirect) {
    	
        try {
            Afiliado afiliado = afiliadoService.findById(id);
            Servicio servicio = servicioService.findById(afiliado.getServicio().getId());
            
            Double saldoAcumulado = 0.0;
            List<Afiliado> beneficiarios = afiliadoService.getBeneficiarioByIdByIsBeneficiario(id);
            if (beneficiarios != null) {
                saldoAcumulado = servicio.getCostoBeneficiario() + servicio.getInscripcionBeneficiario();

                model.addAttribute("saldoAcumulado", saldoAcumulado);
                model.addAttribute("beneficiarios", beneficiarios);

            }
           
            model.addAttribute("id", id);
            model.addAttribute("afiliado", afiliado);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error al momento de ejecutar el proceso: " + e);
            redirect.addFlashAttribute("error", "Ocurrió un error ");

            return "redirect:/afiliados/bienvenido/" + id;
        }
        return "afiliados/bienvenido";
    }

    @RequestMapping(value = "/guardar")
    public String guardaSaldoAfiliado(Model model, SessionStatus status, RedirectAttributes redirect) {
        return "redirect:/";
    }

    /**
     * Método para mostrar los estados Dentro del list box de crear afiliados
     *
     * @param(name = "estados")
     */

    @ModelAttribute("estados")
    public List<String> getAllEstados() {
        return afiliadoService.getAllEstados();
    }

    /**
     * Método para mostrar los países Dentro del list box de crear afiliados
     *
     * @param(name = "paises")
     */

    @ModelAttribute("paises")
    public List<Paises> getAllPaises() {
        return afiliadoService.getAllPaises();
    }

    /**
     * Validación de correo
     *
     * @param str
     * @return
     */

    public static boolean isNullOrEmpty(String str) {
        if (str != null && !str.isEmpty()) {
            return false;
        }

        return true;
    }

}
