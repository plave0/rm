package z02_airport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class AirportServer {

    public static final String FLIGHTS_PATH = "2021_sep1/test/02";
    public static final int SERVER_PORT = 12345;

    public static void main(String[] args) {

        AirportServer server = new AirportServer();
        server.startServer();

    }

    private Map<String, List<String>> flights;

    public AirportServer() {
        this.flights = loadFlights();
    }

    public void startServer() {

        try (ServerSocket serverSocket = new ServerSocket(AirportServer.SERVER_PORT)) {

            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.err.println("Client accepted");

                new Thread(new ServerThread(clientSocket, this.flights)).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, List<String>> loadFlights() {

        Map<String, List<String>> flights = new HashMap<>();

        try (DirectoryStream<Path> directoryStream =
                     Files.newDirectoryStream(Paths.get(AirportServer.FLIGHTS_PATH)))
        {

            for(Path path : directoryStream) {

                if(Files.isRegularFile(path)) {

                    String fileName = path.getFileName().toString();
                    flights.put(fileName, new Vector<>());

                    try (Scanner airportScanner = new Scanner(path)) {

                        while(airportScanner.hasNextLine()) {
                            flights.get(fileName).add(airportScanner.nextLine());
                        }
                    }

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return flights;
    }
}
