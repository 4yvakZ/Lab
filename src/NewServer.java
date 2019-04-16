import activity.Activity;
import people.Donut;
import people.Fool;
import people.Human;
import rocket.Rocket;
import rocket.room.Room;
import rocket.room.Type;
import space.objects.Earth;
import space.objects.Moon;
import space.objects.SpaceObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.TreeSet;

import static timeline.Timeline.increaseTime;
import static timeline.Timeline.setTime;

public class NewServer {
    public static void main(String[] args) {
        try {
            System.out.println("Welcome to Server side");
            DatagramSocket socket = new DatagramSocket(1025);
            InetAddress address = InetAddress.getByName("localhost");
            Activity activity = new Activity();
            setTime(0);
            Room cabin = new Room(Type.CABIN, "Кабина");
            Room foodStorage = new Room(Type.FOODSTORAGE, "Пищевой блок");
            SpaceObject earth = new Earth(6400);
            Moon moon = new Moon(1740, 400000, earth);
            Rocket rocket = new Rocket(12, moon);
            rocket.addRoom(cabin);
            rocket.addRoom(foodStorage);
            Fool fool = new Fool("Незнайка", 3, cabin, 10);
            Donut donut = new Donut("Пончик", 2, cabin, "Пышка");
            rocket.addPassenger(fool);
            rocket.addPassenger(donut);
            moon.orbitInfo();
            TreeSet<Human> passengers = rocket.getPassengers();
            activity.start("test.csv", cabin, rocket);
            while (true) {
                byte[] buf = new byte[65000];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String received = new String(packet.getData(), 0, packet.getLength());
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (received.equalsIgnoreCase("exit")) break;
                //System.out.println(received);
                switch (received) {
                    case "add":
                        activity.add(passengers, received);
                        break;
                    case "show":
                        activity.show(passengers);
                        break;
                    case "info":
                        activity.info(passengers);
                        break;
                    case "help":
                        //System.out.print(help);
                        String msg = "help - test";
                        byte[] buffer = msg.getBytes();
                        DatagramPacket packet1 = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                        socket.send(packet1);
                        break;
                    case "remove_lower":
                        activity.removeLower(passengers, received);
                        break;
                    case "remove":
                        activity.remove(passengers, received);
                        break;
                    case "load":
                        //activity.load(passengers, args[0], rocket);
                        break;
                    case "add_if_max":
                        activity.addIfMax(passengers, received);
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
                        System.out.println("Input error. Please try again or see 'help'.");
                        break;
                }
            }
            socket.close();
        } catch (Exception e) {
            System.out.println("Oops... Something went wrong.");
        }
    }
}
