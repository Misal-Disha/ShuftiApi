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

        // Extract verification data
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
        response.put("country", null);  // Set country to null as per your requirement

        // Proofs section
        Map<String, Object> proofs = new HashMap<>();
        proofs.put("verification_report", "https://ns.shuftipro.com/api/pea/****************************");
        proofs.put("access_token", "********************************************************");
        response.put("proofs", proofs);

        // Add verification_data section
        Map<String, Object> verificationResponse = new HashMap<>();
        Map<String, Object> amlData = new HashMap<>();

        amlData.put("filters", Arrays.asList("sanction", "warning", "fitness-probity", "pep", "pep-class-1", "pep-class-2", "pep-class-3", "pep-class-4"));

        // Create the hits array dynamically
        List<Map<String, Object>> hits = new ArrayList<>();

        // Populate hits with dynamic data
        hits.add(createHit("John Dew", "1952-05-03", firstName, lastName));
        hits.add(createHit("John Dow", null, firstName, lastName, 
            new String[][] {
                {"Address", "c/o Kingsbridge Corporate Solutions, Business Hive, 13 Dudley Street, Grimsby, NorthEast Lincolnshire DN31 2AW"},
                {"Company Name", "J A DOW JOINERY & MANUFACTURERS LIMITED"},
                {"Designation", "Director"}
            }));
        hits.add(createHit("John Dow", "2022-11-05", firstName, lastName, 
            new String[][] {
                {"Address", "56 Warwick Road   SCUNTHORPE DN16 1EZ"},
                {"Date Of Death", "2022-11-05"}
            }));
        hits.add(createHit("The John Townsend Trust", null, firstName, lastName, 
            new String[][] {
                {"Company Number", "06769267"}
            }));

        amlData.put("hits", hits);
        verificationResponse.put("aml_data", amlData);
        verificationData.put("background_checks", backgroundChecks);
        response.put("verification_data", verificationResponse);

        // Other sections
        Map<String, Object> info = new HashMap<>();
        Map<String, Object> agent = new HashMap<>();
        agent.put("is_desktop", false);
        agent.put("is_phone", false);
        agent.put("useragent", "PostmanRuntime/7.37.3");
        info.put("agent", agent);
        response.put("info", info);

        // Log the response for debugging
        System.out.println("Response: " + response);

        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createHit(String name, String dateOfBirth, String firstName, String lastName) {
        return createHit(name, dateOfBirth, firstName, lastName, null);
    }

    private Map<String, Object> createHit(String name, String dateOfBirth, String firstName, String lastName, String[][] additionalFields) {
        Map<String, Object> hit = new HashMap<>();
        hit.put("name", name);
        hit.put("entity_type", null);
        hit.put("score", "");
        hit.put("match_types", Arrays.asList("category", "country", "entity_type", "profile_name"));
        hit.put("alternative_names", new ArrayList<>());
        hit.put("assets", new ArrayList<>());
        hit.put("associates", new ArrayList<>());

        Map<String, Object> fields = new HashMap<>();

        // Common fields
        fields.put("First Name", Collections.singletonList(Map.of("value", firstName, "source", "", "tag", "first_name")));
        fields.put("Last Name", Collections.singletonList(Map.of("value", lastName, "source", "", "tag", "last_name")));

        // Date of Birth if available
        if (dateOfBirth != null) {
            fields.put("Date Of Birth", Collections.singletonList(Map.of("value", dateOfBirth, "source", "", "tag", "date_of_birth")));
        }

        // Add additional fields dynamically
        if (additionalFields != null) {
            for (String[] fieldData : additionalFields) {
                String fieldName = fieldData[0];
                String fieldValue = fieldData[1];
                fields.put(fieldName, Collections.singletonList(Map.of("value", fieldValue, "source", "", "tag", fieldName.toLowerCase().replaceAll(" ", "_"))));
            }
        }

        hit.put("fields", fields);
        hit.put("media", new ArrayList<>());
        hit.put("source_notes", new HashMap<>());
        hit.put("sources", Arrays.asList("ShuftiPro Internal Database", "Wikidata"));
        hit.put("types", new ArrayList<>());

        return hit;
    }




    @GetMapping("/redirect")
    public void handleRedirect(@RequestBody String requestData) {
        System.out.println("Redirect received: " + requestData);
//        return ResponseEntity.ok("Redirect handled successfully");
    }
}

