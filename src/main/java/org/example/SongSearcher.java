package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SongSearcher {
    public static ArrayList<String> getSongYTLinks(ArrayList<String> names) throws IOException, ParseException {
        ArrayList<String> urls = new ArrayList<>();
        String baseUrl = "https://www.googleapis.com/youtube/v3/search";
        String params = "?part=snippet&maxResults=10&&type=video&key="+System.getenv("YOUTUBE_API_KEY");
        for (String name:names){
            String url = baseUrl+params+"&q="+ URLEncoder.encode(name, StandardCharsets.UTF_8);
//            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
//            System.out.println(responseCode);
//            System.out.println(responseMessage);
//            System.out.println(response);

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response.toString());
            JSONArray items = (JSONArray) json.get("items");
            if (items.size()>0){
                JSONObject item = (JSONObject) items.get(0);
                JSONObject ids = (JSONObject) item.get("id");
                String videoId = ids.get("videoId").toString();
                String link = "https://www.youtube.com/watch?v="+videoId;
                urls.add(link);
                System.out.println(link);
            }else {
                System.out.println("No result found for song: "+name);
            }

        }
        return urls;
    }
}
