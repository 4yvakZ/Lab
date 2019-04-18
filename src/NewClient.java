import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import people.Donut;
import people.Fool;
import people.Human;
import rocket.room.Room;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import static java.lang.System.out;

public class NewClient {
    private Room room;
    public static void main(String[] args) throws IOException {
        NewClient nc = new NewClient();

        out.println("Welcome to Client side");
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

            out.print("->");
            String line = scanner.nextLine();
            String words[] = line.split(" ");
            if (words[0].equalsIgnoreCase("readJSON"))
            {
                Human human = null;
                try {
                    human = nc.readJSON(words[1]);
                } catch (ParseException e) {
                    System.out.println("Error");
                }
                /*ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
                String str = words[1];
                oos.writeObject(human);
                oos.flush();
                oos.close();
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                try {
                    Human human1 = (Human) objectInputStream.readObject();
                } catch (ClassNotFoundException e) {
                    System.out.println("Error");
                }
                objectInputStream.close();
                buf = h.getBytes();*/
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream objos = new ObjectOutputStream(bos);
                objos.writeObject(human);
                objos.close();

                byte[] buf1 = bos.toByteArray();
                DatagramPacket packet = new DatagramPacket(buf1, buf1.length, address, 1025);
                socket.send(packet);
            } else {
                if (line.equalsIgnoreCase("disconnect")) break;
                buf = line.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 1025);
                socket.send(packet);
            }

            byte[] buffer = new byte[65000];
            DatagramPacket packet1 = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            received = new String(packet1.getData(), 0, packet1.getLength());
            out.println(received);
        }
        socket.close();
    }
    private Human readJSON(String string) throws ParseException, NullPointerException {
        Human human;

        JSONObject jo = (JSONObject) new JSONParser().parse(string);
        String name;
        if(jo.get("name") instanceof String) {
            name = (String) jo.get("name");
        }else{
            throw new ParseException(0);
        }
        String foodName;
        try {
            if(jo.get("foodName") instanceof String) {
                foodName = (String) jo.get("foodName");
            }else{
                throw new ParseException(0);
            }
        }catch (NullPointerException e){
            foodName = "";
        }
        int timeUntilHunger;
        if(jo.get("timeUntilHunger") instanceof Long) {
            timeUntilHunger = ((Long) jo.get("timeUntilHunger")).intValue();
        }else{
            throw new ParseException(0);
        }
        int thumbLength;
        try {
            if(jo.get("thumbLength") instanceof Long) {
                thumbLength = ((Long)jo.get("thumbLength")).intValue();
            }else {
                throw new ParseException(0);
            }
        }catch (NullPointerException e){
            thumbLength = 0;
        }
        if (timeUntilHunger < 1) throw new ParseException(1);
        if (name.isEmpty()){
            human = new Human(timeUntilHunger, room);
        }else if (thumbLength > 0){
            if (!foodName.isEmpty()) {
                human = new Fool(name, timeUntilHunger, room, foodName, thumbLength);
            }else{
                human = new  Fool(name, timeUntilHunger, room, thumbLength);
            }
        }else if (!foodName.isEmpty()){
            human = new Donut(name, timeUntilHunger, room, foodName);
        }else{
            human = new Human(name, timeUntilHunger, room);
        }
        return human;
    }
}
