package org.swrlapi.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

class ServerThread extends Thread {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                JsonRequester requester = new JsonRequester();
                out.write(requester.response(in.readLine()) + "\n");
                out.flush();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

public class SocketRequester {
    public static final int PORT = 16374;
    public static LinkedList<ServerThread> serverList = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new ServerThread(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}
