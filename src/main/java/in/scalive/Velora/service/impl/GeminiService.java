package in.scalive.Velora.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.scalive.Velora.dto.request.ProductSearchFilterDTO;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent}")
    private String geminiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    public GeminiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ProductSearchFilterDTO extractFilters(String userPrompt) {

    	String systemInstruction = """
    You are a smart search filter extractor for a gift shop e-commerce app.
    The app ONLY sells gifts — so never put "gift" or "gifts" in any field.
    
    Your job:
    1. Extract search filters from the user's message.
    2. Generate a short, lightly sarcastic or entertaining message based on user's intent.
    
    FIELD RULES:
    - "name"     → specific product name (e.g., "teddy bear", "mug"). Null if generic.
    - "category" → occasion or gift type (e.g., "birthday", "wedding", "anniversary",
                   "for kids", "luxury", "personalized"). NOT the word "gift/gifts".
    - "brand"    → brand name if mentioned. Null otherwise.
    - "minPrice" → minimum budget (number). Null if not mentioned.
    - "maxPrice" → maximum budget (number). Null if not mentioned.
    - "message"  → max 15 words, friendly + lightly sarcastic, never offensive.
    
    STRICT RULES:
    - Return ONLY valid JSON. No markdown, no explanation, no extra text.
    - If user says "birthday gifts" → category: "birthday", name: null
    - If user says "teddy bear"     → name: "teddy bear", category: null
    - If user says "cheap gifts under 500" → maxPrice: 500, category: null
    
    JSON format:
    {
      "name": "string or null",
      "category": "string or null",
      "brand": "string or null",
      "minPrice": number or null,
      "maxPrice": number or null,
      "message": "short entertaining message"
    }
    """;

        Map<String, Object> requestBody = Map.of(
            "system_instruction", Map.of(
                "parts", List.of(Map.of("text", systemInstruction))
            ),
            "contents", List.of(
                Map.of("parts", List.of(Map.of("text", userPrompt)))
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 🔴 NEW: retry config
        int maxRetries = 3;
        int attempt = 0;
        long delay = 2000; // 2 sec

        // 🔴 NEW: retry loop start
        while (attempt < maxRetries) {
            try {

                // 🔴 CHANGED: API call ab loop ke andar
                ResponseEntity<String> response = restTemplate.postForEntity(
                    geminiUrl + "?key=" + apiKey,
                    entity,
                    String.class
                );

                String rawResponse = response.getBody();

                // ---- SAME parsing logic ----
                Map<String, Object> responseMap = objectMapper.readValue(rawResponse, Map.class);
                List candidates = (List) responseMap.get("candidates");
                Map firstCandidate = (Map) candidates.get(0);
                Map content = (Map) firstCandidate.get("content");
                List parts = (List) content.get("parts");
                Map firstPart = (Map) parts.get(0);
                String jsonText = ((String) firstPart.get("text")).trim();
                System.out.println("Gemini RAW: " + jsonText);

                if (jsonText.startsWith("```")) {
                    jsonText = jsonText.replaceAll("```json", "")
                                       .replaceAll("```", "")
                                       .trim();
                }

                return objectMapper.readValue(jsonText, ProductSearchFilterDTO.class);

            } catch (Exception e) {

                // 🔴 NEW: retry handling
                attempt++;

                boolean is503 = e.getMessage() != null && e.getMessage().contains("503");

                if (attempt >= maxRetries) {
                    System.err.println("Gemini failed after retries: " + e.getMessage());
                    return new ProductSearchFilterDTO(); // 🔴 fallback
                }

                System.err.println("Retry " + attempt + " due to error: " + e.getMessage());

                try {
                    // 🔴 NEW: exponential backoff
                    Thread.sleep(delay);
                    delay *= 2; // 2s → 4s → 8s
                } catch (InterruptedException ie) {
                    // 🔴 NEW: interrupt handling
                    Thread.currentThread().interrupt();
                }
            }
        }

        // 🔴 NEW: safety fallback
        return new ProductSearchFilterDTO();
    }
}
