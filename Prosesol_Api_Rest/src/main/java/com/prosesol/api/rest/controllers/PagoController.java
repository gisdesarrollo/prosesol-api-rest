package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.controllers.exception.OpenpayException;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Pago;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.IPagoService;
import com.prosesol.api.rest.utils.CalcularFecha;

import mx.openpay.client.Charge;
import mx.openpay.client.Customer;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * @author Luis Enrique Morales Soriano
 * @version 1.0
 */

@Controller
@RequestMapping("/pagos")
public class PagoController {

    protected final Logger LOG = LoggerFactory.getLogger(PagoController.class);

    @Value("${openpay.id}")
    private String merchantId;

    @Value("${openpay.pk}")
    private String privateKey;

    @Value("${openpay.url}")
    private String openpayURL;

    @Value("${openpay.url.dashboard.pdf}")
    private String openpayDashboardUrl;

    @Autowired
    private CalcularFecha calcularFechas;

    @Autowired
    private IAfiliadoService afiliadoService;

    @Autowired
    private IPagoService pagoService;

    private OpenpayAPI apiOpenpay;

    private Customer customer;

    private int code;

    private int errorCode;

    public PagoController() {
        customer = new Customer();
    }


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

        if (rfc.length() < 13) {

            LOG.info("ERR: ", "El RFC no cumple con los campos necesarios");
            redirect.addFlashAttribute("error", "El RFC No cumple con los campos necesarios");
            return "redirect:/pagos/tarjeta/buscar";

        }

        if (afiliado == null) {

            LOG.info("ERR: ", "No existe el afiliado, proporcione otro RFC");
            redirect.addFlashAttribute("error", "No existe el afiliado, proporcione otro RFC");
            return "redirect:/pagos/tarjeta/buscar";

        } else {

            System.out.println(afiliado.toString());

            if (afiliado.getIsBeneficiario() == false) {

                if (afiliado.getSaldoCorte() == null) {
                    LOG.info("El afiliado va al corriente de sus pagos");
                    redirect.addFlashAttribute("info", "Usted no cuenta con un saldo al cual " +
                            "se le puede hacer el cargo, consulte a contacto@prosesol.org para dudas o aclaraciones");
                    return "redirect:/pagos/tarjeta/buscar";
                } else if (afiliado.getSaldoCorte().equals(0.0)) {
                    LOG.info("El afiliado va al corriente de sus pagos");
                    redirect.addFlashAttribute("info", "Usted va al corriente con su pago, " +
                            "no es necesario que realice su pago");
                    return "redirect:/pagos/tarjeta/buscar";
                } else {
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
                if (value.contains("reference")) {
                    reference = value.substring(value.lastIndexOf("=") + 1);
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
        String id = null;

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

            System.out.println(afiliado.toString());

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
                System.out.println(value);
                if (value.contains("id")) {
                    id = value.substring(value.lastIndexOf("=") + 1);
                    System.out.println(id);
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
                + id);
        servletResponse.setStatus(302);

        return null;

    }

    /**
     * Generación de pago con tarjeta
     *
     * @param id
     * @param tokenId
     * @param deviceSessionId
     * @param redirect
     * @param status
     * @return
     */
    @RequestMapping(value = "/tarjeta/{id}", method = RequestMethod.POST)
    public String realizarCargoTarjeta(@PathVariable("id") Long id,
                                       @ModelAttribute("token_id") String tokenId,
                                       @ModelAttribute("deviceIdHiddenFieldName") String deviceSessionId,
                                       RedirectAttributes redirect, SessionStatus status) {

        Afiliado afiliado = afiliadoService.findById(id);
        String periodo = "MENSUAL";
        Integer corte = 0;

        apiOpenpay = new OpenpayAPI(openpayURL, privateKey, merchantId);
        BigDecimal amount = BigDecimal.valueOf(afiliado.getSaldoCorte());

        amount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        try {

            Pago pago = new Pago();

            customer.setName(afiliado.getNombre());
            customer.setLastName(afiliado.getApellidoPaterno() + ' ' + afiliado.getApellidoMaterno());

            boolean isNotValid = isNullOrEmpty(afiliado.getEmail());

            if (isNotValid) {
                customer.setEmail("mail@mail.com");
            } else {
                customer.setEmail(afiliado.getEmail());
            }

            CreateCardChargeParams creditCardcharge = new CreateCardChargeParams()
                    .cardId(tokenId)
                    .amount(amount)
                    .description("Cargo a nombre de: " + afiliado.getNombre())
                    .deviceSessionId(deviceSessionId)
                    .customer(customer);

            @SuppressWarnings("deprecation")
            Charge charge = apiOpenpay.charges().create(creditCardcharge);

            if (charge.getStatus().equals("completed")) {
                pago.setFechaPago(new Date());
                pago.setMonto(charge.getAmount().doubleValue());
                pago.setRfc(afiliado.getRfc());
                pago.setReferenciaBancaria(charge.getAuthorization());
                pago.setEstatus(charge.getStatus());

                DateFormat formatoFecha = new SimpleDateFormat("dd");
                String dia = formatoFecha.format(afiliado.getFechaAlta());
                corte = Integer.parseInt(dia);
                Date fechaCorte = calcularFechas.calcularFechas(periodo, corte);

                afiliado.setEstatus(1);
                afiliado.setFechaCorte(fechaCorte);
                afiliado.setSaldoCorte(0.00);


                pagoService.save(pago);
                afiliadoService.save(afiliado);

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
