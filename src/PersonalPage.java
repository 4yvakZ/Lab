import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.util.Locale;
import java.util.ResourceBundle;

public class PersonalPage extends JFrame {
    private Locale ruLocale = new Locale("ru","RU");
    private Locale slLocale = new Locale("sl","SL");
    private Locale plLocale = new Locale("pl","PL");
    private Locale esLocale = new Locale("es","ES");
    private ResourceBundle bundle;
    private JComboBox<Locale> languageComboBox = new JComboBox();
    private JLabel label, label1;
    private JButton btn_send, back;
    private JPanel contentPane;
    public PersonalPage(String username, DatagramSocket socket) {
        bundle = ResourceBundle.getBundle("Bundle", ruLocale);
        setTitle("Personal page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 900, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        label = new JLabel(bundle.getString("name") + ", " + username + "!");
        label.setBounds(15, 25, 400, 25);
        contentPane.add(label);

        JTable table = new JTable(10, 1);
        table.setBounds(640, 80, 240, 380);
        contentPane.add(table);

        //Stream<String> streamFromCollection = collection.stream();
        //collection.stream().filter(«a1»::equals).count();

        JTable table2 = new JTable(10, 5);
        table2.setBounds(260, 80, 360, 400);
        contentPane.add(table2);

        String[] items = {
                "add",
                "remove",
                "add_if_max",
                "remove_lower"
        };
        JComboBox comboBox = new JComboBox(items);
        comboBox.setBounds(325, 25, 125, 25);
        contentPane.add(comboBox);

        label1 = new JLabel(bundle.getString("language") + " ", SwingConstants.RIGHT);
        label1.setBounds(575, 25, 100, 25);
        contentPane.add(label1);

        languageComboBox.addItem(ruLocale);
        languageComboBox.addItem(slLocale);
        languageComboBox.addItem(plLocale);
        languageComboBox.addItem(esLocale);
        languageComboBox.setBounds(675, 25, 75, 25);
        contentPane.add(languageComboBox);

        languageComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateLanguage(languageComboBox.getItemAt(languageComboBox.getSelectedIndex()), username);
            }
        });

        btn_send = new JButton(bundle.getString("send"));
        btn_send.setBounds(450, 25, 150, 25);
        contentPane.add(btn_send);

        //Canvas canvas = new Draw();
        //canvas.setSize(900, 500);
        //add(canvas);

        back = new JButton(bundle.getString("exit"));
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StartPage window = new StartPage(socket);
                window.setVisible(true);
                dispose();
            }
        });
        back.setBounds(800, 0, 100, 50);
        contentPane.add(back);
    }
    private void updateLanguage(Locale locale, String username) {
        bundle = ResourceBundle.getBundle("Bundle", locale);
        label.setText(bundle.getString("name") + ", " + username + "!");
        label1.setText(bundle.getString("language") + " ");
        btn_send.setText(bundle.getString("send"));
        back.setText(bundle.getString("exit"));
    }
}