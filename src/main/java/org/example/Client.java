package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;
    private String serverIPAddress;

    public Client(String serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
    }

    public void startClient() {
        try (Socket socket = new Socket(serverIPAddress, 5000)) {
            client = socket;
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inputHandler =  new InputHandler();
            Thread t = new Thread(inputHandler);
             t.start();
             String inMessage;
             while ((inMessage = in.readLine()) != null){
                 System.out.println(inMessage);
             }
        } catch (IOException e) {
            shutDown();
        }
    }

    public void shutDown() {
        done = true;
        try {
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            //do nothing
        }
    }

    class InputHandler implements Runnable{
        BufferedReader inReader;
        @Override
        public void run() {
            try(BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in))){
                this.inReader = inReader;
             while (!done){
                 String message = this.inReader.readLine();
                 if (message.equals("/quit")){
                     shutDown();
                 }else {
                     out.println(message);
                 }
             }
            }catch (IOException e) {
                shutDown();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AsciiArt.displayArt();
        System.out.println("Enter the IpAddress of the Server");
        Scanner scanner = new Scanner(System.in);
        String ipAddress = scanner.next();
        Client client = new Client(ipAddress);
        client.startClient();
    }
}
