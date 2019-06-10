import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PersonalPage extends JFrame {
    private JPanel contentPane;
    public PersonalPage(String username) {
        setTitle("Personal page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 900, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel label = new JLabel("Hello, " + username + "!");
        label.setFont(new Font("Tahoma", Font.BOLD, 14));
        label.setBounds(12, 0, 400, 50);
        contentPane.add(label);

        Object[][] array = new String[][] {{"obj1"} , {"obj2"}, {"obj3"}};
        Object[] header = new String[] {"Objects"};
        JTable table = new JTable(array, header);
        table.setBounds(10, 80, 280, 380);
        contentPane.add(table);

        JTable table2 = new JTable();
        table2.setBounds(310, 80, 280, 380);
        contentPane.add(table2);

        JButton back = new JButton("Sign out");
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StartPage window = new StartPage();
                window.setVisible(true);
                setVisible(false);
            }
        });
        back.setBounds(800, 0, 100, 50);
        contentPane.add(back);
    }
}