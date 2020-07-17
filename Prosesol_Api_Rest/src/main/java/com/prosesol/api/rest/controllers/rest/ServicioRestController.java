package com.prosesol.api.rest.controllers.rest;

import com.prosesol.api.rest.controllers.exception.AfiliadoException;
import com.prosesol.api.rest.models.entity.Servicio;
import com.prosesol.api.rest.services.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ServicioRestController {

    @Autowired
    private IServicioService servicioService;

    @GetMapping("/servicios")
    public ResponseEntity<?> getAllServicios(){

        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
        List<Servicio> servicios = new ArrayList<Servicio>();

        try{
            List<Servicio> listaServicios = servicioService.findAll();

            for(Servicio s : listaServicios){
                Servicio servicio = new Servicio();

                servicio.setId(s.getId());
                servicio.setNombre(s.getNombre());
                servicios.add(servicio);
            }

            if(servicios.equals(null)){
                throw new AfiliadoException(4000, "No existen servicios en la base de datos");
            }else{
                response.put("servicios", servicios);
                response.put("estatus", "OK");
                response.put("code", HttpStatus.OK.value());
                response.put("mensaje", "Consulta realizada correctamente");
            }
        }catch(DataAccessException dae){
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

}
