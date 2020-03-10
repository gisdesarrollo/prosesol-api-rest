package com.prosesol.api.rest.utils;

import com.prosesol.api.rest.models.entity.LogAR;
import com.prosesol.api.rest.services.ILogARService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class GeneraArchivoLog {

    @Autowired
    private ILogARService logARService;

    protected final Log LOG = LogFactory.getLog(GeneraArchivoLog.class);

    private Date fechaCreacion;
    private ByteArrayOutputStream outputStream;
    private LogAR logAR;


    /**
     * Generación de archivo
     * @param nombre
     * @param numRegistros
     * @param log
     * @throws IOException ioExc
     */
    public void generarArchivo(String nombre, Integer numRegistros, List<String> log){

        init();

        try {
            for (String registro : log) {
                outputStream.write(registro.getBytes());
            }

            byte data[] = outputStream.toByteArray();

            DateFormat getDateFormat = new SimpleDateFormat("dd/MM/yyyy'_'HH:mm:ss");
            String dateFormat = getDateFormat.format(fechaCreacion);

            logAR = new LogAR(nombre + "_" + dateFormat + ".txt", fechaCreacion, numRegistros,
                    data, false, false);

            logARService.save(logAR);
            outputStream.close();

        }catch (IOException ioExc){
            ioExc.printStackTrace();
        }

        LOG.info("Archivo creado correctamente");
    }

    public void init(){
        fechaCreacion = new Date();
        outputStream = new ByteArrayOutputStream();
    }
}
