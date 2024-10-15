package com.example.aml_integration.controller;

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
//        String verificationStatus = (String) requestData.get("verification_status");
//        
//        System.out.println("Reference: " + reference);
//        System.out.println("Event: " + event);
//        System.out.println("Verification Status: " + verificationStatus);
//
//        return ResponseEntity.ok("Callback response received!");
//    }
    
    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody Map<String, Object> requestData) {
        System.out.println("Callback received: " + requestData);

        // Extract main fields
        String reference = (String) requestData.get("reference");
        String event = (String) requestData.get("event");
        String country = (String) requestData.get("country");

        // Extract nested fields from verification_data -> background_checks -> name
        Map<String, Object> verificationData = (Map<String, Object>) requestData.get("verification_data");
        Map<String, Object> backgroundChecks = (Map<String, Object>) verificationData.get("background_checks");
        Map<String, Object> nameData = (Map<String, Object>) backgroundChecks.get("name");

        String firstName = (String) nameData.get("first_name");
        String lastName = (String) nameData.get("last_name");
        String dob = (String) backgroundChecks.get("dob");

        // Extract declined reason and codes
        String declinedReason = (String) requestData.get("declined_reason");
        List<String> declinedCodes = (List<String>) requestData.get("declined_codes");

        // Print extracted data
        System.out.println("Reference: " + reference);
        System.out.println("Event: " + event);
        System.out.println("Country: " + country);
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("DOB: " + dob);
        System.out.println("Declined Reason: " + declinedReason);
        System.out.println("Declined Codes: " + declinedCodes);

        return ResponseEntity.ok("Callback received and processed successfully");
    }


    @GetMapping("/redirect")
    public void handleRedirect(@RequestBody String requestData) {
        System.out.println("Redirect received: " + requestData);
//        return ResponseEntity.ok("Redirect handled successfully");
    }
}

