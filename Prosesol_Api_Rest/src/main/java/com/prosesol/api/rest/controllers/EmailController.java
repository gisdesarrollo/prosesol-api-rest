package com.prosesol.api.rest.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class EmailController {

    @Value("${doppler.relay.url}")
    private String dopplerUrl;

    @Value("${doppler.relay.account.id}")
    private int accountId;

    @Value("${doppler.relay.api.key}")
    private String dopplerApiKey;

    public void getAllTemplates(){

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer " + dopplerApiKey);
        headers.set("Content-Type", "application/json; charset=utf-8");
        HttpEntity<String> entity = new HttpEntity<String>(" ", headers);

        ResponseEntity<String> response = restTemplate.exchange(dopplerUrl + accountId + "/templates"
                , HttpMethod.GET, entity, String.class);

        System.out.println(response);

    }

}
