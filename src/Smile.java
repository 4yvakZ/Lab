import rocket.room.Room;

import java.awt.*;

class Smile extends Component {
    private Color color;
    private int timeUntilHunger, thumbLength;
    private String name, user, food;
    private Room room;

    public Smile(Color color, int timeUntilHunger, int thumbLength, String name, String user, String food, Room room) {
        this.color = color;
        this.timeUntilHunger = timeUntilHunger;
        this.thumbLength = thumbLength;
        this.name = name;
        this.user = user;
        this.food = food;
        this.room = room;
    }
    public void paint(Graphics g) {
        g.setColor(color);
        int fat = (int) (thumbLength + 75);
        int eyes = (int) (timeUntilHunger*0.1 + 5);
        int mouth = (int) (timeUntilHunger*0.1 + 10);
        int x = (int)(getWidth()/2-fat/2);
        int y = (int)(getHeight()/2-fat/2);
        g.fillOval(x, y, fat, fat);
        g.setColor(Color.WHITE);
        g.drawArc(x+fat/4, (int) (y+fat*0.7), (int) (fat*0.5), mouth, 180, 180);
        g.drawOval((int) (x+0.75*fat), (int) (y+fat*0.3), eyes, eyes);
        g.drawOval((int) (x+0.25*fat), (int) (y+fat*0.3), eyes, eyes);
    }
    public int getTimeUntilHunger() {
        return timeUntilHunger;
    }
    public int getThumbLength() {
        return thumbLength;
    }
    @Override
    public String getName() {
        return name;
    }
    public String getUser() {
        return user;
    }
    public String getFood() {
        return food;
    }
    public Room getRoom() {
        return room;
    }
    @Override
    public Point getLocationOnScreen() {
        return super.getLocationOnScreen();
    }

}