package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.controllers.exception.AfiliadoException;
import com.prosesol.api.rest.services.IAfiliadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EstadosRestController {

    @Autowired
    private IAfiliadoService afiliadoService;

    @GetMapping("/estados")
    public ResponseEntity<?> getAllEstados(){

        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();

        try{

            List<String> estados = afiliadoService.getAllEstados();

            if(estados.equals(null)){
                throw new AfiliadoException(4000, "No hay estados qué cargar");
            }else{
                response.put("estados", estados);
                response.put("estatus", "OK");
                response.put("code", HttpStatus.OK);
                response.put("mensajes", "Consulta realizada correctamente");
            }


        }catch(AfiliadoException ae){
            response.put("estatus", "ERR");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("mensaje", ae.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
