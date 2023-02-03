package z02_pogodi_broj;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.random.RandomGenerator;

public class ClientManager implements Runnable {

    private Socket clientSocket;
    private int targetNumber;

    public ClientManager(Socket clientSocket) {

        Random rnd = new Random();
        this.clientSocket = clientSocket;
        this.targetNumber = rnd.nextInt(1, 101);
    }

    @Override
    public void run() {

        try {

            Scanner clientScanner = new Scanner(this.clientSocket.getInputStream());
            PrintWriter clientWriter = new PrintWriter(this.clientSocket.getOutputStream());

            clientWriter.println("Pogodi koji broj od 1 do 100 sam zamislio");
            clientWriter.flush();

            boolean clientRunning = true;
            while (clientRunning) {

                int guess = clientScanner.nextInt();

                if (guess == this.targetNumber) {
                    clientWriter.println("Pogodak!");
                    clientWriter.flush();
                    clientRunning = false;
                }
                else if (guess > this.targetNumber) {
                    clientWriter.println("Zamisljeni broj je manji od toga");
                    clientWriter.flush();
                }
                else if (guess < this.targetNumber) {
                    clientWriter.println("Zamisljeni broj je veci od toga");
                    clientWriter.flush();
                }
            }

            this.clientSocket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
