package com.ppd.client;

public class MainClient {
    public static void main(String[] args) {
        int noClients = 3;
        for (int i = 0; i < noClients; ++i) {
            new Client().start();
        }
    }
}
