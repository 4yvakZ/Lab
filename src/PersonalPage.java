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
    private JLabel helloLabel, languageLabel;

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

        helloLabel = new JLabel(bundle.getString("name") + ", " + username + "!");
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

        JLabel commandLabel = new JLabel("Command");
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

        JLabel nameLabel = new JLabel("Name", SwingConstants.CENTER);
        //nameLabel.setFont(font);
        JLabel timeUntilHungerLabel = new JLabel("Time Until Hunger", SwingConstants.CENTER);
        //timeUntilHungerLabel.setFont(font);
        JLabel foodNameLabel = new JLabel("Food Name", SwingConstants.CENTER);
        //foodNameLabel.setFont(font);
        JLabel thumbLengthLabel = new JLabel("Thumb Length", SwingConstants.CENTER);
        //thumbLengthLabel.setFont(font);

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

        JTable humansTable = new JTable(10, 5);

        bottomBox.add(humansTable);
        bottomBox.add(Box.createHorizontalStrut(5));

        JTable usersTable = new JTable(10, 1);

        bottomBox.add(usersTable);
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
        helloLabel.setText(bundle.getString("name") + ", " + username + "!");
        languageLabel.setText(bundle.getString("language") + " ");
        sendButton.setText(bundle.getString("send"));
        back.setText(bundle.getString("back"));
    }
}