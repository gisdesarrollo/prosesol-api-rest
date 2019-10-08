package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.controllers.exception.AfiliadoException;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Servicio;
import com.prosesol.api.rest.models.entity.custom.AfiliadoJsonRequest;
import com.prosesol.api.rest.models.entity.custom.AfiliadoRequest;
import com.prosesol.api.rest.repository.BeneficiarioRepository;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.ICustomerKeyService;
import com.prosesol.api.rest.utils.CalcularFecha;
import com.prosesol.api.rest.utils.GenerarClave;
import com.prosesol.api.rest.utils.ValidateAfiliadoRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/api")
public class AfiliadoRestController {

	protected static final Log LOG = LogFactory.getLog(AfiliadoRestController.class);

	private static DecimalFormat decimalFormat = new DecimalFormat("#.##");

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

	@GetMapping("/afiliados")
	public ResponseEntity<?> getAllAfiliados() {

		List<Afiliado> afiliados = null;
		LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();

		try {
			afiliados = afiliadoService.findAll();

			if (afiliados == null) {
				throw new AfiliadoException(4000, "No hay afiliados en la base de datos");
			}else{
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
		}catch (AfiliadoException ae){
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
		}catch (DataAccessException dae) {
			response.put("estatus",
					"ERR");
			response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.put("mensaje",
					"Error al realizar la consulta en la base de datos" );
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("afiliado", mostrarAfiliado);
		response.put("estatus", "OK");
		response.put("code", HttpStatus.OK.value());
		response.put("mensaje", "Consulta realizada correctamente");

		return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.OK);
	}

	@PostMapping("/afiliados/crear")
	public ResponseEntity<?> createAfiliado(@RequestBody AfiliadoJsonRequest afiliadoJsonRequestList){

		Afiliado afiliado = new Afiliado();
		List<AfiliadoRequest> afiliadoJsonResponse = new ArrayList<AfiliadoRequest>();
		LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();

		try{

			int afiliadoCounter = 0;
			int beneficiarioCounter = 0;

			AfiliadoRequest afiliadoResponse = new AfiliadoRequest();
			Afiliado beneficiario = new Afiliado();
			List<Afiliado> beneficiariosResponse = new ArrayList<Afiliado>();
			for(AfiliadoRequest afiliadoRequest : afiliadoJsonRequestList.getAfiliado()){

				LocalDate fechaNacimiento = afiliadoRequest.getFechaNacimiento().toInstant()
						.atZone(ZoneId.of("America/Mexico_City"))
						.toLocalDate();

				Date fechaCorte = calcularFecha.calcularFechas("MENSUAL", fechaNacimiento.getDayOfMonth());

				afiliado = validateAfiliadoRequest.validateAfiliadoFromJson(afiliadoRequest);
				afiliado.setClave(generarClave.getClaveAfiliado(clave));
				afiliado.setFechaCorte(fechaCorte);
				afiliadoService.save(afiliado);

				if(afiliadoRequest.getBeneficiario() != null){

					Afiliado titular = afiliadoService.findById(afiliado.getId());
					Servicio servicio = afiliado.getServicio();
					for(Afiliado beneficiarios : afiliadoRequest.getBeneficiario()){

						validateAfiliadoRequest.validateBeneficiarioFromJson(beneficiarios);

						beneficiarios.setClave(generarClave.getClaveAfiliado(clave));
						beneficiarios.setFechaAlta(new Date());
						beneficiarios.setEstatus(1);
						beneficiarios.setServicio(servicio);

						afiliadoService.save(beneficiarios);

						beneficiarioRepository.insertBeneficiario(beneficiarios, afiliado.getId());

						beneficiario.setClave(beneficiarios.getClave());
						beneficiario.setNombre(beneficiarios.getNombre());
						beneficiario.setApellidoPaterno(beneficiarios.getApellidoPaterno());
						beneficiario.setApellidoMaterno(beneficiarios.getApellidoMaterno());

						beneficiariosResponse.add(beneficiario);

						beneficiarioCounter++;
					}
				}

				afiliadoResponse.setClave(afiliado.getClave());
				afiliadoResponse.setNombre(afiliado.getNombre());
				afiliadoResponse.setApellidoPaterno(afiliado.getApellidoPaterno());
				afiliadoResponse.setApellidoMaterno(afiliado.getApellidoMaterno());
				afiliadoResponse.setBeneficiario(beneficiariosResponse);

				afiliadoJsonResponse.add(afiliadoResponse);

			}

			System.out.println("Total de Afiliados: " + afiliadoCounter);
			System.out.println("Total de Beneficiarios: " + beneficiarioCounter);

		}catch (AfiliadoException e){
			LOG.error("Error en el proceso de creación", e);
			response.put("estatus", "ERR");
			response.put("code", e.getCode());
			response.put("message", e.getMessage());

			if(afiliado.getId() != null && afiliado.getId() > 0){
				afiliadoService.deleteById(afiliado.getId());
			}

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(NullPointerException npe){
			LOG.error("Error en el proceso de creación", npe);
			response.put("estatus", "ERR");
			response.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
			response.put("message", "Hubo un error inseperado en el servicio");

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(DataIntegrityViolationException dae){
			response.put("estatus",
					"ERR");
			response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.put("mensaje",
					"El afiliado ya se encuentra registrado en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("afiliado", afiliadoJsonResponse);
		response.put("estatus", "OK");
		response.put("code", HttpStatus.OK.value());
		response.put("mensaje", "Se han insertado correctamente los registros");

		return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.OK);
	}

}
