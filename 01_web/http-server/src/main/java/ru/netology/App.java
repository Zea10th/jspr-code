package ru.netology;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        new Server(9999).run();
    }
}
