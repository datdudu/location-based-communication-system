package com.locationBasedCommunicationSystem.communication;

import java.io.*;
import java.net.*;

public class SyncCommunicationClient {
    public static boolean sendMessage(String ip, int port, String message) {
        try (Socket socket = new Socket(ip, port)) {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(message + "\n");
            out.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}