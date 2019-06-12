import rocket.room.Room;
import rocket.room.Type;

import javax.swing.*;
import java.awt.*;

public class Draw extends Canvas {
    private int x0=0, x1=800, y0=0, y1=400;
    private int k1=0, k2=0, k3=0, k4=0;
    //private Graphics g;
    //private Box room1 = Box.createHorizontalBox();
    @Override
    public void paint(Graphics g) {
        //this.g = g;
        g.setColor(Color.BLACK);
        g.drawRect(x0, y0, x1/2, y1/2);
        g.drawRect(x1/2, y1/2, x1, y1);
        g.drawRect(x0, y0, x1, y1);
        //room1.setBounds(x0, y0, x1/2, y1/2);
    }
    public void paint_smile(int ws, int hs, Color color, int thumbLength, int fat) {
        fat = (int) (fat*0.1+75);
        //thumbLength = (int) (thumbLength*0.1+25);
        //Canvas canvas = new Draw();
        Graphics g = getGraphics();
        g.setColor(color);
        /*g.drawLine(ws, hs, ws+12, hs);
        g.drawLine(ws, hs+12, ws+12, hs+12);
        g.drawLine(ws, hs+25, ws+12, hs+25);
        g.drawLine(ws, hs+37, ws+12, hs+37);
        g.drawLine(ws, hs+50, ws+25, hs+50);
        g.drawLine(ws+12, hs, ws+12, hs-thumbLength);
        g.drawLine(ws+12, hs-thumbLength, ws+25, hs-thumbLength);
        g.drawLine(ws+25, hs-thumbLength, ws+25, hs);
        g.drawLine(ws, hs, ws, hs+50);
        g.drawArc(ws+25, hs, 5, 50, 270, 180);*/
        g.fillOval((int) (ws-75-(fat-75)/2), (int) (hs-37-(fat-75)/2), fat, fat);
        g.setColor(Color.WHITE);
        g.drawArc(ws-37-18, hs+10, 36, 10, 180, 180);
        g.drawOval(ws-27, hs-12, 5, 5);
        g.drawOval(ws-52, hs-12, 5, 5);
    }
    public void smile2room(Color color, int thumbLength, int fat, Room room) {
        if (room.getType() == Type.CABIN) {
            k1++;
            int mid_y = y1/4;
            paint_smile(k1*100, mid_y, color, thumbLength, fat);
        }
        /*if (room.name == "пищевой блок") {
            k2++;
            int mid_y = y1/4;
            paint_smile(g, x1/2 + k2*50, mid_y, color, thumbLength, fat);
        }
        if (room.name == "тех отсек") {
            k3++;
            int mid_y = 3*y1/4;
            paint_smile(g, k3*50, mid_y, color, thumbLength, fat);
        }
        if (room.name == "склад") {
            k4++;
            int mid_y = 3*y1/4;
            paint_smile(g, x1/2 + k4*50, mid_y, color, thumbLength, fat);
        }*/
    }
}
