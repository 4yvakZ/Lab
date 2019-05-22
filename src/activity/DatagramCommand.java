package activity;


import org.json.simple.parser.ParseException;
import people.Human;
import rocket.Rocket;
import rocket.room.Room;

import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ConcurrentSkipListSet;

import static timeline.Timeline.getCurrentTime;
import static timeline.Timeline.increaseTime;

public final class DatagramCommand extends Thread{
    private final SocketAddress received;
    private final ConcurrentSkipListSet<Human> passengers;
    private final Activity activity;
    private final DatagramChannel channel;
    private Room foodStorage;
    private final String msg;
    private Rocket rocket;

    public DatagramCommand(SocketAddress received, ConcurrentSkipListSet<Human> passengers, Activity activity, DatagramChannel channel, String msg, Human human) throws IOException {
        this.received = received;
        this.passengers = passengers;
        this.activity = activity;
        this.channel = channel;
        this.msg = msg;
        start();
        sendAnswer(human);
    }
    public DatagramCommand(SocketAddress received, ConcurrentSkipListSet<Human> passengers, Activity activity, DatagramChannel channel, Room foodStorage, String msg, Rocket rocket, String username) throws IOException {
        this.received = received;
        this.passengers = passengers;
        this.activity = activity;
        this.channel = channel;
        this.foodStorage = foodStorage;
        this.msg = msg;
        this.rocket = rocket;
        start();
        sendAnswer(username);
    }

    private void sendAnswer(Human human) throws IOException {
        try {
            switch (msg) {
                case "add":
                    channel.send(ByteBuffer.wrap(activity.add(passengers, human).getBytes()),received);
                    break;
                case "remove_lower":
                    channel.send(ByteBuffer.wrap(activity.removeLower(passengers, human).getBytes()), received);
                    break;
                case "remove":
                    channel.send(ByteBuffer.wrap(activity.remove(passengers, human).getBytes()), received);
                    break;
                case "add_if_max":
                    channel.send(ByteBuffer.wrap(activity.addIfMax(passengers, human).getBytes()), received);
                    break;
                default:
                    channel.send(ByteBuffer.wrap("Input error. Please try again or see 'help'.".getBytes()), received);
                    break;
            }
        }catch (NullPointerException e) {
            channel.send(ByteBuffer.wrap("Wrong format!".getBytes()), received);
        }catch (ArrayIndexOutOfBoundsException e){
            channel.send(ByteBuffer.wrap("Wrong format!".getBytes()), received);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendAnswer(String username) throws IOException {
        String command = msg.split(" ", 2)[0];
        try {
            switch (command) {
                case "load":
                    channel.send(ByteBuffer.wrap(activity.load(msg.split(" ", 2)[1], rocket, foodStorage, username).getBytes()), received);
                    break;
                /*case "add":
                    channel.send(ByteBuffer.wrap(activity.add(passengers, msg.split(" ", 2)[1], foodStorage).getBytes()),received);
                    break;*/
                case "show":
                    channel.send(ByteBuffer.wrap(activity.show(passengers).getBytes()), received);
                    break;
                case "info":
                    channel.send(ByteBuffer.wrap(activity.info(passengers).getBytes()), received);
                    break;
                case "help":
                    String help = "List of Command:\nhelp\nadd\nshow\ninfo\nremove_lower {element}\nremove {element}\nadd_if_max {element}\nnext_hour\ndisconnect\nshutdown\nimport file";
                    channel.send(ByteBuffer.wrap(help.getBytes()), received);
                    break;
                /*case "remove_lower":
                    channel.send(ByteBuffer.wrap(activity.removeLower(passengers, msg.split(" ", 2)[1]).getBytes()), received);
                    break;
                case "remove":
                    channel.send(ByteBuffer.wrap(activity.remove(passengers, msg.split(" ", 2)[1]).getBytes()), received);
                    break;
                case "add_if_max":
                    channel.send(ByteBuffer.wrap(activity.addIfMax(passengers, msg.split(" ", 1)[1], foodStorage).getBytes()), received);
                    break;*/
                case "next_hour":
                    StringBuilder out = new StringBuilder();
                    passengers.stream().filter(x -> x.isHungryNow()).forEach(x ->{
                        out.append(x.goTo(foodStorage) + "\n");
                        out.append(x.eat()+ "\n");
                    });
                    if (!out.toString().isEmpty()) {
                        out.deleteCharAt(out.length() - 1);
                        channel.send(ByteBuffer.wrap(out.toString().getBytes()), received);
                    }else{
                        channel.send(ByteBuffer.wrap(((getCurrentTime()) + ": Ничего не происходит").getBytes()), received);
                    }
                    increaseTime();
                    break;
                default:
                    channel.send(ByteBuffer.wrap("Input error. Please try again or see 'help'.".getBytes()), received);
                    break;
            }
        }catch (NullPointerException e) {
            channel.send(ByteBuffer.wrap("Wrong format!".getBytes()), received);
        }catch (ParseException e) {
            channel.send(ByteBuffer.wrap("Wrong format!".getBytes()), received);
        }catch (ArrayIndexOutOfBoundsException e){
            channel.send(ByteBuffer.wrap("Wrong format!".getBytes()), received);
        }
    }
}
