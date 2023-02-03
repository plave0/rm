package z02_exchange;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class ExchangeServer {

    static private final int DEFAULT_PORT = 12345;

    public static void main(String[] args) {

        // Caches currency rates
        Map<String, Double> currencyMap = new HashMap<String, Double>();
        try (
                Scanner database = new Scanner(Path.of("2022_jan2/kursna_lista.txt"))
        ) {
            while (database.hasNextLine()) {
                String currencyName = database.next();
                double currencyValue = database.nextDouble();
                currencyMap.put(currencyName, currencyValue);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.err.println("Currencies loaded:");
        System.err.println(currencyMap.toString());

        // Start server
        try (
                ServerSocketChannel serverChannel = ServerSocketChannel.open();
                Selector selector = Selector.open()
        ) {

            // Scoket configuration
            serverChannel.bind(new InetSocketAddress(DEFAULT_PORT));
            serverChannel.configureBlocking(false);

            // Selector cofiguracion
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            // Server loop
            boolean serverListening = true;
            while(serverListening) {

                // Selection
                selector.select();
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = readyKeys.iterator();

                // Itereting through selected items
                while(keyIterator.hasNext()) {

                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    try {
                        if (key.isAcceptable()) {

                            SocketChannel client = serverChannel.accept();
                            client.configureBlocking(false);

                            SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);

                            ByteBuffer messageBuffer = ByteBuffer.allocate(1024);
                            messageBuffer.clear();

                            clientKey.attach(messageBuffer);

                            System.err.println("Client attached");
                        }
                        else if(key.isReadable()) {

                            // Setup for reading
                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer messageBufer = (ByteBuffer) key.attachment();

                            // Reading
                            client.read(messageBufer);

                            // Parsing message if whole message received
                            String messageString = new String(messageBufer.array(), 0, messageBufer.position(), StandardCharsets.US_ASCII);

                            if(messageString.endsWith("\n")) {

                                // Parsing request
                                StringTokenizer messageTokenizer = new StringTokenizer(messageString);
                                String currencyName = messageTokenizer.nextToken().toLowerCase();
                                double amount = Double.parseDouble(messageTokenizer.nextToken());

                                // Preparing response
                                double exchangeRate = currencyMap.get(currencyName);

                                messageBufer.clear();
                                messageBufer.put((Double.toString(exchangeRate * amount) + "\n").getBytes(StandardCharsets.US_ASCII));
                                messageBufer.flip();

                                key.interestOps(SelectionKey.OP_WRITE);
                            }
                        }
                        else if (key.isWritable()) {

                            // Setup for writing
                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer messageBuffer = (ByteBuffer) key.attachment();

                            // Responding
                            client.write(messageBuffer);

                            // Check if request sent
                            if (!messageBuffer.hasRemaining()) {
                                key.cancel();
                            }

                        }
                    } catch (IOException clientIoException) {
                        key.cancel();
                        try {
                            key.channel().close();
                        }
                        catch (IOException ex1) {
                            clientIoException.printStackTrace();
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
