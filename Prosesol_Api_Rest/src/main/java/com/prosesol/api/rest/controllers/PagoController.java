package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.models.entity.*;
import com.prosesol.api.rest.models.entity.Plan;
import com.prosesol.api.rest.repository.AfiliadoRepository;
import com.prosesol.api.rest.services.*;
import com.prosesol.api.rest.utils.CalcularFecha;
import mx.openpay.client.*;
import mx.openpay.client.core.OpenpayAPI;
import mx.openpay.client.core.requests.transactions.CreateBankChargeParams;
import mx.openpay.client.core.requests.transactions.CreateCardChargeParams;
import mx.openpay.client.core.requests.transactions.CreateStoreChargeParams;
import mx.openpay.client.exceptions.OpenpayServiceException;
import mx.openpay.client.exceptions.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Luis Enrique Morales Soriano
 * @version 1.0
 */

@Controller
@RequestMapping("/pagos")
public class PagoController {

    protected final Logger LOG = LoggerFactory.getLogger(PagoController.class);
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
    private AfiliadoRepository afiliadoRepository;

    private OpenpayAPI apiOpenpay;

    private int code;

    private int errorCode;

    /**
     * Búsqueda de RFC para pagos con tarjeta
     *
     * @return
     */
    @GetMapping(value = "/tarjeta/buscar")
    public String pagoTarjetaBuscar() {
        return "/pagos/tarjeta";
    }

    /**
     * Búsqueda de RFC para generar referencia bancaria
     *
     * @return
     */
    @GetMapping(value = "/banco/buscar")
    public String pagoEfectivoBancoBuscar() {
        return "/pagos/banco";
    }

    /**
     * Búsqueda de RFC para generar referencia en pago en tiendas
     *
     * @return
     */
    @GetMapping(value = "/tienda/buscar")
    public String pagoEfectivoTiendaBuscar() {
        return "/pagos/tienda";
    }

    /**
     * Validación de RFC y saldo al corte del Afiliado para pagos con tarjeta
     *
     * @param rfc
     * @param model
     * @param redirect
     * @return
     */
    @RequestMapping(value = "/tarjeta")
    public String obtenerAfiliadoByRfcTarjeta(@ModelAttribute(name = "rfc") String rfc, Model model,
                                              RedirectAttributes redirect) {

        Afiliado afiliado = afiliadoService.findByRfc(rfc);
        Cliente cliente = clienteService.getClienteByIdAfiliado(afiliado);

        if (rfc.length() < 13) {

            LOG.info("ERR: ", "El RFC no cumple con los campos necesarios");
            redirect.addFlashAttribute("error", "El RFC No cumple con los campos necesarios");
            return "redirect:/pagos/tarjeta/buscar";

        }else if(cliente != null){
            LOG.info("INFO: ", "No puede realizar su pago ya que su servicio está domiciliado");
            redirect.addFlashAttribute("warning",
                    "No puede realizar su pago ya que su servicio está domiciliado");
            return "redirect:/pagos/tarjeta/buscar";
        }


        if (afiliado == null) {

            LOG.info("ERR: ", "No existe el afiliado, proporcione otro RFC");
            redirect.addFlashAttribute("error", "No existe el afiliado, proporcione otro RFC");
            return "redirect:/pagos/tarjeta/buscar";

        } else {

            if (afiliado.getIsBeneficiario() == false) {

                if (afiliado.getSaldoCorte() == null) {
                    LOG.info("El afiliado no cuenta con un saldo");
                    redirect.addFlashAttribute("info", "Usted no cuenta con un saldo al cual " +
                            "se le puede hacer el cargo, póngase en contacto a contacto@prosesol.org para dudas o aclaraciones");
                    return "redirect:/pagos/tarjeta/buscar";
                }
                else {
                    model.addAttribute("afiliado", afiliado);
                }
            } else {
                LOG.info("ERR: ", "El afiliado no es titular del servicio");
                redirect.addFlashAttribute("error", "El afiliado no es titular del servicio");
                return "redirect:tarjeta/buscar";
            }
        }

        return "/pagos";

    }

    /**
     * Validación del RFC y generación de la referencia para pagos en tienda
     *
     * @param rfc
     * @param model
     * @param redirect
     * @param servletResponse
     * @return
     * @throws {OpenpayServiceException, ServiceUnavailableException}
     */

    @RequestMapping(value = "/tienda")
    public String generarReferenciaTiendaByRfc(@ModelAttribute(name = "rfc") String rfc,
                                               Model model, RedirectAttributes redirect,
                                               HttpServletResponse servletResponse) {

        Afiliado afiliado = afiliadoService.findByRfc(rfc);
        String reference = null;
        String idTransaccion = null;

        Pago pago = new Pago();
        Customer customer = new Customer();

        if (rfc.length() < 13) {

            LOG.info("ERR: ", "El RFC no cumple con los campos necesarios");
            redirect.addFlashAttribute("error", "El RFC No cumple con los campos necesarios");
            return "redirect:/pagos/tienda/buscar";

        }

        if (afiliado == null) {

            LOG.info("ERR: ", "No existe el afiliado, proporcione otro RFC");
            redirect.addFlashAttribute("error", "No existe el afiliado, proporcione otro RFC");
            return "redirect:/pagos/tienda/buscar";

        } else {

            System.out.println(afiliado.toString());

            if (afiliado.getIsBeneficiario() == false) {
                if (afiliado.getSaldoCorte().equals(0.0)) {
                    LOG.info("El afiliado va al corriente de sus pagos");
                    redirect.addFlashAttribute("info", "Usted va al corriente con su pago, " +
                            "no es necesario que realice su pago");
                    return "redirect:/pagos/tienda/buscar";
                } else {
                    model.addAttribute("afiliado", afiliado);
                }
            } else {
                LOG.info("ERR: ", "El afiliado no es titular del servicio");
                redirect.addFlashAttribute("error", "El afiliado no es titular del servicio");
                return "redirect:/pagos/tienda/buscar";
            }
        }

        try {

            OpenpayAPI openpayAPI = new OpenpayAPI(openpayURL, privateKey, merchantId);
            BigDecimal amount = BigDecimal.valueOf(afiliado.getSaldoCorte());

            customer.setName(afiliado.getNombre());
            customer.setLastName(afiliado.getApellidoPaterno() + ' '
                    + afiliado.getApellidoMaterno());

            boolean isNotValid = isNullOrEmpty(afiliado.getEmail());

            if (isNotValid) {
                customer.setEmail("mail@mail.com");
            } else {
                customer.setEmail(afiliado.getEmail());
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
            afiliadoRepository.insertRelAfiliadosPagos(afiliado, pago.getId());

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

    /**
     * Validación del RFC y generación de referencia SPEI bancaria
     *
     * @param rfc
     * @param model
     * @param redirect
     * @param servletResponse
     * @return
     * @throws {OpenpayServiceException, ServiceUnavailableException}
     */

    @RequestMapping(value = "/banco")
    public String generarReferenciaSpeiByRfc(@ModelAttribute(name = "rfc") String rfc,
                                             Model model, RedirectAttributes redirect,
                                             HttpServletResponse servletResponse) {

        Afiliado afiliado = afiliadoService.findByRfc(rfc);
        String idTransaccion = null;

        Pago pago = new Pago();
        Customer customer = new Customer();

        if (rfc.length() < 13) {

            LOG.info("ERR: ", "El RFC no cumple con los campos necesarios");
            redirect.addFlashAttribute("error", "El RFC No cumple con los campos necesarios");
            return "redirect:/pagos/banco/buscar";

        }

        if (afiliado == null) {

            LOG.info("ERR: ", "No existe el afiliado, proporcione otro RFC");
            redirect.addFlashAttribute("error", "No existe el afiliado, proporcione otro RFC");
            return "redirect:/pagos/banco/buscar";

        } else {

            if (afiliado.getIsBeneficiario() == false) {
                if (afiliado.getSaldoCorte().equals(0.0)) {
                    LOG.info("El afiliado va al corriente de sus pagos");
                    redirect.addFlashAttribute("info", "Usted va al corriente con su pago, " +
                            "no es necesario que realice su pago");
                    return "redirect:/pagos/banco/buscar";
                } else {
                    model.addAttribute("afiliado", afiliado);
                }
            } else {
                LOG.info("ERR: ", "El afiliado no es titular del servicio");
                redirect.addFlashAttribute("error", "El afiliado no es titular del servicio");
                return "redirect:/pagos/banco/buscar";
            }
        }

        try {

            OpenpayAPI openpayAPI = new OpenpayAPI(openpayURL, privateKey, merchantId);
            BigDecimal amount = BigDecimal.valueOf(afiliado.getSaldoCorte());

            customer.setName(afiliado.getNombre());
            customer.setLastName(afiliado.getApellidoPaterno() + ' '
                    + afiliado.getApellidoMaterno());

            boolean isNotValid = isNullOrEmpty(afiliado.getEmail());

            if (isNotValid) {
                customer.setEmail("mail@mail.com");
            } else {
                customer.setEmail(afiliado.getEmail());
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
            afiliadoRepository.insertRelAfiliadosPagos(afiliado, pago.getId());

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
     * Generación de pago con tarjeta
     *
     * @param id
     * @param tokenId
     * @param redirect
     * @param status
     * @return
     */
    @RequestMapping(value = "/tarjeta/{id}", method = RequestMethod.POST)
    public String realizarCargoTarjeta(@PathVariable("id") Long id,
                                       @ModelAttribute("token_id") String tokenId,
                                       @ModelAttribute("deviceIdHiddenFieldName") String deviceSessionId,
                                       @RequestParam(value = "suscripcion", required = false) boolean suscripcion,
                                       @RequestParam(value = "montoPagar", required = false)Double montoPagar,
                                       RedirectAttributes redirect, SessionStatus status, Model vista) {

        Afiliado afiliado = afiliadoService.findById(id);
        Customer customer = new Customer();

        apiOpenpay = new OpenpayAPI(openpayURL, privateKey, merchantId);

        BigDecimal amount = BigDecimal.valueOf(montoPagar);
        amount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        try {

            Pago pago = new Pago();

            customer.setName(afiliado.getNombre());
            customer.setLastName(afiliado.getApellidoPaterno() + ' ' + afiliado.getApellidoMaterno());
            customer.requiresAccount(false);

            boolean isNotValid = isNullOrEmpty(afiliado.getEmail());

            if(!montoPagar.equals(afiliado.getSaldoCorte()) && afiliado.getIsInscripcion()){
                vista.addAttribute("error", "Para poder inscribirse al programa, " +
                        "deberá de realizar el pago total de su servicio (Costo + Inscripción)");
                return "pagos/tarjeta";
            }

            if (isNotValid) {
                customer.setEmail("mail@mail.com");
            } else {
                customer.setEmail(afiliado.getEmail());
            }
            //se crea cliente para crear la tarjeta
            customer = apiOpenpay.customers().create(customer);
            LOG.info("Customer: " + customer);
            if (customer.getId() == null) {
                redirect.addFlashAttribute("error", "Error al momento de crear el cliente");
                return "redirect:/pagos/tarjeta/buscar";
            }

            if(suscripcion){
                Plan plan = planService.getPlanByIdServicio(afiliado.getServicio());
                if(plan != null){
                    Card card = new Card().tokenId(tokenId);

                    card = apiOpenpay.cards().create(customer.getId(), card);
                    if(card != null){
                        // Se crea la suscripción del afiliado al plan
                        Subscription subscription = new Subscription();
                        subscription.planId(plan.getPlanOpenpay());
                        subscription.trialEndDate(afiliado.getFechaCorte());
                        subscription.sourceId(card.getId());
                        subscription = apiOpenpay.subscriptions().create(customer.getId(),
                                subscription);

                        LOG.info("Subscription: " + subscription);

                        if(!subscription.getStatus().equals("trial")){
                            redirect.addFlashAttribute("error", "Error al momento de suscribir");
                            return "redirect:/pagos/tarjeta/buscar";
                        }else{ // Se crea la suscripcion y el cliente en la base de datos
                            Suscripcion sus = new Suscripcion(plan, subscription.getId(), true);
                            suscripcionService.save(sus);
                            Cliente cliente = new Cliente(sus, customer.getId(), true, afiliado);
                            clienteService.save(cliente);
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
                    .description("Cargo a nombre de: " + afiliado.getNombre());


            @SuppressWarnings("deprecation")
            Charge charge = apiOpenpay.charges().create(customer.getId(), creditCardcharge);
            LOG.info("Charge: " + charge);
            if (charge.getStatus().equals("completed")) {

                // Se verifica si eel afiliado se ha inscrito por primera vez o es su segundo pago
                if (afiliado.getIsInscripcion()) {
                    // Se obtiene la lista de beneficiarios
                    List<Afiliado> beneficiarios = afiliadoService
                            .getBeneficiarioByIdByIsBeneficiario(afiliado.getId());
                    Double restaCostoInscripcion = new Double(0.0);

                    if (beneficiarios.size() > 0) {
                        for (Afiliado beneficiario : beneficiarios) {
                            Double inscripcionBeneficiario = beneficiario.getServicio()
                                    .getInscripcionBeneficiario();
                            restaCostoInscripcion = restaCostoInscripcion + inscripcionBeneficiario;
                            if (beneficiario.getEstatus() != 1 &&
                                    beneficiario.getServicio().getId() != idTotal) {
                                beneficiario.setEstatus(1);
                                beneficiario.setFechaAfiliacion(new Date());
                                afiliadoService.save(beneficiario);
                            }
                        }
                        // Se restan los costos de inscripción tanto de afiliados como beneficiarios
                        Double saldoAcumulado = afiliado.getSaldoAcumulado() - afiliado.getServicio()
                                .getInscripcionTitular() - restaCostoInscripcion;
                        afiliado.setSaldoAcumulado(saldoAcumulado);
                        afiliado.setIsInscripcion(false);

                    } else {
                        // Se resta el saldo acumulado obteniendo la inscripción de su servicio
                        Double saldoAcumulado = afiliado.getSaldoAcumulado() -
                                afiliado.getServicio().getInscripcionTitular();
                        afiliado.setSaldoAcumulado(saldoAcumulado);
                        afiliado.setIsInscripcion(false);
                    }
                }

                pago.setFechaPago(new Date());
                pago.setMonto(charge.getAmount().doubleValue());
                pago.setReferenciaBancaria(charge.getAuthorization());
                pago.setEstatus(charge.getStatus());
                pago.setTipoTransaccion("Pago con tarjeta");
                pago.setIdTransaccion(charge.getId());

                Double restaSaldoCorte = 0.0;

                restaSaldoCorte = afiliado.getSaldoCorte() - montoPagar;
                afiliado.setSaldoCorte(restaSaldoCorte);

                // Envío email bienvenida
                boolean isnotEmptyEmail = isNullOrEmpty(afiliado.getEmail());
                if (!isnotEmptyEmail) {

                    List<String> correos = new ArrayList<>();
                    List<File> adjuntos = new ArrayList<>();
                    List<String> templates;

                    try {

                        Map<String, String> model = new LinkedHashMap<>();
                        model.put("nombre", afiliado.getNombre() + " " + afiliado.getApellidoPaterno() +
                                " " + afiliado.getApellidoMaterno());
                        model.put("servicio", afiliado.getServicio().getNombre());
                        model.put("rfc", afiliado.getRfc());

                        correos.add(afiliado.getEmail());
                        templates = emailController.getAllTemplates();

                        // Se compara el id del estatus para saber qué tipo de correo se enviará
                        if (afiliado.getEstatus() == 3) {
                            // Comparamos los id's de los servicios para obtener el template correcto
                            if (afiliado.getServicio().getId() == idIndividual) {
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
                            } else if (afiliado.getServicio().getId() == idTotal) {
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
                        } else if (afiliado.getEstatus() == 1) {
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

                if (afiliado.getEstatus() != 1 && afiliado.getServicio().getId() != idTotal) {
                    afiliado.setEstatus(1);
                    afiliado.setFechaAfiliacion(new Date());
                }

                pagoService.save(pago);
                afiliadoService.save(afiliado);
                afiliadoRepository.insertRelAfiliadosPagos(afiliado, pago.getId());

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

}
