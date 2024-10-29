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

@Controller  // Change from @RestController to @Controller
@RequestMapping("/api/aml")
public class AMLController {

    @Autowired
    private AMLService amlService;

    // Constructor injection
    public AMLController(AMLService amlService) {
        this.amlService = amlService;
    }
    
    
    @PostMapping("/check")
    public ResponseEntity<String> performAMLCheck(@RequestBody String requestData) throws Exception {
    	System.out.println("AML check request received: " + requestData);
        
        // Call the AML check service (assuming this method returns the response as a String)
        String result = amlService.performAMLCheck();
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

            // Optional: Add other details like name, dob, etc., if needed
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
            String declinedReason = (String) resultMap.get("declined_reason");
            List<String> declinedCodes = (List<String>) resultMap.get("declined_codes");
            
            responseMap.put("declined_reason", declinedReason);
            responseMap.put("declined_codes", declinedCodes);
        }

        // Convert the response map to JSON and return
        String jsonResponse = objectMapper.writeValueAsString(responseMap);
        return ResponseEntity.ok(jsonResponse);
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
//        System.out.println("Detailed response with status API:");
//        System.out.println(detailedResponse);

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

//         Regular handling for other cases (verification accepted or other events)
//        responseData.put("reference", reference);
//        responseData.put("event", event);

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

//        System.out.println("111");
//        System.out.println(response);
        return response.getBody();
    }


    @GetMapping("/redirect")
    public void handleRedirect(@RequestBody String requestData) {
        System.out.println("Redirect received: " + requestData);
//        return ResponseEntity.ok("Redirect handled successfully");
    }
    
    @GetMapping("/amlCheckPage")
    public String getAmlCheckPage() {
        return "amlCheck";  // Return the JSP page name without .jsp extension
    }
    
    @RequestMapping("/welcome")
	public String Welcome() {
    	System.out.println("Hellooo!");
		return "welcomepage";
	}
    
    @GetMapping("/result")
    public String showAMLResultPage(@RequestParam("reference") String reference, Model model) {
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

        return "resultpage"; // Name of the JSP page
    }


    
    @GetMapping("/amldeclined")
    public String showDeclinedVerification(@RequestParam("reference") String reference, Model model) {
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
                    hit.put("matchPercentage", ((Double) hitData.get("score")) * 100);
                    hit.put("dob", backgroundChecks.get("dob"));
                    hit.put("appearsOn", hitData.get("types"));
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
                    System.out.println("countries : "+ countries );
                    hit.put("countries", countries);
                    
                    Map<String, Object> relevanceData = (Map<String, Object>) hitData.get("relevance");
                    if (relevanceData != null) {
                        List<String> matchTypes = (List<String>) relevanceData.get("match_types");
                        hit.put("relevance", matchTypes);
                        
                        System.out.println("relevance : " +  matchTypes);
                    }
                   

                    hits.add(hit);
                }
                model.addAttribute("hits", hits);
                System.out.println("Hits: " + hits);

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
                model.addAttribute("categories", new ArrayList<>(categories));
                System.out.println("Sources :");
                System.out.println(sources);
                System.out.println("Categories :");
                System.out.println(categories);
            }
        }
        
        return "aml-declined"; // Name of the JSP page
    }

}

