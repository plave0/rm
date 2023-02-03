package z02_odradio;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class ClientManager implements Runnable{

    private Socket clientSocket;
    private final Queue<String> taskQueue;

    public ClientManager(Socket clientSocket, Queue<String> taskQueue) {
        this.clientSocket = clientSocket;
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {

        try {

            BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String clientName = clientReader.readLine();

            System.err.println(clientName + " se povezao");;

            boolean clientConected = true;
            while(clientConected) {

                String operator = clientReader.readLine();
                String argument = clientReader.readLine();

                if (operator == null) {
                    System.err.println(clientName + " se odvezao");
                    clientConected = false;
                    continue;
                }

                if (operator.equals("odradi")) {

                    String task;
                    synchronized (this.taskQueue) {
                        task = this.taskQueue.poll();
                    }

                    if (task != null) {
                        clientWriter.write(task);
                    }
                    clientWriter.newLine();
                    clientWriter.flush();

                }
                else if (operator.equals("dodaj")) {

                    synchronized (this.taskQueue) {
                        this.taskQueue.add(argument);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
