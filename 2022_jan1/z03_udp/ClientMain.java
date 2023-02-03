package z03_udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ClientMain {

    public static void main(String[] args) {

        try (DatagramSocket datagramSocket = new DatagramSocket(12345)) {

            String fileName = "file1.txt";
            int lineNumber = 1;

            byte[] fileNameData = ByteBuffer.allocate(2).putShort((short) 1).array();
        }
        catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
}
