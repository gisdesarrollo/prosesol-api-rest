package com.prosesol.api.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Controller
@RequestMapping("/obtener")
public class ObtenerAvisoTermino {

    @Value("${archivo.aviso}")
    private String avisoPrivacidad;

    @Value("${archivo.terminos}")
    private String terminosCondiciones;

    @GetMapping(value = "/aviso")
    public void getAvisoPrivacidad(HttpServletResponse response){
        try{
            File file = ResourceUtils.getFile(avisoPrivacidad);

            if(file.exists()){
                response.setContentType("application/pdf");
                response.setHeader("Content-disposition", "attachment;filename" + file.getName());
                response.flushBuffer();
            }else{
                System.out.println("Archivo no encontrado");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
