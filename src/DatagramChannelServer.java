import security.EmailValidator;
import security.PasswordGenerator;
import PostgresSQL.PostgresConnector;
import activity.*;
import mail.MailSender;
import people.Human;
import rocket.Rocket;
import rocket.SpeedException;
import rocket.room.Room;
import rocket.room.Type;
import security.User;
import space.objects.Earth;
import space.objects.Moon;
import space.objects.SpaceObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentSkipListSet;

import static timeline.Timeline.setTime;
import static security.Serializer.*;
import static security.MD2Hasher.*;

public class DatagramChannelServer {
    public static void main(String[] args) throws IOException {
        DatagramChannel server = DatagramChannel.open();
        InetSocketAddress iAdd = new InetSocketAddress("localhost", 8989);
        server.bind(iAdd);
        System.out.println("Server Started: " + iAdd);

        ConcurrentSkipListSet<String> onlineUsers = new ConcurrentSkipListSet<>();
        Activity activity = Activity.getActivity();
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
        rocket.addRoom(new Room(Type.ENGINE,"Тех отсек"));
        rocket.addRoom(new Room(Type.STORAGE, "Склад"));
        ConcurrentSkipListSet<Human> passengers = rocket.getPassengers();

        Connection connection = (new PostgresConnector()).getSQLConnection();
        //activity.start(cabin, rocket, "ivan009ki@gmail.com");// RESET FROM CSV
        activity.start(cabin, rocket, connection);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            activity.save(passengers, connection);
        }));

        ConcurrentSkipListSet<User> users = new ConcurrentSkipListSet<>();
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()){
                users.add(new User(resultSet.getString("login"),resultSet.getString("password")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ClientPacket clientPacket = null;
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(650000);
            SocketAddress remoteAdd = server.receive(buffer);
            buffer.flip();
            int limits = buffer.limit();
            byte[] bytes = new byte[limits];
            buffer.get(bytes, 0, limits);
            try {
                clientPacket = (ClientPacket) deserialize(buffer.array());
            } catch (ClassNotFoundException e) {
                System.out.println("Not serialized object");
            }

            if(clientPacket.isPing()){
                onlineUsers.add(clientPacket.getUser().getLogin());
                ServerPacket serverPacket = new ServerPacket(onlineUsers, passengers);
                server.send(ByteBuffer.wrap(serialize(serverPacket)), remoteAdd);
                continue;
            }
            User user = clientPacket.getUser();
            String msg = clientPacket.getCommand();

            if (user == null) {
                registration(users, clientPacket,server,remoteAdd, connection);
                continue;
            }

            boolean authorisation = false;
            for (User user1 : users) {
                if (user.equals(user1)) {
                    authorisation = true;
                    break;
                }
            }
            if (!authorisation) {
                server.send(ByteBuffer.wrap(serialize(new ServerPacket("Wrong login or password!"))), remoteAdd);
                continue;
            }else if (msg == null){
                server.send(ByteBuffer.wrap(serialize(new ServerPacket("Welcome back "+user.getLogin()))), remoteAdd);
                continue;
            }

            System.out.println(msg);
            String command = msg.split(" ", 2)[0];
            if (msg.equalsIgnoreCase("shutdown")) {
                server.send(ByteBuffer.wrap(serialize(new ServerPacket("Server shutdown"))), remoteAdd);
                break;
            } else if (command.equals("add") || command.equals("remove_lower") || command.equals("add_if_max") || command.equals("remove")) {
                Human human = clientPacket.getHuman();
                new DatagramCommand(remoteAdd, passengers, activity, server, command, human);
            } else {
                new DatagramCommand(remoteAdd, passengers, activity, server, foodStorage, msg, rocket, user.getLogin());
            }
        }
        System.out.println("Server shutdown");
        server.close();
    }

    static void registration(ConcurrentSkipListSet<User> users, ClientPacket clientPacket, DatagramChannel server, SocketAddress remoteAdd, Connection connection) throws IOException {
        boolean userExist = false;
        EmailValidator emailValidator = new EmailValidator();
        for (User user1 : users) {
            if (user1.getLogin().equals(clientPacket.getCommand())) userExist = true;
        }
        if (userExist) {
            server.send(ByteBuffer.wrap(serialize(new ServerPacket("User with this email already exist"))), remoteAdd);
            return;
        }
        if (!emailValidator.validate(clientPacket.getCommand())) {
            server.send(ByteBuffer.wrap(serialize(new ServerPacket("Invalid email address"))), remoteAdd);
            return;
        }
        PasswordGenerator passwordGenerator = new PasswordGenerator(true, true, true);
        String password = passwordGenerator.generatePassword(16);
        User user = new User(clientPacket.getCommand(), hashString(password));
        MailSender mailSender = new MailSender(user.getLogin(), "Welcome new User,\n\n"
                + "Your password is " + password);
        mailSender.send();
        users.add(user);
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO users (login, password) VALUES('" + user.getLogin() + "','" + user.getPassword() + "')");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        server.send(ByteBuffer.wrap(serialize(new ServerPacket("Please check your email :D"))), remoteAdd);
    }
}
