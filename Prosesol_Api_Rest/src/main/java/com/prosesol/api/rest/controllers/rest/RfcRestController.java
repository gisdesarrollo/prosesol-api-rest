package com.prosesol.api.rest.controllers.rest;

import com.josketres.rfcfacil.Rfc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@RestController
@RequestMapping("/afiliados")
public class RfcRestController {

    protected static final Log LOG = LogFactory.getLog(RfcRestController.class);

    @RequestMapping(value = "/generarRfc", method = RequestMethod.GET)
    @ResponseBody
    public String generarRfc(@RequestParam(value = "nombre")String nombre,
                             @RequestParam(value = "apellidoPaterno")String apellidoPaterno,
                             @RequestParam(value = "apellidoMaterno")String apellidoMaterno,
                             @RequestParam(value = "fechaNacimiento")String fechaNacimiento){

        Rfc rfc = null;

        try{

            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(fechaNacimiento);
            LocalDate fecha = date.toInstant()
                              .atZone(ZoneId.systemDefault())
                              .toLocalDate();

            rfc = new Rfc.Builder()
                         .name(nombre)
                         .firstLastName(apellidoPaterno)
                         .secondLastName(apellidoMaterno)
                         .birthday(fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear())
                         .build();

        }catch (Exception e){
            LOG.error("Error al momento de generar el rfc", e);
        }

        return rfc.toString();
    }

}
