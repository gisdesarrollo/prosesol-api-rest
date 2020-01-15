package com.prosesol.api.rest.controllers.utils;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Pago;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.IPagoService;
import mx.openpay.client.Subscription;
import mx.openpay.client.core.OpenpayAPI;
import mx.openpay.client.exceptions.OpenpayServiceException;
import mx.openpay.client.exceptions.ServiceUnavailableException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */

@RestController
@RequestMapping("/api")
public class ActualizarSuscripcionRestController {

    protected final static Log LOG = LogFactory.getLog(ActualizarSuscripcionRestController.class);

    @Autowired
    private IPagoService pagoService;

    @Autowired
    private IAfiliadoService afiliadoService;

    @Value("${openpay.id}")
    private String merchantId;

    @Value("${openpay.pk}")
    private String privateKey;

    @Value("${openpay.url}")
    private String openpayURL;

    @PostMapping("/actualizar/suscripcion/{rfc}")
    public ResponseEntity<?> actualizarSuscripcion(@PathVariable String rfc){

        List<Pago> pagos = pagoService.getPagoByRfc(rfc);
        Afiliado afiliado = afiliadoService.findByRfc(rfc);
        OpenpayAPI openpayAPI = new OpenpayAPI(openpayURL, privateKey, merchantId);

        try {

            for(Pago pago : pagos){
                boolean isSuscripcion = isNullOrEmpty(pago.getIdSuscripcion());
                Subscription suscripcion;
                if(!isSuscripcion){
                    // Obtener la suscripcion con base a los datos de la tabla de pagos
                    suscripcion = openpayAPI.subscriptions().get(pago.getIdCliente(), pago.getIdSuscripcion());
                    String card = suscripcion.getCardId();

                    //Actualizar la suscripcion con base a la tabla de pagos y a la suscripcion
                    suscripcion.planId(pago.getIdPlan());
                    suscripcion.trialEndDate(afiliado.getFechaCorte());
                    suscripcion.sourceId(card);

                    suscripcion = openpayAPI.subscriptions().update(suscripcion);

                    LOG.info("Suscripcion actualizada: " + suscripcion);
                }
            }

        }catch (ServiceUnavailableException | OpenpayServiceException opEx){
            opEx.printStackTrace();
        }


        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * Método que evalúa si el campo está vacío
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
