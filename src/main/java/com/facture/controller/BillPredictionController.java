package com.facture.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class BillPredictionController {

    @PostMapping("/get-prediction")
    public String getPrediction(@RequestBody Map<String, Object> requestData) {
        String apiUrl = "http://127.0.0.1:5000/";

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Create request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);

        // Send request to the Flask API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        // Return the response from the Flask API (the prediction)
        return response.getBody();
    }
}
