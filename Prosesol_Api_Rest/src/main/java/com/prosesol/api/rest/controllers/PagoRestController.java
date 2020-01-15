package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.controllers.exception.PagoException;
import com.prosesol.api.rest.models.entity.Pago;
import com.prosesol.api.rest.services.IPagoService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoRestController {

	protected static Log LOG = LogFactory.getLog(PagoRestController.class);
	
	@Value("${openpay.id}")
	private String openpayId;
	
	@Value("${openpay.pk}")
	private String openpayPk;
	
	@Value("${openpay.url}")
	private String openpayURL;
	
	@Autowired
	private IPagoService pagoService;

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
	public ResponseEntity<?> registrarPago(@Valid @RequestBody Pago pago, BindingResult result){

		LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();

		try {

			if(pago.getRfc().length() < 13){
				throw new PagoException(4000, "El rfc no cumple con la longitud correcta");
			}
			if(result.hasErrors()){
				throw new PagoException(4000, "Datos obligatorios: (rfc, monto, referencia bancaria)");
			}

			pago.setFechaPago(new Date());
			pagoService.save(pago);

			response.put("estatus", "OK");
			response.put("code", HttpStatus.CREATED.value());
			response.put("mensaje", "El pago se ha guardado correctamente");
		}catch(DataAccessException dae) {

			LOG.error(dae);

			response.put("estatus", "ERR");
			response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.put("mensaje", dae.getMessage());
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (PagoException pe){

			LOG.error(pe);

			response.put("estatus", "ERR");
			response.put("code", pe.getCode());
			response.put("mensaje", pe.getMessage());

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<LinkedHashMap<String, Object>>(response, HttpStatus.CREATED);
	}

}
