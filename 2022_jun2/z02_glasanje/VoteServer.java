package z02_glasanje;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.*;

public class VoteServer {

    public static void main(String[] args) {

        VoteServer server = null;
        try {
            server = new VoteServer(12345);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        server.startServer();
    }

    private List<String> candidates;
    private Map<String, Integer> votes;
    private int serverPort;

    private VoteServer(int serverPort) throws FileNotFoundException {

        this.candidates = new Vector<String>();
        this.votes = new TreeMap<String, Integer>();
        this.serverPort = serverPort;
        this.loadCandidates();
    }

    private void loadCandidates() throws FileNotFoundException {

        Scanner candidateScanner = new Scanner(new File("2022_jun2/test/lista.txt"));

        while(candidateScanner.hasNextLine()) {
            String candidate = candidateScanner.nextLine().toLowerCase();
            this.candidates.add(candidate);
            this.votes.put(candidate, 0);
        }
    }

    private String confirmationMessage(String candidateName) {

        int numVotes = this.votes.values()
                .stream()
                .reduce(Integer::sum)
                .get();

        int candidateVotes = this.votes.get(candidateName);

        double votePercentage = (candidateVotes / (double) numVotes);

        return "Hvala sto ste glasali, trenutni procenat glasova za vaseg kandidata je " + String.format("%.2f", votePercentage);
    }

    public void startServer() {

        try {

            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            Selector selector = Selector.open();

            InetSocketAddress serverAdress = new InetSocketAddress(this.serverPort);
            serverChannel.bind(serverAdress);
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            Instant startInstantn = Instant.now();

            boolean serverListening = true;
            while (serverListening) {

                selector.select(200);
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                Instant currentInstant = Instant.now();
                if(currentInstant.minusSeconds(5).isAfter(startInstantn)) {

                    startInstantn = Instant.from(currentInstant);

                    System.out.println("VOTING LIST:");
                    for (String candidate : votes.keySet()) {
                        System.out.println(candidate + ":  " + votes.get(candidate));
                    }
                    System.out.println("END OF LIST");
                }

                while(keyIterator.hasNext()) {

                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    try {
                        if(key.isAcceptable()) {

                            SocketChannel clientChannel = serverChannel.accept();
                            clientChannel.configureBlocking(false);

                            ByteBuffer messageBuffer = ByteBuffer.allocate(1024);

                            SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
                            clientKey.attach(messageBuffer);
                        }
                        else if (key.isReadable()) {

                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            ByteBuffer messageBuffer = (ByteBuffer) key.attachment();

                            clientChannel.read(messageBuffer);

                            String messageString = new String(messageBuffer.array(), 0, messageBuffer.position());
                            if (messageString.endsWith("\n")) {

                                String vote = messageString.substring(0, messageString.length() - 1).toLowerCase();
                                if (votes.containsKey(vote)) {
                                    votes.merge(vote, 1, Integer::sum);

                                    messageBuffer.clear();
                                    messageBuffer.put(this.confirmationMessage(vote).getBytes(StandardCharsets.US_ASCII));
                                    messageBuffer.put("\n".getBytes(StandardCharsets.US_ASCII));
                                    messageBuffer.flip();
                                }
                                else {
                                    messageBuffer.clear();
                                    messageBuffer.put("Vas glas nije validan".getBytes(StandardCharsets.US_ASCII));
                                    messageBuffer.put("\n".getBytes(StandardCharsets.US_ASCII));
                                    messageBuffer.flip();

                                }

                                key.interestOps(SelectionKey.OP_WRITE);
                            }
                        }
                        else if (key.isWritable()) {

                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            ByteBuffer messageBuffer = (ByteBuffer) key.attachment();

                            clientChannel.write(messageBuffer);

                            if(!messageBuffer.hasRemaining()) {
                                clientChannel.close();
                                key.cancel();
                            }
                        }
                    }
                    catch (IOException iterationException) {
                        key.cancel();
                        try {
                            key.channel().close();
                        }
                        catch (IOException clientClosingException) {
                            clientClosingException.printStackTrace();
                        }
                        iterationException.printStackTrace();
                    }
                }
            }

            serverChannel.close();
            selector.close();
        }
        catch (IOException serverException) {
            throw new RuntimeException(serverException);
        }
    }
}
