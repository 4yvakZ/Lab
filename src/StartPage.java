import activity.ClientPacket;
import people.Human;
import security.User;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;

import static security.MD2Hasher.hashString;
import static security.Serializer.serialize;

public class StartPage extends JFrame {
    private Locale ruLocale = new Locale("ru","RU");
    private Locale slLocale = new Locale("sl","SL");
    private Locale plLocale = new Locale("pl","PL");
    private Locale esLocale = new Locale("es","ES");
    private ResourceBundle bundle;
    private JComboBox<Locale> languageComboBox = new JComboBox<>();
    private JLabel welcomeLabel, loginLabel, passwordLabel,infoLabel;
    private JButton signInButton, signUpButton, exitButton;
    public StartPage(DatagramSocket socket, Locale locale) {
        bundle = ResourceBundle.getBundle("Bundle", locale);
        setTitle(bundle.getString("start_page"));
        int w = 600;
        int h = 400;
        this.setBounds(100, 100, w, h);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Font font = new Font("Arial", Font.BOLD, 14);

        Box mainBox = Box.createVerticalBox();
        mainBox.add(Box.createVerticalGlue());

        welcomeLabel = new JLabel(bundle.getString("welcome") + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 40));
        welcomeLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        mainBox.add(welcomeLabel);
        mainBox.add(Box.createVerticalStrut(20));

        JPanel loginPasswordPanel = new JPanel(new GridLayout(2,2,5,10));

        loginLabel = new JLabel(bundle.getString("login") + ":", SwingConstants.LEFT);
        loginLabel.setFont(font);
        passwordLabel = new JLabel(bundle.getString("password") + ":", SwingConstants.LEFT);
        passwordLabel.setFont(font);
        JTextField loginInput = new JTextField("", SwingConstants.CENTER);
        JPasswordField passwordInput = new JPasswordField("", SwingConstants.CENTER);

        loginPasswordPanel.add(loginLabel);
        loginPasswordPanel.add(loginInput);
        loginPasswordPanel.add(passwordLabel);
        loginPasswordPanel.add(passwordInput);


        loginPasswordPanel.setMaximumSize(new Dimension(300,200));

        mainBox.add(loginPasswordPanel);
        mainBox.add(Box.createVerticalStrut(20));

        JLabel messageLabel = new JLabel("",SwingConstants.CENTER);
        messageLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        messageLabel.setFont(font);

        mainBox.add(messageLabel);
        mainBox.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new GridLayout(1,3,5,10));

        signInButton = new JButton(bundle.getString("sign_in"));
        signUpButton = new JButton(bundle.getString("sign_up"));
        exitButton = new JButton(bundle.getString("exit"));

        buttonPanel.add(signInButton);
        buttonPanel.add(signUpButton);
        buttonPanel.add(exitButton);
        buttonPanel.setMinimumSize(new Dimension(350, 600));
        buttonPanel.setMaximumSize(new Dimension(500, 1000));

        mainBox.add(buttonPanel);
        mainBox.add(Box.createVerticalStrut(20));

        languageComboBox.addItem(ruLocale);
        languageComboBox.addItem(slLocale);
        languageComboBox.addItem(plLocale);
        languageComboBox.addItem(esLocale);
        languageComboBox.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        languageComboBox.setMaximumSize(new Dimension(100,20));
        languageComboBox.setSelectedItem(locale);

        languageComboBox.addActionListener(actionEvent -> updateLanguage(languageComboBox.getItemAt(languageComboBox.getSelectedIndex())));

        mainBox.add(languageComboBox);
        mainBox.add(Box.createVerticalStrut(20));

        infoLabel = new JLabel(bundle.getString("info"),SwingConstants.CENTER);
        infoLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 10));

        mainBox.add(infoLabel);
        mainBox.add(Box.createVerticalGlue());


        signInButton.addActionListener(actionEvent -> {
            String login = loginInput.getText();
            String password = hashString(new String(passwordInput.getPassword()));
            /*try {
                TODO send(null,null, new User(login, password), socket);
                if(!receive(socket).equals("Welcome back "+ login)){
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Wrong login or password!!!");
                    return;
                }
            } catch (IOException e) {
                printMeme();
            }*/
            PersonalPage main_window = new PersonalPage(loginInput.getText(), socket, (Locale) languageComboBox.getSelectedItem());
            main_window.setLocationRelativeTo(null);
            main_window.setVisible(true);
            dispose();
        });

        signUpButton.addActionListener(actionEvent ->{
            String login = loginInput.getText();
            /* TODO
            try {
                send(login, null, null, socket);
                messageLabel.setForeground(Color.BLACK);
                messageLabel.setText(receive(socket));
            } catch (IOException e) {
                printMeme();
            }*/
        });

        exitButton.addActionListener(actionEvent -> System.exit(0));
        setContentPane(mainBox);
    }

    public static void main(String[] args) {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost");
            socket.connect(address, 8989);
            EventQueue.invokeLater(() -> {
                try {
                    Locale locale = new Locale("ru", "RU");
                    StartPage app = new StartPage(socket, locale);
                    app.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }catch (SocketException | UnknownHostException e) {
            printMeme();
        }
    }

    private static void send(String commandWord, Human human, User user, DatagramSocket socket) throws IOException {
        ClientPacket clientPacket = new ClientPacket(commandWord, human, user);
        byte[] buf = serialize(clientPacket);
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.send(packet);
    }

    private static String receive(DatagramSocket socket) throws IOException {
        String received;
        byte[] buffer = new byte[65000];
        DatagramPacket packet1 = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet1);
        received = new String(packet1.getData(), 0, packet1.getLength());
        System.out.println(received);
        return received;
    }
    private void updateLanguage(Locale locale) {
        bundle = ResourceBundle.getBundle("Bundle", locale);
        welcomeLabel.setText(bundle.getString("welcome") + "!");
        loginLabel.setText(bundle.getString("login"));
        passwordLabel.setText(bundle.getString("password"));
        signInButton.setText(bundle.getString("sign_in"));
        signUpButton.setText(bundle.getString("sign_up"));
        exitButton.setText(bundle.getString("exit"));
        infoLabel.setText(bundle.getString("info"));
    }
    private static void printMeme(){
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