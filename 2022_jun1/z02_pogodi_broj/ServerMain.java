package z02_pogodi_broj;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

    private static final int SERVER_PORT = 12321;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            boolean serverRunning = true;
            while (serverRunning) {

                Socket clientSocket = serverSocket.accept();

                Thread clientThread = new Thread(new ClientManager(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
