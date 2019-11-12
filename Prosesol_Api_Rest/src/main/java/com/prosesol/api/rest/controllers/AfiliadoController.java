package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Servicio;
import com.prosesol.api.rest.services.IAfiliadoService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/afiliados")
public class AfiliadoController {

    protected static final Log logger = LogFactory.getLog(AfiliadoController.class);

    @Value("${app.clave}")
    private String clave;

    @Autowired
    private CalcularFecha calcularFechas;

    @Autowired
    private IAfiliadoService afiliadoService;

    @Autowired
    private IServicioService servicioService;

    @Autowired
    private GenerarClave generarClave;

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
        model.addAttribute("servicio", afiliado.getServicio());

        return "afiliados/crear";
    }

    @RequestMapping(value = "/crear/", method = RequestMethod.POST, params = "action=save")
    public String guardar(@Valid Afiliado afiliado, BindingResult result,
                          Model model, RedirectAttributes redirect, SessionStatus status) {

        String mensajeFlash = null;
        Date date = new Date();

        try {
            if (afiliado.getId() != null) {

                if (afiliado.getIsBeneficiario().equals(true)) {
                    afiliado.setIsBeneficiario(true);
                } else {
                    afiliado.setIsBeneficiario(false);
                }

                mensajeFlash = "Registro editado con éxito";

            } else {
                afiliado.setIsBeneficiario(false);
                afiliado.setFechaAlta(date);
                afiliado.setClave(generarClave.getClaveAfiliado(clave));
                mensajeFlash = "Registro creado con éxito";
            }
            afiliado.setEstatus(2);
            logger.info(mensajeFlash);
            afiliadoService.save(afiliado);
            status.setComplete();

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            logger.error("Error al momento de ejecutar el proceso: " + e);
            redirect.addFlashAttribute("error", "El RFC ya existe en la base de datos ");

            return "redirect:/afiliados/crear";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error al momento de ejecutar el proceso: " + e);

            redirect.addFlashAttribute("error", "Ocurrió un error al momento de insertar el Afiliado");

            return "redirect:/afiliados/crear";
        }

        mensajeFlash = "id del afiliado creado es: " + afiliado.getId();
        logger.info(mensajeFlash);
        redirect.addFlashAttribute("success", "Afiliado creado con éxito");
        return "redirect:/beneficiarios/crear/" + afiliado.getId();
    }

    @RequestMapping(value = "/bienvenido/{id}")
    public String mostrar(@PathVariable("id") Long id, Model model, RedirectAttributes redirect) {

        Double costoTitular, inscripcionTitular, costoBeneficiario, inscripcionBeneficiario, costoTotalAfiliado, costoTotalBeneficiario, mensualidad;
        try {
            Afiliado afiliado = afiliadoService.findById(id);
            Long idServicio = afiliado.getServicio().getId();
            Servicio servicio = servicioService.findById(idServicio);

            costoTitular = servicio.getCostoTitular();
            inscripcionTitular = servicio.getInscripcionTitular();
            //mensualidad = d1;
            costoTotalAfiliado = costoTitular + inscripcionTitular;

            List<Afiliado> beneficiarios = afiliadoService.getBeneficiarioByIdByIsBeneficiario(id);
            if (beneficiarios != null) {
                costoBeneficiario = servicio.getCostoBeneficiario();
                inscripcionBeneficiario = servicio.getInscripcionBeneficiario();
                costoTotalBeneficiario = costoBeneficiario + inscripcionBeneficiario;

                model.addAttribute("costoBeneficiario", costoTotalBeneficiario);
                model.addAttribute("beneficiarios", beneficiarios);
             
            }

            model.addAttribute("costoAfiliado", costoTotalAfiliado);
            model.addAttribute("id", id);
            model.addAttribute("afiliado", afiliado);

        } catch (Exception e) {
        	 e.printStackTrace();
             logger.error("Error al momento de ejecutar el proceso: " + e);
             redirect.addFlashAttribute("error", "Ocurrió un error al momento de insertar el saldo acumulado");

             return "redirect:/afiliados/bienvenido/"+id;
        }
        return "afiliados/bienvenido";
    }

    @RequestMapping(value = "/guardar/{id}")
    public String guardaSaldoAfiliado(@PathVariable("id") Long id, Model model, SessionStatus status,RedirectAttributes redirect) {

        Afiliado afiliado = new Afiliado();

        Double costoTitular, inscripcionTitular, costoBeneficiario, inscripcionBeneficiario, saldoAcumulado;
        //	String periodo = "MENSUAL";
        //	Integer corte=0;
        try {
            afiliado = afiliadoService.findById(id);
            Long idServicio = afiliado.getServicio().getId();
            Servicio serv = servicioService.findById(idServicio);

            costoTitular = serv.getCostoTitular();
            inscripcionTitular = serv.getInscripcionTitular();

            saldoAcumulado = costoTitular + inscripcionTitular;

            List<Afiliado> beneficiarios = afiliadoService.getBeneficiarioByIdByIsBeneficiario(id);
            if (beneficiarios != null) {


                for (Integer x = 0; x < beneficiarios.size(); x++) {
                    costoBeneficiario = serv.getCostoBeneficiario();
                    inscripcionBeneficiario = serv.getInscripcionBeneficiario();
                    saldoAcumulado += costoBeneficiario + inscripcionBeneficiario;
                }

            }

            afiliado.setSaldoAcumulado(saldoAcumulado);
            afiliado.setSaldoCorte(saldoAcumulado);
            afiliadoService.save(afiliado);
            status.setComplete();

        } catch (Exception e) {
        	 e.printStackTrace();
             logger.error("Error al momento de ejecutar el proceso: " + e);
             redirect.addFlashAttribute("error", "Ocurrió un error al momento de insertar el saldo acumulado");

             return "redirect:/afiliados/bienvenido/"+id;
        }

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

}
