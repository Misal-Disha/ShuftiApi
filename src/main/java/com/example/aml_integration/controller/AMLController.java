package com.example.aml_integration.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
//    @PostMapping("/callback")
//    public ResponseEntity<String> handleCallback(@RequestBody Map<String, Object> requestData) {
//        System.out.println("Callback received: " + requestData);
//
//        String reference = (String) requestData.get("reference");
//        String event = (String) requestData.get("event");
//        String country = (String) requestData.get("country");
//
//        Map<String, Object> verificationData = (Map<String, Object>) requestData.get("verification_data");
//        Map<String, Object> backgroundChecks = (Map<String, Object>) verificationData.get("background_checks");
//        Map<String, Object> nameData = (Map<String, Object>) backgroundChecks.get("name");
//
//        String firstName = (String) nameData.get("first_name");
//        String lastName = (String) nameData.get("last_name");
//        String dob = (String) backgroundChecks.get("dob");
//
//        // Extract declined reason and codes
//        String declinedReason = (String) requestData.get("declined_reason");
//        List<String> declinedCodes = (List<String>) requestData.get("declined_codes");
//
//        // Print extracted data
//        System.out.println("Reference: " + reference);
//        System.out.println("Event: " + event);
//        System.out.println("Country: " + country);
//        System.out.println("First Name: " + firstName);
//        System.out.println("Last Name: " + lastName);
//        System.out.println("DOB: " + dob);
//        System.out.println("Declined Reason: " + declinedReason);
//        System.out.println("Declined Codes: " + declinedCodes);
//
//        return ResponseEntity.ok("Callback received and processed successfully");
//    }
    
    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> handleCallback(@RequestBody Map<String, Object> requestData) {
        System.out.println("Callback received: " + requestData);

        // Extract the required fields from the request
        String reference = (String) requestData.get("reference");
        String event = (String) requestData.get("event");
        String country = (String) requestData.get("country");

        // Extract verification data dynamically
        Map<String, Object> verificationData = (Map<String, Object>) requestData.get("verification_data");
        Map<String, Object> backgroundChecks = (Map<String, Object>) verificationData.get("background_checks");
        Map<String, Object> nameData = (Map<String, Object>) backgroundChecks.get("name");

        String firstName = (String) nameData.get("first_name");
        String lastName = (String) nameData.get("last_name");

        // Mask the reference number
        String maskedReference = reference.replaceAll(".", "*");

        // Create the detailed response structure
        Map<String, Object> response = new HashMap<>();

        response.put("reference", maskedReference);
        response.put("event", event);
        response.put("country", country);  
        
        // Proofs section
        Map<String, Object> proofs = new HashMap<>();
        proofs.put("verification_report", "https://ns.shuftipro.com/api/pea/****************************");
        proofs.put("access_token", "********************************************************");
        response.put("proofs", proofs);

        // Add verification_data section
        Map<String, Object> verificationResponse = new HashMap<>();
        Map<String, Object> amlData = new HashMap<>();

        amlData.put("filters", Arrays.asList("sanction", "warning", "fitness-probity", "pep", "pep-class-1", "pep-class-2", "pep-class-3", "pep-class-4"));

        // Create the hits array dynamically from requestData
        List<Map<String, Object>> hits = createHitsFromBackgroundChecks(backgroundChecks);

        amlData.put("hits", hits);
        verificationResponse.put("aml_data", amlData);
        response.put("verification_data", verificationResponse);

        // Other sections
        Map<String, Object> info = new HashMap<>();
        Map<String, Object> agent = (Map<String, Object>) requestData.get("agent"); // Dynamically use agent data from request
        info.put("agent", agent != null ? agent : getDefaultAgentInfo());

        response.put("info", info);

        // Log the response for debugging
        System.out.println("Response: " + response);

        return ResponseEntity.ok(response);
    }

    private List<Map<String, Object>> createHitsFromBackgroundChecks(Map<String, Object> backgroundChecks) {
        List<Map<String, Object>> hits = new ArrayList<>();

        // Iterate over background checks and dynamically create hits based on available data
        List<Map<String, Object>> names = (List<Map<String, Object>>) backgroundChecks.get("names");
        if (names != null) {
            for (Map<String, Object> name : names) {
                Map<String, Object> hit = new HashMap<>();
                String firstName = (String) name.get("first_name");
                String lastName = (String) name.get("last_name");

                hit.put("name", firstName + " " + lastName);
                hit.put("entity_type", name.get("entity_type"));
                hit.put("score", name.get("score"));
                hit.put("match_types", name.get("match_types"));

                // Extract fields dynamically
                Map<String, Object> fields = new HashMap<>();
                fields.put("Country", extractField(backgroundChecks, "country", "tag"));
                fields.put("Date Of Birth", extractField(backgroundChecks, "date_of_birth", "tag"));

                hit.put("fields", fields);
                hits.add(hit);
            }
        }

        return hits;
    }

    private Map<String, Object> extractField(Map<String, Object> backgroundChecks, String fieldName, String tag) {
        Map<String, Object> field = new HashMap<>();
        field.put("value", backgroundChecks.get(fieldName));
        field.put("source", "");
        field.put("tag", tag);
        return field;
    }

    private Map<String, Object> getDefaultAgentInfo() {
        Map<String, Object> agent = new HashMap<>();
        agent.put("is_desktop", false);
        agent.put("is_phone", false);
        agent.put("useragent", "PostmanRuntime/7.37.3");
        return agent;
    }
    



    @GetMapping("/redirect")
    public void handleRedirect(@RequestBody String requestData) {
        System.out.println("Redirect received: " + requestData);
//        return ResponseEntity.ok("Redirect handled successfully");
    }
}

