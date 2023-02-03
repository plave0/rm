package z02_ispiti;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;

public class ExamServer {

    public static void main(String[] args) {

        try (
                ServerSocketChannel serverChannel = ServerSocketChannel.open();
                Selector selector = Selector.open()
        ) {

            // Server configuration
            serverChannel.bind(new InetSocketAddress(12345));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            boolean serverListening = true;
            while(serverListening) {

                selector.select();
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {

                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    try {
                        if (key.isAcceptable()) {
                            System.err.println("Client accpeted");

                            SocketChannel client = serverChannel.accept();
                            ByteBuffer messageBuffer = ByteBuffer.allocate(1024);

                            client.configureBlocking(false);

                            SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);
                            clientKey.attach(messageBuffer);
                        }
                        else if (key.isReadable()) {

                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer messageBuffer = (ByteBuffer) key.attachment();

                            client.read(messageBuffer);

                            String messageString = new String(messageBuffer.array(), 0, messageBuffer.position());

                            if(!messageString.endsWith("\n")) { continue; }

                            System.err.println("Message received");

                            messageString = messageString.substring(0, messageString.length() - 1);
                            String[] tokens = messageString.split(" ");

                            LocalDate date1 = null;
                            LocalDate date2 = null;
                            int pageCount = 0;

                            if (tokens.length == 2) {
                                date1 = LocalDate.now();
                                date2 = LocalDate.parse(tokens[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                pageCount = Integer.parseInt(tokens[1]);
                            }
                            else if (tokens.length == 3) {
                                date1 = LocalDate.parse(tokens[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                date2 = LocalDate.parse(tokens[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                pageCount = Integer.parseInt(tokens[2]);
                            }

                            long dayLeft;
                            if(date1.isAfter(date2)) {
                                dayLeft = 0;
                            }
                            else {
                                dayLeft = date1.datesUntil(date2).count();
                            }

                            messageBuffer.clear();
                            if (dayLeft != 0) {

                                long pagesPerDay = pageCount / dayLeft;
                                String responseString = Long.toString(pagesPerDay);

                                if(dayLeft < 7) {
                                    responseString += ", ostalo je manje od 7 dana";
                                }

                                responseString += "\n";

                                messageBuffer.put(responseString.getBytes());
                            }
                            else {
                                messageBuffer.put("Vidimo se u narednom roku!\n".getBytes());
                            }

                            messageBuffer.flip();
                            key.interestOps(SelectionKey.OP_WRITE);

                        }
                        else if (key.isWritable()) {

                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer messageBuffer = (ByteBuffer) key.attachment();

                            client.write(messageBuffer);

                            if(!messageBuffer.hasRemaining()) {
                                client.close();
                                key.cancel();
                            }
                        }
                    }
                    catch (IOException clientException) {
                        key.cancel();
                        key.channel().close();
                    }
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
