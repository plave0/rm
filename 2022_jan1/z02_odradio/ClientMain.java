package z02_odradio;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientMain {

    private static final int SERVER_PORT = 12345;
    private static final String HOSTNAME = "localhost";

    public static void main(String[] args) {

        try (Socket server = new Socket(ClientMain.HOSTNAME, ClientMain.SERVER_PORT)) {

            BufferedReader serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
            BufferedWriter serverOut = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            boolean clientRunning = true;

            System.out.println("Unesi ime: ");
            String name = systemIn.readLine();

            serverOut.write(name);
            serverOut.newLine();
            serverOut.flush();

            while (clientRunning) {

                System.out.println("Unesi komandu: ");
                String command = systemIn.readLine();
                String operator;
                String argument;
                int spaceIdx = command.indexOf(' ');
                if (spaceIdx != -1) {
                    operator = command.substring(0, spaceIdx).trim().toLowerCase();
                    argument = command.substring(spaceIdx).trim().toLowerCase();
                }
                else {
                    operator = command;
                    argument = "";
                }

                if(
                        !operator.equals("odradi") &&
                        !operator.equals("dodaj") &&
                        !operator.equals("izadji")
                ) {
                    System.err.println("NedozvoljenPa komanda.");
                    continue;
                }

                if(operator.equals("izadji")) {
                    System.out.println("Zatvaranje veze...");
                    clientRunning = false;
                    continue;
                }

                serverOut.write(operator);
                serverOut.newLine();
                serverOut.write(argument);
                serverOut.newLine();
                serverOut.flush();

                if(operator.equals("odradi")) {
                    String task = serverIn.readLine();
                    System.out.println("Vas zadatak je: " + task);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
