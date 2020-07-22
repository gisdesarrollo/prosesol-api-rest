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
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Controller
@RequestMapping("/afiliados")
@SessionAttributes({"respuestas", "candidato", "rfc", "urlPdf"})
@PropertySource({"classpath:hostname.properties", "classpath:application.properties"})
public class AfiliadoController implements IHttpUrlConnection{

    protected static final Log LOG = LogFactory.getLog(AfiliadoController.class);

    @Value("${app.clave}")
    private String clave;

    @Value("${afiliado.servicio.total.id}")
    private Long idTotal;

    @Value("${email.bc4nb.login}")
    private String emailLogin;

    @Value("${password.bc4nb.login}")
    private String passwordLogin;

    @Value("${api.url.insurance}")
    private String urlInsurance;

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

    @Autowired
    private IGetTokenService getTokenService;
    
    @Value("${template.cuestionario.covid}")
    private String templateCuestionarioId;

    @Value("${envio.correo.paralife.cuestionario}")
    private String correoParalife;

    @Value("${servicio.covid.id}")
    private Long servicioCovid;

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


            if(id == servicioCovid) {
                for (RelPreguntaRespuesta rel : relPreguntaRespuestaDto.getRelPreguntaRespuestas()) {
                    if (rel.getRespuesta().getId() == 3) {
                        return "/error/solicitud";
                    }
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
            model.addAttribute("servicioCovid", servicioCovid);

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
                          @ModelAttribute("option") Integer option,
                          @RequestParam(name = "formPersonExpuesta[]", required = false)
                                      List<String> formPersonaExpuesta,
                          Model model,
                          RedirectAttributes redirect,
                          SessionStatus status) {

        String mensajeFlash = null;
        Date date = new Date();
        Double saldoAcumulado = 0.0;
        Integer corte = 0;
        String periodo = "MENSUAL";
        String urlPdf = null;

        try {

            Afiliado buscarAfiliadoExistente = afiliadoService.findByRfc(candidato.getRfc());
        	Candidato buscarCandidatoExistente = candidatoService.findByRfc(candidato.getRfc());
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

            //Se obtiene la hora local MX
            Locale locale = new Locale("es", "MX");
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City"),
                    locale);
            Date fechaMX = calendar.getTime();

            if(candidato.getServicio().getId() == servicioCovid) {
                // Se guardan las preguntas y respuestas del candidato
                for (RelPreguntaRespuesta PRC : respuestas) {
                    RelPreguntaRespuestaCandidato RPRC = new RelPreguntaRespuestaCandidato(candidato,
                            PRC.getPregunta(), PRC.getRespuesta(), fechaMX);

                    relPreguntaRespuestaCandidatoService.save(RPRC);
                }

                // Envío de datos y envío de correo por servicio externo (BC4NB)
                urlPdf = enviarDatosAseguradora(candidato, respuestas, option, formPersonaExpuesta);
                model.addAttribute("urlPdf", urlPdf);

            }
            
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
                          @ModelAttribute(name = "urlPdf") String urlPdf,
                          Model model,
                          RedirectAttributes redirect,
                          SessionStatus status) {
    	
        try {
        	Candidato candidato=candidatoService.findById(id);
           
            model.addAttribute("id", id);
            model.addAttribute("candidato", candidato);
            model.addAttribute("servicioCovid", servicioCovid);
            model.addAttribute("urlPdf", urlPdf);

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Error al momento de ejecutar el proceso: " + e);
            redirect.addFlashAttribute("error", "Ocurrió un error ");

            return "redirect:/afiliados/bienvenido/" + id;
        }

        return "afiliados/bienvenido";
    }

    @RequestMapping(value = "/guardar", method = RequestMethod.POST)
    public String guardaSaldoAfiliado(Candidato candidato,
                                      Model model, RedirectAttributes redirect) {

        String rfc = candidato.getRfc();

        try{
            if(candidato.getServicio().getId() == servicioCovid){
                redirect.addFlashAttribute("rfc", rfc);
                return "redirect:/pagos/tarjeta";
            }
        }catch (Exception e){
            e.printStackTrace();
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

    /**
     * Método que se encarga de enviar la información para la póliza de seguro Covid-19
     * @param candidato
     * @param respuestas
     * @param option
     * @param formPersonaExpuesta
     * @return
     * @throws IOException
     */
    private String enviarDatosAseguradora(Candidato candidato, List<RelPreguntaRespuesta> respuestas,
                                          Integer option, List<String> formPersonaExpuesta)
            throws IOException {

        URL url = new URL(urlInsurance);
        String urlPdf = null;
        HttpURLConnection urlConnection = null;
        urlConnection = openConnection(urlConnection, "POST", url);

        if(urlConnection != null){
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            JSONObject data = new JSONObject();

            // Creación del payload
            data.put("insured_relationship", "Titular");
            data.put("titular_name", candidato.getNombre());
            data.put("titular_last_name", candidato.getApellidoPaterno());
            data.put("titular_mother_name", candidato.getApellidoMaterno());
            data.put("titular_gender", candidato.getSexo().toUpperCase());
            data.put("titular_birthday", candidato.getFechaNacimiento());
            data.put("titular_cellphone", candidato.getTelefonoMovil());
            data.put("titular_address", candidato.getCalle());
            data.put("titular_email", candidato.getEmail());
            data.put("titular_num_ext", candidato.getNoExterior());
            data.put("titular_zip", candidato.getCodigoPostal().toString());
            data.put("titular_state", candidato.getEntidadFederativa());
            data.put("titular_town", candidato.getCiudad());
            data.put("titular_city", candidato.getCiudad());
            data.put("titular_suburb", candidato.getCalle());
            data.put("titular_occupation", candidato.getOcupacion());

            // Verificar respuestas
            for(RelPreguntaRespuesta respuesta : respuestas){
                if(respuesta.getPregunta().getId() == 2){
                    if(respuesta.getRespuesta().getId() == 4){
                        data.put("diagnosed_covid", false);
                    }
                }else if(respuesta.getPregunta().getId() == 3){
                    if(respuesta.getRespuesta().getId() == 4){
                        data.put("is_hospitalized", false);
                    }
                }
            }

            // Se evalúa la opción de los datos de la persona políticamente expuesta
            if(option == 1){
                data.put("exposed_value", "1");
                LinkedHashMap<Integer, String> exposedData = new LinkedHashMap<>();
                exposedData.put(0, "exposed_name");
                exposedData.put(1, "exposed_last_name");
                exposedData.put(2, "exposed_title");
                exposedData.put(3, "exposed_relationship");
                exposedData.put(4, "exposed_period");
                for(Map.Entry<Integer, String> exposedInfo : exposedData.entrySet()){
                    data.put(exposedInfo.getValue(), formPersonaExpuesta.get(exposedInfo.getKey()));
                    if(exposedInfo.getKey() == 4){
                        if(formPersonaExpuesta.get(exposedInfo.getKey()).length() > 0){
                            data.put(exposedInfo.getValue(), formPersonaExpuesta.get(
                                    exposedInfo.getKey()));
                        }else{
                            data.put(exposedInfo.getValue(), "-");
                        }
                    }
                }
            }else{
                data.put("exposed_value", "0");
            }

            os.write(data.toString().getBytes("UTF-8"));
            os.close();

            //Leemos respuesta
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();

            String result;
            while((result = bufferedReader.readLine()) != null){
                sb.append(result);
                LOG.info(sb.toString());
            }

            JSONObject response = new JSONObject(sb.toString());
            urlPdf = response.getString("insurance_url");

            urlConnection.disconnect();

        }

        return urlPdf;
    }

    @Override
    public HttpURLConnection openConnection(HttpURLConnection httpUrlConnection, String method, URL url) {

        String token = getTokenService.getTokenWithEmailAndPassword(emailLogin, passwordLogin);

        try{
            httpUrlConnection = (HttpURLConnection)url.openConnection();
            httpUrlConnection.setRequestMethod(method);
            httpUrlConnection.setRequestProperty("Content-Type", "application/json");
            httpUrlConnection.setRequestProperty("Accept", "application/json");
            httpUrlConnection.setRequestProperty("Authorization", "Bearer " + token);
        }catch (IOException e){
            e.printStackTrace();
        }


        return httpUrlConnection;
    }
}
