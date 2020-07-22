package com.prosesol.api.rest.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class GetTokenServiceImpl implements IGetTokenService{

    @Value("${api.url.login}")
    private String urlLogin;

    @Override
    public String getTokenWithEmailAndPassword(String email, String password) {

        String jsonResponse = null;

        try{

            URL url = new URL(urlLogin);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setDoOutput(true);

            String data = URLEncoder.encode("email", "UTF-8") + "=" +
                    URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +
                    URLEncoder.encode(password, "UTF-8");

            try(OutputStream os = connection.getOutputStream()){
                byte[] input = data.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))){
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while((responseLine = br.readLine()) != null){
                    response.append(responseLine.trim());
                }

                JSONObject jsonObject = new JSONObject(response.toString());
                jsonResponse = jsonObject.getString("access_token");

            }

        }catch (IOException ioExc){
            ioExc.printStackTrace();
        }

        return jsonResponse;
    }
}
