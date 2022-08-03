package org.example;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private  List<ConnectionHandler> connections;
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private boolean done;

    public Server() {
        connections = new ArrayList<>();
        done = false;
    }

    public void startServer() {
        while (!done) {
            try (ServerSocket serverSocket = new ServerSocket(5000)) {
                this.serverSocket = serverSocket;
                pool = Executors.newCachedThreadPool();
                Socket client = serverSocket.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            } catch (Exception e) {
                shutDownServer();
            }
        }
    }

    public void shutDownServer() {
        try {
            done = true;
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // ignore;
        }
        for (ConnectionHandler ch : connections) {
            ch.shutDownConnection();
        }
    }

    class ConnectionHandler implements Runnable {
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (
                    PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            ) {
                out = writer;
                in = reader;
                String message;
                out.println("Enter your Nickname : ");
                nickname = in.readLine();
                System.out.println(nickname + " joined successfully");
                out.println("connected!, \nInstructions \n/quit -> To leave chat; \n/nick NewNickname -> To Change you nickname");
                broadcast(nickname + " joined the chat");
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/quit")) {
                        System.out.println(nickname + " left the chat");
                        broadcast(nickname + " left the chat");
                        shutDownConnection();
                    } else if (message.startsWith("/nick")) {
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            System.out.println(nickname + " renamed themselves to " + messageSplit[1]);
                            broadcast(nickname + " renamed themselves to " + messageSplit[1]);
                            nickname = messageSplit[1];
                            out.println("Successfully changed nickname to " + nickname);
                        } else {
                            out.println("No nickname was provided ");
                        }
                    } else {
                        broadcast(nickname + ": " + message);
                    }
                }
            } catch (IOException e) {
                shutDownConnection();
            }
        }

        /* HELPERS */
        public void sendMessage(String message) {
            out.println(message);
        }

        public void broadcast(String message) {
            for (ConnectionHandler ch : connections) {
                if (!ch.nickname.equals(nickname)) {
                    ch.sendMessage(message);
                }
            }

        }

        public void shutDownConnection() {
            try {
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}


