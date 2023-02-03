package z02_ispiti;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ExamClient {

    public static void main(String[] args) {

        boolean clientRunning = true;
        while (clientRunning) {
            try (SocketChannel serverChannel = SocketChannel.open(new InetSocketAddress(12345))) {

                serverChannel.configureBlocking(false);

                ByteBuffer messageBuffer = ByteBuffer.allocate(1024);
                Scanner systemInScanner = new Scanner(System.in);

                String requestMessage = systemInScanner.nextLine();

                if (requestMessage.equalsIgnoreCase("exit")) {
                    clientRunning = false;
                    continue;
                }
                requestMessage += "\n";
                messageBuffer.put(requestMessage.getBytes());

                messageBuffer.flip();
                while(messageBuffer.hasRemaining()) {
                    serverChannel.write(messageBuffer);
                }
                System.err.println("Message sent");

                messageBuffer.clear();
                boolean responseRead = false;
                while(!responseRead) {
                    serverChannel.read(messageBuffer);
                    String responseString = new String(messageBuffer.array(), 0, messageBuffer.position());
                    if(responseString.endsWith("\n")) {
                        responseRead = true;
                    }
                }

                String responseMessage = new String(messageBuffer.array(), 0, messageBuffer.position());
                System.out.println(responseMessage);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
