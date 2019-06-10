import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class StartPage extends JFrame {
    public StartPage() {
        super("Start page");
        int w = 400;
        int h = 300;
        this.setBounds(100, 100, w, h);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel label = new JLabel("Welcome!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        label.setBounds(0, 0, w, h/4);
        panel.add(label);

        JLabel label1 = new JLabel("Login ", SwingConstants.RIGHT);
        label1.setBounds(0, 4*h/16, w/4, h/8);
        panel.add(label1);

        JLabel label2 = new JLabel("Password ", SwingConstants.RIGHT);
        label2.setBounds(0, 8*h/16, w/4, h/8);
        panel.add(label2);

        JTextField input1 = new JTextField("", SwingConstants.CENTER);
        input1.setBounds(w/4, 4*h/16, w/2, h/8);
        panel.add(input1);

        JPasswordField input2 = new JPasswordField("", SwingConstants.CENTER);
        input2.setBounds(w/4, 8*h/16, w/2, h/8);
        panel.add(input2);

        JButton button1 = new JButton("Sign in");
        button1.setBounds(0, 3*h/4, w/3, h/4);
        panel.add(button1);

        JButton button2 = new JButton("Sign up");
        button2.setBounds(w/3, 3*h/4, w/3, h/4);
        panel.add(button2);

        JButton button3 = new JButton("Exit");
        button3.setBounds(2*w/3, 3*h/4, w/3, h/4);
        panel.add(button3);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                PersonalPage main_window = new PersonalPage(input1.getText());
                main_window.setLocationRelativeTo(null);
                main_window.setVisible(true);
                setVisible(false);
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        container.add(panel);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    StartPage app = new StartPage();
                    app.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}