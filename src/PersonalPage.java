import activity.ClientPacket;
import activity.ServerPacket;
import people.Donut;
import people.Fool;
import people.Human;
import security.User;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentSkipListSet;

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
    private JLabel helloLabel, languageLabel, commandLabel, nameLabel, timeUntilHungerLabel, foodNameLabel, thumbLengthLabel, objectsLabel, usersLabel;
    private JScrollPane usersScroll, objectsScroll;
    private ConcurrentSkipListSet<Human> passengers;
    private ConcurrentSkipListSet<String> users;

    public PersonalPage(User user, DatagramSocket socket, Locale locale) {
        Thread update = new Thread(() -> {
            while (true) {
                try {
                    send(user, socket);
                    ServerPacket serverPacket = receive(socket);
                    if (serverPacket.isPing()) {
                        //TODO tables update
                        continue;
                    }else{
                        //TODO print server massege
                    }
                } catch (IOException e) {
                    printMeme();
                }
            }
        });

        //onStart(user,socket);
        Font font = new Font("Arial", Font.BOLD, 14);
        bundle = ResourceBundle.getBundle("Bundle", locale);
        setTitle(bundle.getString("personal_page"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 1080, 720);

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
        languageComboBox.addItem(slLocale);
        languageComboBox.addItem(plLocale);
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

        JPanel humanInfoPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        nameLabel = new JLabel(bundle.getString("name"), SwingConstants.CENTER);
        timeUntilHungerLabel = new JLabel(bundle.getString("time_until_hunger"), SwingConstants.CENTER);
        foodNameLabel = new JLabel(bundle.getString("food_name"), SwingConstants.CENTER);
        thumbLengthLabel = new JLabel(bundle.getString("thumb_length"), SwingConstants.CENTER);

        JTextField nameTextField = new JTextField("", SwingConstants.CENTER);
        JTextField timeUntilHungerTextField = new JTextField("", SwingConstants.CENTER);
        JTextField foodNameTextField = new JTextField("", SwingConstants.CENTER);
        JTextField thumbLengthTextField = new JTextField("", SwingConstants.CENTER);

        humanInfoPanel.add(nameLabel);
        humanInfoPanel.add(nameTextField);
        humanInfoPanel.add(timeUntilHungerLabel);
        humanInfoPanel.add(timeUntilHungerTextField);
        humanInfoPanel.add(foodNameLabel);
        humanInfoPanel.add(foodNameTextField);
        humanInfoPanel.add(thumbLengthLabel);
        humanInfoPanel.add(thumbLengthTextField);
        humanInfoPanel.setMaximumSize(new Dimension(100, 150));

        bottomBox.add(humanInfoPanel);
        bottomBox.add(Box.createHorizontalStrut(5));

        Box objectsBox = Box.createVerticalBox();

        objectsLabel = new JLabel(bundle.getString("objects"), SwingConstants.CENTER);
        objectsLabel.setFont(font);

        objectsBox.add(objectsLabel);

        JTable objectsTable = new JTable(10,6);

        /*JTable objectsTable = new JTable(getData(), new String[]{
                bundle.getString("name"),
                bundle.getString("time_until_hunger"),
                bundle.getString("food_name"),
                bundle.getString("thumb_length"),
                bundle.getString("room"),
                bundle.getString("user"),
                bundle.getString("data")
        });*/
        objectsTable.setFillsViewportHeight(true);
        objectsScroll = new JScrollPane(objectsTable);
        objectsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        objectsBox.add(objectsScroll);

        bottomBox.add(objectsBox);
        bottomBox.add(Box.createHorizontalStrut(5));

        Box usersBox = Box.createVerticalBox();

        usersLabel = new JLabel(bundle.getString("users"), SwingConstants.CENTER);
        usersLabel.setFont(font);

        usersBox.add(usersLabel);

        usersScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JTable usersTable = new JTable();

        usersScroll.add(usersTable);
        usersScroll.setMinimumSize(usersLabel.getSize());

        usersBox.add(usersScroll);

        bottomBox.add(usersBox);
        bottomBox.add(Box.createHorizontalStrut(20));

        mainBox.add(bottomBox);

        mainBox.add(Box.createHorizontalStrut(20));
        languageComboBox.addActionListener(actionEvent -> updateLanguage(languageComboBox.getItemAt(languageComboBox.getSelectedIndex()), user.getLogin()));


        //Canvas canvas = new Draw();
        //canvas.setSize(900, 500);
        //add(canvas);

        back.addActionListener(e -> {
            StartPage window = new StartPage(socket, (Locale) languageComboBox.getSelectedItem());
            window.setVisible(true);
            dispose();
        });

        setContentPane(mainBox);

        //TODO update.start();
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
        setTitle(bundle.getString("personal_page"));
        //objectsScroll.setMinimumSize(objectsLabel.getSize());
        //usersScroll.setMinimumSize(usersLabel.getSize());
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

    private Object[][] getData(){
        Object[][] data = new Object[passengers.size()][7];
        Object[] humans = passengers.stream().toArray();
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
            data[i][4] = ((Human)human).getUsername();
            data[i][5] = ((Human)human).getRoom();
            data[i][6] = ((Human)human).getTime();
        }
        return data;
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