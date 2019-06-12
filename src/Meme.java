import javax.swing.*;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Meme extends JFrame {

    public Meme(int x, int y) throws IOException {
        setTitle("ALERT! ALERT! ALERT! ALERT! ALERT! ALERT! ALERT! ALERT! ALERT! ALERT! ALERT! ALERT! ALERT! ALERT! ALERT!");
        int w = 1000;
        int h = 850;
        this.setBounds(x, y, w, h);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.WHITE);

        Dimension dimension = new Dimension(w, h);

        JLabel welcomeLabel = new JLabel("ВАС ЗАХВАТИЛИ ТЕРРОРИСТЫ ИЗ R3135!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 40));
        welcomeLabel.setForeground(Color.RED);
        welcomeLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        JLabel label = new JLabel("ОНИ ПОДОРВАЛИ СЕРВЕР!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 40));
        label.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        label.setForeground(Color.RED);

        ImageIcon image = new ImageIcon("src/meme.jpg");
        image.setImage(getScaledImage(image.getImage(),image.getIconWidth()/2, image.getIconHeight()/2));
        JLabel meme = new JLabel(image, SwingConstants.CENTER);
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(welcomeLabel);
        panel.add(label);
        panel.add( meme);
        meme.setMaximumSize(dimension);
        panel.setMaximumSize(new Dimension(w, h));
        panel.setPreferredSize(new Dimension(w,h));
        setContentPane(panel);
    }

    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
}