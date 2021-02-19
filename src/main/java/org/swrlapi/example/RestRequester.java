package org.swrlapi.example;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class RestRequester {
    public static void main(String[] args) throws IOException {
        JsonRequester requester = new JsonRequester();
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        server.createContext("/api/check", (exchange -> {
            String response = requester.response(exchange.getRequestBody().toString());
            Headers headers = new Headers();
            headers.set("Content-Type", "application/json");
            exchange.getResponseHeaders().putAll(headers);
            exchange.sendResponseHeaders(200, response.length());;

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.flush();
            os.close();
            exchange.close();
        }));

        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
