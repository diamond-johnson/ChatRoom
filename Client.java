package ChatRoom;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final int PORT = 7670;
    private static final String HOST = "localhost";
    private static Socket socket;
    private static PrintWriter writer;
    private static BufferedReader reader;

    public static void main(String[] args) throws Exception {
        socket = new Socket(HOST, PORT);
        System.out.println("Connected to chat server");
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Scanner scanner = new Scanner(System.in);
        String message = reader.readLine();
        if (message.equals("NICK")) {
            System.out.print("Choose a nickname: ");
            String nickname = scanner.nextLine();
            writer.println(nickname);
        }
        Thread readerThread = new Thread(new ReaderThread());
        readerThread.start();
        Thread writerThread = new Thread(new WriterThread(scanner));
        writerThread.start();
    }

    private static class ReaderThread extends Thread {
        @Override
        public void run() {
            String message;
            try {
                while (true) {
                    message = reader.readLine();
                    if (message!= null) {
                        System.out.println(message);
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading from server: " + e);
            }
        }
    }

    private static class WriterThread extends Thread {
        private Scanner scanner;

        public WriterThread(Scanner scanner) {
            this.scanner = scanner;
        }

        @Override
        public void run() {
            String message;
            try {
                while (true) {
                    message = scanner.nextLine();
                    writer.println(message);
                }
            } catch (Exception e) {
                System.out.println("Error writing to server: " + e);
            }
        }
    }
}