package com.locationBasedCommunicationSystem.communication;

import com.locationBasedCommunicationSystem.model.User;

import java.io.*;
import java.net.*;

public class SyncCommunicationServer extends Thread {
    private User user;
    private ServerSocket serverSocket;
    private boolean running = true;
    private MessageListener listener;

    public interface MessageListener {
        void onMessage(String message);
    }

    public SyncCommunicationServer(User user, MessageListener listener) throws IOException {
        this.user = user;
        this.listener = listener;
        this.serverSocket = new ServerSocket(user.getPort());
    }

    @Override
    public void run() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String received = in.readLine();
                if (listener != null) listener.onMessage(received);
                clientSocket.close();
            } catch (IOException e) {
                if (running) e.printStackTrace();
            }
        }
    }

    public void stopServer() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}