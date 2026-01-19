package com.iprody.leonidm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer {
    private static final String SPACE = " ";
    private static final String BASE_DIRECTORY = "E:\\projects\\prody\\lesson-2\\payment-service\\simple-http-server\\static\\";
    private static final String OK = "200 OK";
    private static final String NOT_FOUND = "404 NOT FOUND";
    private static final String DEFAULT_TEXT_TYPE = "text/plain";
    private static final Map<String, String> CONTENT_TYPE = new HashMap<>();

    static {
        CONTENT_TYPE.put("html", "text/html");
        CONTENT_TYPE.put("css", "text/css");
        CONTENT_TYPE.put("js", "text/javascript");
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started at http://localhost:8080");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new
                    OutputStreamWriter(clientSocket.getOutputStream()));

            // Чтение запроса
            String line;
            String resourceName = null;
            boolean isFirstLine = true;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                if (isFirstLine) {
                    String[] requestMetadata = line.split(SPACE);
                    if (requestMetadata.length == 3) {
                        if (requestMetadata[0].equals("GET") && requestMetadata[1] != null && !requestMetadata[1].isEmpty()) {
                            resourceName = requestMetadata[1];
                        }
                    }
                }
                System.out.println(line);

                isFirstLine = false;
            }
            // Простой ответ
            sendResponse(resourceName, out);

            clientSocket.close();
        }
    }

    private static void sendResponse(String resourceName, BufferedWriter out) throws IOException {
        if (resourceName == null || resourceName.isEmpty()) {
            writeOutStatus(out, NOT_FOUND);
            out.flush();
            return;
        }

        Path filePath = Paths.get(BASE_DIRECTORY, Paths.get(resourceName).normalize().toString());
        final boolean fileExists = Files.exists(filePath);
        String status = fileExists ? OK : NOT_FOUND;

        if (!fileExists) {
            writeOutStatus(out, NOT_FOUND);
            out.flush();
            return;
        }

        List<String> contentStrings = Files.readAllLines(filePath);
        writeOutStatus(out, status);
        String contentType = resolveContentType(resourceName);
        out.write("Content-Type: " + contentType + "; charset=UTF-8\r\n");
        if (!contentStrings.isEmpty()) {
            int contentLength = contentStrings.stream()
                    .mapToInt(String::length)
                    .sum();
            out.write("Content-Length: " + contentLength + "\r\n");
            out.write("\r\n");
            contentStrings.forEach(str -> {
                try {
                    out.write("\r\n");
                    out.write(str);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
        out.flush();
    }

    private static String resolveContentType(String resourceName) {
        String[] splitResourceName = resourceName.split("\\.");
        if (splitResourceName.length < 2) {
            return DEFAULT_TEXT_TYPE;
        }
        String fileType = splitResourceName[splitResourceName.length - 1];
        return CONTENT_TYPE.getOrDefault(fileType, DEFAULT_TEXT_TYPE);
    }

    private static void writeOutStatus(BufferedWriter out, String status) throws IOException {
        out.write("HTTP/1.1 " + status + "\r\n");
    }
}
