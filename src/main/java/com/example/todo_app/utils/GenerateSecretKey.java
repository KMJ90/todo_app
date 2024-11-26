package com.example.todo_app.utils;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;

public class GenerateSecretKey {
    public static void main(String[] args) {
        String base64Key = Base64.getEncoder()
                .encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
        System.out.println("Generated Base64 Key: " + base64Key);
    }
}
