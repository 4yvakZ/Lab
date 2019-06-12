import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CustomListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getX() > 0 && e.getX() < 400) {
            if (e.getY() > 0 && e.getY() < 200) {
                JOptionPane.showMessageDialog(null, "param");
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}