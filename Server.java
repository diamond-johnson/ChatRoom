package ChatRoom;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 7670;
    private static Set<PrintWriter> writers = new HashSet<PrintWriter>();
    private static Set<String> nicknames = new HashSet<String>();

    public static void main(String[] args) throws Exception {
        System.out.println("Chat Server is running on port " + PORT);
        ServerSocket serverSocket = new ServerSocket(PORT);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Connected with " + socket.getInetAddress());
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writers.add(writer);
            Thread handler = new Thread(new Handler(socket));
            handler.start();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String nickname;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("NICK");
                nickname = reader.readLine();
                if (nicknames.contains(nickname)) {
                    writer.println("Nickname already exists. Please choose another one.");
                    nickname = reader.readLine();
                }
                nicknames.add(nickname);
                broadcast("Nickname of the client is " + nickname + "!", true);
                String message;
                while (true) {
                    message = reader.readLine();
                    if (message!= null) {
                        broadcast(nickname + ": " + message, true);
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client# " + nickname + ": " + e);
            } finally {
                if (nickname!= null) {
                    nicknames.remove(nickname);
                }
                if (writer!= null) {
                    writers.remove(writer);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket: " + e);
                }
                broadcast(nickname + " left the chat!", true);
            }
        }

        private void broadcast(String message, boolean isAdmin) {
            for (PrintWriter writer : writers) {
                if (isAdmin) {
                    writer.println("Admin: " + message);
                } else {
                    writer.println(message);
                }
            }
        }
    }
}