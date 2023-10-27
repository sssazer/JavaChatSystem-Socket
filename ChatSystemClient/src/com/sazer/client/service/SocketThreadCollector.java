package com.sazer.client.service;

import java.net.Socket;

public class SocketThreadCollector {

    private static SocketThread communicationThread = null;

    public static SocketThread getCommunicationThread() {
        return communicationThread;
    }

    public static void setCommunicationThread(SocketThread communicationThread) {
        SocketThreadCollector.communicationThread = communicationThread;
    }
}
