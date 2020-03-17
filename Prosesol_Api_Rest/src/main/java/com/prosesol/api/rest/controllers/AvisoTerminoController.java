package com.prosesol.api.rest.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;

@Controller
@RequestMapping("/obtener")
public class AvisoTerminoController {

    protected static final Log logger = LogFactory.getLog(AvisoTerminoController.class);

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
                response.setHeader("Content-disposition", "attachment;filename=" + file.getName());
                BufferedInputStream bufferedInputStream =
                        new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream bufferedOutputStream =
                        new BufferedOutputStream(response.getOutputStream());

                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while((bytesRead = bufferedInputStream.read(buffer))!= -1){
                    bufferedOutputStream.write(buffer, 0, bytesRead);
                }

                bufferedOutputStream.flush();
                bufferedInputStream.close();
            }else{
                logger.error("Archivo no encontrado");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/termino")
    public void getTerminosCondiciones(HttpServletResponse response){
        try{
            File file = ResourceUtils.getFile(terminosCondiciones);

            if(file.exists()){
                response.setContentType("application/pdf");
                response.setHeader("Content-disposition", "attachment;filename=" + file.getName());
                BufferedInputStream bufferedInputStream =
                        new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream bufferedOutputStream =
                        new BufferedOutputStream(response.getOutputStream());

                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while((bytesRead = bufferedInputStream.read(buffer))!= -1){
                    bufferedOutputStream.write(buffer, 0, bytesRead);
                }

                bufferedOutputStream.flush();
                bufferedInputStream.close();
            }else{
                logger.error("Archivo no encontrado");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
