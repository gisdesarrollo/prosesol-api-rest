package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.controllers.exception.AfiliadoException;
import com.prosesol.api.rest.models.entity.*;
import com.prosesol.api.rest.models.entity.custom.PreguntaRespuestaCandidatoCustom;
import com.prosesol.api.rest.models.entity.custom.PreguntaRespuestaCustom;
import com.prosesol.api.rest.models.entity.dto.RelPreguntaRespuestaDto;
import com.prosesol.api.rest.models.rel.RelPreguntaRespuesta;
import com.prosesol.api.rest.models.rel.RelPreguntaRespuestaCandidato;
import com.prosesol.api.rest.services.*;
import com.prosesol.api.rest.utils.CalcularFecha;
import com.prosesol.api.rest.utils.GenerarClave;
import com.prosesol.api.rest.utils.Paises;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/afiliados")
@SessionAttributes("respuestas")
public class AfiliadoController {

    protected static final Log LOG = LogFactory.getLog(AfiliadoController.class);

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
    
    @Autowired
    private ICandidatoService candidatoService;

    @Autowired
    private IGetTemplateByServicio getTemplateByServicio;

    @Autowired
    private IPreguntaService preguntaService;

    @Autowired
    private IRespuestaService respuestaService;

    @Autowired
    private IRelPreguntaRespuestaService relPreguntaRespuestaService;

    @Autowired
    private IRelPreguntaRespuestaCandidatoService relPreguntaRespuestaCandidatoService;
    
    @Autowired
    private EmailController emailController;
    
    @Value("${template.cuestionario.covid}")
    private String templateCuestionarioId;
    
    @RequestMapping(value = "/servicio")
    public String seleccionarServicio(Model model) {
        List<Servicio> servicios = servicioService.findAll();

        model.addAttribute("servicios", servicios);
        return "afiliados/servicio";
    }

    @RequestMapping(value = "/servicio/{id}")
    public String getIdServicio(@PathVariable("id") Long id,
                                @ModelAttribute RelPreguntaRespuestaDto relPreguntaRespuestaDto,
                                Model model,
                                RedirectAttributes redirect) {

        Servicio servicio = servicioService.findById(id);
        Candidato afiliado = new Candidato();
        afiliado.setServicio(servicio);
        List<RelPreguntaRespuesta> respuestas = relPreguntaRespuestaDto.getRelPreguntaRespuestas();

        try {

            for(RelPreguntaRespuesta rel : relPreguntaRespuestaDto.getRelPreguntaRespuestas()){
                if(rel.getRespuesta().getId() == 3){
                    return "/error/solicitud";
                }
            }

            Integer servicioEmpresa = getTemplateByServicio.getTemplateByIdServicio(servicio.getId());

            if (servicio == null) {
                redirect.addFlashAttribute("error", "Debes seleccionar un servicio");
                return "redirect:/afiliados/servicio";
            }

            model.addAttribute("afiliado", afiliado);
            model.addAttribute("servicio", servicio);
            model.addAttribute("servicioEmpresa", servicioEmpresa);
            model.addAttribute("respuestas", respuestas);

        }catch (AfiliadoException aE){
            redirect.addFlashAttribute("error", aE.getMessage());
            return "redirect:/afiliados/servicio";
        }

        return "afiliados/crear";
    }

    @RequestMapping(value = "/servicio/cuestionario/{id}")
    public String getIdServicioCovid(@PathVariable("id")Long id, Model model,
                                     RedirectAttributes redirect){

        List<Pregunta> preguntas = preguntaService.findAll();
        List<PreguntaRespuestaCustom> relPreguntaRespuestas = relPreguntaRespuestaService.getPreguntaRespuesta();
        Servicio servicio = servicioService.findById(id);

        try{

            if(servicio != null) {
                model.addAttribute("preguntas", preguntas);
                model.addAttribute("relPreguntaRespuestas", relPreguntaRespuestas);
                model.addAttribute("servicio", servicio);
            }else{
                LOG.error("ERROR: ", new NullPointerException());
                throw new NullPointerException();
            }
        }catch(AfiliadoException aExc){
            redirect.addFlashAttribute("error", aExc.getMessage());
            return "redirect:/servicio/cuestionario/" + servicio.getId();
        }

        return "afiliados/servicioCovid";
    }

    @RequestMapping(value = "/crear", method = RequestMethod.POST)
    public String guardar(Candidato candidato,
                          @ModelAttribute(name = "respuestas")
                                  List<RelPreguntaRespuesta> respuestas,
                          @RequestParam(value = "option") Long option,
                          RedirectAttributes redirect,
                          SessionStatus status) {

        String mensajeFlash = null;
        Date date = new Date();
        Double saldoAcumulado = 0.0;
        Integer corte = 0;
        String periodo = "MENSUAL";

        Pregunta pregunta;
        Respuesta respuesta;

        try {

            Afiliado buscarAfiliadoExistente = afiliadoService.findByRfc(candidato.getRfc());
        	Candidato buscarCandidatoExistente=candidatoService.findByRfc(candidato.getRfc());
        	Servicio servicio = servicioService.findById(candidato.getServicio().getId());

            Periodicidad periodicidad = periodicidadService.getPeriodicidadByNombrePeriodo(periodo);

            if (buscarAfiliadoExistente != null || buscarCandidatoExistente!=null) {
                LOG.error("Error afiliado ya se encuentra registrado");
                redirect.addFlashAttribute("error", "El Afiliado ya se encuentra registrado");

                return "redirect:/afiliados/servicio/" + candidato.getServicio().getId();
            }

            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            LocalDate fechaNacimiento = candidato.getFechaNacimiento().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            int fNacimiento = fechaNacimiento.getYear();

            int rangoEdad = currentYear - fNacimiento;
            if (rangoEdad >= 55 && servicio.getId() == idTotal) {
                redirect.addFlashAttribute("error", "A partir de los 55 años, no puede contratar este servicio");
                return "redirect:/afiliados/servicio/" + candidato.getServicio().getId();
            }
            
            
            if (candidato.getId() != null) {
				
            } else {
                saldoAcumulado = servicio.getCostoTitular() + servicio.getInscripcionTitular();
                candidato.setIsInscripcion(true);
                
                candidato.setSaldoAcumulado(saldoAcumulado);
                candidato.setSaldoCorte(saldoAcumulado);
                candidato.setIsBeneficiario(false);
                candidato.setFechaAlta(date);
                candidato.setClave(generarClave.getClaveAfiliado(clave));
                
                DateFormat formatoFecha = new SimpleDateFormat("dd");
                String dia = formatoFecha.format(candidato.getFechaAlta());
                corte = Integer.parseInt(dia);
                Date fechaCorte = calcularFechas.calcularFechas(periodo, corte);
                candidato.setFechaCorte(fechaCorte);
                candidato.setPeriodicidad(periodicidad);
                
                mensajeFlash = "Registro creado con éxito";
                 
            }
            candidato.setEstatus(3);
            LOG.info(mensajeFlash);
            candidatoService.save(candidato);

            // Se guardan las preguntas y respuestas del candidato
            for(RelPreguntaRespuesta PRC : respuestas){
                pregunta = preguntaService.findById(PRC.getPregunta().getId());
                respuesta = respuestaService.findById(PRC.getRespuesta().getId());
                RelPreguntaRespuestaCandidato RPRC = new RelPreguntaRespuestaCandidato(candidato,
                        pregunta, respuesta);

                relPreguntaRespuestaCandidatoService.save(RPRC);
            }
          //implementa el envio de correos
            if(servicio!=null) {
            	Long idC=74L;
            	List<String> correos = new ArrayList<>();
            	JSONArray rPRC = new JSONArray();
            	JSONObject jsonObjectParameters = new JSONObject();
            	
            	List<PreguntaRespuestaCandidatoCustom> rPRCService=relPreguntaRespuestaCandidatoService.getPreguntaAndRespuestaBycandidatoById(idC);
            	for(PreguntaRespuestaCandidatoCustom resultadoPRC :rPRCService) {
            		rPRC.put(getDatosJson(resultadoPRC.getPregunta(),resultadoPRC.getRespuesta()));
    				
            	}
            	jsonObjectParameters.put("resultado", rPRC);
            	 
            	 correos.add("gama_9416@hotmail.com");
                  emailController.sendEmailCuestionario(templateCuestionarioId, correos, jsonObjectParameters);

            }

            status.setComplete();
            
        } catch (DataIntegrityViolationException e) {
            LOG.error("Error al momento de ejecutar el proceso: " + e);
            redirect.addFlashAttribute("error", "El RFC ya existe en la base de datos ");

            return "redirect:/afiliados/servicio/" + candidato.getServicio().getId();
        } catch (Exception e) {
            LOG.error("Error al momento de ejecutar el proceso: " + e);

            redirect.addFlashAttribute("error", "Ocurrió un error al momento de insertar el Afiliado");

            return "redirect:/afiliados/servicio/" + candidato.getServicio().getId();
        }
      
        mensajeFlash = "id del afiliado creado es: " + candidato.getId();
        LOG.info(mensajeFlash);
        redirect.addFlashAttribute("success", "Afiliado creado con éxito");
        return "redirect:/afiliados/bienvenido/" +candidato.getId() ;
    }

    @RequestMapping(value = "/bienvenido/{id}")
    public String mostrar(@PathVariable("id") Long id,
                          Model model, RedirectAttributes redirect) {
    	
        try {
        	Candidato candidato=candidatoService.finById(id);
           
            model.addAttribute("id", id);
            model.addAttribute("afiliado", candidato);

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Error al momento de ejecutar el proceso: " + e);
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
    
    /**
     * Metodo para los arrays json
     *
     * @param pregunta
     * @param respuesta
     * @return
     */
    public JSONObject getDatosJson(String pregunta, String respuesta){
    	JSONObject cuestionario = new JSONObject();
    	   cuestionario.put("pregunta", pregunta);
    	   cuestionario.put("respuesta", respuesta);
    	   return cuestionario ;
    }

}
