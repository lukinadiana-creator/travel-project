package io.github.lukinadiana_creator.travel_planner.hotel;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Component
public class HotelbedsSignature {

    public String generate(String apiKey, String secret) {
        long timestamp = Instant.now().getEpochSecond();
        String value = apiKey + secret + timestamp;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("SHA-256 algorithm is not available", e);
        }
    }
}
