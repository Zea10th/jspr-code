package ru.netology;

import java.io.IOException;
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

    public void run() throws IOException {
        int count = 0;
        while (count < CLIENT_CAPACITY) {
            executorService.submit(new ServerHandler(serverSocket));
            count++;
        }
    }
}
