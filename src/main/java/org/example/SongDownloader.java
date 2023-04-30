package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SongDownloader {
    public static Song generateDownloadLink(String link, String dir) throws ParseException, IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://ytstream-download-youtube-videos.p.rapidapi.com/dl?id="+extractVideoId(link)))
                .header("content-type", "application/octet-stream")
                .header("X-RapidAPI-Key", "bb834ebd60mshbda2ab24352bae8p137102jsne7dad0e3970e")
                .header("X-RapidAPI-Host", "ytstream-download-youtube-videos.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        Song song = new Song();
        JSONObject json = (JSONObject) new JSONParser().parse(responseBody);
        String fileName = (String) json.get("title");
        JSONArray formats = (JSONArray) json.get("adaptiveFormats");
        for (Object format : formats) {
            JSONObject formatData = (JSONObject) format;
            if (formatData.get("audioQuality") != null) {
                String audioQuality = formatData.get("audioQuality").toString();
                if (audioQuality != null) {
                    if (audioQuality.equals("AUDIO_QUALITY_MEDIUM")) {
                        String downloadUrl = formatData.get("url").toString();
                        song.setDownloadLink(downloadUrl);
                        song.setName(fileName);
                        break;
                    }
                }
            }
        }
        return song;
    }

    public static void downloadAudio(String fileName,String fileUrl,String dir) throws IOException, InterruptedException {

        URL url = new URL(fileUrl);
        String saveDir = dir+sanitizeFilename(fileName)+".mp3";
        System.out.println(saveDir);
        InputStream inputStream = url.openStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        OutputStream outputStream = new FileOutputStream(saveDir);

        long startTime = System.nanoTime();
        long totalBytesRead = 0;
        long fileSize = url.openConnection().getContentLengthLong();
        int progressInterval = 102400; // Print progress every 100 KB downloaded

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;

            if (totalBytesRead % progressInterval == 0) {
                long currentTime = System.nanoTime();
                long elapsedTime = currentTime - startTime;
                double downloadSpeed = (totalBytesRead / 1048576.0) / (elapsedTime / 1000000000.0);
                double progress = (totalBytesRead / (double) fileSize) * 100.0;
                String progressText = String.format("Downloaded %.2f%% (%.2f MB / %.2f MB) at %.2f MB/s", progress, totalBytesRead / 1048576.0, fileSize / 1048576.0, downloadSpeed);
                System.out.print("\r" + progressText);
            }
        }

        inputStream.close();
        outputStream.close();

        System.out.println("\nFile downloaded successfully.");


    }

    public static void downloadSongs(ArrayList<String> youtubeLinks,String dir) throws ParseException, IOException, InterruptedException {
        for (String youtubeLink:youtubeLinks){
            Song song = generateDownloadLink(youtubeLink,dir);
            try {
                downloadAudio(song.getName(), song.getDownloadLink(), dir);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public static String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9\\.\\s]", "_");
    }

    public static String extractVideoId(String videoLink) {
        String videoId = null;

        // Define the pattern to match YouTube video IDs
        Pattern pattern = Pattern.compile("(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*");

        // Use the pattern to search for the video ID in the link
        Matcher matcher = pattern.matcher(videoLink);

        // If a match is found, extract the video ID
        if (matcher.find()) {
            videoId = matcher.group();
        }

        return videoId;
    }


}
