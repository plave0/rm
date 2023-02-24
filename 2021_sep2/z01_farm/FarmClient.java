package z01_farm;

import z03_udp.ClientMain;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FarmClient {

    public static final String HOST_NAME = "localhost";
    public static final int PORT = 12345;
    public static void main(String[] args) {

        FarmClient client = new FarmClient();
        client.startClient();
    }

    public void startClient() {

        try (
                Socket serverSocket = new Socket(FarmClient.HOST_NAME, FarmClient.PORT);
                Scanner systemInScanner = new Scanner(System.in);
                BufferedWriter serverWritter = new BufferedWriter(
                        new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.US_ASCII)
                );
                BufferedReader serverReader = new BufferedReader(
                        new InputStreamReader(serverSocket.getInputStream(), StandardCharsets.US_ASCII)
                );
        ) {

            boolean clientRunnign = true;
            while (clientRunnign) {

                String command = systemInScanner.nextLine();

                serverWritter.write(command);
                serverWritter.newLine();
                serverWritter.flush();

                if(command.startsWith("exit")) {

                    clientRunnign = false;
                }
                else {

                    int farmSize = serverReader.read();
                    char[] farmBuffer;
                    farmBuffer = serverReader.readLine().toCharArray();
                    Farm farm = new Farm(farmBuffer, farmSize);
                    farm.printFarm();
                }
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
