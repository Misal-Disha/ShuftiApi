package com.example.aml_integration.controller;


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

//    @GetMapping("/check")
//    public ResponseEntity<String> performAMLCheck() {
//        try {
//            String result = amlService.performAMLCheck();
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error occurred while performing AML check");
//        }
//    }
    
    @PostMapping("/check")
    public ResponseEntity<String> performAMLCheck(@RequestBody String requestData) throws Exception {
        System.out.println("AML check request received: " + requestData);
        String result = amlService.performAMLCheck(requestData);
        System.out.println(result);
        return ResponseEntity.ok("AML check processed successfully");
    }
}

