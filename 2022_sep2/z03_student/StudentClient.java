package z03_student;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class StudentClient {

    public static final int BUFFER_SIZE = 512;
    public static final String HOST_NAME = "localhost";
    public static final int HOST_PORT = 12321;

    public static void main(String[] args) {

        StudentClient client = new StudentClient();
        client.start();
    }

    public void start() {

        try (
                Scanner systemInScanner = new Scanner(System.in);
                DatagramSocket clientSocket = new DatagramSocket();
        ) {

            System.out.println("Unesi komandu:");
            byte[] requestBuffer = systemInScanner.nextLine().getBytes(StandardCharsets.US_ASCII);

            InetAddress serverAddress = InetAddress.getByName(StudentClient.HOST_NAME);
            DatagramPacket requestPacket = new DatagramPacket(
                    requestBuffer
                    , requestBuffer.length
                    , serverAddress
                    , StudentClient.HOST_PORT
            );
            clientSocket.send(requestPacket);

            byte[] responseBuffer = new byte[StudentClient.BUFFER_SIZE];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, StudentClient.BUFFER_SIZE);

            clientSocket.receive(responsePacket);

            String responseString = new String(
                    responsePacket.getData(), 0, responsePacket.getLength(), StandardCharsets.US_ASCII
            );
            System.out.println(responseString);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

