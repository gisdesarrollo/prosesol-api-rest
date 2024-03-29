package com.prosesol.api.rest.controllers.rest;

import com.prosesol.api.rest.controllers.exception.AfiliadoException;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Servicio;
import com.prosesol.api.rest.models.entity.custom.AfiliadoJsonRequest;
import com.prosesol.api.rest.models.entity.custom.AfiliadoJsonResponse;
import com.prosesol.api.rest.models.entity.schemas.AfiliadoRequest;
import com.prosesol.api.rest.models.entity.schemas.AfiliadoResponse;
import com.prosesol.api.rest.repository.BeneficiarioRepository;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.ICustomerKeyService;
import com.prosesol.api.rest.utils.CalcularFecha;
import com.prosesol.api.rest.utils.GeneraArchivoLog;
import com.prosesol.api.rest.utils.GenerarClave;
import com.prosesol.api.rest.utils.ValidateAfiliadoRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.*;


@RestController
@RequestMapping("/api")
public class AfiliadoRestController {

    protected static final Log LOG = LogFactory.getLog(AfiliadoRestController.class);
    private final static String NOMBRE_ARCHIVO = "CARGA_MASIVA_";

    private static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Value("${masBienestar.servicio.basico}")
    private Long mBBasico;

    @Value("${masBienestar.servicio.ampliado}")
    private Long mBAmpliado;

    @Value("${app.clave}")
    private String clave;

    @Autowired
    private IAfiliadoService afiliadoService;

    @Autowired
    private ICustomerKeyService customerService;

    @Autowired
    private ValidateAfiliadoRequest validateAfiliadoRequest;

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private GenerarClave generarClave;

    @Autowired
    private CalcularFecha calcularFecha;

    @Autowired
    private GeneraArchivoLog generaArchivoLog;

    @GetMapping("/afiliados")
    public ResponseEntity<?> getAllAfiliados() {

        List<Afiliado> afiliados = null;
        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();

        try {
            afiliados = afiliadoService.findAll();

            if (afiliados == null) {
                throw new AfiliadoException(4000, "No hay afiliados en la base de datos");
            } else {
                response.put("afiliados", afiliados);
                response.put("estatus", "OK");
                response.put("code", HttpStatus.OK.value());
                response.put("mensaje", "Consulta realizada correctamente");
            }

        } catch (DataAccessException dae) {
            response.put("estatus",
                    "ERR");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("mensaje",
                    "Error al realizar la consulta en la base de datos " + HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AfiliadoException ae) {
            response.put("estatus", "ERR");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("mensaje", ae.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        }

        return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/afiliados/{rfc}")
    public ResponseEntity<?> getAfiliadoByRfc(@PathVariable String rfc) {

        Afiliado afiliado = afiliadoService.findByRfc(rfc);
        Afiliado mostrarAfiliado = new Afiliado();
        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();

        try {
            if (rfc.length() < 13) {
                throw new AfiliadoException(HttpStatus.BAD_REQUEST.value(), "El RFC no cumple con la longitud correcta");
            }

            if (afiliado == null) {
                response.put("estatus", "ERR");
                response.put("code", HttpStatus.NOT_FOUND.value());
                response.put("mensaje", "El rfc del afiliado no existe en la base de datos");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else if (afiliado
                    .getIsBeneficiario() == false) {

                mostrarAfiliado.setId(afiliado.getId());
                mostrarAfiliado.setNombre(afiliado.getNombre());
                mostrarAfiliado.setApellidoPaterno(afiliado.getApellidoPaterno());
                mostrarAfiliado.setApellidoMaterno(afiliado.getApellidoMaterno());
                mostrarAfiliado.setRfc(afiliado.getRfc());
                mostrarAfiliado.setFechaCorte(afiliado.getFechaCorte());
                mostrarAfiliado.setSaldoAcumulado(afiliado.getSaldoAcumulado());
                mostrarAfiliado.setSaldoCorte(afiliado.getSaldoCorte());

            } else {
                response.put("estatus", "ERR");
                response.put("code", HttpStatus.OK.value());
                response.put("mensaje", "El afiliado no es titular del servicio");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            }
        } catch (DataAccessException dae) {
            response.put("estatus",
                    "ERR");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("mensaje",
                    "Error al realizar la consulta en la base de datos");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("afiliado", mostrarAfiliado);
        response.put("estatus", "OK");
        response.put("code", HttpStatus.OK.value());
        response.put("mensaje", "Consulta realizada correctamente");

        return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/afiliados/crear")
    public ResponseEntity<?> createAfiliado(@RequestBody AfiliadoJsonRequest afiliadoJsonRequestList)
            throws AfiliadoException {

        Afiliado afiliado = new Afiliado();
        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();

        List<String> log = new ArrayList<>();
        Integer numRegistros = 0;

        try {
            AfiliadoJsonResponse afiliadoJsonResponse = new AfiliadoJsonResponse();
            for (AfiliadoRequest afiliadoRequest : afiliadoJsonRequestList.getAfiliado()) {

                numRegistros++;

                AfiliadoResponse afiliadoResponse = new AfiliadoResponse();
                List<AfiliadoResponse> listaBeneficiarios = new ArrayList<>();

                afiliado = validateAfiliadoRequest.validateAfiliadoFromJson(afiliadoRequest);
                if (afiliado == null) {
                    log.add("El afiliado " + afiliadoRequest.getNombre() + " " +
                            afiliadoRequest.getApellidoPaterno() + " " +
                            afiliadoRequest.getApellidoMaterno() +
                            " ya se encuentra registrado" + "\n");
                } else {
                    afiliado.setClave(generarClave.getClaveAfiliado(clave));
                    afiliado.setFechaCorte(null);
                    afiliadoService.save(afiliado);

                    if (afiliadoRequest.getBeneficiario() != null) {

                        Servicio servicio = afiliado.getServicio();
                        for (Afiliado beneficiarios : afiliadoRequest.getBeneficiario()) {

                            AfiliadoResponse beneficiarioResponse = new AfiliadoResponse();
                            validateAfiliadoRequest.validateBeneficiarioFromJson(beneficiarios);

                            beneficiarios.setClave(generarClave.getClaveAfiliado(clave));
                            beneficiarios.setFechaAlta(new Date());
                            beneficiarios.setEstatus(1);
                            beneficiarios.setServicio(servicio);

                            afiliadoService.save(beneficiarios);

                            beneficiarioRepository.insertBeneficiario(beneficiarios, afiliado.getId());

                            beneficiarioResponse.setClave(beneficiarios.getClave());
                            beneficiarioResponse.setNombre(beneficiarios.getNombre());
                            beneficiarioResponse.setApellidoPaterno(beneficiarios.getApellidoPaterno());
                            beneficiarioResponse.setApellidoMaterno(beneficiarios.getApellidoMaterno());
                            beneficiarioResponse.setRfc(beneficiarios.getRfc());

                            listaBeneficiarios.add(beneficiarioResponse);
                            afiliadoResponse.setBeneficiarios(listaBeneficiarios);

                        }
                    }

                    afiliadoResponse.setClave(afiliado.getClave());
                    afiliadoResponse.setNombre(afiliado.getNombre());
                    afiliadoResponse.setApellidoPaterno(afiliado.getApellidoPaterno());
                    afiliadoResponse.setApellidoMaterno(afiliado.getApellidoMaterno());
                    afiliadoResponse.setRfc(afiliado.getRfc());

                    afiliadoJsonResponse.getAfiliado().add(afiliadoResponse);

                    if (afiliado.getServicio().getId().equals(mBBasico) ||
                            afiliado.getServicio().getId().equals(mBAmpliado)) {
                        log.add("Se ha registrado el afiliado: " +
                                afiliado.getNombre() + " " +
                                afiliado.getApellidoPaterno() + " " +
                                afiliado.getApellidoMaterno() +
                                " con RFC: " + afiliado.getRfc() + "\n");
                    }
                }
            }

            generaArchivoLog.generarArchivo(NOMBRE_ARCHIVO + "_MAS_BIENESTAR",
                    numRegistros, log);

            if(afiliadoJsonResponse.getAfiliado().size() > 0){
                response.put("afiliados", afiliadoJsonResponse);
                response.put("estatus", "OK");
                response.put("code", HttpStatus.OK.value());
                response.put("mensaje", "Se han insertado correctamente los registros");
            }else{
                response.put("estatus", "OK");
                response.put("code", HttpStatus.OK.value());
                response.put("mensaje", "Todos los registros se insertaron con anterioridad");
            }

        } catch (AfiliadoException e) {

            if (log.size() > 0) {
                generaArchivoLog.generarArchivo(NOMBRE_ARCHIVO + "_MAS_BIENESTAR",
                        numRegistros, log);
            }

            LOG.error("Error en el proceso de creación", e);
            response.put("estatus", "ERR");
            response.put("code", e.getCode());
            response.put("message", e.getMessage());

            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException npe) {

            if (log.size() > 0) {
                generaArchivoLog.generarArchivo(NOMBRE_ARCHIVO + "_MAS_BIENESTAR",
                        numRegistros, log);
            }

            LOG.error("Error en el proceso de creación", npe);
            response.put("estatus", "ERR");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Hubo un error inesperado en el servicio");

            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
