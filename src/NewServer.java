import activity.Activity;
import activity.Command;
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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentSkipListSet;

import static timeline.Timeline.setTime;

public class NewServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to Server side");
        DatagramSocket socket = new DatagramSocket(1025);
        InetAddress address = InetAddress.getByName("localhost");
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
        //System.out.println(rocket.toString() + ".");
        //moon.orbitInfo();
        ConcurrentSkipListSet<Human> passengers = rocket.getPassengers();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            activity.save(passengers);
        }));

        activity.start(cabin, rocket);
        while (true) {
            byte[] buf = new byte[65000];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String received = new String(packet.getData(), 0, packet.getLength());
            if (received.equalsIgnoreCase("exit")) {
                buf = "Server shutdown".getBytes();
                socket.send(new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort()));
                break;
            } else {
                new Command(packet, passengers, activity, socket, foodStorage, rocket);
                //socket.send(packet);
            }
            //System.out.println(received);
        }
        socket.close();
    }
}
