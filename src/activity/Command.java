package activity;

import org.json.simple.parser.ParseException;
import people.Human;
import rocket.Rocket;
import rocket.room.Room;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentSkipListSet;

import static timeline.Timeline.getCurrentTime;
import static timeline.Timeline.increaseTime;

public class Command extends Thread {
    private DatagramPacket received;
    private ConcurrentSkipListSet<Human> passengers;
    private Activity activity;
    private DatagramSocket socket;
    private Room foodStorage;
    private Rocket rocket;

    public Command(DatagramPacket received, ConcurrentSkipListSet<Human> passengers, Activity activity, DatagramSocket socket, Room foodStorage, Rocket rocket) throws IOException {
        this.received = received;
        this.passengers = passengers;
        this.activity = activity;
        this.socket = socket;
        this.foodStorage = foodStorage;
        this.rocket = rocket;
        start();
        sendAnswer();
    }

    public void sendAnswer() throws IOException {
        String string = new String(received.getData(), 0, received.getLength());
        String command = string.split("", 1)[0];
        byte[] buffer;
        try {
            switch (command) {
                case "add":
                    buffer = activity.add(passengers, string.split("", 1)[1]).getBytes();
                    socket.send(new DatagramPacket(buffer, buffer.length, received.getAddress(), received.getPort()));
                    break;
                case "show":
                    buffer = activity.show(passengers).getBytes();
                    socket.send(new DatagramPacket(buffer, buffer.length, received.getAddress(), received.getPort()));
                    break;
                case "info":
                    buffer = activity.info(passengers).getBytes();
                    socket.send(new DatagramPacket(buffer, buffer.length, received.getAddress(), received.getPort()));
                    break;
                case "help":
                    String help = "List of Command:\nhelp\nadd\nshow\ninfo\nremove_lower {element}\nremove {element}\nadd_if_max\nnext_hour\ndisconnect\nexit";
                    buffer = help.getBytes();
                    socket.send(new DatagramPacket(buffer, buffer.length, received.getAddress(), received.getPort()));
                    break;
                case "remove_lower":
                    buffer = activity.removeLower(passengers, string.split("", 1)[1]).getBytes();
                    socket.send(new DatagramPacket(buffer, buffer.length, received.getAddress(), received.getPort()));
                    break;
                case "remove":
                    buffer = activity.remove(passengers, string.split("", 1)[1]).getBytes();
                    socket.send(new DatagramPacket(buffer, buffer.length, received.getAddress(), received.getPort()));
                    break;
                case "add_if_max":
                    buffer = activity.addIfMax(passengers, string.split("", 1)[1]).getBytes();
                    socket.send(new DatagramPacket(buffer, buffer.length, received.getAddress(), received.getPort()));
                    break;
                case "next_hour":
                    StringBuilder out = new StringBuilder();
                    passengers.stream().filter(x -> x.isHungryNow()).forEach(x ->{
                        out.append(x.goTo(foodStorage) + "\n");
                        out.append(x.eat()+ "\n");
                    });
                    if (!out.toString().isEmpty()) {
                        out.deleteCharAt(out.length() - 1);
                        buffer = out.toString().getBytes();
                        socket.send(new DatagramPacket(buffer, buffer.length, received.getAddress(), received.getPort()));
                    }else{
                        buffer = ((getCurrentTime()-1) + ": Ничего не происходит").getBytes();
                        socket.send(new DatagramPacket(buffer, buffer.length, received.getAddress(), received.getPort()));
                    }
                    increaseTime();
                    break;
                default:
                    byte[] buf = "Input error. Please try again or see 'help'.".getBytes();
                    socket.send(new DatagramPacket(buf, buf.length, received.getAddress(), received.getPort()));
                    break;
            }
        }catch (NullPointerException e) {
            byte[] buf = "Wrong format!".getBytes();
            socket.send(new DatagramPacket(buf, buf.length, received.getAddress(), received.getPort()));
//            System.out.println("Wrong format");
        }catch (ParseException e) {
            byte[] buf = "Wrong format!".getBytes();
            socket.send(new DatagramPacket(buf, buf.length, received.getAddress(), received.getPort()));
//            System.out.println("Wrong format");
        }catch (ArrayIndexOutOfBoundsException e){
            byte[] buf = "Wrong format!".getBytes();
            socket.send(new DatagramPacket(buf, buf.length, received.getAddress(), received.getPort()));
        }
    }
}
