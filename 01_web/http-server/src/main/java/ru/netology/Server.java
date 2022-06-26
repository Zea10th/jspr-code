package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ServerSocket serverSocket;
    static final int CLIENT_CAPACITY = 64;
    private final ExecutorService executorService;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.executorService = Executors.newFixedThreadPool(CLIENT_CAPACITY);
    }

    public void run() {
        while (true) {
            try {
                var socket = serverSocket.accept();
                executorService.submit(new ServerHandler(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
