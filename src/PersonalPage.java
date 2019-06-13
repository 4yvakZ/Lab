import activity.ClientPacket;
import activity.ServerPacket;
import org.json.simple.parser.ParseException;
import people.Donut;
import people.Fool;
import people.Human;
import rocket.room.Room;
import security.User;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantLock;

import static security.Serializer.deserialize;
import static security.Serializer.serialize;

public class PersonalPage extends JFrame {
    private static Locale ruLocale = new Locale("ru", "RU");
    private Locale slLocale = new Locale("sl", "SL");
    private Locale plLocale = new Locale("pl", "PL");
    private Locale esLocale = new Locale("es", "ES");
    private ResourceBundle bundle;
    private JComboBox<Locale> languageComboBox = new JComboBox<>();
    private JButton sendButton, back;
    private JLabel helloLabel;
    private JLabel languageLabel;
    private JLabel commandLabel;
    private JLabel nameLabel;
    private JLabel timeUntilHungerLabel;
    private JLabel foodNameLabel;
    private JLabel thumbLengthLabel;
    private JLabel objectsLabel;
    private JLabel usersLabel;
    private JLabel roomLabel;
    private JLabel messageLabel;
    private JLabel sortLabel;
    private ConcurrentSkipListSet<Human> passengers;
    private ConcurrentSkipListSet<String> users;
    private JTable usersTable, objectsTable;
    private MyTableModel usersTableModel, objectsTableModel;
    private ReentrantLock lock = new ReentrantLock();
    private JComboBox<String> sortComboBox;
    private int sortingIndex = 0;
    private ConcurrentHashMap<String, Color> usersColours = new ConcurrentHashMap<>();
    private ArrayList<Smile> smiles = new ArrayList<>();
    private  Box cabin = Box.createHorizontalBox();
    private Box foodStorage = Box.createHorizontalBox();
    private Box engine = Box.createHorizontalBox();
    private Box storage = Box.createHorizontalBox();
    public PersonalPage(User user, DatagramSocket socket, Locale locale) {
        Thread ping = new Thread(() -> {
            while (true) {
                if(!lock.isLocked()) {
                    try {
                        send(user, socket);
                        ServerPacket serverPacket = receive(socket);
                        if (serverPacket.isPing()) {
                            if (users.equals(serverPacket.getOnlineUsers()) && passengers.equals(serverPacket.getPassengers())) {
                                continue;
                            } else {
                                users = serverPacket.getOnlineUsers();
                                usersTableModel.setData(getUsersData());
                                usersTable.repaint();
                                passengers = serverPacket.getPassengers();
                                objectsTableModel.setData(getObjectsData());
                                objectsTable.repaint();
                                ConcurrentSkipListSet<String> usernames = new ConcurrentSkipListSet<>();
                                for (Human human: passengers) {
                                    usernames.add(human.getUsername());
                                }
                                for (String username: usernames) {
                                    if(!usersColours.containsKey(username)){
                                        usersColours.put(username, Color.getHSBColor((float)(Math.random()*256),(float)(Math.random()*256),(float)(Math.random()*256)));
                                    }
                                }
                                updateSmiles();
                            }
                        }
                    } catch (IOException e) {
                        StartPage.printMeme();
                    }
                }
            }
        });

        //TODO uncomment this
        onStart(user,socket);
        ping.start();
        Font font = new Font("Arial", Font.BOLD, 14);
        bundle = ResourceBundle.getBundle("Bundle", locale);
        setTitle(bundle.getString("personal_page"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 1366, 920);

        Box mainBox = Box.createVerticalBox();

        mainBox.add(Box.createVerticalStrut(10));

        Box topBox = Box.createHorizontalBox();

        helloLabel = new JLabel(bundle.getString("hello") + ", " + user.getLogin() + "!");
        helloLabel.setFont(font);

        topBox.add(helloLabel);
        topBox.add(Box.createHorizontalGlue());

        String[] items = {
                "edit",
                "add",
                "remove",
                "add_if_max",
                "remove_lower"
        };

        JPanel topPanel = new JPanel(new GridLayout(1, 5, 5, 5));

        commandLabel = new JLabel(bundle.getString("command")+":", SwingConstants.RIGHT);
        commandLabel.setFont(font);

        topPanel.add(commandLabel);

        JComboBox<String> commandComboBox = new JComboBox<>(items);

        topPanel.add(commandComboBox);

        sendButton = new JButton(bundle.getString("send"));

        topPanel.add(sendButton);


        languageLabel = new JLabel(bundle.getString("language") + ":", SwingConstants.RIGHT);
        languageLabel.setFont(font);

        topPanel.add(languageLabel);

        languageComboBox.addItem(ruLocale);
        Locale slLocale = new Locale("sl", "SL");
        languageComboBox.addItem(slLocale);
        Locale plLocale = new Locale("pl", "PL");
        languageComboBox.addItem(plLocale);
        Locale esLocale = new Locale("es", "ES");
        languageComboBox.addItem(esLocale);
        languageComboBox.setSelectedItem(locale);

        topPanel.add(languageComboBox);

        back = new JButton(bundle.getString("back"));

        topPanel.add(back);
        topPanel.setMaximumSize(new Dimension(100, 30));

        topBox.add(topPanel);

        mainBox.add(topBox);
        mainBox.add(Box.createVerticalStrut(10));

        Box bottomBox = Box.createHorizontalBox();

        bottomBox.add(Box.createHorizontalStrut(20));

        JPanel humanInfoPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        nameLabel = new JLabel(bundle.getString("name"), SwingConstants.CENTER);
        timeUntilHungerLabel = new JLabel(bundle.getString("time_until_hunger"), SwingConstants.CENTER);
        foodNameLabel = new JLabel(bundle.getString("food_name"), SwingConstants.CENTER);
        thumbLengthLabel = new JLabel(bundle.getString("thumb_length"), SwingConstants.CENTER);
        roomLabel = new JLabel(bundle.getString("room"), SwingConstants.CENTER);
        sortLabel = new JLabel(bundle.getString("sort")+":", SwingConstants.CENTER);

        JTextField nameTextField = new JTextField("", SwingConstants.CENTER);
        JTextField timeUntilHungerTextField = new JTextField("", SwingConstants.CENTER);
        JTextField foodNameTextField = new JTextField("", SwingConstants.CENTER);
        JTextField thumbLengthTextField = new JTextField("", SwingConstants.CENTER);
        JTextField roomTextFiled = new JTextField("", SwingConstants.CENTER);

        sortComboBox = new JComboBox<>(getSortParameters(locale));

        humanInfoPanel.add(nameLabel);
        humanInfoPanel.add(nameTextField);
        humanInfoPanel.add(timeUntilHungerLabel);
        humanInfoPanel.add(timeUntilHungerTextField);
        humanInfoPanel.add(foodNameLabel);
        humanInfoPanel.add(foodNameTextField);
        humanInfoPanel.add(thumbLengthLabel);
        humanInfoPanel.add(thumbLengthTextField);
        humanInfoPanel.add(roomLabel);
        humanInfoPanel.add(roomTextFiled);
        humanInfoPanel.add(sortLabel);
        humanInfoPanel.add(sortComboBox);

        humanInfoPanel.setMaximumSize(new Dimension(150, 200));

        bottomBox.add(humanInfoPanel);
        bottomBox.add(Box.createHorizontalStrut(5));

        Box objectsBox = Box.createVerticalBox();

        objectsLabel = new JLabel(bundle.getString("objects"), SwingConstants.CENTER);
        objectsLabel.setFont(font);

        objectsBox.add(objectsLabel);
        //TODO uncomment this
        objectsTableModel = new MyTableModel(new String[]{
                bundle.getString("name"),
                bundle.getString("time_until_hunger"),
                bundle.getString("food_name"),
                bundle.getString("thumb_length"),
                bundle.getString("room"),
                bundle.getString("user"),
                bundle.getString("data")
        }, getObjectsData());
        objectsTable = new JTable(objectsTableModel);

        objectsTable.setFillsViewportHeight(true);
        JScrollPane objectsScroll = new JScrollPane(objectsTable);
        resizeColumnWidth(objectsTable);

        objectsBox.add(objectsScroll);

        bottomBox.add(objectsBox);
        bottomBox.add(Box.createHorizontalStrut(5));

        Box usersBox = Box.createVerticalBox();

        usersLabel = new JLabel(bundle.getString("users"), SwingConstants.CENTER);
        usersLabel.setFont(font);
        usersBox.add(usersLabel);
        usersBox.setPreferredSize(usersBox.getMinimumSize());

        //TODO uncomment this
        usersTableModel = new MyTableModel(new String[]{bundle.getString("user")}, getUsersData());
        usersTable = new JTable(usersTableModel);
        usersTable.setFillsViewportHeight(true);
        JScrollPane usersScroll = new JScrollPane(usersTable);
        resizeColumnWidth(usersTable);

        usersBox.add(usersScroll);

        bottomBox.add(usersBox);
        bottomBox.add(Box.createHorizontalStrut(20));

        mainBox.add(bottomBox);

        mainBox.add(Box.createHorizontalStrut(10));
        messageLabel = new JLabel("", SwingConstants.CENTER);
        mainBox.add(messageLabel);
        languageComboBox.addActionListener(actionEvent -> updateLanguage(languageComboBox.getItemAt(languageComboBox.getSelectedIndex()), user.getLogin()));

        sortComboBox.addActionListener(actionEvent -> {
            sortingIndex = sortComboBox.getSelectedIndex();
            objectsTableModel.setData(getObjectsData());
            objectsTable.repaint();
        });


        JPanel rooms = new JPanel(new GridLayout(2, 4));
        JLabel cabinLabel = new JLabel("Кабина", SwingConstants.CENTER);
        JLabel foodstorageLabel = new JLabel("Пищевой блок", SwingConstants.CENTER);
        JLabel engineLabel = new JLabel("Тех отсек", SwingConstants.CENTER);
        JLabel storageLabel = new JLabel("Склад", SwingConstants.CENTER);
        rooms.add(cabinLabel);
        rooms.add(foodstorageLabel);
        rooms.add(engineLabel);
        rooms.add(storageLabel);

        for (Human human : passengers) {
            Room room = human.getRoom();
            int thumbLength = 0;
            if(human instanceof Fool){
                thumbLength = ((Fool) human).getThumbLength();
            }
            String food = null;
            if(human instanceof Donut){
                food = ((Donut) human).getFoodName();
            }


            Smile smile = new Smile(usersColours.get(human.getUsername()), human.getTimeUntilHunger(), thumbLength, human.getName(), human.getUsername(), food, room);
            smiles.add(smile);
            switch (room.getType()){
                case FOODSTORAGE:
                    foodStorage.add(smile, JComponent.CENTER_ALIGNMENT);
                    break;
                case CABIN:
                    cabin.add(smile, JComponent.CENTER_ALIGNMENT);
                    break;
                case ENGINE:
                    engine.add(smile, JComponent.CENTER_ALIGNMENT);
                    break;
                case STORAGE:
                    storage.add(smile, JComponent.CENTER_ALIGNMENT);
                    break;
            }
        }

        rooms.add(cabin);
        rooms.add(foodStorage);
        rooms.add(engine);
        rooms.add(storage);


        //rooms.add(cabin);
        //rooms.add(cabin);
        //rooms.setBounds(0, 600, 900, 300);
        //rooms.setSize(getWidth(), 300);
        //JScrollPane scrollPane = new JScrollPane();
        //scrollPane.setBounds(0, 0, 900, 300);
        //scrollPane.add(rooms);
        mainBox.add(rooms);


        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Smile smile : smiles) {
                    if (Math.abs(e.getLocationOnScreen().x - smile.getLocationOnScreen().x-smile.getWidth()/2) < 32) {
                        if (Math.abs(e.getLocationOnScreen().y - smile.getLocationOnScreen().y- smile.getHeight()/2) < 32) {
                            messageLabel.setText("");
                            nameTextField.setText(smile.getName());
                            timeUntilHungerTextField.setText(String.valueOf(smile.getTimeUntilHunger()));
                            foodNameTextField.setText(smile.getName());
                            roomTextFiled.setText(smile.getRoom().toString());
                            thumbLengthTextField.setText(String.valueOf((smile.getThumbLength())));
                            Object[][] data = ((MyTableModel) objectsTable.getModel()).getData();
                            int kek = 0;
                            for (int i = 0; i < data.length; i++) {
                                if((data[i][0].toString()).equals(smile.getName())&&data[i][5].toString().equals(smile.getUser())){
                                    kek = i;
                                    break;
                                }
                            }
                            ListSelectionModel selectionModel = objectsTable.getSelectionModel();
                            selectionModel.setSelectionInterval(kek, kek);
                        }
                    }
                }
            }
        });

        back.addActionListener(e -> {
            StartPage window = new StartPage(socket, (Locale) languageComboBox.getSelectedItem());
            window.setVisible(true);
            dispose();
        });

        back.addActionListener(e -> {
            StartPage window = new StartPage(socket, (Locale) languageComboBox.getSelectedItem());
            window.setVisible(true);
            dispose();
        });

        objectsTable.getSelectionModel().addListSelectionListener(event -> {
            messageLabel.setText("");
            nameTextField.setText(objectsTable.getValueAt(objectsTable.getSelectedRow(), 0).toString());
            timeUntilHungerTextField.setText(objectsTable.getValueAt(objectsTable.getSelectedRow(), 1).toString());
            foodNameTextField.setText(objectsTable.getValueAt(objectsTable.getSelectedRow(), 2).toString());
            roomTextFiled.setText(objectsTable.getValueAt(objectsTable.getSelectedRow(), 4).toString());
            thumbLengthTextField.setText(objectsTable.getValueAt(objectsTable.getSelectedRow(), 3).toString());
        });

        //TODO uncomment this
        sendButton.addActionListener(e -> {
            messageLabel.setText("");
            lock.lock();
            try {
                Human human;
                String name = nameTextField.getText();
                int timeUntilHunger;
                try {
                    timeUntilHunger = Integer.parseInt(timeUntilHungerTextField.getText());
                }catch (NumberFormatException ex){
                    timeUntilHunger = 0;
                }
                String foodName = foodNameTextField.getText();
                int thumbLength;
                try {
                    thumbLength = Integer.parseInt(thumbLengthTextField.getText());
                }catch (NumberFormatException ex){
                    thumbLength = 0;
                }
                String username = user.getLogin();
                Room tempRoom;
                switch (roomTextFiled.getText()) {
                    case "Склад":
                        tempRoom = new Room(rocket.room.Type.STORAGE, "Склад");
                        break;
                    case "Пищевой блок":
                        tempRoom = new Room(rocket.room.Type.FOODSTORAGE, "Пищевой блок");
                        break;
                    case "Кабина":
                        tempRoom = new Room(rocket.room.Type.CABIN, "Кабина");
                        break;
                    case "Тех отсек":
                        tempRoom = new Room(rocket.room.Type.ENGINE, "Тех отсек");
                        break;
                    default:
                        throw new ParseException(1);
                }
                human = getHuman(name, timeUntilHunger, foodName, thumbLength, username, tempRoom);
                boolean isCommandEdit = false;
                switch (Objects.requireNonNull(commandComboBox.getSelectedItem()).toString()) {
                    case "edit":
                        isCommandEdit = true;
                        Human editableHuman;
                        Room editableRoom;
                        String editableName = objectsTable.getValueAt(objectsTable.getSelectedRow(), 0).toString();
                        try {
                            timeUntilHunger = Integer.parseInt(objectsTable.getValueAt(objectsTable.getSelectedRow(), 1).toString());
                        }catch (NumberFormatException ex){
                            timeUntilHunger = 0;
                        }
                        String editableFoodName = objectsTable.getValueAt(objectsTable.getSelectedRow(), 2).toString();
                        switch (objectsTable.getValueAt(objectsTable.getSelectedRow(), 4).toString()) {
                            case "Склад":
                                editableRoom = new Room(rocket.room.Type.STORAGE, "Склад");
                                break;
                            case "Пищевой блок":
                                editableRoom = new Room(rocket.room.Type.FOODSTORAGE, "Пищевой блок");
                                break;
                            case "Кабина":
                                editableRoom = new Room(rocket.room.Type.CABIN, "Кабина");
                                break;
                            case "Тех отсек":
                                editableRoom = new Room(rocket.room.Type.ENGINE, "Тех отсек");
                                break;
                            default:
                                throw new ParseException(1);
                        }
                        try {
                            thumbLength = Integer.parseInt(objectsTable.getValueAt(objectsTable.getSelectedRow(), 3).toString());
                        }catch (NumberFormatException ex){
                            thumbLength = 0;
                        }
                        if (timeUntilHunger < 1) throw new ParseException(1);
                        if (editableName.isEmpty()) {
                            editableHuman = new Human(timeUntilHunger, username, editableRoom);
                        } else if (thumbLength > 0) {
                            if (!editableFoodName.isEmpty()) {
                                editableHuman = new Fool(editableName, timeUntilHunger, editableRoom, editableFoodName, thumbLength, username);
                            } else {
                                editableHuman = new Fool(editableName, timeUntilHunger, editableRoom, thumbLength, username);
                            }
                        } else if (!editableFoodName.isEmpty()) {
                            editableHuman = new Donut(editableName, timeUntilHunger, editableRoom, editableFoodName, username);
                        } else {
                            editableHuman = new Human(editableName, timeUntilHunger, username, editableRoom);
                        }
                        send("remove",editableHuman,user,socket);
                        ServerPacket serverPacket = receive(socket);
                        if(!serverPacket.getAnswer().equals("Nothing was removed")){
                            send("add", human, user, socket);
                        }
                        break;
                    case "add":
                        send("add",human,user,socket);
                        break;
                    case "remove":
                        send("remove",human,user,socket);
                        break;
                    case "remove_lower":
                        send("remove_lower",human,user,socket);
                        break;
                    case "add_if_max":
                        send("add_if_max",human,user,socket);
                        break;
                }
                ServerPacket serverPacket = receive(socket);
                if(!isCommandEdit) {
                    messageLabel.setText(serverPacket.getAnswer());
                }else {
                    messageLabel.setText("Object was successfully edited");
                }
                send(user, socket);
                serverPacket = receive(socket);
                if (serverPacket.isPing()) {
                    users = serverPacket.getOnlineUsers();
                    usersTableModel.setData(getUsersData());
                    usersTable.repaint();
                    passengers = serverPacket.getPassengers();
                    objectsTableModel.setData(getObjectsData());
                    ConcurrentSkipListSet<String> usernames = new ConcurrentSkipListSet<>();
                    for (Human humankek: passengers) {
                        usernames.add(humankek.getUsername());
                    }
                    for (String usernamekek: usernames) {
                        if(!usersColours.containsKey(usernamekek)){
                            usersColours.put(usernamekek, Color.getHSBColor((float)(Math.random()*256),(float)(Math.random()*256),(float)(Math.random()*256)));
                        }
                    }
                    updateSmiles();
                }
                objectsTable.repaint();
            } catch (ParseException ex) {
                messageLabel.setText("Wrong input!");
            } catch (IOException ex) {
                StartPage.printMeme();
                dispose();
            }
            lock.unlock();
        });
        setContentPane(mainBox);


    }

    public static Human getHuman(String name, int timeUntilHunger, String foodName, int thumbLength, String username, Room tempRoom) throws ParseException {
        Human human;
        if (timeUntilHunger < 1) throw new ParseException(1);
        if (name.isEmpty()) {
            human = new Human(timeUntilHunger, username, tempRoom);
        } else if (thumbLength > 0) {
            if (!foodName.isEmpty()) {
                human = new Fool(name, timeUntilHunger, tempRoom, foodName, thumbLength, username);
            } else {
                human = new Fool(name, timeUntilHunger, tempRoom, thumbLength, username);
            }
        } else if (!foodName.isEmpty()) {
            human = new Donut(name, timeUntilHunger, tempRoom, foodName, username);
        } else {
            human = new Human(name, timeUntilHunger, username, tempRoom);
        }
        return human;
    }

    private void updateLanguage(Locale locale, String username) {
        bundle = ResourceBundle.getBundle("Bundle", locale);
        helloLabel.setText(bundle.getString("hello") + ", " + username + "!");
        languageLabel.setText(bundle.getString("language") + " ");
        sendButton.setText(bundle.getString("send"));
        back.setText(bundle.getString("back"));
        commandLabel.setText(bundle.getString("command"));
        nameLabel.setText(bundle.getString("name"));
        timeUntilHungerLabel.setText(bundle.getString("time_until_hunger"));
        foodNameLabel.setText(bundle.getString("food_name"));
        thumbLengthLabel.setText(bundle.getString("thumb_length"));
        objectsLabel.setText(bundle.getString("objects"));
        usersLabel.setText(bundle.getString("users"));
        roomLabel.setText(bundle.getString("room"));
        setTitle(bundle.getString("personal_page"));
        JTableHeader th = usersTable.getTableHeader();
        th.getColumnModel().getColumn(0).setHeaderValue( bundle.getString("user") );
        th.repaint();
        th = objectsTable.getTableHeader();
        th.getColumnModel().getColumn(0).setHeaderValue( bundle.getString("name") );
        th.getColumnModel().getColumn(1).setHeaderValue( bundle.getString("time_until_hunger") );
        th.getColumnModel().getColumn(2).setHeaderValue( bundle.getString("food_name") );
        th.getColumnModel().getColumn(3).setHeaderValue( bundle.getString("thumb_length") );
        th.getColumnModel().getColumn(4).setHeaderValue( bundle.getString("room") );
        th.getColumnModel().getColumn(5).setHeaderValue( bundle.getString("user") );
        th.getColumnModel().getColumn(6).setHeaderValue( bundle.getString("data") );
        th.repaint();
        messageLabel.setText("");
        sortLabel.setText(bundle.getString("sort")+":");
        sortComboBox.setModel(new DefaultComboBoxModel<>(getSortParameters(locale)));
        sortComboBox.setSelectedIndex(sortingIndex);
    }

    private void send(String commandWord, Human human, User user, DatagramSocket socket) throws IOException {
        ClientPacket clientPacket = new ClientPacket(commandWord, human, user);
        byte[] buf = serialize(clientPacket);
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.send(packet);
    }

    private void send(User user, DatagramSocket socket) throws IOException {
        ClientPacket clientPacket = new ClientPacket(user);
        byte[] buf = serialize(clientPacket);
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.send(packet);
    }

    private ServerPacket receive(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[65000];
        DatagramPacket packet1 = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet1);
        ServerPacket serverPacket = null;
        try {
            serverPacket = (ServerPacket) deserialize(packet1.getData());
        } catch (ClassNotFoundException e) {
            System.out.println("Not serialized object");
        }
        return serverPacket;
    }


    private void onStart(User user, DatagramSocket socket){
        lock.lock();
        try {
            send(user, socket);
            ServerPacket serverPacket = receive(socket);
            if (serverPacket.isPing()) {
                passengers = serverPacket.getPassengers();
                users = serverPacket.getOnlineUsers();
                ConcurrentSkipListSet<String> usernames = new ConcurrentSkipListSet<>();
                for (Human human: passengers) {
                    usernames.add(human.getUsername());
                }
                for (String username: usernames) {
                    usersColours.put(username, Color.getHSBColor((float)(Math.random()*256),(float)(Math.random()*256),(float)(Math.random()*256)));
                }
            }else{
                System.out.println("Somehow server did it");
            }
        } catch (IOException e) {
            printMeme();
        }
        lock.unlock();
    }

    private void checkColors(){

    }

    private Object[][] getObjectsData(){
        Object[][] data = new Object[passengers.size()][7];
        Object[] humans;
        switch (sortingIndex){
            case 0:
                humans = passengers.stream().sorted(Comparator.comparing(Human::getName)).toArray();
                break;
            case 1:
                humans = passengers.stream().sorted(Comparator.comparingInt(Human::getTimeUntilHunger)).toArray();
                break;
            case 2:
                humans = passengers.stream().sorted((o1, o2) -> {
                    if(o1 instanceof Donut){
                        if(o2 instanceof Donut){
                            return ((Donut) o1).getFoodName().compareTo(((Donut) o2).getFoodName());
                        }
                        return 100500;
                    }
                    if(o2 instanceof Donut){
                        return -100500;
                    }
                    return 0;
                }).toArray();
                break;
            case 3:
                humans = passengers.stream().sorted((o1, o2) -> {
                    if(o1 instanceof Fool){
                        if(o2 instanceof Fool){
                            return ((Fool) o1).getThumbLength()-((Fool) o2).getThumbLength();
                        }
                        return 100500;
                    }
                    if(o2 instanceof Fool){
                        return -100500;
                    }
                    return 0;
                }).toArray();
                break;
            case 4:
                humans = passengers.stream().sorted(Comparator.comparing(Human::getRoom)).toArray();
                break;
            case 5:
                humans = passengers.stream().sorted(Comparator.comparing(Human::getUsername)).toArray();
                break;
            case 6:
                humans = passengers.stream().sorted(Comparator.comparing(Human::getTime)).toArray();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sortingIndex);
        }
        for(int i = 0; i<passengers.size(); i++){
            Object human = humans[i];
            data[i][0] = ((Human)human).getName();
            data[i][1] = ((Human)human).getTimeUntilHunger();
            if(human instanceof Donut){
                data[i][2] = ((Donut)human).getFoodName();
            }else {
                data[i][2] = "";
            }
            if(human instanceof Fool){
                data[i][3] = ((Fool)human).getThumbLength();
            }else {
                data[i][3] = "";
            }
            data[i][5] = ((Human)human).getUsername();
            data[i][4] = ((Human)human).getRoom();
            data[i][6] = ((Human)human).getTime();
        }
        return data;
    }

    private void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            columnModel.getColumn(column).setPreferredWidth(200);
        }
    }

    private String[] getSortParameters(Locale locale){
        bundle = ResourceBundle.getBundle("Bundle", locale);
        return new String[]{
                bundle.getString("name"),
                bundle.getString("time_until_hunger"),
                bundle.getString("food_name"),
                bundle.getString("thumb_length"),
                bundle.getString("room"),
                bundle.getString("user"),
                bundle.getString("data")
        };
    }

    private Object[][] getUsersData(){
        Object[][] data = new Object[users.size()][1];
        Object[] usernames = users.toArray();
        for(int i = 0; i<users.size(); i++){
            data[i][0] = usernames[i];
        }
        return data;
    }

    private void printMeme(){
        Meme[] app = new Meme[0];
        try {
            app = new Meme[]{new Meme(0, 0),
                    new Meme(870, 540),
                    new Meme(900, 10),
                    new Meme(20, 600),
                    new Meme(450, 200)};
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Meme lol :
                app) {
            lol.setVisible(true);
        }
    }

    private void updateSmiles(){
        for (Smile smile :smiles) {
            smile.setVisible(false);
        }
        smiles.removeAll(smiles);
        for (Human human : passengers) {
            Room room = human.getRoom();
            int thumbLength = 0;
            if(human instanceof Fool){
                thumbLength = ((Fool) human).getThumbLength();
            }
            String food = null;
            if(human instanceof Donut){
                food = ((Donut) human).getFoodName();
            }


            Smile smile = new Smile(usersColours.get(human.getUsername()), human.getTimeUntilHunger(), thumbLength, human.getName(), human.getUsername(), food, room);
            smiles.add(smile);
            switch (room.getType()){
                case FOODSTORAGE:
                    foodStorage.add(smile, JComponent.CENTER_ALIGNMENT);
                    break;
                case CABIN:
                    cabin.add(smile, JComponent.CENTER_ALIGNMENT);
                    break;
                case ENGINE:
                    engine.add(smile, JComponent.CENTER_ALIGNMENT);
                    break;
                case STORAGE:
                    storage.add(smile, JComponent.CENTER_ALIGNMENT);
                    break;
            }
        }
    }
}