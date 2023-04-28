package org.example;

import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        String token = Token.getAccessToken();
        System.out.println(token);
    }
}