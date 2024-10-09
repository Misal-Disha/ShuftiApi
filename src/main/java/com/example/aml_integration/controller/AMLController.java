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

	@GetMapping("/callback")
	public ResponseEntity<String> callback() {
		return ResponseEntity.ok("Server is running");
	}

	@GetMapping("/redirect")
	public ResponseEntity<String> redirect() throws Exception {
		return ResponseEntity.ok("AML check processed successfully");
	}
}
