package utb.fai;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class App {

    public static void main(String[] args) {
        int port = 12345;
        int max_threads = 10;

        // Spracovanie vstupnych parametrov
        if (args.length >= 1) {
            try {
                int p = Integer.parseInt(args[0].trim());
                if (p > 0 && p <= 65535) port = p;
            } catch (NumberFormatException e) {
                System.err.println("Neplatny port, pouzivam " + port);
            }
        }
        if (args.length >= 2) {
            try {
                int t = Integer.parseInt(args[1].trim());
                if (t > 0) max_threads = t;
            } catch (NumberFormatException e) {
                System.err.println("Neplatny pocet vlakien, pouzivam " + max_threads);
            }
        }

        // Pool s obmedzenym poctom vlakien
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                max_threads, max_threads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        // Hlavna slucka servera
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server bezi na porte " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new ClientThread(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Chyba servera: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }
}
