import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class NewClient {
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to Client side");
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName("localhost");
        Scanner scanner = new Scanner(System.in);
        byte[] buf;
        String received = null;
        while (true) {
            /*if (!socket.isConnected()) {
                System.out.println("Connection error. Please try again later.");
                //break;
                System.exit(-1);
            }*/

            System.out.print("->");
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("disconnect")) break;
            buf = line.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 1025);
            socket.send(packet);


            byte[] buffer = new byte[65000];
            DatagramPacket packet1 = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            received = new String(packet1.getData(), 0, packet1.getLength());
            System.out.println(received);
        }
        socket.close();
    }
}
