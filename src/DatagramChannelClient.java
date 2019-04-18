import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class DatagramChannelClient {
    public static void main(String[] args) throws IOException {
        DatagramChannel client = DatagramChannel.open();
        client.bind(null);
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 1025);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("->");
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("close")) {
                System.out.println("Closed.");
                break;
            }
            ByteBuffer buffer = ByteBuffer.wrap(line.getBytes());
            client.send(buffer, serverAddress);
            buffer.clear();

            ByteBuffer buf = ByteBuffer.allocate(1024);
            client.receive(buf);
            String reply = new String(buf.array(), 0, buf.position());
            System.out.println(reply);

            buffer.flip();
        }
        client.close();
    }
}
