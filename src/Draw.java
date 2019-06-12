import javax.swing.*;
import java.awt.*;

public class Draw extends Canvas {
    @Override
    public void paint(Graphics g) {
        //JPanel panel = new JPanel(new GridLayout(2, 2));
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 400, 200);
        g.drawRect(400, 200, 800, 400);
        g.drawRect(0, 0, 800, 400);
        paint_smile(g, 100, 100, Color.RED, 0, 0);
        paint_smile(g, 300, 100, Color.BLUE, 5, 30);
    }
    public void paint_smile(Graphics g, int ws, int hs, Color color, int thumbLength, int fat) {
        fat = (int) (fat*0.1+75);
        thumbLength = (int) (thumbLength*0.1+25);
        g.setColor(color);
        g.drawLine(ws, hs, ws+12, hs);
        g.drawLine(ws, hs+12, ws+12, hs+12);
        g.drawLine(ws, hs+25, ws+12, hs+25);
        g.drawLine(ws, hs+37, ws+12, hs+37);
        g.drawLine(ws, hs+50, ws+25, hs+50);
        g.drawLine(ws+12, hs, ws+12, hs-thumbLength);
        g.drawLine(ws+12, hs-thumbLength, ws+25, hs-thumbLength);
        g.drawLine(ws+25, hs-thumbLength, ws+25, hs);
        g.drawLine(ws, hs, ws, hs+50);
        g.drawArc(ws+25, hs, 5, 50, 270, 180);
        g.drawOval((int) (ws-75-(fat-75)/2), (int) (hs-37-(fat-75)/2), fat, fat);
        g.drawArc(ws-37-18, hs+10, 36, 10, 180, 180);
        g.drawOval(ws-27, hs-12, 5, 5);
        g.drawOval(ws-52, hs-12, 5, 5);
    }
}
