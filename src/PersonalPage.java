import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.util.Locale;
import java.util.ResourceBundle;

public class PersonalPage extends JFrame {
    private static Locale ruLocale = new Locale("ru", "RU");
    private Locale slLocale = new Locale("sl", "SL");
    private Locale plLocale = new Locale("pl", "PL");
    private Locale esLocale = new Locale("es", "ES");
    private ResourceBundle bundle;
    private JComboBox<Locale> languageComboBox = new JComboBox<>();
    private JButton sendButton, back;
    private JLabel helloLabel, languageLabel, commandLabel, nameLabel, timeUntilHungerLabel, foodNameLabel, thumbLengthLabel, objectsLabel, usersLabel;

    public PersonalPage(String username, DatagramSocket socket, Locale locale) {
        Font font = new Font("Arial", Font.BOLD, 14);
        bundle = ResourceBundle.getBundle("Bundle", locale);
        setTitle(bundle.getString("personal_page"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 900, 500);

        Box mainBox = Box.createVerticalBox();

        mainBox.add(Box.createVerticalStrut(20));

        Box topBox = Box.createHorizontalBox();

        topBox.add(Box.createVerticalStrut(20));

        helloLabel = new JLabel(bundle.getString("hello") + ", " + username + "!");
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

        commandLabel = new JLabel(bundle.getString("command"));
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
        topPanel.setMaximumSize(new Dimension(1000, 50));
        topBox.add(Box.createHorizontalStrut(20));

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

        bottomBox.add(humanInfoPanel);
        bottomBox.add(Box.createHorizontalStrut(5));

        Box objectsBox = Box.createVerticalBox();

        objectsLabel = new JLabel(bundle.getString("objects"), SwingConstants.CENTER);
        objectsLabel.setFont(font);

        objectsBox.add(objectsLabel);

        JTable humansTable = new JTable(10, 5);

        objectsBox.add(humansTable);

        bottomBox.add(objectsBox);
        bottomBox.add(Box.createHorizontalStrut(5));

        Box usersBox = Box.createVerticalBox();

        usersLabel = new JLabel(bundle.getString("users"), SwingConstants.CENTER);
        usersLabel.setFont(font);

        usersBox.add(usersLabel);

        JTable usersTable = new JTable(10, 1);

        usersBox.add(usersTable);

        bottomBox.add(usersBox);
        bottomBox.add(Box.createHorizontalStrut(20));

        mainBox.add(bottomBox);

        mainBox.add(Box.createHorizontalStrut(20));
        languageComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateLanguage(languageComboBox.getItemAt(languageComboBox.getSelectedIndex()), username);
            }
        });


        //Canvas canvas = new Draw();
        //canvas.setSize(900, 500);
        //add(canvas);

        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StartPage window = new StartPage(socket, (Locale) languageComboBox.getSelectedItem());
                window.setVisible(true);
                dispose();
            }
        });
        back.setBounds(800, 0, 100, 50);

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
    }
}