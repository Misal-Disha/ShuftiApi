package com.example.aml_integration.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     
        // Initialize the response map
        Map<String, Object> response = new HashMap<>();
     
        // Extract and mask the reference (safe null check)
        String reference = requestData != null ? (String) requestData.get("reference") : null;
        String maskedReference = reference != null ? reference.replaceAll(".", "*") : "N/A";
        response.put("reference", maskedReference);
     
        // Extract event and country (safe null checks)
        String event = requestData != null ? (String) requestData.get("event") : "N/A";
        String country = requestData != null ? (String) requestData.get("country") : "N/A";
        response.put("event", event);
        response.put("country", country);
     
        // Extract proofs section (safe null checks)
        Map<String, Object> proofs = new HashMap<>();
        Map<String, Object> requestProofs = requestData != null ? (Map<String, Object>) requestData.get("proofs") : null;
        String verificationReport = requestProofs != null ? (String) requestProofs.get("verification_report") : "N/A";
        String accessToken = requestProofs != null ? (String) requestProofs.get("access_token") : "N/A";
        proofs.put("verification_report", verificationReport);
        proofs.put("access_token", accessToken);
        response.put("proofs", proofs);
     
        // Extract verification data
        Map<String, Object> verificationData = requestData != null ? (Map<String, Object>) requestData.get("verification_data") : null;
        Map<String, Object> backgroundChecks = verificationData != null ? (Map<String, Object>) verificationData.get("background_checks") : null;
     
        // Process AML data with null-safe extraction of filters and hits
        Map<String, Object> verificationResponse = new HashMap<>();
        Map<String, Object> amlData = new HashMap<>();
     
        // Safely extract filters
        List<String> filters = backgroundChecks != null ? (List<String>) backgroundChecks.get("filters") : new ArrayList<>();
        amlData.put("filters", filters);
     
        // Dynamically generate hits from background checks
        List<Map<String, Object>> hits = createHitsFromBackgroundChecks(backgroundChecks);
        amlData.put("hits", hits);
     
        verificationResponse.put("aml_data", amlData);
        response.put("verification_data", verificationResponse);
     
        // Other sections: Add agent information safely
        Map<String, Object> info = new HashMap<>();
        Map<String, Object> agent = requestData != null ? (Map<String, Object>) requestData.get("agent") : getDefaultAgentInfo();
        info.put("agent", agent);
        response.put("info", info);
     
        // Log the response for debugging
        System.out.println("Response: " + response);
     
        return ResponseEntity.ok(response);
    }
    
    private List<Map<String, Object>> createHitsFromBackgroundChecks(Map<String, Object> backgroundChecks) {
        List<Map<String, Object>> hits = new ArrayList<>();
     
        // Return an empty list if backgroundChecks or hits are null
        if (backgroundChecks == null) {
            return hits;
        }
     
        // Extract the hits array from the backgroundChecks
        List<Map<String, Object>> hitList = (List<Map<String, Object>>) backgroundChecks.get("hits");
        if (hitList == null || hitList.isEmpty()) {
            return hits;
        }
     
        // Loop through each hit in the hit list
        for (Map<String, Object> hitData : hitList) {
            Map<String, Object> hit = new HashMap<>();
     
            // Safely extract name and entity_type
            String name = (String) hitData.get("name");
            String entityType = (String) hitData.get("entity_type");
     
            // Add default values if they are null
            hit.put("name", name != null ? name : "N/A");
            hit.put("entity_type", entityType != null ? entityType : "N/A");
            hit.put("score", hitData.getOrDefault("score", "")); // Use empty string as default for score
            hit.put("match_types", hitData.getOrDefault("match_types", new ArrayList<>())); // Default empty list if not present
     
//             Handle nested fields
            Map<String, Object> fields = new HashMap<>();
            Map<String, Object> hitFields = (Map<String, Object>) hitData.get("fields");
            if (hitFields != null) {
                for (Map.Entry<String, Object> entry : hitFields.entrySet()) {
                    String fieldName = entry.getKey();
                    List<Map<String, String>> fieldValues = (List<Map<String, String>>) entry.getValue();
                    fields.put(fieldName, fieldValues);
                }
            }
     
            hit.put("fields", fields);
     
            // Add the hit to the hits list
            hits.add(hit);
        }
     
        return hits;
    }
    

    private Map<String, Object> extractField(Map<String, Object> source, String fieldName, String tag) {
        Map<String, Object> field = new HashMap<>();
        field.put("value", source.get(fieldName));
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

