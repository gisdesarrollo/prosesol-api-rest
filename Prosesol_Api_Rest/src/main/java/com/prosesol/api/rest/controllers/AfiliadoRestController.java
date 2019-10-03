package com.prosesol.api.rest.controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.prosesol.api.rest.models.entity.custom.AfiliadoJsonRequest;
import com.prosesol.api.rest.models.entity.custom.AfiliadoRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.prosesol.api.rest.controllers.exception.AfiliadoException;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.CustomerKey;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.ICustomerKeyService;

import javax.validation.Valid;

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

	@GetMapping("/afiliados")
	public ResponseEntity<?> getAllAfiliadoas() {

		List<Afiliado> afiliados = null;
		LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();

		try {
			afiliados = afiliadoService.findAll();
		} catch (DataAccessException dae) {
			response.put("estatus",
					"ERR: " + dae.getMessage().concat(": ").concat(dae.getMostSpecificCause().getMessage()));
			response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.put("mensaje",
					"Error al realizar la consulta en la base de datos " + HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (afiliados == null) {
			response.put("estatus", "ERR");
			response.put("code", HttpStatus.OK.value());
			response.put("mensaje", "No existen afiliados en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}

		response.put("afiliados", afiliados);
		response.put("estatus", "OK");
		response.put("code", HttpStatus.OK.value());
		response.put("mensaje", "Consulta realizada correctamente");

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
					"ERR: " + dae.getMessage().concat(": ").concat(dae.getMostSpecificCause().getMessage()));
			response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.put("mensaje",
					"Error al realizar la consulta en la base de datos " + HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("afiliado", mostrarAfiliado);
		response.put("estatus", "OK");
		response.put("code", HttpStatus.OK.value());
		response.put("mensaje", "Consulta realizada correctamente");

		return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.OK);
	}

	@PostMapping("/afiliados/crear")
	public ResponseEntity<?> createAfiliado(@RequestBody List<AfiliadoJsonRequest> afiliadoJsonRequestList){

		Afiliado afiliado = new Afiliado();
		List<Afiliado> beneficiario = new ArrayList<Afiliado>();
		LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();

		try{

			for(AfiliadoJsonRequest afiliadoJsonRequest : afiliadoJsonRequestList){
				System.out.println(afiliadoJsonRequest.toString());
			}

		}catch (Exception e){

		}


		return null;
	}

}
