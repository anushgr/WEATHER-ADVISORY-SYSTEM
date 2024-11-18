package com.weatherapp.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class GeminiService {
    // The correct endpoint for Gemini API
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    private static final String API_KEY = ""; //give your gemini api key

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String getExplanation(String weatherDescription) {
        try {
            // Create the request body according to Gemini API specifications
            ObjectNode requestBody = objectMapper.createObjectNode();
            
            // Create contents array
            ArrayNode contents = requestBody.putArray("contents");
            ObjectNode content = objectMapper.createObjectNode();
            
            // Create the parts array
            ArrayNode parts = objectMapper.createArrayNode();
            ObjectNode textPart = objectMapper.createObjectNode();
            textPart.put("text", "i am giving you the weather data , can you give description of what clothes to wear according to the weather data, give in brief think and give " + weatherDescription);
            parts.add(textPart);
            
            // Add parts to content
            content.put("role", "user");
            content.set("parts", parts);
            
            // Add content to contents array
            contents.add(content);

            // Add generation config
            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("topP", 0.8);
            generationConfig.put("topK", 40);
            requestBody.set("generationConfig", generationConfig);

            String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

            // Create HTTP request with API key as query parameter
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "?key=" + API_KEY))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            // Send request and get response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse response
            var responseJson = objectMapper.readTree(response.body());

            // Extract the generated text from the response
            if (responseJson.has("candidates") && 
                responseJson.get("candidates").size() > 0 && 
                responseJson.get("candidates").get(0).has("content") &&
                responseJson.get("candidates").get(0).get("content").has("parts") &&
                responseJson.get("candidates").get(0).get("content").get("parts").size() > 0) {
                
                return responseJson
                    .get("candidates")
                    .get(0)
                    .get("content")
                    .get("parts")
                    .get(0)
                    .get("text")
                    .asText();
            }

            return "No explanation generated.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error in getting explanation: " + e.getMessage();
        }
    }
}
