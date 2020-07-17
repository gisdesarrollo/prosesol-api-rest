package com.prosesol.api.rest.controllers.rest;

import com.prosesol.api.rest.response.entity.ZipCodeRS;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Luis Enrique Morales Soriano
 */
@RestController
@RequestMapping("/api")
@PropertySource("classpath:hostname.properties")
public class ZipRestController {

    @Value("${api.url.zip}")
    private String url;

    @Value("${api.url.login}")
    private String urlLogin;

    @Value("${email.bc4nb.login}")
    private String emailLogin;

    @Value("${password.bc4nb.login}")
    private String passwordLogin;

    @GetMapping("/zip")
    public ResponseEntity<?> getZipCode(@RequestParam(value = "codigoPostal") String codigoPostal)
            throws MalformedURLException {

        RestTemplate restTemplate = new RestTemplate();
        String authToken = getTokenWhenLogin(emailLogin, passwordLogin);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("zip", codigoPostal);

        HttpEntity<?> httpEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<ZipCodeRS> responseEntity =
                restTemplate.exchange(url, HttpMethod.POST, httpEntity, ZipCodeRS.class);

        ZipCodeRS zipCodeRS = responseEntity.getBody();

        System.out.println(zipCodeRS.toString());

        return responseEntity;
    }


    private String getTokenWhenLogin(String email, String password)
            throws MalformedURLException {

        URL url = new URL(urlLogin);
        String jsonResponse = null;

        try{

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
