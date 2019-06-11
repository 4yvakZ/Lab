import java.awt.*;

public class Draw extends Canvas {
    /*public void paint(String name, int thumbLength, int timeUntilHunger, String username, String foodName) {

    }*/
    @Override
    public void paint(Graphics g) {
        int hm = 200, wm = 200, hs = 100, ws = 25;
        g.setColor(new Color(255, 0, 0));
        g.drawLine(ws, hs, ws+25, hs);
        g.drawLine(ws, hs+25, ws+25, hs+25);
        g.drawLine(ws, hs+50, ws+25, hs+50);
        g.drawLine(ws, hs+75, ws+25, hs+75);
        g.drawLine(ws, hs+100, ws+50, hs+100);
        g.drawLine(ws+25, hs, ws+25, hs-50);
        g.drawLine(ws+25, hs-50, ws+50, hs-50);
        g.drawLine(ws+50, hs-50, ws+50, hs);
        g.drawLine(ws, hs, ws, hs+100);
        g.drawArc(ws+50, hs, 10, hs, 270, 180);
        //g.fillOval();
    }
}
