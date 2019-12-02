package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.utils.Archivos;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class EmailController {

    protected static final Log LOG = LogFactory.getLog(EmailController.class);

    @Value("${doppler.relay.url}")
    private String dopplerUrl;

    @Value("${doppler.relay.account.id}")
    private int accountId;

    @Value("${doppler.relay.api.key}")
    private String dopplerApiKey;

    @Autowired
    private Archivos archivos;

    /**
     * Obtiene los templates de doppler relay
     * @throws IOException
     */
    public List getAllTemplates(){

        List<String> templates = new ArrayList<>();

        try {

            URL url = new URL(dopplerUrl + accountId + "/templates");

            HttpURLConnection connection = null;

            connection = openConnection(connection, "GET", url);

            InputStream content = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = in.readLine()) != null){
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            for(int i = 0; i < jsonArray.length(); i++){
                templates.add(jsonArray.getJSONObject(i).getString("id"));
            }

            for(String template : templates){
                LOG.info("Id templates: " + template);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return templates;
    }

    public void sendEmail(String idTemplate, List<String> correos, List<File> attachments){
        try{
            URL url = new URL(dopplerUrl + accountId + "/templates/" + idTemplate + "/message");

            // Se abre la conexión
            HttpURLConnection urlConnection = null;
            urlConnection = openConnection(urlConnection, "POST", url);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();

            // Cabeceras principales
            JSONObject json = new JSONObject();
            json.put("from_name", "Prosesol");
            json.put("from_email", "contacto@prosesol.org");
            json.put("model", "{}");

            // Arreglo para el envío de correos
            JSONArray jsonArrayCorreos = new JSONArray();
            JSONObject itemCorreos = new JSONObject();
            for(String correo : correos){
                itemCorreos.put("email", correo);
                itemCorreos.put("type", "to");
            }
            jsonArrayCorreos.put(itemCorreos);
            json.put("recipients", jsonArrayCorreos);

            // Agregar archivos adjuntos
            JSONArray jsonArrayAdjuntos = new JSONArray();
            JSONObject itemAdjuntos = new JSONObject();
            if(attachments.size() > 0){
                for(File adjunto : attachments){

                    String archivo = archivos.encode(adjunto);

                    itemAdjuntos.put("content_type", "application/pdf");
                    itemAdjuntos.put("base64_content", archivo);
                    itemAdjuntos.put("filename", "Archivo adjunto");
                }

                jsonArrayAdjuntos.put(itemAdjuntos);
                json.put("attachments", jsonArrayAdjuntos);
            }

            LOG.info("Arreglo para el envío de correos: " + json.toString());

            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            // Leemos la respuesta
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();

            String result;
            LOG.info("Respuesta del servicio:");
            while((result = bufferedReader.readLine()) != null){
                sb.append(result);
                System.out.println(result);
            }

            urlConnection.disconnect();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public HttpURLConnection openConnection(HttpURLConnection urlConnection, String method, URL url){

        try {
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8;");
            urlConnection.setRequestProperty("Authorization", "Bearer " + dopplerApiKey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urlConnection;
    }
}
