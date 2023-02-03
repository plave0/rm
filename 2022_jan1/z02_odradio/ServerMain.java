package z02_odradio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class ServerMain {

    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {

        Queue<String> taskQueue = new ConcurrentLinkedQueue<>();

        try (ServerSocket serverSocet = new ServerSocket(SERVER_PORT)) {

            boolean serverRuning = true;
            while (serverRuning) {

                Socket clientSocket = serverSocet.accept();

                Thread clientThread = new Thread(new ClientManager(clientSocket, taskQueue));
                clientThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
