package z02_airport;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.Scanner;

public class AirportClient {

    public static final String SERVER_NAME = "localhost";
    public static final int SERVER_PORT = 12345;

    public static void main(String[] args) {

        AirportClient client = new AirportClient();
        client.startClient();
    }

    public void startClient() {

        try (
                Socket serverSocket = new Socket(AirportClient.SERVER_NAME, AirportClient.SERVER_PORT);
                BufferedWriter serverWriter = new BufferedWriter(
                        new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.US_ASCII)
                );
                BufferedReader serverReader = new BufferedReader(
                        new InputStreamReader(serverSocket.getInputStream(), StandardCharsets.US_ASCII)
                );
                Scanner systemIn = new Scanner(System.in);
        ) {
            System.out.println("AVAILABLE AIRPORTS:");
            readAndDisplayLines(serverReader);

            System.out.println("CHOSE AIRPORT:");
            serverWriter.write(systemIn.nextLine());
            serverWriter.newLine();
            serverWriter.flush();

            System.out.println("AVAILABLE FLIGHTS:");
            readAndDisplayLines(serverReader);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readAndDisplayLines(BufferedReader serverReader) throws IOException {

        String airport;
        while(!(airport = serverReader.readLine()).equals("")) {
            System.out.println(airport);
        }
    }
}
