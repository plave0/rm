package z01_farm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class FarmServer {

    public static final int SERVER_PORT = 12345;
    public static void main(String[] args) {

        try (Scanner systemInScanner = new Scanner(System.in);) {

            FarmServer farmServer = new FarmServer(systemInScanner.nextInt());
            farmServer.startServer();
        }
    }

    private Farm farm;
    private int farmSize;

    public FarmServer(int farmSize) {

        this.farmSize = farmSize;
        this.farm = new Farm(farmSize);
    }

    public void startServer() {

        System.err.println("Starting server...");

        try (ServerSocket serverSocket = new ServerSocket(FarmServer.SERVER_PORT)) {

            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.err.println("Client accepted");

                new Thread(new ServerWorkerRunnable(clientSocket, farm)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
