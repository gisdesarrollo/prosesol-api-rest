package com.prosesol.api.rest.controllers;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Candidato;
import com.prosesol.api.rest.models.entity.Cliente;
import com.prosesol.api.rest.models.entity.Pago;
import com.prosesol.api.rest.models.entity.Plan;
import com.prosesol.api.rest.models.entity.Suscripcion;
import com.prosesol.api.rest.repository.AfiliadoRepository;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.ICandidatoService;
import com.prosesol.api.rest.services.IClienteService;
import com.prosesol.api.rest.services.IPagoService;
import com.prosesol.api.rest.services.IPlanService;
import com.prosesol.api.rest.services.ISuscripcionService;
import com.prosesol.api.rest.utils.CalcularFecha;

import mx.openpay.client.Card;
import mx.openpay.client.Charge;
import mx.openpay.client.Customer;
import mx.openpay.client.Subscription;
import mx.openpay.client.core.OpenpayAPI;
import mx.openpay.client.core.requests.transactions.CreateBankChargeParams;
import mx.openpay.client.core.requests.transactions.CreateCardChargeParams;
import mx.openpay.client.core.requests.transactions.CreateStoreChargeParams;
import mx.openpay.client.exceptions.OpenpayServiceException;
import mx.openpay.client.exceptions.ServiceUnavailableException;

@Controller
@RequestMapping("/pagos")
public class PagoCandidatoController {

	protected final Logger LOG = LoggerFactory.getLogger(PagoCandidatoController.class);
	private final static String ID_TEMPLATE_FT = "77e17b72";
	private final static String ID_TEMPLATE_IB = "2cd93909";
	private final static String ID_TEMPLATE_PI = "0044a2f4";
	private final static String ID_TEMPLATE_PM = "d1112f68";

	@Value("${openpay.id}")
	private String merchantId;

	@Value("${openpay.pk}")
	private String privateKey;

	@Value("${openpay.url}")
	private String openpayURL;

	@Value("${openpay.url.dashboard.pdf}")
	private String openpayDashboardUrl;

	@Value("${afiliado.servicio.individual.id}")
	private Long idIndividual;

	@Value("${afiliado.servicio.total.id}")
	private Long idTotal;

	@Value("${archivo.plan.familiar}")
	private String archivoPlanFamiliar;

	@Value("${archivo.plan.individual}")
	private String archivoPlanIndividual;

	@Autowired
	private EmailController emailController;

	@Autowired
	private CalcularFecha calcularFechas;

	@Autowired
	private IAfiliadoService afiliadoService;

	@Autowired
	private IPagoService pagoService;

	@Autowired
	private IPlanService planService;

	@Autowired
	private ISuscripcionService suscripcionService;

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private ICandidatoService candidatoService;

	@Autowired
	private AfiliadoRepository afiliadoRepository;

	private OpenpayAPI apiOpenpay;

	private int code;

	private int errorCode;

	@RequestMapping(value = "/tarjetas/{id}", method = RequestMethod.POST)
	public String pagoCandidatoBytarjeta(@PathVariable("id") Long id, @ModelAttribute("token_id") String tokenId,
			@ModelAttribute("deviceIdHiddenFieldName") String deviceSessionId,
			@RequestParam(value = "suscripcion", required = false) boolean suscripcion,
			@RequestParam(value = "montoPagar", required = false) Double montoPagar, RedirectAttributes redirect,
			SessionStatus status, Model vista) {

		Candidato candidato = candidatoService.finById(id);
        Customer customer = new Customer();
        Suscripcion sus = new Suscripcion();
        apiOpenpay = new OpenpayAPI(openpayURL, privateKey, merchantId);

        BigDecimal amount = BigDecimal.valueOf(montoPagar);
        amount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        try {
        	Cliente cliente = new Cliente();            
            Pago pago = new Pago();
            customer.setName(candidato.getNombre());
            customer.setLastName(candidato.getApellidoPaterno() + ' ' + candidato.getApellidoMaterno());
            customer.requiresAccount(false);

            boolean isNotValid = isNullOrEmpty(candidato.getEmail());

            if(!montoPagar.equals(candidato.getSaldoCorte()) && candidato.getIsInscripcion()){
                vista.addAttribute("error", "Para poder inscribirse al programa, " +
                        "deberá de realizar el pago total de su servicio (Costo + Inscripción)");
                return "pagos/tarjeta";
            }

            if (isNotValid) {
                customer.setEmail("mail@mail.com");
            } else {
                customer.setEmail(candidato.getEmail());
            }
            //se crea cliente para crear la tarjeta
            customer = apiOpenpay.customers().create(customer);
            LOG.info("Customer: " + customer);
            if (customer.getId() == null) {
                redirect.addFlashAttribute("error", "Error al momento de crear el cliente");
                return "redirect:/pagos/tarjeta/buscar";
            }

            if(suscripcion){
                Plan plan = planService.getPlanByIdServicio(candidato.getServicio());
                if(plan != null){
                    Card card = new Card().tokenId(tokenId);

                    card = apiOpenpay.cards().create(customer.getId(), card);
                    if(card != null){
                        // Se crea la suscripción del afiliado al plan
                        Subscription subscription = new Subscription();
                        subscription.planId(plan.getPlanOpenpay());
                        subscription.trialEndDate(candidato.getFechaCorte());
                        subscription.sourceId(card.getId());
                        subscription = apiOpenpay.subscriptions().create(customer.getId(),
                                subscription);

                        LOG.info("Subscription: " + subscription);

                        if(!subscription.getStatus().equals("trial")){
                            redirect.addFlashAttribute("error", "Error al momento de suscribir");
                            return "redirect:/pagos/tarjeta/buscar";
                        }else{ // Se crea la suscripcion y el cliente en la base de datos
                        	System.out.println(plan.getId());
                        	System.out.println(subscription.getId());
                        	
                           sus = new Suscripcion(plan, subscription.getId(), true);
                           suscripcionService.save(sus);
                          
                        }
                    }
                }else{
                    redirect.addFlashAttribute("error", "No se ha podido procesar " +
                            "su pago, su servicio no puede ser domiciliado");
                    return "redirect:/pagos/tarjeta/buscar";
                }
            }

            CreateCardChargeParams creditCardcharge = new CreateCardChargeParams()
                    .cardId(tokenId)
                    .amount(amount)
                    .description("Cargo a nombre de: " + candidato.getNombre());


            @SuppressWarnings("deprecation")
            Charge charge = apiOpenpay.charges().create(customer.getId(), creditCardcharge);
            LOG.info("Charge: " + charge);
            if (charge.getId()!=null) {

                // Se verifica si eel afiliado se ha inscrito por primera vez o es su segundo pago
                if (candidato.getIsInscripcion()) {
                    
                      // Se resta el saldo acumulado obteniendo la inscripción de su servicio
                        Double saldoAcumulado = candidato.getSaldoAcumulado() -
                                candidato.getServicio().getInscripcionTitular();
                        candidato.setSaldoAcumulado(saldoAcumulado);
                        candidato.setIsInscripcion(false);
                    
                }

                pago.setFechaPago(new Date());
                pago.setMonto(charge.getAmount().doubleValue());
                pago.setReferenciaBancaria(charge.getAuthorization());
                pago.setEstatus(charge.getStatus());
                pago.setTipoTransaccion("Pago con tarjeta");
                pago.setIdTransaccion(charge.getId());

                Double restaSaldoCorte = 0.0;

                restaSaldoCorte = candidato.getSaldoCorte() - montoPagar;
                candidato.setSaldoCorte(restaSaldoCorte);

                // Envío email bienvenida
                boolean isnotEmptyEmail = isNullOrEmpty(candidato.getEmail());
                if (!isnotEmptyEmail) {

                    List<String> correos = new ArrayList<>();
                    List<File> adjuntos = new ArrayList<>();
                    List<String> templates;

                    try {

                        Map<String, String> model = new LinkedHashMap<>();
                        model.put("nombre", candidato.getNombre() + " " + candidato.getApellidoPaterno() +
                                " " + candidato.getApellidoMaterno());
                        model.put("servicio", candidato.getServicio().getNombre());
                        model.put("rfc", candidato.getRfc());

                        correos.add(candidato.getEmail());
                        templates = emailController.getAllTemplates();

                        // Se compara el id del estatus para saber qué tipo de correo se enviará
                        if (candidato.getEstatus() == 3) {
                            // Comparamos los id's de los servicios para obtener el template correcto
                            if (candidato.getServicio().getId() == idIndividual) {
                                for (String template : templates) {
                                    int separator = template.indexOf("-");
                                    String idTemplateStr = template.substring(0, separator);
                                    if (idTemplateStr.equals(ID_TEMPLATE_IB)) {
                                        adjuntos.add(ResourceUtils.getFile(archivoPlanIndividual));
                                        String templateBienvenidoIB = template;
                                        LOG.info("Template de bienvenido Individual Básico: " + templateBienvenidoIB);
                                        emailController.sendEmail(templateBienvenidoIB, correos, adjuntos, model);
                                    }
                                    if (idTemplateStr.equals(ID_TEMPLATE_PI)) {
                                        String templatePagoIN = template;
                                        LOG.info("Template de inscripción: " + templatePagoIN);
                                        emailController.sendEmail(templatePagoIN, correos, adjuntos, model);
                                    }
                                }
                            } else if (candidato.getServicio().getId() == idTotal) {
                                for (String template : templates) {
                                    int separator = template.indexOf("-");
                                    String idTemplateStr = template.substring(0, separator);
                                    if (idTemplateStr.equals(ID_TEMPLATE_FT)) {
                                        adjuntos.add(ResourceUtils.getFile(archivoPlanFamiliar));
                                        String templateBienvenidoFT = template;
                                        LOG.info("Template de bienvenido Familiar Total: " + templateBienvenidoFT);
                                        emailController.sendEmail(templateBienvenidoFT, correos, adjuntos, model);
                                    }
                                    if (idTemplateStr.equals(ID_TEMPLATE_PI)) {
                                        String templatePagoIN = template;
                                        LOG.info("Template de inscripción: " + templatePagoIN);
                                        emailController.sendEmail(templatePagoIN, correos, adjuntos, model);
                                    }
                                }
                            }
                        } else if (candidato.getEstatus() == 1) {
                            for (String template : templates) {
                                int separator = template.indexOf("-");
                                String idTemplateStr = template.substring(0, separator);
                                if (idTemplateStr.equals(ID_TEMPLATE_PM)) {
                                    String templateBienvenidoPM = template;
                                    LOG.info("Template pago mensualidad: " + templateBienvenidoPM);
                                    emailController.sendEmail(templateBienvenidoPM, correos, adjuntos, model);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (candidato.getEstatus() != 1 && candidato.getServicio().getId() != idTotal) {
                    candidato.setEstatus(1);
                    candidato.setFechaAfiliacion(new Date());
                }
         
                pagoService.save(pago);
                Afiliado afiliado=datosAfiliado(candidato);
                afiliadoService.save(afiliado);
                	if(suscripcion) {
                		cliente = new Cliente(sus, customer.getId(), true, afiliado);
                		clienteService.save(cliente);
                	}
                afiliadoRepository.insertRelAfiliadosPagos(afiliado, pago.getId());
                candidatoService.deleteById(candidato.getId());
                redirect.addFlashAttribute("success", "Su pago se ha realizado correctamente con el " +
                        "siguiente número de folio: " + charge.getAuthorization());

                status.setComplete();
            }


        } catch (OpenpayServiceException | ServiceUnavailableException e) {

            String response = e.toString().substring(e.toString().indexOf("("), e.toString().lastIndexOf(")"));
            String responseValues[] = response.split(",");

            ArrayList<String> list = new ArrayList<String>(Arrays.asList(responseValues));

            for (String value : list) {
                System.out.println(value);
                if (value.contains("httpCode")) {
                    code = Integer.parseInt(value.substring(value.lastIndexOf("=") + 1));
                }
                if (value.contains("errorCode")) {
                    errorCode = Integer.parseInt(value.substring(value.lastIndexOf("=") + 1));
                }
            }

            String descripcionError = evaluarCodigoError(errorCode);
            redirect.addFlashAttribute("error", descripcionError);
            return "redirect:/pagos/tarjeta/buscar";

        }

        return "redirect:/pagos/tarjeta/buscar";


	}
	
	 @RequestMapping(value = "/tienda/{rfc}")
	    public String generarReferenciaTiendaByRfc(@PathVariable("rfc") String rfc,
	                                               Model model, RedirectAttributes redirect,
	                                               HttpServletResponse servletResponse) {
		 Candidato candidato =candidatoService.findByRfc(rfc);
		 String reference = null;
	     String idTransaccion = null;
	     Pago pago = new Pago();
	     Customer customer = new Customer();
		 
		 try {

	            OpenpayAPI openpayAPI = new OpenpayAPI(openpayURL, privateKey, merchantId);
	            BigDecimal amount = BigDecimal.valueOf(candidato.getSaldoCorte());

	            customer.setName(candidato.getNombre());
	            customer.setLastName(candidato.getApellidoPaterno() + ' '
	                    + candidato.getApellidoMaterno());

	            boolean isNotValid = isNullOrEmpty(candidato.getEmail());

	            if (isNotValid) {
	                customer.setEmail("mail@mail.com");
	            } else {
	                customer.setEmail(candidato.getEmail());
	            }

	            CreateStoreChargeParams createStoreChargeParams = new CreateStoreChargeParams()
	                    .description("Cargo a Tienda")
	                    .amount(amount)
	                    .customer(customer);

	            Charge charge = openpayAPI.charges().createCharge(createStoreChargeParams);

	            String response = charge.toString().substring(charge.toString().indexOf("("),
	                    charge.toString().lastIndexOf(")"));

	            String responseValues[] = response.split(",");

	            ArrayList<String> list = new ArrayList<String>(Arrays.asList(responseValues));

	            for (String value : list) {
	                LOG.info(value);
	                if (value.contains("reference")) {
	                    reference = value.substring(value.lastIndexOf("=") + 1);
	                }
	                if (value.contains("id=")) {
	                    idTransaccion = value.substring(value.lastIndexOf("=") + 1);
	                }
	            }

	            pago.setMonto(amount.doubleValue());
	            pago.setFechaPago(new Date());
	            pago.setReferenciaBancaria("000000000");
	            pago.setTipoTransaccion("Referencia en tienda");
	            pago.setEstatus("in_progress");
	            pago.setIdTransaccion(idTransaccion);


	            pagoService.save(pago);
	            afiliadoRepository.insertRelCandidatoPagos(candidato, pago.getId());

	        } catch (OpenpayServiceException | ServiceUnavailableException e) {
	            String response = e.toString().substring(e.toString().indexOf("("), e.toString().lastIndexOf(")"));
	            String responseValues[] = response.split(",");

	            ArrayList<String> list = new ArrayList<String>(Arrays.asList(responseValues));

	            for (String value : list) {
	                if (value.contains("httpCode")) {
	                    code = Integer.parseInt(value.substring(value.lastIndexOf("=") + 1));
	                }
	                if (value.contains("errorCode")) {
	                    errorCode = Integer.parseInt(value.substring(value.lastIndexOf("=") + 1));
	                }
	            }

	            String descripcionError = evaluarCodigoError(errorCode);
	            redirect.addFlashAttribute("error", descripcionError);
	            return "redirect:/pagos/tienda/buscar";
	        }

	        servletResponse.setHeader("Location", openpayDashboardUrl + "/paynet-pdf/" + merchantId + "/"
	                + reference);
	        servletResponse.setStatus(302);

	        return null;
		
	 }
	 
	 @RequestMapping(value = "/banco/{rfc}")
	    public String generarReferenciaSpeiByRfc(@PathVariable("rfc") String rfc,
	                                             Model model, RedirectAttributes redirect,
	                                             HttpServletResponse servletResponse) {
		 
		 Candidato candidato =candidatoService.findByRfc(rfc);
	     String idTransaccion = null;
	     Pago pago = new Pago();
	     Customer customer = new Customer();
		 
	     try {

	            OpenpayAPI openpayAPI = new OpenpayAPI(openpayURL, privateKey, merchantId);
	            BigDecimal amount = BigDecimal.valueOf(candidato.getSaldoCorte());

	            customer.setName(candidato.getNombre());
	            customer.setLastName(candidato.getApellidoPaterno() + ' '
	                    + candidato.getApellidoMaterno());

	            boolean isNotValid = isNullOrEmpty(candidato.getEmail());

	            if (isNotValid) {
	                customer.setEmail("mail@mail.com");
	            } else {
	                customer.setEmail(candidato.getEmail());
	            }

	            CreateBankChargeParams createStoreChargeParams = new CreateBankChargeParams()
	                    .description("Cargo con banco")
	                    .amount(amount)
	                    .customer(customer);

	            Charge charge = openpayAPI.charges().createCharge(createStoreChargeParams);

	            String response = charge.toString().substring(charge.toString().indexOf("("),
	                    charge.toString().lastIndexOf(")"));

	            String responseValues[] = response.split(",");

	            ArrayList<String> list = new ArrayList<String>(Arrays.asList(responseValues));

	            for (String value : list) {
	                if (value.contains("id")) {
	                    idTransaccion = value.substring(value.lastIndexOf("=") + 1);
	                }
	            }

	            pago.setMonto(amount.doubleValue());
	            pago.setFechaPago(new Date());
	            pago.setReferenciaBancaria("000000000");
	            pago.setTipoTransaccion("SPEI");
	            pago.setEstatus("in_progress");
	            pago.setIdTransaccion(idTransaccion);


	            pagoService.save(pago);
	            afiliadoRepository.insertRelCandidatoPagos(candidato, pago.getId());

	        } catch (OpenpayServiceException | ServiceUnavailableException e) {
	            String response = e.toString().substring(e.toString().indexOf("("), e.toString().lastIndexOf(")"));
	            String responseValues[] = response.split(",");

	            ArrayList<String> list = new ArrayList<String>(Arrays.asList(responseValues));

	            for (String value : list) {
	                System.out.println(value);
	                if (value.contains("httpCode")) {
	                    code = Integer.parseInt(value.substring(value.lastIndexOf("=") + 1));
	                }
	                if (value.contains("errorCode")) {
	                    errorCode = Integer.parseInt(value.substring(value.lastIndexOf("=") + 1));
	                }
	            }

	            String descripcionError = evaluarCodigoError(errorCode);
	            redirect.addFlashAttribute("error", descripcionError);
	            return "redirect:/pagos/banco/buscar";
	        }

	        servletResponse.setHeader("Location", openpayDashboardUrl + "/spei-pdf/" + merchantId + "/"
	                + idTransaccion);
	        servletResponse.setStatus(302);

		 
		 return null;
	 }
	
	/**
     * Códigos de errores de open pay para las tarjetas rechazadas
     *
     * @param errorCode
     * @return
     */
    private String evaluarCodigoError(int errorCode) {

        String descripcionError = null;

        switch (errorCode) {
            case 3001:
                descripcionError = "La tarjeta fue rechazada";
                break;
            case 3002:
                descripcionError = "La tarjeta ha expirado";
                break;
            case 3003:
                descripcionError = "La tarjeta no tiene fondos suficientes";
                break;
            case 3004:
                descripcionError = "La tarjeta ha sido identificada como una tarjeta robada, verifique esta" +
                        " información con su banco";
                break;
            case 3005:
                descripcionError = "La tarjeta ha sido rechazada por el sistema antifraudes, verifique esta" +
                        " información con su banco";
                break;
        }

        return descripcionError;
    }

    /**
     * Método que evalúa si el email está vacío
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
     * Método para pasar datos del candidato a afiliado
     *
     * @param candidato
     * @return
     */  
 public Afiliado datosAfiliado(Candidato candidato) {
    	
    	Afiliado afiliado=new Afiliado();
    		afiliado.setClave(candidato.getClave());
    		afiliado.setNombre(candidato.getNombre());
    		afiliado.setApellidoPaterno(candidato.getApellidoPaterno());
    		afiliado.setApellidoMaterno(candidato.getApellidoMaterno());
    		afiliado.setFechaNacimiento(candidato.getFechaNacimiento());
    		afiliado.setLugarNacimiento(candidato.getLugarNacimiento());
    		afiliado.setEstadoCivil(candidato.getEstadoCivil());
    		afiliado.setOcupacion(candidato.getOcupacion());
    		afiliado.setSexo(candidato.getSexo());
    		afiliado.setPais(candidato.getPais());
    		afiliado.setCurp(candidato.getCurp());
    		afiliado.setNss(candidato.getNss());
    		afiliado.setRfc(candidato.getRfc());
    		afiliado.setTelefonoFijo(candidato.getTelefonoFijo());
    		afiliado.setTelefonoMovil(candidato.getTelefonoMovil());
    		afiliado.setEmail(candidato.getEmail());
    		afiliado.setDireccion(candidato.getDireccion());
    		afiliado.setMunicipio(candidato.getMunicipio());
    		afiliado.setCodigoPostal(candidato.getCodigoPostal());
    		afiliado.setEntidadFederativa(candidato.getEntidadFederativa());
    		afiliado.setInfonavit(candidato.getInfonavit());
    		afiliado.setNumeroInfonavit(candidato.getNumeroInfonavit());
    		afiliado.setFechaAlta(candidato.getFechaAlta());
    		afiliado.setFechaAfiliacion(candidato.getFechaAfiliacion());
    		afiliado.setFechaCorte(candidato.getFechaCorte());
    		afiliado.setSaldoAcumulado(candidato.getSaldoAcumulado());
    		afiliado.setSaldoCorte(candidato.getSaldoCorte());
    		afiliado.setEstatus(candidato.getEstatus());
    		afiliado.setInscripcion(candidato.getInscripcion());
    		afiliado.setServicio(candidato.getServicio());
    		afiliado.setComentarios(candidato.getComentarios());
    		afiliado.setIsBeneficiario(candidato.getIsBeneficiario());
    		afiliado.setIsInscripcion(candidato.getIsInscripcion());
    		afiliado.setPeriodicidad(candidato.getPeriodicidad());
    		afiliado.setCorte(candidato.getCorte());
		return afiliado;
    	
    }

}
