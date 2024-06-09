package com.example.sbbackend.helpers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
public class Utils {

    public static String toSlug(String input) {
        // Remove accents and normalize the string
        String slug = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Remove non-ASCII characters
        slug = slug.replaceAll("[^\\p{ASCII}]", "");
        // Replace spaces and other characters with hyphens
        slug = slug.replaceAll("[\\s+]", "-");
        // Remove any characters that are not alphanumeric or hyphens
        slug = slug.replaceAll("[^a-zA-Z0-9-]", "");
        // Convert to lowercase
        slug = slug.toLowerCase();
        return slug;
    }

    public static String getCookieValue(HttpServletRequest request) {
        // Get all cookies from the request
        Cookie[] cookies = request.getCookies();

        // Check if cookies exist
        if (cookies != null) {
            // Loop through each cookie
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName());
                // Check if the current cookie's name matches the desired name
                if (cookie.getName().equals("token")) {
                    // Return the value of the cookie
                    return cookie.getValue();
                }
            }
        }

        // Return null if the cookie with the specified name is not found
        return null;
    }

}
