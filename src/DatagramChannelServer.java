import activity.Activity;
import activity.DatagramCommand;
import people.Donut;
import people.Fool;
import people.Human;
import rocket.Rocket;
import rocket.SpeedException;
import rocket.room.Room;
import rocket.room.Type;
import space.objects.Earth;
import space.objects.Moon;
import space.objects.SpaceObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentSkipListSet;

import static timeline.Timeline.setTime;

public class DatagramChannelServer {
    public static void main(String[] args) throws IOException {
        DatagramChannel server = DatagramChannel.open();
        InetSocketAddress iAdd = new InetSocketAddress("localhost", 8989);
        server.bind(iAdd);
        System.out.println("Server Started: " + iAdd);


        Activity activity = new Activity();
        setTime(0);
        Room cabin = new Room(Type.CABIN, "Кабина");
        Room foodStorage = new Room(Type.FOODSTORAGE, "Пищевой блок");
        SpaceObject earth = new Earth(6400);
        Moon moon = new Moon(1740, 400000, earth);
        Rocket rocket = null;
        try {
            rocket = new Rocket(12, moon);
        } catch (SpeedException e) {
            e.printStackTrace();
        }
        rocket.addRoom(cabin);
        rocket.addRoom(foodStorage);
        Fool fool = new Fool("Незнайка", 3, cabin, 10);
        Donut donut = new Donut("Пончик", 2, cabin, "Пышка");
        rocket.addPassenger(fool);
        rocket.addPassenger(donut);
        ConcurrentSkipListSet<Human> passengers = rocket.getPassengers();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            activity.save(passengers);
        }));

        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(650000);
            //receive buffer from client.
            SocketAddress remoteAdd = server.receive(buffer);
            //change mode of buffer
            buffer.flip();
            int limits = buffer.limit();
            byte bytes[] = new byte[limits];
            buffer.get(bytes, 0, limits);
            String msg = new String(bytes);
            try {
                System.out.println(msg);
                if (msg.equalsIgnoreCase("shutdown")) {
                    server.send(ByteBuffer.wrap("Server shutdown".getBytes(StandardCharsets.UTF_8)), remoteAdd);
                    break;
                } else {
                    //server.send(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)), remoteAdd);
                    new DatagramCommand(remoteAdd, passengers, activity, server, foodStorage, msg);
                }
            } catch (Exception e) {
                System.out.println("Oops... Something went wrong.");
            }
        }
        System.out.println("Server shutdown");
        server.close();
    }
}
