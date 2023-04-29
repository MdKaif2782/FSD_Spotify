package org.example;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Songs {
    public static ArrayList<String> getList() throws IOException, ParseException {
        ArrayList<String> songs = new ArrayList<>();
        String accessToken = Token.getAccessToken();
        // Set up the connection to the Spotify API endpoint
        URL url = new URL("https://api.spotify.com/v1/me/top/tracks?time_range=medium_term&limit=100");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        // Read the response from the server
        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuilder responseBody = new StringBuilder();
        while (scanner.hasNext()) {
            responseBody.append(scanner.next());
        }
        scanner.close();
        System.out.println(responseBody);
        return songs;
    }
}
