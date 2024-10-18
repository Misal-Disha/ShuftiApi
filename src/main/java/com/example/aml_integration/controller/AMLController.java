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

        // Dynamically extract name components
        String firstName = (String) nameData.get("first_name");
        String lastName = (String) nameData.get("last_name");
        String middleName = (String) nameData.get("middle_name");

        // Mask the reference number
        String maskedReference = reference.replaceAll(".", "*");

        // Create the detailed response structure
        Map<String, Object> response = new HashMap<>();

        response.put("reference", maskedReference);
        response.put("event", event);
        response.put("country", country);  

        // Proofs section: dynamically extract if available
        Map<String, Object> proofs = new HashMap<>();
        String verificationReport = (String) verificationData.get("verification_report"); // Assume this is in verificationData
        String accessToken = (String) verificationData.get("access_token"); // Assume this is in verificationData
        proofs.put("verification_report", verificationReport != null ? verificationReport : "N/A");
        proofs.put("access_token", accessToken != null ? accessToken : "N/A");
        response.put("proofs", proofs);

        // Add verification_data section
        Map<String, Object> verificationResponse = new HashMap<>();
        Map<String, Object> amlData = new HashMap<>();

        // Dynamically get filters from background checks
        List<String> filters = (List<String>) backgroundChecks.get("filters");
        amlData.put("filters", filters); // Save filters directly, will be null if not present
        
        // Create the hits array dynamically from background checks
        List<Map<String, Object>> hits = createHitsFromBackgroundChecks(backgroundChecks);
        amlData.put("hits", hits);
        verificationResponse.put("aml_data", amlData);
        response.put("verification_data", verificationResponse);

        // Other sections
        Map<String, Object> info = new HashMap<>();
        Map<String, Object> agent = (Map<String, Object>) requestData.get("agent"); // Use agent data dynamically
        info.put("agent", agent != null ? agent : getDefaultAgentInfo());

        response.put("info", info);

        // Log the response for debugging
        System.out.println("Response: " + response);

        return ResponseEntity.ok(response);
    }

    private List<Map<String, Object>> createHitsFromBackgroundChecks(Map<String, Object> backgroundChecks) {
        List<Map<String, Object>> hits = new ArrayList<>();

        Map<String, Object> nameData = (Map<String, Object>) backgroundChecks.get("name");
        if (nameData != null) {
            String firstName = (String) nameData.get("first_name");
            String middleName = (String) nameData.get("middle_name");
            String lastName = (String) nameData.get("last_name");
            String entityType = (String) nameData.get("entity_type");  // Extract entity_type dynamically

            // Construct the full name dynamically, ensuring middle name is included if present
            String fullName = firstName;
            if (middleName != null && !middleName.isEmpty()) {
                fullName += " " + middleName;  // Add middle name if present
            }
            fullName += " " + lastName;  // Append last name

            // Create hit data
            Map<String, Object> hit = new HashMap<>();
            hit.put("name", fullName);
            hit.put("entity_type", entityType);  
            hit.put("score", "");
            hit.put("match_types", Arrays.asList("category", "country", "entity_type", "profile_name"));

            // Fields for the hit, including dynamic Country extraction
            Map<String, Object> fields = new HashMap<>();

            // Make the country field dynamic
            String country = (String) backgroundChecks.get("country");
            if (country != null && !country.isEmpty()) {
                fields.put("Country", Collections.singletonList(Map.of("value", country, "source", "", "tag", "country")));
            }

            // Other dynamic fields (DOB, First Name, Middle Name, Last Name)
            fields.put("Date Of Birth", Collections.singletonList(Map.of("value", backgroundChecks.get("dob"), "source", "", "tag", "date_of_birth")));
            fields.put("First Name", Collections.singletonList(Map.of("value", firstName, "source", "", "tag", "first_name")));
            fields.put("Middle Name", Collections.singletonList(Map.of("value", middleName, "source", "", "tag", "middle_name")));
            fields.put("Last Name", Collections.singletonList(Map.of("value", lastName, "source", "", "tag", "last_name")));

            hit.put("fields", fields);
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

