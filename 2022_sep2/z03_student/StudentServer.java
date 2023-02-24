package z03_student;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

public class StudentServer {

    public static final int LISTEN_PORT = 12321;
    public static final int BUFFERE_SIZE = 512;
    public static final String DATA_PATH = "2022_sep2/test/03/students.txt";
    public static void main(String[] args) {

        StudentServer server = new StudentServer();
        server.start();
    }

    private Map<String,Student> studentData;

    public StudentServer() {
        this.studentData = this.loadStudents();
    }

    public void start() {

        try (DatagramSocket serverSocket = new DatagramSocket(StudentServer.LISTEN_PORT)) {

            byte[] requestBuffer = new byte[BUFFERE_SIZE];
            DatagramPacket requestPacket = new DatagramPacket(requestBuffer, StudentServer.BUFFERE_SIZE);

            while (true) {

                serverSocket.receive(requestPacket);

                String requestString = new String(
                        requestPacket.getData(), 0, requestPacket.getLength(), StandardCharsets.US_ASCII
                );
                StringTokenizer requestTokenizer = new StringTokenizer(requestString);

                String requestId;
                String requestCommand;
                if (requestTokenizer.countTokens() == 2) {
                    requestId = requestTokenizer.nextToken();
                    requestCommand = requestTokenizer.nextToken();
                }
                else {
                    requestId = "";
                    requestCommand = "";
                }

                byte[] responseBuffer;
                if (requestCommand.equals("ime")) {

                    responseBuffer = this.studentData
                            .get(requestId)
                            .toString()
                            .getBytes(StandardCharsets.US_ASCII);
                }
                else if (requestCommand.equals("prosek")) {

                    responseBuffer = Double.toString(this.studentData
                                    .get(requestId)
                                    .getAverage())
                            .getBytes(StandardCharsets.US_ASCII);
                }
                else {

                    responseBuffer = "Zahtev nije validan!".getBytes(StandardCharsets.US_ASCII);
                }

                DatagramPacket responsPacket = new DatagramPacket(
                        responseBuffer
                        , responseBuffer.length
                        , requestPacket.getAddress()
                        , requestPacket.getPort()
                );
                serverSocket.send(responsPacket);
            }

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String,Student> loadStudents () {

        Map<String,Student> students = new HashMap<>();
        try (Scanner studentDataScanner = new Scanner(Paths.get(StudentServer.DATA_PATH))) {

            while(studentDataScanner.hasNextLine()) {
                Student s = new Student(studentDataScanner.nextLine());
                students.put(s.getId(), s);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return students;
    }
}
