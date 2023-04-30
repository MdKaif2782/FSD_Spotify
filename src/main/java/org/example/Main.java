package org.example;




import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URISyntaxException;
import java.util.ArrayList;

import com.google.gson.Gson;
import org.json.simple.parser.ParseException;

public class Main {


    public static void main(String[] args) throws IOException, ParseException, InterruptedException, URISyntaxException {
        String url = "https://www.youtube.com/watch?v=ziU36CDT1wU";
        String dir = "C:\\Users\\Md Kaif Ibn Zaman\\Downloads\\Spotify Songs\\Favourites\\";
        ArrayList<String> links = SongSearcher.getSongYTLinks(Songs.getSongList());
        System.out.println(links.size());
         SongDownloader.downloadSongs(links,dir);
    }
}