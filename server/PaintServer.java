package com.unn;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import org.json.*;


public class PaintServer {
    private static final int PORT = 2551;

    private static ArrayList<PrintWriter> writers = new ArrayList<PrintWriter>();
    private static ArrayList<String> shapes = new ArrayList<String>();

    public static void main(String[] args) throws Exception {
        System.out.println("The server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                writers.add(out);
                System.out.println("User connected!");

                String json = "{\"color\":\"-16777216\",\"x\":-10,\"y\":-10,\"type\":\"point\"}";
                writers.get(writers.size()-1).println(json);

                if (!shapes.isEmpty())
                {
                    for (String shape : shapes)

                        writers.get(writers.size()-1).println(shape);
                }

                while (true) {
                    json = in.readLine();

                    Object ob = json;
                    JSONObject object = new JSONObject((String)ob);
                    if (object.optString("type").equals("clear")) {
                        shapes.clear();

                        for (PrintWriter writer : writers) {
                            writer.println(json);
                            writer.flush();
                        }

                        json = "{\"color\":\"-16777216\",\"x\":-10,\"y\":-10,\"type\":\"point\"}";

                        for (PrintWriter writer : writers) {
                            writer.println(json);
                            writer.flush();
                        }
                    }
                    else {
                        shapes.add(json);
                        for (PrintWriter writer : writers) {
                            writer.println(json);
                            writer.flush();
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("User disconnected!");
            } finally {
                if (out != null) {
                    writers.remove(out);
                    if (in != null) try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}