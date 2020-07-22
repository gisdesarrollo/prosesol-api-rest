package com.prosesol.api.rest.controllers.rest;

import com.prosesol.api.rest.response.entity.ZipCodeRS;
import com.prosesol.api.rest.services.IGetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.net.MalformedURLException;

/**
 * @author Luis Enrique Morales Soriano
 */
@RestController
@RequestMapping("/api")
@PropertySource("classpath:hostname.properties")
public class ZipRestController {

    @Value("${api.url.zip}")
    private String url;

    @Value("${email.bc4nb.login}")
    private String emailLogin;

    @Value("${password.bc4nb.login}")
    private String passwordLogin;

    @Autowired
    private IGetTokenService getTokenService;

    @GetMapping("/zip")
    public ResponseEntity<?> getZipCode(@RequestParam(value = "codigoPostal") String codigoPostal)
            throws MalformedURLException {

        RestTemplate restTemplate = new RestTemplate();
        String authToken = getTokenService.getTokenWithEmailAndPassword(emailLogin, passwordLogin);

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

}
