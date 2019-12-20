package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Pago;
import com.prosesol.api.rest.models.entity.Webhook;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.IHttpUrlConnection;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;

/**
 * @author Luis Enrique Morales Soriano
 */

@RestController
@RequestMapping("/api/webhook")
public class NotificacionRestController implements IHttpUrlConnection {

    protected static final Log LOG = LogFactory.getLog(NotificacionRestController.class);

    @Autowired
    private IWebhookService webhookService;

    @Autowired
    private IPagoService pagoService;

    @Autowired
    private IAfiliadoService afiliadoService;

    @PostMapping(value = "/transfer")
    public ResponseEntity<?> createWebhook(HttpServletRequest request){

        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
        Webhook wh = null;
        String jb = null;

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
                Pago getPago = pagoService.getRfcByIdTransaccion(idTransaccion);

                String rfc = getPago.getRfc();
                Afiliado afiliado = afiliadoService.findByRfc(rfc);

                // Actualizar saldo al corte a ceros
                Double amount = json.getJSONObject("transaction").getDouble("amount");
                afiliado.setSaldoCorte(amount);

                afiliadoService.save(afiliado);

            }

            response.put("status", "OK");
            response.put("code", HttpStatus.OK);

        }catch(JSONException | IOException je){
            je.printStackTrace();
        }

        return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.OK);
    }

    @Override
    public HttpURLConnection openConnection(HttpURLConnection urlConnection, String method, URL url) {
        try {
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8;");
            urlConnection.setRequestProperty("Authorization", "Bearer " + "dopplerApiKey");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urlConnection;
    }
}
