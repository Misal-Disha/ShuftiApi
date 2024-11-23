package com.example.aml_integration.service;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;

@Service
public class AMLService {
	
	public final RestTemplate restTemplate;
	
	public AMLService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String performAMLCheck(String firstName, String middleName, String lastName, String dob, List<String> filters, List<String> countries, Integer matchScore) throws Exception {
	    String url = "https://api.shuftipro.com/";
	    String CLIENT_ID = "8dfed603060c6178da6e2e942a234ddb2197fe85b5bdcf860387cb82f6d76189";
	    String SECRET_KEY = "6RsGbP8O3gpYEdvcuJcZGmQS2Vf6mhMp";
	    URL obj = new URL(url);
	    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

	    // Add request header
	    con.setRequestMethod("POST");
	    con.setRequestProperty("Content-Type", "application/json");
	    String basicAuth = "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + SECRET_KEY).getBytes(StandardCharsets.UTF_8));
	    con.setRequestProperty("Authorization", basicAuth);

	    // Build the dynamic JSON payload
	    Map<String, Object> payloadMap = new HashMap<>();
	    payloadMap.put("reference", "SP_REQUEST_" + (int) (Math.random() * 10000));
	    payloadMap.put("callback_url", "https://Devshuftipro.flairminds.com/api/aml/callback");
	    payloadMap.put("redirect_url", "https://Devshuftipro.flairminds.com/api/aml/redirect");
	    payloadMap.put("language", "EN");
	    payloadMap.put("verification_mode", "any");
	    payloadMap.put("ttl", 60);

	    Map<String, Object> backgroundChecks = new HashMap<>();
	    backgroundChecks.put("alias_search", "0");
	    backgroundChecks.put("rca_search", "0");
	    backgroundChecks.put("match_score", matchScore != null ? matchScore : 75);  // Use passed matchScore or default to 75

	    Map<String, Object> nameData = new HashMap<>();
	    nameData.put("first_name", firstName);
	    nameData.put("middle_name", middleName);
	    nameData.put("last_name", lastName);
	    backgroundChecks.put("name", nameData);
	    backgroundChecks.put("dob", dob);
	    backgroundChecks.put("filters", filters);
	    backgroundChecks.put("country", countries);  // Add countries to the payload

	    payloadMap.put("background_checks", backgroundChecks);
	    System.out.println("************************************************************************************************");
	    System.out.println("Payload Map : "+ payloadMap);

	    // Convert payload to JSON
	    ObjectMapper objectMapper = new ObjectMapper();
	    String payload = objectMapper.writeValueAsString(payloadMap);

	    // Send post request
	    con.setDoOutput(true);
	    try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
	        wr.writeBytes(payload);
	        wr.flush();
	    }
	    
	 // Handle the response
	    int responseCode = con.getResponseCode();
	    System.out.println("\nSending 'POST' request to URL : " + url);
	    System.out.println("Payload : " + payload);
	    System.out.println("Response Code : " + responseCode);

	    // Check if the response code is not 2xx
	    if (responseCode != 200) {
	        BufferedReader errorIn = new BufferedReader(new InputStreamReader(con.getErrorStream()));
	        String inputLine;
	        StringBuffer errorResponse = new StringBuffer();
	        while ((inputLine = errorIn.readLine()) != null) {
	            errorResponse.append(inputLine);
	        }
	        errorIn.close();
	        throw new Exception("Error response from server: " + errorResponse.toString());
	    }

	    // Success response
	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    String inputLine;
	    StringBuffer response = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {
	        response.append(inputLine);
	    }
	    in.close();

	    // Return the response
	    return response.toString();
	}

}

