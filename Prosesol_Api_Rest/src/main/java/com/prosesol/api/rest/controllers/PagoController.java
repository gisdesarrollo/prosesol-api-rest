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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Luis Enrique Morales Soriano
 * @version 1.0
 */

@Controller
@RequestMapping("/pagos")
public class PagoController {

    protected final Logger LOG = LoggerFactory.getLogger(PagoController.class);
    private final static int ID_TEMPLATE_FT = 1606126;
    private final static int ID_TEMPLATE_IB = 1606124;
    private final static int ID_TEMPLATE_PI = 1606129;
    private final static int ID_TEMPLATE_PM= 1606131;
    
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

        Candidato candidato = candidatoService.findByRfc(rfc);
        Afiliado afiliado = afiliadoService.findByRfc(rfc);
        Cliente cliente = clienteService.getClienteByIdAfiliado(afiliado);
        SimpleDateFormat formatFecha = new SimpleDateFormat("dd-MM-yyyy");
        String fechaFormateada = null;
        if (rfc.length() < 13) {

            LOG.info("ERR: ", "El RFC no cumple con los campos necesarios");
            redirect.addFlashAttribute("error", "El RFC No cumple con los campos necesarios");
            return "redirect:/pagos/tarjeta/buscar";

        } else if (cliente != null) {
            LOG.info("INFO: ", "No puede realizar su pago ya que su servicio está domiciliado");
            redirect.addFlashAttribute("warning",
                    "No puede realizar su pago ya que su servicio está domiciliado");
            return "redirect:/pagos/tarjeta/buscar";
        }

        if (candidato != null) {
            if (candidato.getIsBeneficiario() == false) {

                if (candidato.getSaldoCorte() == null) {
                    LOG.info("El afiliado no cuenta con un saldo");
                    redirect.addFlashAttribute("info", "Usted no cuenta con un saldo al cual " +
                            "se le puede hacer el cargo, póngase en contacto a contacto@prosesol.org para dudas o aclaraciones");
                    return "redirect:/pagos/tarjeta/buscar";
                } else {
                    model.addAttribute("afiliado", candidato);
                     fechaFormateada = formatFecha.format(candidato.getFechaCorte());
                    model.addAttribute("fechaCorte", fechaFormateada);
                }
            } else {
                LOG.info("ERR: ", "El afiliado no es titular del servicio");
                redirect.addFlashAttribute("error", "El afiliado no es titular del servicio");
                return "redirect:tarjeta/buscar";
            }


        } else {


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
                    } else {
                    	fechaFormateada = formatFecha.format(afiliado.getFechaCorte());
                        model.addAttribute("afiliado", afiliado);
                    
                        model.addAttribute("fechaCorte", fechaFormateada);
                    }
                } else {
                    LOG.info("ERR: ", "El afiliado no es titular del servicio");
                    redirect.addFlashAttribute("error", "El afiliado no es titular del servicio");
                    return "redirect:tarjeta/buscar";
                }
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
        Candidato candidato = candidatoService.findByRfc(rfc);
        String reference = null;
        String idTransaccion = null;

        Pago pago = new Pago();
        Customer customer = new Customer();

        if (rfc.length() < 13) {

            LOG.info("ERR: ", "El RFC no cumple con los campos necesarios");
            redirect.addFlashAttribute("error", "El RFC No cumple con los campos necesarios");
            return "redirect:/pagos/tienda/buscar";

        }
        try {
            if (candidato != null) {
                if (candidato.getIsBeneficiario() == false) {
                    if (candidato.getSaldoCorte().equals(0.0)) {
                        LOG.info("El afiliado va al corriente de sus pagos");
                        redirect.addFlashAttribute("info", "Usted va al corriente con su pago, " +
                                "no es necesario que realice su pago");
                        return "redirect:/pagos/tienda/buscar";
                    }
                }

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

            } else {

                if (afiliado == null) {

                    LOG.info("ERR: ", "No existe el afiliado, proporcione otro RFC");
                    redirect.addFlashAttribute("error", "No existe el afiliado, proporcione otro RFC");
                    return "redirect:/pagos/tienda/buscar";

                } else {

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
                }
            }
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
        Candidato candidato = candidatoService.findByRfc(rfc);

        String idTransaccion = null;

        Pago pago = new Pago();
        Customer customer = new Customer();

        if (rfc.length() < 13) {

            LOG.info("ERR: ", "El RFC no cumple con los campos necesarios");
            redirect.addFlashAttribute("error", "El RFC No cumple con los campos necesarios");
            return "redirect:/pagos/banco/buscar";

        }
        try {
            if (candidato != null) {
                if (candidato.getIsBeneficiario() == false) {
                    if (candidato.getSaldoCorte().equals(0.0)) {
                        LOG.info("El afiliado va al corriente de sus pagos");
                        redirect.addFlashAttribute("info", "Usted va al corriente con su pago, " +
                                "no es necesario que realice su pago");
                        return "redirect:/pagos/banco/buscar";
                    }
                }


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

            } else {

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

                }
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
                                       @RequestParam(value = "montoPagar", required = false) Double montoPagar,
                                       RedirectAttributes redirect, SessionStatus status, Model vista) {

        Afiliado afiliado = afiliadoService.findById(id);
        Candidato candidato = candidatoService.findById(id);
        if (candidato != null) {
            afiliado = datosAfiliado(candidato);
        }

        Customer customer = new Customer();
        Suscripcion sus = new Suscripcion();
        apiOpenpay = new OpenpayAPI(openpayURL, privateKey, merchantId);

        BigDecimal amount = BigDecimal.valueOf(montoPagar);
        amount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        try {
            Cliente cliente = new Cliente();
            Pago pago = new Pago();

            customer.setName(afiliado.getNombre());
            customer.setLastName(afiliado.getApellidoPaterno() + ' ' + afiliado.getApellidoMaterno());
            customer.requiresAccount(false);

            boolean isNotValid = isNullOrEmpty(afiliado.getEmail());

            if (!montoPagar.equals(afiliado.getSaldoCorte()) && afiliado.getIsInscripcion()) {
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

            if (suscripcion) {
                Plan plan = planService.getPlanByIdServicio(afiliado.getServicio());
                if (plan != null) {
                    Card card = new Card().tokenId(tokenId);

                    card = apiOpenpay.cards().create(customer.getId(), card);
                    if (card != null) {
                        // Se crea la suscripción del afiliado al plan
                        Subscription subscription = new Subscription();
                        subscription.planId(plan.getPlanOpenpay());
                        subscription.trialEndDate(afiliado.getFechaCorte());
                        subscription.sourceId(card.getId());
                        subscription = apiOpenpay.subscriptions().create(customer.getId(),
                                subscription);

                        LOG.info("Subscription: " + subscription);

                        if (!subscription.getStatus().equals("trial")) {
                            redirect.addFlashAttribute("error", "Error al momento de suscribir");
                            return "redirect:/pagos/tarjeta/buscar";
                        } else { // Se crea la suscripcion y el cliente en la base de datos
                            sus = new Suscripcion(plan, subscription.getId(), true);
                            suscripcionService.save(sus);
                            if (candidato == null) {
                                cliente = new Cliente(sus, customer.getId(), true, afiliado);
                                clienteService.save(cliente);
                            }
                        }
                    }
                } else {
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
                    if (candidato == null) {
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

                        }
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
                    List<Integer> templates;

                    try {

                        Map<String, String> model = new LinkedHashMap<>();
                        model.put("nombre", afiliado.getNombre() + " " + afiliado.getApellidoPaterno() +
                                " " + afiliado.getApellidoMaterno());
                        model.put("servicio", afiliado.getServicio().getNombre());
                        model.put("rfc", afiliado.getRfc());

                        correos.add(afiliado.getEmail());
                        templates=emailController.getTemplateMailjet();

                        // Se compara el id del estatus para saber qué tipo de correo se enviará
                        if (afiliado.getEstatus() == 3) {
                            // Comparamos los id's de los servicios para obtener el template correcto
                            if (afiliado.getServicio().getId() == idIndividual) {
                                for (Integer idTemplate : templates) {
                                    if (idTemplate.equals(ID_TEMPLATE_IB)) {
                                        adjuntos.add(ResourceUtils.getFile(archivoPlanIndividual));
                                        LOG.info("Template de bienvenido Individual Básico: " + idTemplate);
                                        emailController.sendMailJet(model,idTemplate,adjuntos,correos);
                                        
                                    }
                                    if (idTemplate.equals(ID_TEMPLATE_PI)) {
                                        LOG.info("Template de inscripción: " + idTemplate);
                                        emailController.sendMailJet(model,idTemplate,adjuntos,correos);
                                        
                                    }
                                }
                            } else if (afiliado.getServicio().getId() == idTotal) {
                                for (Integer idTemplate : templates) {
                                    if (idTemplate.equals(ID_TEMPLATE_FT)) {
                                        adjuntos.add(ResourceUtils.getFile(archivoPlanFamiliar));
                                        LOG.info("Template de bienvenido Familiar Total: " + idTemplate);
                                        emailController.sendMailJet(model,idTemplate,adjuntos,correos);
                                        
                                    }
                                    if (idTemplate.equals(ID_TEMPLATE_PI)) {
                                        LOG.info("Template de inscripción: " + idTemplate);
                                        emailController.sendMailJet(model,idTemplate,adjuntos,correos);
                                        
                                    }
                                }
                            }
                        } else if (afiliado.getEstatus() == 1) {
                            for (Integer idTemplate : templates) {
                                if (idTemplate.equals(ID_TEMPLATE_PM)) {
                                    LOG.info("Template pago mensualidad: " + idTemplate);
                                    emailController.sendMailJet(model,idTemplate,adjuntos,correos);
                                    
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
                if (suscripcion && candidato != null) {
                    cliente = new Cliente(sus, customer.getId(), true, afiliado);
                    clienteService.save(cliente);
                }
                if (candidato != null) {
                    candidatoService.deleteById(candidato.getId());
                }

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

    /**
     * Método para pasar datos del candidato a afiliado
     *
     * @param candidato
     * @return
     */
    public Afiliado datosAfiliado(Candidato candidato) {

        Afiliado afiliado = new Afiliado();
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
