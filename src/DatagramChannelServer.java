import activity.Activity;
import activity.DatagramCommand;
import activity.DoublePacket;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentSkipListSet;

import static timeline.Timeline.setTime;
import static activity.Serializer.*;

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
            DoublePacket doublePacket = null;
            try {
                doublePacket = (DoublePacket) deserialize(buffer.array());
            } catch (ClassNotFoundException e) {
                System.out.println("Not serialized object");
            }
            String msg = doublePacket.getCommad();
            System.out.println(msg);
            String command = msg.split(" ", 2)[0];
            if (msg.equalsIgnoreCase("shutdown")) {
                server.send(ByteBuffer.wrap("Server shutdown".getBytes(StandardCharsets.UTF_8)), remoteAdd);
                break;
            } else if(command.equals("add")|| command.equals("remove_lower")|| command.equals("add_if_max")|| command.equals("remove")) {
                //ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(msg.split(" ", 2)[1].getBytes()));
                //Human human = (Human) objectInputStream.readObject();
                Human human = doublePacket.getHuman();
                //objectInputStream.close();
                /*buffer.flip();
                limits = buffer.limit();
                bytes = new byte[limits];
                buffer.get(bytes, 0, limits);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream objos = new ObjectInputStream(bis);*/
                human.setRoom(cabin);
                new DatagramCommand(remoteAdd, passengers, activity, server, command, human);
            } else {
                //server.send(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)), remoteAdd);
                new DatagramCommand(remoteAdd, passengers, activity, server, foodStorage, msg, rocket);
            }
        }
        System.out.println("Server shutdown");
        server.close();
    }
}
