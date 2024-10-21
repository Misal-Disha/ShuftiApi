package com.example.aml_integration.service;


import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

@Service
public class AMLService {

	public String performAMLCheck() throws Exception {
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

	    String payload = "{"
	            + "\"reference\": \"SP_REQUEST_" + (int)(Math.random() * 10000) + "\","
	            + "\"callback_url\": \"https://Devshuftipro.flairminds.com/api/aml/callback\"," 
	            + "\"redirect_url\": \"https://Devshuftipro.flairminds.com/api/aml/redirect\","  
//	            + "\"country\": \"GB\","
	            + "\"language\": \"EN\","
	            + "\"verification_mode\": \"any\","
	            + "\"ttl\": 60,"
	            + "\"background_checks\": {"
	            + "    \"alias_search\": \"0\","
	            + "    \"rca_search\": \"0\","
	            + "    \"match_score\": 75,"
	            + "    \"name\": {"
	            + "        \"first_name\": \"Nirav\","
	            + "        \"middle_name\": \"\","
	            + "        \"last_name\": \"Modi\""
	            + "    },"
	            + "    \"dob\": \"2003-02-27\","
	            + "    \"filters\": [\"sanction\", \"fitness-probity\", \"warning\", \"pep\",\"pep-class-1\",\"pep-class-2\",\"pep-class-3\",\"pep-class-4\"]"
	            + "}"
	            + "}";


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

