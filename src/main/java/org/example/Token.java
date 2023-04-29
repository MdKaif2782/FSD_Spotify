package org.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Token {
    public static String getAccessToken() throws IOException, ParseException {
        String token = null;
        // Replace these with your actual client ID and secret
        String clientId = System.getenv("CLIENT_ID");
        String clientSecret = System.getenv("CLIENT_SECRET");

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
            System.out.println("\n\n"+responseBody+"\n\n");
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(responseBody);
            token = json.get("access_token").toString();
        }

        // Disconnect the connection
        connection.disconnect();
        return token;
    }
    public static String getScopedToken(String scope) throws IOException, ParseException {

        return null;
    }
    public static String getAuthCode(String scope){
        String baseUrl = "https://accounts.spotify.com/authorize";
        String clientId = System.getenv("CLIENT_ID");
        String response_type = "code";
        String redirect_uri = "https://github.com/MdKaif2782";
        String url = baseUrl+"?"
                    +"client_id="+clientId
                    +"&response_type="+response_type
                    +"&redirect_uri="+redirect_uri
                    +"&scope="+scope;
        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        // Create a new instance of the ChromeDriver
        WebDriver driver = new ChromeDriver();

        // Navigate to the Spotify login page
        driver.get(url);

        // Find the username and password fields and input your credentials
        WebElement usernameField = driver.findElement(By.id("login-username"));
        usernameField.sendKeys("your_username");
        WebElement passwordField = driver.findElement(By.id("login-password"));
        passwordField.sendKeys("your_password");

        // Click the login button
        WebElement loginButton = driver.findElement(By.id("login-button"));
        loginButton.click();

        // Wait for the page to load and retrieve the redirected URL
        String redirectedUrl = driver.getCurrentUrl();

        // Print the redirected URL to the console
        System.out.println("Redirected URL: " + redirectedUrl);

        // Close the browser window
        driver.quit();
        return null;
    }
}
