package org.example;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException, URISyntaxException {
        Songs.getList();
    }
}