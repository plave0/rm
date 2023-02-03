package z02_exchange;

import com.sun.jdi.ByteValue;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ExchangeClient {

    static private final String HOST_NAME = "localhost";
    static private final int PORT = 12345;

    public static void main(String[] args) {

        try {

            SocketChannel serverChannel = SocketChannel.open(new InetSocketAddress(HOST_NAME, PORT));
            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            // Configure sockets
            serverChannel.configureBlocking(false);

            ByteBuffer messageBuffer = ByteBuffer.allocate(1024);

            // Server loop
            boolean clientRunning = true;
            while(clientRunning) {

                String messageString = systemIn.readLine();

                if(messageString.equalsIgnoreCase("prekid")) {
                    clientRunning = false;
                    continue;
                }

                messageBuffer.clear();

                messageBuffer.put(messageString.getBytes(StandardCharsets.US_ASCII));
                messageBuffer.put("\n".getBytes(StandardCharsets.US_ASCII));

                messageBuffer.flip();

                while(messageBuffer.hasRemaining()) {
                    serverChannel.write(messageBuffer);
                }

                messageBuffer.clear();
                messageBuffer.limit(8);

                while(messageBuffer.hasRemaining()) {
                    serverChannel.read(messageBuffer);
                }

                messageBuffer.flip();
                double result = messageBuffer.getDouble();

                System.out.println(result);

            }

            serverChannel.close();
            systemIn.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
