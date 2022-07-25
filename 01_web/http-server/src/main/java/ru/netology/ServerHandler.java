package ru.netology;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ServerHandler implements Runnable {
    final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html",
            "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final String requestLine;
            final String[] parts;
            final Path filePath;
            final long length;

            try {
                var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                var out = new BufferedOutputStream(socket.getOutputStream());

                requestLine = in.readLine();
                System.out.println("Server: " + requestLine);

                parts = requestLine.split(" ");
                System.out.print("Parts: ");
                Arrays.stream(parts).forEach(System.out::println);

                if (!isValid(parts)) {
                    out.write(returnNotFound());
                    out.flush();
                    break;
                }

                if (!isValidPath(parts[1])) {
                    out.write(returnNotFound());
                    out.flush();
                    break;
                }

                filePath = Path.of(".", "public", parts[1]);

                if (isClassicCase(parts[1], filePath, out)) continue;

                length = Files.size(filePath);

                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + Files.probeContentType(filePath) + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isClassicCase(String path, Path filePath, BufferedOutputStream out) throws IOException {
        final var mimeType = Files.probeContentType(filePath);

        var result = path.equals("/classic.html");
        if (result) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
        }

        return result;
    }

    private boolean isValidPath(String path) {
        return validPaths.contains(path);
    }

    private boolean isValid(String[] parts) {
        return parts.length == 3;
    }

    private byte[] returnNotFound() {
        return (
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes();
    }
}
