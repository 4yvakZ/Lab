package activity;

import org.json.simple.parser.ParseException;
import people.Human;
import rocket.Rocket;
import rocket.room.Room;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentSkipListSet;

import static timeline.Timeline.getCurrentTime;
import static timeline.Timeline.increaseTime;

public class DatagramCommand extends Thread{
    private SocketAddress received;
    private ConcurrentSkipListSet<Human> passengers;
    private Activity activity;
    private DatagramChannel channel;
    private Room foodStorage;
    private String msg;

    public DatagramCommand(SocketAddress received, ConcurrentSkipListSet<Human> passengers, Activity activity, DatagramChannel channel, Room foodStorage, String msg) throws IOException {
        this.received = received;
        this.passengers = passengers;
        this.activity = activity;
        this.channel = channel;
        this.foodStorage = foodStorage;
        this.msg = msg;
        start();
        sendAnswer();
    }

    public void sendAnswer() throws IOException {
        String command = msg.split(" ", 2)[0];
        try {
            switch (command) {
                case "add":
                    channel.send(ByteBuffer.wrap(activity.add(passengers, msg.split(" ", 2)[1], foodStorage).getBytes()),received);
                    break;
                case "show":
                    channel.send(ByteBuffer.wrap(activity.show(passengers).getBytes()), received);
                    break;
                case "info":
                    channel.send(ByteBuffer.wrap(activity.info(passengers).getBytes()), received);
                    break;
                case "help":
                    String help = "List of Command:\nhelp\nadd\nshow\ninfo\nremove_lower {element}\nremove {element}\nadd_if_max\nnext_hour\ndisconnect\nexit";
                    channel.send(ByteBuffer.wrap(help.getBytes()), received);break;
                case "remove_lower":
                    channel.send(ByteBuffer.wrap(activity.removeLower(passengers, msg.split(" ", 2)[1]).getBytes()), received);break;
                case "remove":
                    channel.send(ByteBuffer.wrap(activity.remove(passengers, msg.split(" ", 2)[1]).getBytes()), received);break;
                case "add_if_max":
                    channel.send(ByteBuffer.wrap(activity.addIfMax(passengers, msg.split(" ", 1)[1], foodStorage).getBytes()), received);break;
                case "next_hour":
                    StringBuilder out = new StringBuilder();
                    passengers.stream().filter(x -> x.isHungryNow()).forEach(x ->{
                        out.append(x.goTo(foodStorage) + "\n");
                        out.append(x.eat()+ "\n");
                    });
                    if (!out.toString().isEmpty()) {
                        out.deleteCharAt(out.length() - 1);
                    }else{
                        channel.send(ByteBuffer.wrap(((getCurrentTime() - 1) + ": Ничего не происходит").getBytes()), received);
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
