package org.example;

import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        Token.getAuthCode("user-library-read");
    }
}