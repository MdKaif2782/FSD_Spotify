package org.example;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Songs {
    public static ArrayList<String> getSongList() throws IOException, ParseException, URISyntaxException {
        ArrayList<String> songs = new ArrayList<>();
        String accessToken = Token.getScopedToken();
        // Set up the connection to the Spotify API endpoint
        URL url = new URL("https://api.spotify.com/v1/me/tracks?market=BD&limit=50&offset=0");
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

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(responseBody.toString());
        JSONArray items = (JSONArray) json.get("items");
        for (int i=0;i<items.size();i++){
            JSONObject item = (JSONObject) items.get(i);
            JSONObject track = (JSONObject) item.get("track");
            String name = track.get("name").toString();
            System.out.println((i+1)+". "+name);
            songs.add(name);
        }
        Gson gson = new Gson();
        String writeJson = gson.toJson(songs);

        // Write JSON string to file
        try {
            FileWriter fileWriter = new FileWriter("output.json");
            fileWriter.write(writeJson);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return songs;
    }

}
