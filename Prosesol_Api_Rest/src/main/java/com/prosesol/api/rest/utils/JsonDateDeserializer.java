package com.prosesol.api.rest.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.prosesol.api.rest.controllers.exception.AfiliadoException;
import com.prosesol.api.rest.controllers.exception.CustomExceptionHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Luis Enrique Morales Soriano
 */
public class JsonDateDeserializer extends JsonDeserializer<Date> {

    protected static final Log LOG = LogFactory.getLog(CustomExceptionHandler.class);

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        try{
            return DataTypeHelper.stringToDate(p.getText());
        }catch(AfiliadoException e){
            LOG.error(e.getMessage());
            throw e;
        }
    }

    public static class DataTypeHelper{

        static Date stringToDate(String date) throws AfiliadoException {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                try{
                    return format.parse(date);
                }catch (ParseException pe){
                    throw new AfiliadoException(4000, "Formato de fecha incorrecto, " +
                            "formato válido yyyy-MM-dd");
                }
        }

    }
}
