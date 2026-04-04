package com.example.springbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

@Service
public class GoogleTokenValidator {

    @Value("${google.client.id:YOUR_GOOGLE_CLIENT_ID}")
    private String googleClientId;

    public Map<String, Object> validateToken(String idToken) throws Exception {
        try {
            // Split JWT token into 3 parts
            String[] parts = idToken.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid token format");
            }

            // Decode the payload (second part)
            String payload = parts[1];
            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));

            // Parse JSON payload
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.readValue(decodedPayload, new TypeReference<Map<String, Object>>() {
            });

            // Verify audience (client ID)
            Object aud = claims.get("aud");
            if (aud == null || !aud.toString().equals(googleClientId)) {
                throw new IllegalArgumentException("Invalid audience. Expected: " + googleClientId + ", got: " + aud);
            }

            return claims;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("Invalid Google ID Token: " + e.getMessage(), e);
        }
    }
}
