package com.example.aml_integration.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import com.example.aml_integration.service.AMLService;

@RestController
@RequestMapping("/api/aml")
public class AMLController {

    @Autowired
    private AMLService amlService;
    
    @PostMapping("/check")
    public ResponseEntity<String> performAMLCheck(@RequestBody String requestData) throws Exception {
        System.out.println("AML check request received: " + requestData);
        String result = amlService.performAMLCheck();
        System.out.println(result);
        return ResponseEntity.ok("AML check processed successfully");
    }
    
    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> handleCallback(@RequestBody Map<String, Object> requestData) {
        System.out.println("Callback received: " + requestData);

        // Prepare a Map to store the response data
        Map<String, Object> responseData = new HashMap<>();

        // Extract reference from requestData
        String reference = (String) requestData.get("reference");

        // Fetch detailed response from ShuftiPro API using the reference
        Map<String, Object> detailedResponse = fetchDetailedHitsFromShufti(reference);
//        System.out.println("Detailed response with status API:");
//        System.out.println(detailedResponse);

        // Extract data from detailedResponse
        String event = (String) detailedResponse.get("event");
        String country = (String) detailedResponse.get("country");

        // Add basic data to response
        responseData.put("reference", reference);
        responseData.put("event", event);
        responseData.put("country", country);

        // Extract verification data
        Map<String, Object> verificationData = (Map<String, Object>) detailedResponse.get("verification_data");
        if (verificationData != null) {
            Map<String, Object> backgroundChecks = (Map<String, Object>) verificationData.get("background_checks");
            if (backgroundChecks != null) {
                Map<String, Object> nameData = (Map<String, Object>) backgroundChecks.get("name");

                String firstName = (String) nameData.get("first_name");
                String lastName = (String) nameData.get("last_name");
                String dob = (String) backgroundChecks.get("dob");

                // Add name and DOB to response
                responseData.put("first_name", firstName);
                responseData.put("last_name", lastName);
                responseData.put("dob", dob);

                // Extract AML data (if exists)
                Map<String, Object> amlData = (Map<String, Object>) backgroundChecks.get("aml_data");
                if (amlData != null) {
                    List<String> filters = (List<String>) amlData.get("filters");
                    List<Map<String, Object>> hits = (List<Map<String, Object>>) amlData.get("hits");

                    // Add AML data to response
                    responseData.put("aml_filters", filters);
                    responseData.put("aml_hits", hits);
                }
            }
        }

        // Extract declined reason and codes from detailedResponse
        String declinedReason = (String) detailedResponse.get("declined_reason");
        List<String> declinedCodes = (List<String>) detailedResponse.get("declined_codes");

        // Add declined information to response
        responseData.put("declined_reason", declinedReason);
        responseData.put("declined_codes", declinedCodes);

        // Optionally handle proofs (if available in detailedResponse)
        Map<String, Object> proofs = (Map<String, Object>) detailedResponse.get("proofs");
        if (proofs != null) {
            String reportUrl = (String) proofs.get("verification_report");
            responseData.put("verification_report_url", reportUrl);
        }

        System.out.println(responseData);
        // Return the entire responseData map as JSON in the response
        return ResponseEntity.ok(responseData);
    }


    
    private Map<String, Object> fetchDetailedHitsFromShufti(String reference) {
        // Create an HTTP request to fetch detailed data using the reference from the callback
        String url = "https://api.shuftipro.com/status";  // Example API URL
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("8dfed603060c6178da6e2e942a234ddb2197fe85b5bdcf860387cb82f6d76189", "6RsGbP8O3gpYEdvcuJcZGmQS2Vf6mhMp");  // Basic Auth

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of("reference", reference), headers);
        
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

//        System.out.println("111");
//        System.out.println(response);
        return response.getBody();
    }


    @GetMapping("/redirect")
    public void handleRedirect(@RequestBody String requestData) {
        System.out.println("Redirect received: " + requestData);
//        return ResponseEntity.ok("Redirect handled successfully");
    }
}

