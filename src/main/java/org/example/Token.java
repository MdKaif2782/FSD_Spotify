package org.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;

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

    public static String getScopedToken() throws URISyntaxException, IOException, ParseException {
        //Read Json
        JSONParser parser = new JSONParser();

        FileReader fileReader = new FileReader("src/main/java/org/example/scoped_token.json");

        // read file and parse JSON object
        Object obj = parser.parse(fileReader);
        JSONObject scopedToken = (JSONObject) obj;

        // extract values from JSON object
        String accessToken = (String) scopedToken.get("access_token");
        String refreshToken = (String) scopedToken.get("refresh_token");
        Long timestamp = (Long) scopedToken.get("timestamp");

        if (System.currentTimeMillis()<(timestamp+3600*1000)) return accessToken;


        String client_id = System.getenv("CLIENT_ID");
        String client_secret = System.getenv("CLIENT_SECRET");
        String redirect_uri = "https://github.com/MdKaif2782";
        String scope = "user-library-read";
        String state = UUID.randomUUID().toString();;
        String response_type = "code";
        String authorize_url = "https://accounts.spotify.com/authorize";
        String token_url = "https://accounts.spotify.com/api/token";

        // Step 1: Send user to authorize URL to grant access
        String url = authorize_url + "?client_id=" + client_id + "&redirect_uri=" + redirect_uri
                + "&scope=" + scope + "&state=" + state + "&response_type=" + response_type;
        System.out.println("Please go to the following URL to grant access: ");
        System.out.println(url);

        String authorization_code = null;
        System.out.println("Paste the url you are redirected to");
        Scanner scanner = new Scanner(System.in);
        String inputUrl = scanner.nextLine();
        String splits[] = inputUrl.split("code=");
        String split2[] = splits[splits.length-1].split("&");
        authorization_code = split2[0];
        System.out.println("Authorization code == "+authorization_code);


        // Step 3: Exchange authorization code for access token and refresh token
        String encoded_client_id_secret = Base64.getEncoder().encodeToString((client_id + ":"+client_secret).getBytes());
        URL tokenEndpoint = new URL(token_url);
        HttpURLConnection conn = (HttpURLConnection) tokenEndpoint.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Basic " + encoded_client_id_secret);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes("grant_type=authorization_code&code=" + authorization_code + "&redirect_uri=" + redirect_uri);
        out.flush();
        out.close();

        // Step 4: Read and print access token and refresh token from response
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        String access_token = response.toString().split("\"access_token\":\"")[1].split("\",\"token_type\"")[0];
        String refresh_token = response.toString().split("\"refresh_token\":\"")[1].split("\",\"scope\"")[0];
        System.out.println("\n\n\nAccess Token: " + access_token);
        System.out.println("Refresh Token: " + refresh_token);

        //save to json
        // create JSON object
        JSONObject tokens = new JSONObject();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        tokens.put("timestamp", System.currentTimeMillis());

        // write JSON object to file
        try (FileWriter file = new FileWriter("src/main/java/org/example/scoped_token.json")) {
            file.write(tokens.toJSONString());
            System.out.println("JSON object written to file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
