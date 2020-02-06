package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Pago;
import com.prosesol.api.rest.models.entity.Webhook;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.IPagoService;
import com.prosesol.api.rest.services.IWebhookService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * @author Luis Enrique Morales Soriano
 */

@RestController
@RequestMapping("/webhook")
public class NotificacionRestController {

    protected static final Log LOG = LogFactory.getLog(NotificacionRestController.class);

    @Autowired
    private IWebhookService webhookService;

    @Autowired
    private IPagoService pagoService;

    @Autowired
    private IAfiliadoService afiliadoService;

    @PostMapping(value = "/notificaciones")
    public ResponseEntity<?> createWebhook(HttpServletRequest request){

        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
        Webhook wh = null;
        String jb;

        try{
            jb = IOUtils.toString(request.getReader());
            LOG.info(jb);

            JSONObject json = new JSONObject(jb);
            String status = json.getJSONObject("transaction").getString("status");

            LOG.info(status);

            if(status.equals("completed")){

                String idTransaccion = json.getJSONObject("transaction").getString("id");

                String method = json.getJSONObject("transaction").getString("method");
                if(method.equals("store")){
                    String reference = json.getJSONObject("transaction").getJSONObject("payment_method")
                            .getString("reference");
                    pagoService.actualizarEstatusPagoByIdTransaccion(reference, status, idTransaccion);
                }else if(method.equals("bank_account")){
                    String reference = json.getJSONObject("transaction").getJSONObject("payment_method")
                            .getString("bank");
                    pagoService.actualizarEstatusPagoByIdTransaccion(reference, status, idTransaccion);
                }

                // Obtener el rfc de la tabla de Pagos
                String rfc = pagoService.getRfcByIdTransaccion(idTransaccion);
                Afiliado afiliado = afiliadoService.findByRfc(rfc);

                // Actualizar saldo al corte a ceros
                afiliado.setSaldoCorte(0.0);

                afiliadoService.save(afiliado);

            }

            response.put("status", "OK");
            response.put("code", HttpStatus.OK);

        }catch(JSONException | IOException je){
            LOG.error(je);
            response.put("status", "ERR");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(NullPointerException ne){
            LOG.error(ne);
            response.put("status", "ERR");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
