import java.awt.*;
import java.awt.event.WindowEvent;
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
        Box mainBox = Box.createVerticalBox();

        JLabel welcomeLabel = new JLabel("Welcome!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 40));
        welcomeLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        mainBox.add(Box.createVerticalGlue());
        mainBox.add(welcomeLabel);
        mainBox.add(Box.createVerticalStrut(20));

        JPanel loginPasswordPanel = new JPanel(new GridLayout(2,2,5,10));

        JLabel loginLabel = new JLabel("Login:", SwingConstants.LEFT);
        loginLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel passwordLabel = new JLabel("Password:", SwingConstants.LEFT);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
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
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));

        mainBox.add(messageLabel);
        mainBox.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new GridLayout(1,3,5,10));

        JButton signInButton = new JButton("Sign in");
        JButton signUpButton = new JButton("Sign up");
        JButton exitButton = new JButton("Exit");

        buttonPanel.add(signInButton);
        buttonPanel.add(signUpButton);
        buttonPanel.add(exitButton);
        buttonPanel.setMinimumSize(new Dimension(400, 400));
        buttonPanel.setMaximumSize(new Dimension(400, 1000));

        mainBox.add(buttonPanel);
        mainBox.add(Box.createVerticalGlue());

        signInButton.addActionListener(actionEvent -> {
            /*PersonalPage main_window = new PersonalPage(loginInput.getText());
            main_window.setLocationRelativeTo(null);
            main_window.setVisible(true);
            dispose();*/
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Wrong login or password!!!");
        });

        signUpButton.addActionListener(actionEvent ->{
            messageLabel.setForeground(Color.BLACK);
            messageLabel.setText("Please, check your mail :D");
        });

        exitButton.addActionListener(actionEvent -> System.exit(0));
        container.add(mainBox);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                StartPage app = new StartPage();
                app.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}