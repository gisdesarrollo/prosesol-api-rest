package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.models.entity.Webhook;
import com.prosesol.api.rest.services.IWebhookService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.HTTP;
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
@RequestMapping("/api/webhook")
public class NotificacionRestController {

    protected static final Log LOG = LogFactory.getLog(NotificacionRestController.class);

    @Autowired
    private IWebhookService webhookService;

    @PostMapping(value = "/transfer")
    public ResponseEntity<?> createWebhook(HttpServletRequest request){

        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
        Webhook wh = null;

        String jb = null;

        try{
            jb = IOUtils.toString(request.getReader());
            LOG.info(jb);

            wh = new Webhook(jb);

            webhookService.save(wh);

            response.put("status", "OK");
            response.put("code", HttpStatus.OK);

        }catch(JSONException | IOException je){
            je.printStackTrace();
        }

        return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.OK);
    }

}
