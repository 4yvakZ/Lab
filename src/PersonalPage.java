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
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
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
    private JLabel helloLabel, languageLabel, commandLabel, nameLabel, timeUntilHungerLabel, foodNameLabel, thumbLengthLabel, objectsLabel, usersLabel, roomLabel, messageLabel, sortLabel;
    private ConcurrentSkipListSet<Human> passengers;
    private ConcurrentSkipListSet<String> users;
    private JTable usersTable, objectsTable;
    private MyTableModel usersTableModel, objectsTableModel;
    private ReentrantLock lock = new ReentrantLock();
    private JComboBox<String> sortComboBox;
    private int sortingIndex = 0;
    public PersonalPage(User user, DatagramSocket socket, Locale locale) {
        Thread ping = new Thread(new Runnable() {

            @Override
            public void run() {
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
                                }
                            }
                        } catch (IOException e) {
                            printMeme();
                        }
                    }
                }
            }
        });

        //TODO uncomment this
        //onStart(user,socket);
        //ping.start();
        Font font = new Font("Arial", Font.BOLD, 14);
        bundle = ResourceBundle.getBundle("Bundle", locale);
        setTitle(bundle.getString("personal_page"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 1366, 920);

        Box mainBox = Box.createVerticalBox();

        mainBox.add(Box.createVerticalStrut(20));

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
        mainBox.add(Box.createVerticalStrut(20));

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
        /*objectsTableModel = new MyTableModel(new String[]{
                bundle.getString("name"),
                bundle.getString("time_until_hunger"),
                bundle.getString("food_name"),
                bundle.getString("thumb_length"),
                bundle.getString("room"),
                bundle.getString("user"),
                bundle.getString("data")
        }, getObjectsData());*/
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
        //usersTableModel = new MyTableModel(new String[]{bundle.getString("user")}, getUsersData());
        usersTable = new JTable(usersTableModel);
        usersTable.setFillsViewportHeight(true);
        JScrollPane usersScroll = new JScrollPane(usersTable);
        resizeColumnWidth(usersTable);

        usersBox.add(usersScroll);

        bottomBox.add(usersBox);
        bottomBox.add(Box.createHorizontalStrut(20));

        mainBox.add(bottomBox);

        mainBox.add(Box.createHorizontalStrut(20));
        messageLabel = new JLabel("", SwingConstants.CENTER);
        mainBox.add(messageLabel);
        mainBox.add(Box.createVerticalStrut(20));
        languageComboBox.addActionListener(actionEvent -> updateLanguage(languageComboBox.getItemAt(languageComboBox.getSelectedIndex()), user.getLogin()));

        sortComboBox.addActionListener(actionEvent -> {
            sortingIndex = sortComboBox.getSelectedIndex();
            objectsTableModel.setData(getObjectsData());
            objectsTable.repaint();
        });

        /*Canvas canvas = new Draw(100, 37, Color.RED, 50, 80);
        Canvas canvas = new Draw();
        canvas.setSize(900, 500);
        mainBox.add(canvas);
        Draw draw = new Draw();
        Room room = new Room(rocket.room.Type.CABIN, "Кабина");
        draw.smile2room(Color.RED, 0, 0, room);
        draw.smile2room(Color.BLUE, 0, 50, room);

        Canvas canvas1 = new Draw(100, 37, Color.BLUE, 10, 5);
        mainBox.add(canvas1);*/
        /*addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getX() > 0 && e.getX() < 400) {
                    if (e.getY() > 0 && e.getY() < 200) {
                        JOptionPane.showMessageDialog(null, "param");
                    }
                }
            }
        });*/

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
        /*sendButton.addActionListener(e -> {
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
                Room room;
                switch (roomTextFiled.getText()) {
                    case "Склад":
                        room = new Room(rocket.room.Type.STORAGE, "Склад");
                        break;
                    case "Пищевой блок":
                        room = new Room(rocket.room.Type.FOODSTORAGE, "Пищевой блок");
                        break;
                    case "Кабина":
                        room = new Room(rocket.room.Type.CABIN, "Кабина");
                        break;
                    case "Тех отсек":
                        room = new Room(rocket.room.Type.ENGINE, "Тех отсек");
                        break;
                    default:
                        throw new ParseException(1);
                }
                if (timeUntilHunger < 1) throw new ParseException(1);
                if (name.isEmpty()) {
                    human = new Human(timeUntilHunger, username, room);
                } else if (thumbLength > 0) {
                    if (!foodName.isEmpty()) {
                        human = new Fool(name, timeUntilHunger, room, foodName, thumbLength, username);
                    } else {
                        human = new Fool(name, timeUntilHunger, room, thumbLength, username);
                    }
                } else if (!foodName.isEmpty()) {
                    human = new Donut(name, timeUntilHunger, room, foodName, username);
                } else {
                    human = new Human(name, timeUntilHunger, username, room);
                }
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
                }
                objectsTable.repaint();
            } catch (ParseException ex) {
                messageLabel.setText("Wrong input!");
            } catch (IOException ex) {
                printMeme();
                dispose();
            }
            lock.unlock();
        });*/
        setContentPane(mainBox);


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
        try {
            send(user, socket);
            ServerPacket serverPacket = receive(socket);
            if (serverPacket.isPing()) {
                passengers = serverPacket.getPassengers();
                users = serverPacket.getOnlineUsers();
            }else{
                System.out.println("Somehow server did it");
            }
        } catch (IOException e) {
            printMeme();
        }
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
        return new Object[][]{users.toArray()};
    }
    private void printMeme(){
        System.out.println("2Xi2s:rsiiiiiiSiSiSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS2sr;SsSi5SSsisSSS59ABBBBBBHG255555523&HBBHGX223&&32X9X9&GHAGBG\n" +
                "32S5r;siiiiiiiSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS22X5;:rrrSSsrr;;:;r52hABBBBBHG3555555223ABMBBBA322XX2XXh22&&&HMMG\n" +
                "SS5S:;siiiiiSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSssSSi,,;rS5i5Srir;;r::;S9ABBBBBA92555552223&BMMMMBHhX2222XG&X9&BBHG9\n" +
                "i:,,,riiiiiiiiSSSSSSSSSSSSSSSSSSSSSSSSSS;:;i5ii;;;rS325ss:;,::::r;S&HBBBAhX5522222229ABMMMMMMB&32X2XhAhSii225S\n" +
                "XSissiiiiiiiiSSSSSSSSSSSSSSSSSSSSSSSSS5s;:r5S5;rsrSissrr:rr:,;:;:;;sHHHAhX52299222229ABMMMMMMMBA9X223&Hh39hh93\n" +
                "XSiiiiiiiiSSSSSSSSSSSSSSSS5SSSSSSSSSS55r.;rsS;:i:;55rSis;ss:,,:,,:,::HAhX252h&G3222X9ABMMMMMM#MMBGX22X3339h93X\n" +
                "2SiiiiiiiiiiiiSiiiiisssrrsrsssrrrssiiS;.:;iisrrissss25539Xisr::....,;:925229AH&3222X9ABMMMMMMMMMMB&3222X33993X\n" +
                "XSSisiiiiiiisrrrrr;;;;rrrrrrrrrrrrrr;r,,rSr;r5939hh33Xs3hh25ir;.,.. ,:r5529ABBA322229ABMMMMMMMMBH&9X222X3399XX\n" +
                "2Siiiiissrr;::;;:;;:;rrr;rrrrrrsrrrrr;,rrr2SX&AMM#MH&G253hG3isr::   . ;529&HBB&X22229ABMMMMMBBBAh3X22X3X33993X\n" +
                "2SSisr;;;;;;;rr;;;r;r;;;;srrrrrsrrrrr:,r523hHBM####MH2G523999X5r:.  .i;:GAHBBB&X222X9ABMMMMBBH&3XX222X333399X2\n" +
                "2SSs::;;;;;;;r;;;;;;r;;r;;;rr;;:;;r;r;,sS2h&HBBMMM#BHH3G95393XXSS,.,,rsi:HBBBH&X222X9ABMMBBH&h3XX33XXX2X33933X\n" +
                "2Sis,:;:;;s;;;:;;;:;:::;;::::::::::::r;5sXhGAHHBM##MBBH939iS93X32:..,:s,.hBBBB&3222X9ABMBBHG3XX39ABBA&&933hh3X\n" +
                "2Sii,::;;:;;;:;:::::;;;;;;;:;:;;;;;;;;:Si9G&HBBMMMMMMBA935XXs5532;,.,r:rr;BBBB&X22229ABBBHh3XX3GABMMBBBGhhGG93\n" +
                "2SSi::r;;:::::;;:::;;;;;;rrrr;r;r;;;;;;ir3GAAHHHHAA&9255s,sriS93XS,.::S:r;HBBH&X52229ABHAhXXX9&HBMMMBMBA&&A&h3\n" +
                "XSSs::::,::::::;;r;;;rssrrrrrrrrrrrrr:;S;5239X233GAG5i2XGhh3G&&G3Sr::r:;:r&BBHGX22223&AG9X2X9GHBMMMBBBBAAAHAG9\n" +
                "2Sii:::::::;;;;;;r;;;;;;;;;;;;;;;;;;rr:i;,5ir2sh9hHH99&GGGAHHA&hXSr;;SrS;rXHBH&X2222399XXXXXX39GAHBBBBBHHHBA&h\n" +
                "2Sis::,,::::;;;;::::;;;rrrr;;;r;rrrrrs,:i:sh9hhhGAHHhhGAHMMMBAG32isrriii;;s5HHGX222222222X3XXXXXX339hGhAHHHH&h\n" +
                "2iSs,::::::;:::;;;;;rrr;;;;;;r;;;;;;rS:,is:9&AAHH&HBGGhh&AHHAGhX22Ss:ii;r::2BHhX222222222X9X33hGh3X22XXAAAHH&9\n" +
                "2Sis:;:::;::::;:;;;;;;;:::,,,:::::;:;2rssrsiGAHHA&&MA&9h2G&hhh3XXXSS;5sr5::93AGX52222222X399GAHBBHA&hXX&&G&&h9\n" +
                "2Sis:::::;;;;:;:::,.   ....,,,,:,,,,,2;5ssrsSG&&GG&A&XrrSX3X32G3X2isrsrrS:i29Ah2222222X529hhAHBBBBH&h9XhhhGGh3\n" +
                "2iis::,::;;:::,.            ,,rA&&G5:2;5r;rS,r99Xh3Sr;r;i55ss53h22s;.sis2;;ri    552225S3Gh9GHHHA&h3XXXhh9hh93\n" +
                "2iis,:::;::,,         ...,,3BHBBBHAh&G93rrr,r X3X5iiiS25XrS3hGXh25r::rs;;. ,.    rs22255993XX&Ah22222XX993h993\n" +
                "2iis::;:,,,        ..,,,,,:AAHBHHA9G3hGi2sS:r  59ii9S999GAGhhhh22S::;,ir., .    :, . ,2i2X2SS2X5iS52X3h333993X\n" +
                "2iis::,..       ...,,....,X&&&Xr;2X2sXih29&i;, ,,5S9hGGGh3339G32i;..:.:..  ..        ::i;rir;;rriS23G&H93X333X\n" +
                "2iis,,.       .,,,.....,::&AhS:r;;r,,,,sr2hSs:,,;rr23XXXhhGGh95s,,,:, ,.  , .     ,.;;sr::;;;;;rrrXAABB93X3332\n" +
                "2iis,.      ...,. .,,,.,:G&&3rrsssr,,.:rr392S:.,:s:i23Xhh3h32Sr.,:;rr: ., ,.     ,.rr2s;;;;:::::;:;rrrsXXX3332\n" +
                "5isr. .     .....,,....,,&A&3Xsrrsr;,:;:ih325::.,,,,ri52555Ss,,::;rsii;,i2.   , ::;rSs;;rr;;;;:::::::;;;;r;i32\n" +
                "2isr .      ....,,..,..i;&HAhSsiiS59h2sG&GG3;::.. , ...,;.   ,::;rSi,.2,  ,;,: .irsis:ssrrr;;:::;:;;:::::;;rr;\n" +
                "2isr      .....,,..,,.,2iAHAGXii5XhA&A&AA&9;:,.. , ,:;:,i..   ..rs, .:r2;:sr  rsrSisr;ssrrsr;;:;:;r;:::;;::;;r\n" +
                "  .;     ......,...,...:XAHH&9Si2hGABH&hhXr.    ..  rr;;:h,; ,,, ;:;rs53::;r,,r2i5sr;:ssrrrr;;;r.rsr;::;;:::;;\n" +
                "       ....... ........,5&HH&&XS2&AAA&9i5;.  .  .,. :r,;:,,,.;::,rrrss9;:;:;SsiSrsrr:;ssrssr;;;s,;ss;;;;:;;;;:\n" +
                "       ...........,....;srAH&&9S2hA&G95;,.  ., .:., ,,,:;r;;S:rsssrsr;:.,::;ssSrrssr:;ss;srrr;;s,:rsr;;;;:::rr\n" +
                "      ............ ....s;ii&&hG233&h35; .....  .. .. ,,:;;i;;;5@Biss;:;;sisisir;srs:,rsrrsrrrr;S:,sir;;;;;:;S5\n" +
                "        .........,,..,.r;riiiisS2i5is;; ,:  .  ..  , . ::rir;Ss;issr:,:ssSSiss;ssrr,,;srrsrssrrSr.is;rr:;r:;SS\n" +
                "       ...............Srssrsr5::;::,,::. .           ..:;ris;r:sssr::::i5iSiirrirs;::rsrrssssssSi is;sr;r;ssSs\n" +
                "        ...........,.:Srssrrr;:;r.,...  .  .       . .:,;rsiss;isir:,S;i2iiiirirss;,:rsssisiiisS5.S:ri;rrrS2ss\n" +
                "         .......,...,Si;r,;;9X:,,,:,.   ,          : .::;rr5s;ii3A;:,5;S5siississr;::rssSiisiSiSS,r,sSrr;22Si;\n" +
                "         .....,,....:r2s;5rr;r;..      .,.      .  :, :;;;iSsSr.rs,,.5s2SiSiririsrr::riiSSsiiiSSi,.:r5s;SSSis.\n" +
                "  s        .........ri5Srr:i ::,,..             .  :;.:;:;isSsr,rr,r i;ssissrsrs;r;:,;siSsrrssiss: ,;i;ii5is;,\n" +
                "\n" +
                "█──█─███───██─█────███─████---████─████─████─████─███─████---███─████─█──█---████──███─████─███─█\n" +
                "█──█─█────█─█─█──────█─█──█---█──█─█──█─█──█─█──█──█──█──█---─█──█──█─█─█─---█──██───█─█──█──█──█───\n" +
                "████─███─█──█─████─███─████---█──█─████─█──█─█─────█──█──█---─█──████─██──---████──███─████──█──████\n" +
                "█──█─█───█──█─█──█───█──█─█---█──█─█────█──█─█──█──█──█──█---─█──█──█─█─█─---█──██───█──█─█──█──█──█\n" +
                "█──█─███─█──█─████─███──█─█---█──█─█────████─████──█──████---─█──█──█─█──█---████──███──█─█──█──████\n" +
                "\n" +
                "---█──█---████──█───█─█──█───██─█──██──█──█─█──█─███─█───---████─███─████─████──███─████\n" +
                "---█──█---█──██─█───█─█─█───█─█─█─█──█─█──█─█──█──█──█───---█──█─█───█──█─█──██─█───█──█\n" +
                "---█─██---████──███─█─██───█──█─████─█─████─█─██──█──████---█────███─████─████──███─████\n" +
                "---██─█---█──██─█─█─█─█─█──█──█─█─█──█────█─██─█──█──█──█---█──█─█───█────█──██─█───█\n" +
                "---█──█---████──███─█─█──█─█──█─█──██─────█─█──█──█──████---████─███─█────████──███─█\n"+
                "Power the server!");
    }
}