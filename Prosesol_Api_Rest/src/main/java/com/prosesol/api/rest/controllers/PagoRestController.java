package com.prosesol.api.rest.controllers;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prosesol.api.rest.models.entity.Pago;
import com.prosesol.api.rest.services.IPagoService;

import mx.openpay.client.exceptions.OpenpayServiceException;
import mx.openpay.client.exceptions.ServiceUnavailableException;

@RestController
@RequestMapping("/api/pagos")
public class PagoRestController {
	
	@Value("${openpay.id}")
	private String openpayId;
	
	@Value("${openpay.pk}")
	private String openpayPk;
	
	@Value("${openpay.url}")
	private String openpayURL;
	
	@Autowired
	private IPagoService pagoService;
	
	@GetMapping("/consultar_pagos_all")
	public ResponseEntity<?> consultarPagos() {
		
		List<Pago> pagos = null;
		LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
		
		try {
			pagos = pagoService.findAll();
		}catch(DataAccessException dae) {
			response.put("estatus", "ERR: " + dae.getMessage().concat(": ").concat(dae.getMostSpecificCause().getMessage()));
			response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.put("mensaje", "Error al realizar la consulta en la base de datos " + HttpStatus.INTERNAL_SERVER_ERROR);			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(pagos == null) {
			response.put("estatus", "ERR");
			response.put("code", HttpStatus.OK.value());
			response.put("mensaje", "No existen pagos en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}
		
		response.put("pagos", pagos);
		response.put("estatus", "OK");
		response.put("code", HttpStatus.OK.value());
		response.put("mensaje", "Consulta realizada correctamente");
		
		return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.OK);
	}
	
	@GetMapping("/consultar_pagos/{rfc}")
	public ResponseEntity<?> consultarPago(@PathVariable String rfc) {
		
		List<Pago> pagos = null;
		LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
		
		if(rfc.length() < 13) {
			
			response.put("estatus", "ERR");
			response.put("code", HttpStatus.OK.value());
			response.put("mensaje", "El rfc no cumple con la longitud correcta");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			
		}
	
		try {
			pagos = pagoService.getPagoByRfc(rfc);			
		}catch(DataAccessException dae) {
			response.put("estatus", "ERR: " + dae.getMessage().concat(": ").concat(dae.getMostSpecificCause().getMessage()));
			response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.put("mensaje", "Error al realizar la consulta en la base de datos " + HttpStatus.INTERNAL_SERVER_ERROR);			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(pagos == null) {
			response.put("estatus", "ERR");
			response.put("code", HttpStatus.OK.value());
			response.put("mensaje", "El pago: ".concat(rfc).concat(" no existe en la base de datos"));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}
		
		response.put("pagos", pagos);
		response.put("estatus", "OK");
		response.put("code", HttpStatus.OK.value());
		response.put("mensaje", "Consulta realizada correctamente");
		
		return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.OK);
	}
	
	@PostMapping("/registrar_pago")
	public ResponseEntity<?> registrarPago(@Valid @RequestBody Pago pago) throws OpenpayServiceException, ServiceUnavailableException {
		
		Pago pagoRegistrado = null;
		LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
						
		if(pago.getRfc().length() < 13){
			
			response.put("estatus", "ERR");
			response.put("code", HttpStatus.OK.value());
			response.put("mensaje", "El rfc no cumple con la longitud correcta");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}
		
		try {			
			pago.setFechaPago(new Date());		
			pagoRegistrado = pagoService.save(pago);
		}catch(DataAccessException dae) {
			response.put("estatus", "ERR: " + dae.getMessage().concat(": ").concat(dae.getMostSpecificCause().getMessage()));
			response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.put("mensaje", "Error al realizar la inserción en la base de datos" + HttpStatus.INTERNAL_SERVER_ERROR);
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("pago", pagoRegistrado);
		response.put("estatus", "OK");
		response.put("code", HttpStatus.CREATED.value());
		response.put("mensaje", "Pago realizado exitosamente");		
		
		return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/borrar_pago/{id}")
	public ResponseEntity<?> borrarPago(@PathVariable Long id) {

		LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
		
		try {
			pagoService.deleteById(id);
		}catch(DataAccessException dae) {
			
			response.put("estatus", "ERR: " + dae.getMessage().concat(": ").concat(dae.getMostSpecificCause().getMessage()));
			response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.put("mensaje", "No se pudo el registro" + HttpStatus.INTERNAL_SERVER_ERROR);
			
			return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
		response.put("estatus", "OK");
		response.put("code", HttpStatus.OK.value());
		response.put("mensaje", "El registro del pago se ha borrado correctamente");
		
		return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.OK);
	}
}
