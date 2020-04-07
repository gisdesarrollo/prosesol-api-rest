package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Candidato;
import com.prosesol.api.rest.models.entity.Pago;
import com.prosesol.api.rest.models.entity.Webhook;
import com.prosesol.api.rest.repository.AfiliadoRepository;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.ICandidatoService;
import com.prosesol.api.rest.services.IPagoService;
import com.prosesol.api.rest.services.IWebhookService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

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
    
    @Autowired
    private ICandidatoService candidatoService;
    
    @Autowired
	private AfiliadoRepository afiliadoRepository;

    @Value("${afiliado.servicio.total.id}")
    private Long idTotal;

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

                // Obtener el rfc candidato de la tabla de Pagos
                String rfcC = pagoService.getRfcCandidatoByIdTransaccion(idTransaccion);
                Pago pago= pagoService.getPagosByIdTransaccion(idTransaccion);
                Candidato candidato = candidatoService.findByRfc(rfcC);
                
                if(candidato!=null) {
                	 // Verificar si es pago de inscripción
                	if(candidato.getIsInscripcion()) {
                		 Double restaCostoInscripcion = new Double(0.0);
                		// Se resta el saldo acumulado obteniendo la inscripción de su servicio
                         Double saldoAcumulado = candidato.getSaldoAcumulado() -
                                 candidato.getServicio().getInscripcionTitular();
                         candidato.setSaldoAcumulado(saldoAcumulado);
                         candidato.setIsInscripcion(false);
                	}
                	 // Actualizar saldo al corte a ceros
                    candidato.setSaldoCorte(0.0);
                    if (candidato.getEstatus() != 1 && candidato.getServicio().getId() != idTotal) {
                        candidato.setEstatus(1);
                        candidato.setFechaAfiliacion(new Date());
                    }
                    Afiliado datosA = datosAfiliado(candidato);
                    afiliadoService.save(datosA);
                    candidatoService.deleteRelCandidatoPagosById(candidato.getId());
                    candidatoService.deleteById(candidato.getId());
                    afiliadoRepository.insertRelAfiliadosPagos(datosA,pago.getId());
                }else {
                
                // Obtener el rfc afiliado de la tabla de Pagos
                String rfcA = pagoService.getRfcByIdTransaccion(idTransaccion);
                Afiliado afiliado = afiliadoService.findByRfc(rfcA);
                if(afiliado!=null) {	                
                // Verificar si es pago de inscripción y también si tiene beneficiarios
                if(afiliado.getIsInscripcion()){
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

                // Actualizar saldo al corte a ceros
                afiliado.setSaldoCorte(0.0);
                if (afiliado.getEstatus() != 1 && afiliado.getServicio().getId() != idTotal) {
                    afiliado.setEstatus(1);
                    afiliado.setFechaAfiliacion(new Date());
                }

                afiliadoService.save(afiliado);
             }
            }
               
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
    
    /**
     * Método para pasar datos de candidato a afiliado
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
