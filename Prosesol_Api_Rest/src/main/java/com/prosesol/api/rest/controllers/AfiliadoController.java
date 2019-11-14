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

    @RequestMapping(value = "/crear", method = RequestMethod.POST)
    public String guardar(Afiliado afiliado, BindingResult result,
                          Model model, RedirectAttributes redirect, SessionStatus status) {

        String mensajeFlash = null;
        Date date = new Date();
        Servicio servicio = servicioService.findById(afiliado.getServicio().getId());
        Double saldoAcumulado=0.0;
        try {
            if (afiliado.getId() != null) {

                if (afiliado.getIsBeneficiario().equals(true)) {
                    afiliado.setIsBeneficiario(true);
                } else {
                    afiliado.setIsBeneficiario(false);
                }

                mensajeFlash = "Registro editado con éxito";

            } else {
            	saldoAcumulado=servicio.getCostoTitular() + servicio.getInscripcionTitular();
            	afiliado.setSaldoAcumulado(saldoAcumulado);
            	afiliado.setSaldoCorte(saldoAcumulado);
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

            return "redirect:/afiliados/servicio/"+afiliado.getServicio().getId();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error al momento de ejecutar el proceso: " + e);

            redirect.addFlashAttribute("error", "Ocurrió un error al momento de insertar el Afiliado");

            return "redirect:/afiliados/servicio/"+afiliado.getServicio().getId();
        }

        mensajeFlash = "id del afiliado creado es: " + afiliado.getId();
        logger.info(mensajeFlash);
        redirect.addFlashAttribute("success", "Afiliado creado con éxito");
        return "redirect:/beneficiarios/crear/" + afiliado.getId();
    }

    @RequestMapping(value = "/bienvenido/{id}")
    public String mostrar(@PathVariable("id") Long id, Model model, RedirectAttributes redirect) {

        try {
            Afiliado afiliado = afiliadoService.findById(id);
           Servicio servicio = servicioService.findById(afiliado.getServicio().getId());

           Double saldoAcumulado=0.0;
         

            List<Afiliado> beneficiarios = afiliadoService.getBeneficiarioByIdByIsBeneficiario(id);
            if (beneficiarios != null) {
               saldoAcumulado=servicio.getCostoBeneficiario() + servicio.getInscripcionBeneficiario();

                model.addAttribute("saldoAcumulado", saldoAcumulado);
                model.addAttribute("beneficiarios", beneficiarios);
             
            }
   
         
            model.addAttribute("id", id);
            model.addAttribute("afiliados", afiliado);

        } catch (Exception e) {
        	 e.printStackTrace();
             logger.error("Error al momento de ejecutar el proceso: " + e);
             redirect.addFlashAttribute("error", "Ocurrió un error ");

             return "redirect:/afiliados/bienvenido/"+id;
        }
        return "afiliados/bienvenido";
    }

    @RequestMapping(value = "/guardar")
    public String guardaSaldoAfiliado(Model model, SessionStatus status,RedirectAttributes redirect) {

       

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
