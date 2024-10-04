package com.example.aml_integration.urls;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class URLController {
	@PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody String requestData) {
        System.out.println("Callback received: " + requestData);
        return ResponseEntity.ok("Callback received successfully");
    }

    @PostMapping("/redirect")
    public ResponseEntity<String> handleRedirect(@RequestBody String requestData) {
        System.out.println("Redirect received: " + requestData);
        return ResponseEntity.ok("Redirect handled successfully");
    }
}
