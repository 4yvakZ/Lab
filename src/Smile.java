import rocket.room.Room;

import java.awt.*;

class Smile extends Component {
    private Color color;
    private int timeUntilHunger, thumbLength;

    public Smile(Color color, int timeUntilHunger, int thumbLength) {
        this.color = color;
        this.timeUntilHunger = timeUntilHunger;
        this.thumbLength = thumbLength;
    }
    public void paint(Graphics g) {
        g.setColor(color);
        int fat = (int) (thumbLength + 75);
        int eyes = (int) (timeUntilHunger*0.1 + 5);
        int mouth = (int) (timeUntilHunger*0.1 + 10);
        g.fillOval(0, 0, fat, fat);
        g.setColor(Color.WHITE);
        g.drawArc(fat/2, (int) (fat*0.7), (int) (fat*0.5), mouth, 180, 180);
        g.drawOval((int) (0.75*fat), (int) (fat*0.3), eyes, eyes);
        g.drawOval((int) (0.25*fat), (int) (fat*0.3), eyes, eyes);
    }
}
