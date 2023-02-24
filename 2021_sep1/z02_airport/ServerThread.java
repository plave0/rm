package z02_airport;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ServerThread implements Runnable {

    private Socket clientSocket;
    private Map<String, List<String>> flights;
    public ServerThread(Socket clientSocket, Map<String, List<String>> flights) {
        this.clientSocket = clientSocket;
        this.flights = flights;
    }
    @Override
    public void run() {

        try (
                PrintStream clientWriter = new PrintStream(
                        new BufferedOutputStream(clientSocket.getOutputStream()),
                        true,
                        StandardCharsets.US_ASCII
                        );
                Scanner clientScanner = new Scanner(
                        this.clientSocket.getInputStream()
                )
        ) {

            this.flights.keySet().forEach(clientWriter::println);
            clientWriter.println();

            String clientAirportChoice = clientScanner.nextLine();

            this.flights.get(clientAirportChoice).forEach(clientWriter::println);
            clientWriter.println();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
