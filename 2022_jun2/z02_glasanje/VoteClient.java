package z02_glasanje;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class VoteClient {

    public static void main(String[] args) {

        try {
            InetSocketAddress serverAddress = new InetSocketAddress(12345);
            SocketChannel serverChannel = SocketChannel.open(serverAddress);

            ByteBuffer messageBuffer = ByteBuffer.allocate(1024);

            Scanner systemIn = new Scanner(System.in);

            messageBuffer.put(systemIn.nextLine().getBytes(StandardCharsets.US_ASCII));
            messageBuffer.put("\n".getBytes(StandardCharsets.US_ASCII));
            messageBuffer.flip();
            serverChannel.write(messageBuffer);

            messageBuffer.clear();
            serverChannel.read(messageBuffer);

            String response = new String(messageBuffer.array(), 0, messageBuffer.position());
            System.out.println(response);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
