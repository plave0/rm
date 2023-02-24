package z01_farm;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class ServerWorkerRunnable implements Runnable {

    private Socket clientSocket;
    private Farm farm;
    public ServerWorkerRunnable(Socket clientSocket, Farm farm) {
        this.clientSocket = clientSocket;
        this.farm = farm;
    }

    @Override
    public void run() {

        try (
            BufferedReader clientReader = new BufferedReader(
                    new InputStreamReader(this.clientSocket.getInputStream())
            );
            BufferedWriter clientWritter = new BufferedWriter(
                    new OutputStreamWriter(this.clientSocket.getOutputStream())
            )
        ) {

            boolean threadRunning = true;
            while(threadRunning) {

                String command = clientReader.readLine();
                StringTokenizer commandTokenize = new StringTokenizer(command);
                String opperand = commandTokenize.nextToken();

                switch (opperand) {
                    case "list" -> this.sendFarm(clientWritter);
                    case "mark" -> {
                        int i = Integer.parseInt(commandTokenize.nextToken());
                        int j = Integer.parseInt(commandTokenize.nextToken());
                        this.farm.mark(i, j);
                        this.sendFarm(clientWritter);
                    }
                    case "repair" -> {
                        int i = Integer.parseInt(commandTokenize.nextToken());
                        int j = Integer.parseInt(commandTokenize.nextToken());
                        this.farm.repair(i, j);
                        this.sendFarm(clientWritter);
                    }
                    case "exit" -> threadRunning = false;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFarm(BufferedWriter clientWritter) throws IOException {

        clientWritter.write(this.farm.getFarmSize());
        clientWritter.write(this.farm.getFarm());
        clientWritter.write("\n");
        clientWritter.flush();
    }
}
