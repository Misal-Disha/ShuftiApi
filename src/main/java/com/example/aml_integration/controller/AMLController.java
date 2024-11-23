package com.example.aml_integration.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.aml_integration.service.AMLService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Controller
@RequestMapping("/api/aml")
public class AMLController {

    @Autowired
    private AMLService amlService;

    // Constructor injection
    public AMLController(AMLService amlService) {
        this.amlService = amlService;
    }
    
    @GetMapping("/aml-form")
    public String showAmlCheckForm() {
    	System.out.println("Testing...");
        return "amlCheckForm";  
    }
    
    @PostMapping("/check")
    public ResponseEntity<String> performAMLCheck(@RequestBody Map<String, Object> requestData) throws Exception {
        try {
    	System.out.println("AML check request received: " + requestData);

        // Extract required fields from requestData and validate
        String firstName = (String) requestData.get("firstName");
        String middleName = (String) requestData.getOrDefault("middleName", ""); // Optional
        String lastName = (String) requestData.get("lastName");
        String dob = (String) requestData.get("dob");
        List<String> filters = (List<String>) requestData.get("filters");
        List<String> countries = (List<String>) requestData.get("countries"); // New field
//        Integer matchScore = Integer.parseInt((String) requestData.get("matchScore"));
        
        Integer matchScore = 75; // default value
        String matchScoreStr = (String) requestData.get("matchScore");

        if (matchScoreStr != null && !matchScoreStr.isEmpty()) {
            matchScore = Integer.parseInt(matchScoreStr);
        }


        if (firstName == null || lastName == null || dob == null || filters == null || filters.isEmpty()) {
            return ResponseEntity.badRequest().body("Required fields are missing or invalid. Make sure to include firstName, lastName, dob, and at least one filter.");
        }

        // Ensure countries and matchScore are optional fields, so they don't invalidate the request
        if (countries == null) {
            countries = Collections.emptyList();
        }
//        if (matchScore == null) {
//            matchScore = 75; // Default match score if not provided
//        }

        // Call the AML check service, passing dynamic data
        String result = amlService.performAMLCheck(firstName, middleName, lastName, dob, filters, countries, matchScore);
        System.out.println("AML check result: " + result);

        // Parse the result as a JSON object
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);

        // Prepare response data based on the "event" field in the response
        Map<String, Object> responseMap = new HashMap<>();
        String event = (String) resultMap.get("event");
        String reference = (String) resultMap.get("reference");

        // Add basic data to the response
        responseMap.put("reference", reference);

        // Check if the AML verification was accepted or declined
        if ("verification.accepted".equals(event)) {
            responseMap.put("status", "Passed");
            responseMap.put("data", "AML check was successful!");

            // Extract additional details if available
            Map<String, Object> verificationData = (Map<String, Object>) resultMap.get("verification_data");
            if (verificationData != null) {
                Map<String, Object> backgroundChecks = (Map<String, Object>) verificationData.get("background_checks");
                if (backgroundChecks != null) {
                    Map<String, Object> nameData = (Map<String, Object>) backgroundChecks.get("name");
                    if (nameData != null) {
                        responseMap.put("first_name", nameData.get("first_name"));
                        responseMap.put("last_name", nameData.get("last_name"));
                    }
                    responseMap.put("dob", backgroundChecks.get("dob"));
                }
            }
        } else if ("verification.declined".equals(event)) {
            responseMap.put("status", "Failed");
            responseMap.put("data", "AML check failed.");

            // Add the declined reason and codes to the response
            responseMap.put("declined_reason", resultMap.get("declined_reason"));
            responseMap.put("declined_codes", resultMap.get("declined_codes"));
        }

        // Convert the response map to JSON and return
        String jsonResponse = objectMapper.writeValueAsString(responseMap);
        return ResponseEntity.ok(jsonResponse);
        }catch(Exception e) {
        	return ResponseEntity.ok("amlRetryPage");
        }
    }
    
    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> handleCallback(@RequestBody Map<String, Object> requestData) {
        System.out.println("Callback received: " + requestData);

        // Prepare a Map to store the response data
        Map<String, Object> responseData = new HashMap<>();

        // Extract reference from requestData
        String reference = (String) requestData.get("reference");

        // Fetch detailed response from ShuftiPro API using the reference
        Map<String, Object> detailedResponse = fetchDetailedHitsFromShufti(reference);

        // Extract data from detailedResponse
        String event = (String) detailedResponse.get("event");
        String country = (String) detailedResponse.get("country");

        // Add basic data to response
        responseData.put("reference", reference);
        responseData.put("event", event);
        responseData.put("country", country);

        // Extract verification data
        Map<String, Object> verificationData = (Map<String, Object>) detailedResponse.get("verification_data");
        if (verificationData != null) {
            Map<String, Object> backgroundChecks = (Map<String, Object>) verificationData.get("background_checks");
            if (backgroundChecks != null) {
                Map<String, Object> nameData = (Map<String, Object>) backgroundChecks.get("name");

                String firstName = (String) nameData.get("first_name");
                String lastName = (String) nameData.get("last_name");
                String dob = (String) backgroundChecks.get("dob");

                // Add name and DOB to response
                responseData.put("first_name", firstName);
                responseData.put("last_name", lastName);
                responseData.put("dob", dob);

                // Extract AML data (if exists)
                Map<String, Object> amlData = (Map<String, Object>) backgroundChecks.get("aml_data");
                if (amlData != null) {
//                    List<String> filters = (List<String>) amlData.get("filters");
                    List<String> filters = new ArrayList<>(Arrays.asList("sanction", "fitness-probity", "warning", "pep", "pep-class-1", "pep-class-2", "pep-class-3", "pep-class-4"));

                    List<Map<String, Object>> hits = (List<Map<String, Object>>) amlData.get("hits");

                    // Add AML data to response
                    responseData.put("aml_filters", filters);
                    responseData.put("aml_hits", hits);
                }
            }
        }

        // Extract declined reason and codes from detailedResponse
        String declinedReason = (String) detailedResponse.get("declined_reason");
        List<String> declinedCodes = (List<String>) detailedResponse.get("declined_codes");

        // Add declined information to response
        responseData.put("declined_reason", declinedReason);
        responseData.put("declined_codes", declinedCodes);

        // Optionally handle proofs (if available in detailedResponse)
        Map<String, Object> proofs = (Map<String, Object>) detailedResponse.get("proofs");
        if (proofs != null) {
            String reportUrl = (String) proofs.get("verification_report");
            responseData.put("verification_report_url", reportUrl);
        }
        
        if ("verification.declined".equals(event)) {
            // Return the detailed response to the frontend (optional, if you want to display or redirect from frontend)
        	return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/show-declined?reference=" + reference)
                    .body(detailedResponse);

        }

        System.out.println(responseData);
        
        // Return the entire responseData map as JSON in the response
        return ResponseEntity.ok(responseData);
    }

    private Map<String, Object> fetchDetailedHitsFromShufti(String reference) {
        // Create an HTTP request to fetch detailed data using the reference from the callback
        String url = "https://api.shuftipro.com/status";  // Example API URL
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("8dfed603060c6178da6e2e942a234ddb2197fe85b5bdcf860387cb82f6d76189", "6RsGbP8O3gpYEdvcuJcZGmQS2Vf6mhMp");  // Basic Auth

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of("reference", reference), headers);
        
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        return response.getBody();
    }


    @GetMapping("/redirect")
    public void handleRedirect(@RequestBody String requestData) {
        System.out.println("Redirect received: " + requestData);
//        return ResponseEntity.ok("Redirect handled successfully");
    }
    
    @GetMapping("/amlCheckPage")
    public String getAmlCheckPage() {
        return "amlCheck";
    }
    
    @RequestMapping("/welcome")
	public String Welcome() {
    	System.out.println("Hellooo!");
		return "welcomepage";
	}
    
    @GetMapping("/result")
    public String showAMLResultPage(@RequestParam("reference") String reference, Model model) {
    	try {
	        // Fetch detailed response using the reference
	        Map<String, Object> detailedResponse = fetchDetailedHitsFromShufti(reference);
	
	        // Add basic info to the model
	        model.addAttribute("reference", detailedResponse.get("reference"));
	        model.addAttribute("browserInfo", detailedResponse.get("info.agent.useragent"));
	
	        // Get the user's name
	        Map<String, Object> nameData = (Map<String, Object>) ((Map<String, Object>) detailedResponse.get("verification_data"))
	                .get("background_checks");
	        Map<String, Object> name = (Map<String, Object>) nameData.get("name");
	        
	        model.addAttribute("firstName", name.get("first_name"));
	        model.addAttribute("lastName", name.get("last_name"));
	        model.addAttribute("dob", nameData.get("dob"));
	
	        // Extract filters for verification
	        List<String> filters = (List<String>) ((Map<String, Object>) nameData.get("aml_data")).get("filters");
	        model.addAttribute("filters", filters);
	        System.out.println("Testing 1");
	        System.out.println(filters);
	
	        // Add match score (assuming it's in "verification_result.background_checks")
	        Map<String, Object> verificationResult = (Map<String, Object>) detailedResponse.get("verification_result");
	        Integer matchScore = (Integer) verificationResult.get("background_checks");
	        model.addAttribute("matchScore", matchScore != null ? matchScore : 0); // Default to 0 if null
	        System.out.println("Match score : " + matchScore);
	        
	        Map<String, Object> info = (Map<String, Object>) detailedResponse.get("info");
	        if (info != null) {
	            Map<String, Object> geolocation = (Map<String, Object>) info.get("geolocation");
	            if (geolocation != null) {
	                String ip = (String) geolocation.get("ip");
	                model.addAttribute("ip", ip);
	                System.out.println("IP: " + ip);
	                
	                String location1 = (String) geolocation.get("city") + " " + geolocation.get("region_name");
	                model.addAttribute("location", location1);
	                System.out.println("Location: " + location1);
	            }
	        }
	        
	        String country1="";
	        model.addAttribute("country", detailedResponse.get("country") != null ? country1 : "N/A");
	        
	
	        // Get the current date
	        String currentDate = new SimpleDateFormat("MMMM dd, yyyy, hh:mm:ss a").format(new Date());
	        model.addAttribute("currentDate", currentDate);
	
	        return "resultpage";
    	} 
    	catch(Exception e){
    		return "amlRetryPage";
    	}
    }

    @GetMapping("/amldeclined")
    public String showDeclinedVerification(@RequestParam("reference") String reference, Model model) {
    	try {
        // Fetch detailed response using the reference
        Map<String, Object> detailedResponse = fetchDetailedHitsFromShufti(reference);
 
        // Add basic info to the model
        model.addAttribute("reference", detailedResponse.get("reference"));
        System.out.println("Reference: " + detailedResponse.get("reference"));
 
        model.addAttribute("event", detailedResponse.get("event"));
        System.out.println("Event: " + detailedResponse.get("event"));
 
        model.addAttribute("country", detailedResponse.get("country"));
        System.out.println("Country: " + detailedResponse.get("country"));
 
        // Extract verification data
        Map<String, Object> verificationData = (Map<String, Object>) detailedResponse.get("verification_data");
        if (verificationData != null) {
            Map<String, Object> backgroundChecks = (Map<String, Object>) verificationData.get("background_checks");
            if (backgroundChecks != null) {
                Map<String, Object> nameData = (Map<String, Object>) backgroundChecks.get("name");
 
                model.addAttribute("firstName", nameData.get("first_name"));
                System.out.println("First Name: " + nameData.get("first_name"));
 
                model.addAttribute("lastName", nameData.get("last_name"));
                System.out.println("Last Name: " + nameData.get("last_name"));
 
                model.addAttribute("dob", backgroundChecks.get("dob"));
                System.out.println("DOB: " + backgroundChecks.get("dob"));
                
                Map<String, Object> amlData = (Map<String, Object>) backgroundChecks.get("aml_data");
                List<String> filters = (List<String>) amlData.get("filters");
                model.addAttribute("filters", filters);
                System.out.println(filters);
 
                List<Map<String, Object>> detailedChecks = (List<Map<String, Object>>) amlData.get("hits");
                model.addAttribute("detailedChecks", detailedChecks);
                
                List<Map<String, Object>> hits = new ArrayList<>();
                for (Map<String, Object> hitData : detailedChecks) {
                	Map<String, Object> hit = new HashMap<>();
                    hit.put("name", hitData.get("name"));
//                    hit.put("matchPercentage", ((Double) hitData.get("score")) * 100);
                    hit.put("matchPercentage", ((Number) hitData.get("score")).doubleValue() * 100);
                    hit.put("dob", backgroundChecks.get("dob"));
                    hit.put("appearsOn", hitData.get("types"));
                    hit.put("entityType", hitData.get("entity_type"));

                    System.out.println("Appears on :" + hitData.get("types"));
                    
                    List<Map<String, Object>> sourceDetails = (List<Map<String, Object>>) hitData.get("source_details");
                    List<String> countries = new ArrayList<>();
                    if (sourceDetails != null) {
                        for (Map<String, Object> detail : sourceDetails) {
                            List<String> detailCountries = (List<String>) detail.get("countries");
                            if (detailCountries != null) {
                                countries.addAll(detailCountries);
                            }
                        }
                    }
                    
                    Object fieldsObject = hitData.get("fields");
                    if (fieldsObject instanceof Map) {
                        Map<String, List<Map<String, Object>>> fields = (Map<String, List<Map<String, Object>>>) fieldsObject;

                        // Check if fields contains "address" and "placeOf_birth" keys
                        if (fields.containsKey("Address")) {
                            // Extract address if available
                            List<Map<String, Object>> addressList = fields.get("Address");
                            if (addressList != null && !addressList.isEmpty()) {
                                String address = (String) addressList.get(0).get("value"); // Take the first value for address
                                model.addAttribute("address", address);
                                hit.put("address", address);
                                System.out.println("Address: " + address);
                            } else {
                                System.out.println("Address list is empty or null.");
                            }
                        } else {
                            System.out.println("No 'address' key found in fields.");
                        }

                        if (fields.containsKey("Place Of Birth")) {
                            // Extract place of birth if available
                            List<Map<String, Object>> placeOfBirthList = fields.get("Place Of Birth");
                            if (placeOfBirthList != null && !placeOfBirthList.isEmpty()) {
                                String placeOfBirth = (String) placeOfBirthList.get(0).get("value"); // Take the first value for place of birth
                                model.addAttribute("placeOfBirth", placeOfBirth);
                                hit.put("placeOfBirth", placeOfBirth);
                                System.out.println("Place of Birth: " + placeOfBirth);
                            } else {
                                System.out.println("Place of birth list is empty or null.");
                            }
                        } else {
                            System.out.println("No 'placeOf_birth' key found in fields.");
                        }
                        
                        List<Map<String, Object>> categoryList = fields.get("Category");
                        if (categoryList != null && !categoryList.isEmpty()) {
                            hit.put("category", categoryList.get(0).get("value"));
                        }

                        // Country and Nationality
                        List<Map<String, Object>> countryList = fields.get("Country");
                        if (countryList != null && !countryList.isEmpty()) {
                            hit.put("country", countryList.get(0).get("value"));
                        }

                        List<Map<String, Object>> nationalityList = fields.get("Nationality");
                        if (nationalityList != null && !nationalityList.isEmpty()) {
                            hit.put("nationality", nationalityList.get(0).get("value"));
                        }
                        // Notes
                        List<Map<String, Object>> notesList = fields.get("Notes");
                        if (notesList != null && !notesList.isEmpty()) {
                            hit.put("notes", notesList.get(0).get("value"));
                        }
                        
                        List<Map<String, Object>> first_name = fields.get("First Name");
                        if (notesList != null && !notesList.isEmpty()) {
                            hit.put("first_name", first_name.get(0).get("value"));
                        }
                        
                        List<Map<String, Object>> last_name = fields.get("Last Name");
                        if (notesList != null && !notesList.isEmpty()) {
                            hit.put("last_name", last_name.get(0).get("value"));
                        }
                    } 
                    
                    List<Map<String, Object>> associatesList = (List<Map<String, Object>>) hitData.get("associates");
                    if (associatesList != null && !associatesList.isEmpty()) {
                        List<String> relatives = new ArrayList<>();
                        List<String> spouseNames = new ArrayList<>();
                        
                        for (Map<String, Object> associate : associatesList) {
                            String name = (String) associate.get("name");
                            String association = (String) associate.get("association");

                            // Collect all relatives with formatted relation
                            relatives.add(name + (association != null ? " (" + association + ")" : ""));
                            
                            // Collect only "spouse" names
                            if ("spouse".equalsIgnoreCase(association)) {
                                spouseNames.add(name);
                            }
                        }
                        
                        // Add lists to hit
                        hit.put("relatives", relatives);
                        hit.put("spouseNames", spouseNames);  // List of spouse names
                    }


                    
                    if (fieldsObject instanceof Map) {
                        Map<String, List<Map<String, Object>>> fields = (Map<String, List<Map<String, Object>>>) fieldsObject;
                        
                        // Check if "Gender" is present and extract the value
                        List<Map<String, Object>> genderField = fields.get("Gender");
                        if (genderField != null && !genderField.isEmpty()) {
                            String gender = (String) genderField.get(0).get("value");
                            hit.put("gender", gender);
                            model.addAttribute("gender", gender); // Add gender to model if needed
                        }
                    }
                    
                    System.out.println("countries : "+ countries );
                    hit.put("countries", countries);
                    
                    hit.put("name", hitData.getOrDefault("name", ""));
//                    hit.put("matchPercentage", ((Double) hitData.getOrDefault("score", 0.0)) * 100);
                    hit.put("matchPercentage", ((Number) hitData.getOrDefault("score",0.0)).doubleValue() * 100);

                    List<String> alternativeNames = (List<String>) hitData.get("alternative_names");
                    hit.put("alternativeNames", alternativeNames != null ? alternativeNames : new ArrayList<>());
                    System.out.println("alternativeNames :"+alternativeNames);
                    
                    List<String> matchTypes = (List<String>) hitData.get("match_types");
                    if (matchTypes != null) {
                        hit.put("relevance", matchTypes);
                        System.out.println("relevance : " + matchTypes);
                    }
 
                    hits.add(hit);
                }
                model.addAttribute("hits", hits);
                System.out.println("Hits: " + hits);
                String hitsJson = new Gson().toJson(hits);
                System.out.println("hitsJson: " + hitsJson); // Log to verify
                model.addAttribute("hitsJson", hitsJson);
 
            }
 
        }
 
        // Add declined information to the model
        model.addAttribute("declinedReason", detailedResponse.get("declined_reason"));
        System.out.println("Declined Reason: " + detailedResponse.get("declined_reason"));
 
        model.addAttribute("declinedCodes", detailedResponse.get("declined_codes"));
        System.out.println("Declined Codes: " + detailedResponse.get("declined_codes"));
 
        // Add current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
 
        model.addAttribute("date", now.format(dateFormatter));
        model.addAttribute("time", now.format(timeFormatter));
        
        Map<String, Object> info = (Map<String, Object>) detailedResponse.get("info");
        if (info != null) {
            Map<String, Object> geolocation = (Map<String, Object>) info.get("geolocation");
            if (geolocation != null) {
                String ip = (String) geolocation.get("ip");
                model.addAttribute("ip", ip);
                System.out.println("IP: " + ip);
                
                String location1 = (String) geolocation.get("city") + " " + geolocation.get("region_name");
                model.addAttribute("location", location1);
                System.out.println("Location: " + location1);
            }
            
            Map<String, Object> geolocationAgent = (Map<String, Object>) info.get("agent");
            if (geolocationAgent != null) {
                String browser_name = (String) geolocationAgent.get("browser_name");
                model.addAttribute("browser", browser_name);
                System.out.println("Browser name: " + browser_name);
                
            }
        }
        
        if (verificationData != null) {
            Map<String, Object> backgroundChecks = (Map<String, Object>) verificationData.get("background_checks");
            if (backgroundChecks != null) {
                Map<String, Object> amlData = (Map<String, Object>) backgroundChecks.get("aml_data");
 
                // Fetch categories and sources
                List<Map<String, Object>> detailedChecks = (List<Map<String, Object>>) amlData.get("hits");
                Set<String> sources = new HashSet<>();
                Set<String> categories = new HashSet<>();
 
                if (detailedChecks != null) {
                    for (Map<String, Object> hit : detailedChecks) {
                        // Add unique sources
                        List<String> hitSources = (List<String>) hit.get("sources");
                        if (hitSources != null) {
                            sources.addAll(hitSources);
                        }
 
                        // Extract categories from source details
                        List<Map<String, Object>> sourceDetails = (List<Map<String, Object>>) hit.get("source_details");
                        if (sourceDetails != null) {
                            for (Map<String, Object> detail : sourceDetails) {
                                List<Map<String, String>> sourceCategories = (List<Map<String, String>>) detail.get("source_categories");
                                if (sourceCategories != null) {
                                    for (Map<String, String> category : sourceCategories) {
                                        categories.addAll(category.keySet());
                                    }
                                }
                            }
                        }
                    }
                    
                }
 
                model.addAttribute("sources", new ArrayList<>(sources));
//                hit.put("sources", new ArrayList<>(sources));
                model.addAttribute("categories", new ArrayList<>(categories));
//                hit.put("categories", new ArrayList<>(categories));
                System.out.println("Sources :");
                System.out.println(sources);
                System.out.println("Categories :");
                System.out.println(categories);
            }
        }
        
        return "aml-declined";
    	}
    	catch(Exception e){
    		return "amlRetryPage";
    	}
    }
    
    @GetMapping("/detailedamldeclined1")
    public String showDetailedDeclinedVerification1(@RequestParam("reference") String reference, Model model) {
        System.out.println("Fetching detailed verification for reference: " + reference);
 
        // Fetch detailed response using the reference
        Map<String, Object> detailedResponse = fetchDetailedHitsFromShufti(reference);
        System.out.println("Fetched detailed response: " + detailedResponse);
 
        // Check if the response contains the necessary data
        if (detailedResponse == null) {
            System.out.println("No data found for reference: " + reference);
            model.addAttribute("errorMessage", "No data found for the provided reference.");
            return "detailed-aml-declined"; // Return with an error message
        }
 
        // Populate basic data
        model.addAttribute("reference", detailedResponse.get("reference"));
        System.out.println("Rreference : "+reference);
        model.addAttribute("event", detailedResponse.get("event"));
        model.addAttribute("country", detailedResponse.get("country"));
        System.out.println("Country : "+detailedResponse.get("country"));
        model.addAttribute("declinedReason", detailedResponse.get("declined_reason"));
        System.out.println("DdeclinedReason : "+detailedResponse.get("declined_reason"));
        model.addAttribute("declinedCodes", detailedResponse.get("declined_codes"));
 
        // Add current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        model.addAttribute("date", now.format(dateFormatter));
        model.addAttribute("time", now.format(timeFormatter));
 
        // Extract verification data
        Map<String, Object> verificationData = (Map<String, Object>) detailedResponse.get("verification_data");
        if (verificationData != null) {
            Map<String, Object> backgroundChecks = (Map<String, Object>) verificationData.get("background_checks");
            if (backgroundChecks != null) {
            	
            	String dob = (String) backgroundChecks.get("dob");
                model.addAttribute("dob", dob); // Add to model for JSP usage
                System.out.println("DOB : "+dob);
 
                // Extracting name
                Map<String, Object> nameData = (Map<String, Object>) backgroundChecks.get("name");
                if (nameData != null) {
                    String firstName = (String) nameData.get("first_name");
                    String middleName = (String) nameData.get("middle_name");
                    String lastName = (String) nameData.get("last_name");
 
                    model.addAttribute("firstName", firstName);
                    model.addAttribute("middleName", middleName);
                    model.addAttribute("lastName", lastName);
                    
                    System.out.println("First name : "+firstName);
                    System.out.println("Last name : "+lastName);
                }
            	
                Map<String, Object> amlData = (Map<String, Object>) backgroundChecks.get("aml_data");
                if (amlData != null) {
                    List<Map<String, Object>> detailedChecks = (List<Map<String, Object>>) amlData.get("hits");
                    if (detailedChecks != null) {
                        List<Map<String, Object>> hits = new ArrayList<>();
                        for (Map<String, Object> hitData : detailedChecks) {
                            Map<String, Object> hit = new HashMap<>();
                            hit.put("name", hitData.getOrDefault("name", ""));
//                            hit.put("matchPercentage", ((Double) hitData.getOrDefault("score", 0.0)) * 100);
                            hit.put("matchPercentage", ((Number) hitData.getOrDefault("score", 0.0)).doubleValue() * 100);

 
                            List<String> alternativeNames = (List<String>) hitData.get("alternative_names");
                            hit.put("alternativeNames", alternativeNames != null ? alternativeNames : new ArrayList<>());
                            System.out.println("alternativeNames :"+alternativeNames);
                            // Safely extract fields as a list
                            Object fieldsObj = hitData.get("fields");
                            if (fieldsObj instanceof List) {
                                List<Map<String, Object>> fieldsList = (List<Map<String, Object>>) fieldsObj;
                                System.out.println("FieldList : "+fieldsList);
                                
                                // Use the new extraction method
                                hit.put("category", extractFieldValue(fieldsList, "Category"));
                                System.out.println("category"+extractFieldValue(fieldsList, "Category"));
                                hit.put("entityType", extractFieldValue(fieldsList, "Entity Type"));
                                System.out.println("entityType : "+extractFieldValue(fieldsList, "Entity Type"));
                                hit.put("gender", extractFieldValue(fieldsList, "Gender"));
                                System.out.println("Gender : "+extractFieldValue(fieldsList, "Gender"));
                                hit.put("nationality", extractFieldValue(fieldsList, "Nationality"));
                                System.out.println("Nationality : "+extractFieldValue(fieldsList, "Nationality"));
                            } else {
                                System.out.println("Expected 'fields' to be a List, but found: " + fieldsObj);
                            }
 
                            // Extract source details
                            Object sourceDetailsObj = hitData.get("source_details");
                            List<String> countries = new ArrayList<>();
                            List<String> descriptions = new ArrayList<>();

                            if (sourceDetailsObj instanceof List) {
                                List<Map<String, Object>> sourceDetails = (List<Map<String, Object>>) sourceDetailsObj;
                                for (Map<String, Object> detail : sourceDetails) {
                                    // Extract countries
                                    List<String> detailCountries = (List<String>) detail.get("countries");
                                    if (detailCountries != null) {
                                        countries.addAll(detailCountries);
                                    }
                                    
                                    // Extract description
                                    String description = (String) detail.get("description");
                                    if (description != null) {
                                        descriptions.add(description);
                                    }
                                }
                            } else {
                                System.out.println("Expected 'source_details' to be a List, but found: " + sourceDetailsObj);
                            }

                            // Add the extracted countries and descriptions to the hit map
                            hit.put("countries", countries);
                            hit.put("criminalCharge", descriptions);

                            // Extract match types
                            List<String> matchTypes = (List<String>) hitData.get("match_types");
                            hit.put("relevance", matchTypes != null ? matchTypes : new ArrayList<>());
 
                            hits.add(hit);
                        }
                        model.addAttribute("hits", hits);
                        String hitsJson = new Gson().toJson(hits);
                        System.out.println("hitsJson: " + hitsJson); // Log to verify
                        model.addAttribute("hitsJson", hitsJson);
 
                       // model.addAttribute("hitsJson", new Gson().toJson(hits)); // Use Gson or similar library to convert to JSON
 
                        System.out.println("Processed " + hits.size() + " hits.");
                    } else {
                        System.out.println("No detailed checks found.");
                    }
                } else {
                    System.out.println("amlData is null.");
                }
            } else {
                System.out.println("backgroundChecks is null.");
            }
        } else {
            System.out.println("verificationData is null.");
        }
 
        // Populate additional details like geolocation and agent info
        Map<String, Object> info = (Map<String, Object>) detailedResponse.get("info");
        if (info != null) {
            Map<String, Object> geolocation = (Map<String, Object>) info.get("geolocation");
            if (geolocation != null) {
                model.addAttribute("ip", geolocation.getOrDefault("ip", ""));
                String location = geolocation.getOrDefault("city", "") + " " + geolocation.getOrDefault("region_name", "");
                model.addAttribute("location", location);
            }
 
            Map<String, Object> geolocationAgent = (Map<String, Object>) info.get("agent");
            if (geolocationAgent != null) {
                model.addAttribute("browser", geolocationAgent.getOrDefault("browser_name", ""));
            }
        }
 
        return "detailed-aml-declined"; // Ensure this matches your JSP name
    }
 
    // Modified extractFieldValue method to handle List of Maps
    private String extractFieldValue(List<Map<String, Object>> fieldsList, String key) {
        for (Map<String, Object> field : fieldsList) {
            if (field.containsKey(key)) {
                return (String) field.get(key); // Assuming each field is a Map with key-value pairs
            }
        }
        return "";
    }
    
// Method to extract address from fields
    private String extractAddress(Map<String, Object> fields) {
        // Check if "Address" exists in the fields
        if (fields.containsKey("Address")) {
            Object addressObj = fields.get("Address");
            if (addressObj instanceof List) {
                List<Map<String, Object>> addressList = (List<Map<String, Object>>) addressObj;
                if (!addressList.isEmpty()) {
                    // Assuming we want the value from the first entry
                    Map<String, Object> addressEntry = addressList.get(0);
                    Object valueObj = addressEntry.get("value");
                    if (valueObj instanceof String) {
                        return (String) valueObj; // Return the address string
                    }
                }
            }
        }
        return ""; // Return empty string if address is not found
    }

    
    @GetMapping("/configureFilter")
    public String configureFilter(Model model) {

    	System.out.println("Logger1");
        return "configure"; 
    }







}

