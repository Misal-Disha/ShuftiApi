package com.example.aml_integration.controller;

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
    
//    @GetMapping("/callback")
//    public void handleCallback(@RequestBody String requestData) {
//        System.out.println("Callback received: " + requestData);
////        return ResponseEntity.ok("Callback received successfully");
//        
//    }
    
    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody Map<String, Object> requestData) {
        System.out.println("Callback received: " + requestData);

        String reference = (String) requestData.get("reference");
        String event = (String) requestData.get("event");
        String verificationStatus = (String) requestData.get("verification_status");
        
        System.out.println("Reference: " + reference);
        System.out.println("Event: " + event);
        System.out.println("Verification Status: " + verificationStatus);

        return ResponseEntity.ok("Callback received and processed successfully");
    }

    @GetMapping("/redirect")
    public void handleRedirect(@RequestBody String requestData) {
        System.out.println("Redirect received: " + requestData);
//        return ResponseEntity.ok("Redirect handled successfully");
    }
}

