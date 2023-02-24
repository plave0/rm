package z03_loto;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LotoClient {

    public static void main(String[] args) {
        LotoClient client = new LotoClient();
        client.start();
    }

    public void start() {

        try (
                SocketChannel serverChannel = SocketChannel.open(new InetSocketAddress("localhost", 12345));
                Scanner systemInScanner = new Scanner(System.in);
        ) {

            String inputLine = systemInScanner.nextLine();
            List<Integer> combination = this.getCombinationFromString(inputLine);

            ByteBuffer clientBuffer = ByteBuffer.allocate(28);
            this.writeCombinationToBuffer(combination, clientBuffer);

            while (clientBuffer.hasRemaining()) {
                serverChannel.write(clientBuffer);
            }

            clientBuffer.clear();
            clientBuffer.limit(4);

            while (clientBuffer.hasRemaining()) {
                serverChannel.read(clientBuffer);
            }

            clientBuffer.flip();
            int guessCount = clientBuffer.getInt();
            System.out.println(guessCount);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCombinationToBuffer(List<Integer> combination, ByteBuffer clientBuffer) {
        combination.forEach(clientBuffer::putInt);
        clientBuffer.flip();
    }

    private List<Integer> getCombinationFromString(String line) {
         return Arrays.stream(line.split(" "))
                     .map(Integer::parseInt)
                     .toList();
    }
}
