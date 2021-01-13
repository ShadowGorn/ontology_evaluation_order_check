package org.swrlapi.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleRequester {
    public static void main(String[] args) throws IOException {
        while(true) {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            JsonRequester requester = new JsonRequester();
            System.out.println(requester.response(in.readLine()));
        }
    }
}
