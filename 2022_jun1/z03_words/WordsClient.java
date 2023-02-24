package z03_words;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class WordsClient {

    public static final int BUFFER_SIZE = 512;
    public static void main(String[] args) {

        WordsClient client = new WordsClient();
        client.start();
    }

    public void start() {

        try (
                DatagramSocket clientSocket = new DatagramSocket();
                Scanner systemInScanner = new Scanner(System.in);
        ) {

            String indexLine = systemInScanner.nextLine();
            byte[] requestBuffer = indexLine.getBytes(StandardCharsets.US_ASCII);
            DatagramPacket requestPacket = new DatagramPacket(
                requestBuffer, requestBuffer.length, InetAddress.getByName("localhost"), 12345
            );
            clientSocket.send(requestPacket);

            byte[] responseBuffer = new byte[BUFFER_SIZE];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            clientSocket.receive(responsePacket);
            String responseString = new String(
                    responsePacket.getData(),
                    0, responsePacket.getLength(),
                    StandardCharsets.US_ASCII
            );
            System.out.println(responseString);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
