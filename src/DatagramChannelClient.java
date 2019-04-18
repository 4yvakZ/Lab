import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class DatagramChannelClient {
    public static void main(String[] args) throws IOException {
        DatagramChannel client = null;
        client = DatagramChannel.open();
        client.bind(null);
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 1025);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("->");
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("close")) break;
            ByteBuffer buffer = ByteBuffer.wrap(line.getBytes());
            client.send(buffer, serverAddress);
            buffer.clear();
            /*if (!client.isConnected()) {
                System.out.println("Disconnected.");
                break;
            }*/
            client.receive(buffer);
            String reply = new String(buffer.array(), StandardCharsets.UTF_8);
            System.out.println(reply);
            buffer.flip();
        }
        client.close();
    }
}
