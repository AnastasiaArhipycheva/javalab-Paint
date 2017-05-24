package com.unn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class PaintServer {
    private static final int PORT =2551;

    private static HashSet<ObjectOutputStream> writers = new HashSet<ObjectOutputStream>();

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
        private ObjectInputStream in;
        private ObjectOutputStream out;


        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new ObjectOutputStream(this.socket.getOutputStream());
                in = new ObjectInputStream(this.socket.getInputStream());

                writers.add(out);
                while (!Thread.currentThread().isInterrupted()) {
                    String json = in.readUTF();
                    System.out.println(json);
                    for (ObjectOutputStream writer : writers) {
                        writer.writeUTF(json);
                        writer.flush();
                    }
                   // in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
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
