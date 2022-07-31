package org.example;

import java.io.IOException;
import java.net.Socket;

/**
 * Client
 */
public class App {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {


        }catch (IOException e){
            System.out.println("Client Error : " + e.getMessage());
        }
    }
}
