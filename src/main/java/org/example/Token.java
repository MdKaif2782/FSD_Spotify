package org.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Token {
    public static String getAccessToken() throws IOException, ParseException {
        String token = null;
        // Replace these with your actual client ID and secret
        String clientId = "2205258a623f4954b02032029fa7e2b2";
        String clientSecret = "fc8cfbe5c95f4d3bb9bd03de542819a0";

        // Encode the client ID and secret as a Base64-encoded string
        String authHeader = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        // Create the POST request with the required headers and request body
        URL url = new URL("https://accounts.spotify.com/api/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Basic " + authHeader);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        // Set the request body to grant_type=client_credentials
        String requestBody = "grant_type=client_credentials";
        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(requestBodyBytes, 0, requestBodyBytes.length);
        }

        // Read the response from the server
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            // Extract the access token from the response body
            String responseBody = responseBuilder.toString();
            System.out.println(responseBody);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(responseBody);
            String accessToken = json.get("access_token").toString();
            System.out.println("\n\nAccess token: " + accessToken);
            token = accessToken;
        }

        // Disconnect the connection
        connection.disconnect();

        return token;
    }
}