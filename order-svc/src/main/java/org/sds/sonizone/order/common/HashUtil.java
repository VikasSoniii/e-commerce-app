package org.sds.sonizone.order.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.Base64;

public class HashUtil {

    public static String generateIdempotencyKey(String customerName, String product, int quantity, LocalDate orderDate) {
        String input = customerName + "|" + product + "|" + quantity + "|" + orderDate;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash); // compact and safe
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate idempotency key", e);
        }
    }
}