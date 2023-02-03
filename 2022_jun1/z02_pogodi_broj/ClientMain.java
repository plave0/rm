package z02_pogodi_broj;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {

        try (Socket serverSocket = new Socket("localhost", 12321)) {

            Scanner serverScanner = new Scanner(serverSocket.getInputStream());
            PrintWriter serverWriter = new PrintWriter(serverSocket.getOutputStream());
            Scanner systemIn = new Scanner(System.in);

            String initMessage = serverScanner.nextLine();
            System.out.println(initMessage);

            boolean clientRunning = true;
            while (clientRunning) {

                int guess = systemIn.nextInt();
                serverWriter.println(guess);
                serverWriter.flush();

                String serverResponse = serverScanner.nextLine();
                System.out.println(serverResponse);

                if(serverResponse.equals("Pogodak!")) {
                    clientRunning = false;
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
