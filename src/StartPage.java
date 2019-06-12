import activity.ClientPacket;
import activity.ServerPacket;
import people.Human;
import security.User;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;

import static security.MD2Hasher.hashString;
import static security.Serializer.deserialize;
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
        loginInput.setText("l21mi0@onemail.host");
        passwordInput.setText("K#jf,dFBTcS3qsr/");

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
            User user = new User(login, password);
            //TODO uncomment this
            try {
                send(null,null, user, socket);
                if(!receive(socket).equals("Welcome back "+ login)){
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Wrong login or password!!!");
                    return;
                }
            } catch (IOException e) {
                printMeme();
                return;
            }
            PersonalPage main_window = new PersonalPage(user, socket, (Locale) languageComboBox.getSelectedItem());
            main_window.setLocationRelativeTo(null);
            main_window.setVisible(true);
            dispose();
        });

        signUpButton.addActionListener(actionEvent ->{
            String login = loginInput.getText();
            //TODO uncomment this
            /*try {
                send(login, null, null, socket);
                messageLabel.setForeground(Color.BLACK);
                messageLabel.setText(receive(socket));
            } catch (IOException e) {
                printMeme();
                return;
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

    private  void send(String commandWord, Human human, User user, DatagramSocket socket) throws IOException {
        ClientPacket clientPacket = new ClientPacket(commandWord, human, user);
        byte[] buf = serialize(clientPacket);
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.send(packet);
    }

    private String receive(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[65000];
        DatagramPacket packet1 = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet1);
        ServerPacket serverPacket = null;
        try {
            serverPacket = (ServerPacket) deserialize(packet1.getData());
        } catch (ClassNotFoundException e) {
            System.out.println("Not serialized object");
        }
        if(serverPacket.isPing()){
            return null;
        }
        return serverPacket.getAnswer();
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
        setTitle(bundle.getString("start_page"));
    }
    private static void printMeme(){
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
}