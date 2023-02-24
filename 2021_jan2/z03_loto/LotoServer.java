package z03_loto;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.stream.Collectors;

public class LotoServer {

    public static void main(String[] args) {

        LotoServer server = new LotoServer();
        server.start();
    }

    private List<Integer> lotoCombination;

    public LotoServer() {

        this.lotoCombination = this.randomCombination();
    }

    public void start() {

        try(
                ServerSocketChannel serverChannel = ServerSocketChannel.open();
                Selector selector = Selector.open();
        ) {

            serverChannel.bind(new InetSocketAddress(12345));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {

                selector.select();
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {

                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if(key.isAcceptable()) {

                        SocketChannel clientChannel = serverChannel.accept();
                        ByteBuffer clientBuffer = ByteBuffer.allocate(28);

                        clientChannel.configureBlocking(false);

                        SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
                        clientKey.attach(clientBuffer);
                    }
                    else if (key.isReadable()) {

                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer clientBuffer = (ByteBuffer) key.attachment();

                        clientChannel.read(clientBuffer);
                        if (!clientBuffer.hasRemaining()) {

                            clientBuffer.flip();
                            List<Integer> clientCombination = this.readCombinationFromBuffer(clientBuffer);
                            int guessCount = this.countGuesses(this.lotoCombination, clientCombination);

                            clientBuffer.clear();
                            clientBuffer.putInt(guessCount);
                            clientBuffer.flip();

                            key.interestOps(SelectionKey.OP_WRITE);
                        }
                    }
                    else if (key.isWritable()) {

                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer clientBuffer = (ByteBuffer) key.attachment();

                        if (clientBuffer.hasRemaining()) {
                            clientChannel.write(clientBuffer);
                        }
                        else {
                            clientChannel.close();
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int countGuesses(List<Integer> lotoCombination, List<Integer> clientCombination) {

        int count = 0;
        for(var i : clientCombination) {
            if (lotoCombination.contains(i)) { count += 1; }
        }

        return count;
    }

    private List<Integer> readCombinationFromBuffer(ByteBuffer clientBuffer) {

        List<Integer> combination = new ArrayList<>(7);
        IntBuffer intView = clientBuffer.asIntBuffer();

        while (intView.hasRemaining()) {
            combination.add(intView.get());
        }

        return combination;
    }

    private List<Integer> randomCombination() {

        List<Integer> randomCombination = new ArrayList<>(7);
        Random rnd = new Random();
        int insertIndx = 0;
        while (insertIndx != 7) {
            int nextInt = rnd.nextInt(1, 40);
            if (!randomCombination.contains(nextInt)) {
                randomCombination.add(insertIndx, nextInt);
                insertIndx += 1;
            }
        }
        return randomCombination;
    }
}
