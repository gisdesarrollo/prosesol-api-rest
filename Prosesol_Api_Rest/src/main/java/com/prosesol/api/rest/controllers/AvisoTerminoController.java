package com.prosesol.api.rest.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpRequest;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

@RestController
@RequestMapping("/obtener")
@PropertySource("classpath:messages.properties")
public class AvisoTerminoController {

    protected static final Log LOG = LogFactory.getLog(AvisoTerminoController.class);

    private static final String APPLICATION_PDF = "application/pdf";

    @Value("${archivo.avisoProsesol}")
    private String avisoPrivacidadProsesol;

    @Value("${archivo.terminosProsesol}")
    private String terminosCondicionesProsesol;

    @Value("${archivo.avisoAssismex}")
    private String avisoPrivacidadAssismex;

    @Value("${archivo.terminoAssismex}")
    private String terminosCondicionesAssismex;

    @Value("${error.descarga.archivo}")
    private String errorDescargaArchivo;

    @Value("${hostname.prosesol.url}")
    private String hostnameProsesol;

    @Value("${hostname.assismex.url}")
    private String hostnameAssismex;

    private String hostname;

    @RequestMapping(value = "/aviso", method = RequestMethod.GET, produces = APPLICATION_PDF)
    public @ResponseBody void getAvisoPrivacidad(HttpServletRequest request,
                                                 HttpServletResponse response){

        String url = request.getRequestURL().toString();
        LOG.info(request.getRequestURL().toString());

        try {

            if(url.equals(hostnameProsesol.concat("/obtener/aviso"))){
                getArchivoPDF(response, avisoPrivacidadProsesol);
            }else if(url.equals(hostnameAssismex.concat("/obtener/aviso"))){
                getArchivoPDF(response, avisoPrivacidadAssismex);
            }else{
                LOG.error(errorDescargaArchivo);
            }


        }catch(IOException ioExc){
            ioExc.printStackTrace();
            LOG.error(errorDescargaArchivo, ioExc);
        }
    }

    @RequestMapping(value = "/termino", method = RequestMethod.GET, produces = APPLICATION_PDF)
    public @ResponseBody void getTerminoCondiciones(HttpServletRequest request,
                                                    HttpServletResponse response){

        String url = request.getRequestURL().toString();
        LOG.info(request.getRequestURL().toString());

        try {

            if(url.equals(hostnameProsesol.concat("/obtener/termino"))){
                getArchivoPDF(response, terminosCondicionesProsesol);
            }else if(url.equals(hostnameAssismex.concat("/obtener/termino"))){
                getArchivoPDF(response, terminosCondicionesAssismex);
            }else {
                LOG.error(errorDescargaArchivo);
            }

        }catch (IOException ioExc){
            ioExc.printStackTrace();
            LOG.error(errorDescargaArchivo, ioExc);
        }
    }

    private static void getArchivoPDF(HttpServletResponse response, String archivo)
            throws IOException{

        File file = ResourceUtils.getFile(archivo);

        InputStream in = new FileInputStream(file);
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));

        FileCopyUtils.copy(in, response.getOutputStream());

    }
}
