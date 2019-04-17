import activity.Activity;
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
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentSkipListSet;

import static timeline.Timeline.increaseTime;
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
        moon.orbitInfo();
        ConcurrentSkipListSet<Human> passengers = rocket.getPassengers();
        activity.start("test.csv", cabin, rocket);

        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //receive buffer from client.
            SocketAddress remoteAdd = server.receive(buffer);
            //change mode of buffer
            buffer.flip();
            int limits = buffer.limit();
            byte bytes[] = new byte[limits];
            buffer.get(bytes, 0, limits);
            String msg = new String(bytes);

            if (msg.equalsIgnoreCase("exit")) break;
            try {
                switch (msg) {
                    case "add":
                        activity.add(passengers, msg);
                        break;
                    case "show":
                        activity.show(passengers);
                        break;
                    case "info":
                        activity.info(passengers);
                        break;
                    case "help":
                        //System.out.print(help);
                        String reply = "help message - test";
                        ByteBuffer buf = ByteBuffer.wrap(reply.getBytes());
                        server.send(buf, remoteAdd);
                        break;
                    case "remove_lower":
                        activity.removeLower(passengers, msg);
                        break;
                    case "remove":
                        activity.remove(passengers, msg);
                        break;
                    case "load":
                        //activity.load(passengers, args[0], rocket);
                        break;
                    case "add_if_max":
                        activity.addIfMax(passengers, msg);
                        break;
                    case "next_hour":
                        for (Human human : passengers) {
                            if (human.isHungryNow()) {
                                human.goTo(foodStorage);
                                human.eat();
                            }
                        }
                        increaseTime();
                        break;
                    default:
                        String err_msg = "Input error. Please try again or see 'help'.";
                        System.out.println(err_msg);
                        ByteBuffer buf1 = ByteBuffer.wrap(err_msg.getBytes(StandardCharsets.UTF_8));
                        server.send(buf1, remoteAdd);
                        break;
                }
            } catch (Exception e) {
                System.out.println("Oops... Something went wrong.");
            }
        }
        server.close();
    }
}
