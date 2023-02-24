package z03_words;

import javax.print.attribute.standard.PresentationDirection;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.stream.Collectors;

public class WordsServer {

    public  static final int BUFFER_SIZE = 512;
    public static void main(String[] args) {

        WordsServer server = new WordsServer();
        server.start();
    }

    private List<String> words;

    public WordsServer() {

        this.words = this.loadWords();
    }

    public void start() {

        try (DatagramSocket serverSocket = new DatagramSocket(12345)){

            byte[] requestBuffer = new byte[BUFFER_SIZE];
            DatagramPacket requestPacket = new DatagramPacket(requestBuffer, BUFFER_SIZE);

            while (true) {

                serverSocket.receive(requestPacket);

                List<Integer> indexList =
                        Arrays.stream(
                                new String(
                                    requestPacket.getData(),
                                    0, requestPacket.getLength(),
                                    StandardCharsets.US_ASCII
                                ).split(" "))
                                .map(Integer::parseInt)
                                .toList();

                String responseString = this.buildResponse(indexList);
                byte[] responseBuffer = responseString.getBytes(StandardCharsets.US_ASCII);
                DatagramPacket responsePacket = new DatagramPacket(
                        responseBuffer, responseBuffer.length, requestPacket.getAddress(), requestPacket.getPort()
                );
                serverSocket.send(responsePacket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildResponse(List<Integer> indexList) {

        return indexList.stream()
                .filter(i -> (i < this.words.size()))
                .map(this.words::get)
                .collect(Collectors.joining(" "));
    }

    private List<String> loadWords() {

        List<String> words = new Vector<>();
        try (Scanner wordListScanner = new Scanner(Paths.get("2022_jun1/test/03/words.txt"))) {

            while(wordListScanner.hasNextLine()) {
                words.add(wordListScanner.nextLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return words;
    }
}
