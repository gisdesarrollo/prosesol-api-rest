package com.prosesol.api.rest.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.services.IAfiliadoService;

@RestController
@RequestMapping("/api/pagos")
public class AfiliadoRestController {

	@Autowired
	private IAfiliadoService afiliadoService;

	@GetMapping("/afiliado")
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
			response.put("code", HttpStatus.NOT_FOUND.value());
			response.put("mensaje", "No existen afiliados en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		response.put("afiliados", afiliados);
		response.put("estatus", "OK");
		response.put("code", HttpStatus.OK.value());
		response.put("mensaje", "Consulta realizada correctamente");

		return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping("/afiliado/{rfc}")
	public ResponseEntity<?> getAfiliadoByRfc(@PathVariable String rfc) {

		Afiliado afiliado = afiliadoService.findByRfc(rfc);
		Afiliado mostrarAfiliado = new Afiliado();
		LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();

		if(rfc.length() < 13) {
			
			response.put("estatus", "ERR");
			response.put("code", HttpStatus.LENGTH_REQUIRED.value());
			response.put("mensaje", "El rfc no cumple con la longitud correcta");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			
		}
		
		if (afiliado == null) {
			response.put("estatus", "ERR");
			response.put("code", HttpStatus.NOT_FOUND.value());
			response.put("mensaje", "El rfc del afiliado no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		} else if (afiliado.isBeneficiario().equals("0")) {

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
			response.put("code", HttpStatus.NOT_FOUND.value());
			response.put("mensaje", "El afiliado no es titular del servicio");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			
			mostrarAfiliado.setId(afiliado.getId());
			mostrarAfiliado.setNombre(afiliado.getNombre());
			mostrarAfiliado.setApellidoPaterno(afiliado.getApellidoPaterno());
			mostrarAfiliado.setApellidoMaterno(afiliado.getApellidoMaterno());
			mostrarAfiliado.setRfc(afiliado.getRfc());
			mostrarAfiliado.setFechaCorte(afiliado.getFechaCorte());
			mostrarAfiliado.setSaldoAcumulado(afiliado.getSaldoAcumulado());
			mostrarAfiliado.setSaldoCorte(afiliado.getSaldoCorte());
			

		} catch (DataAccessException dae) {
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

}
