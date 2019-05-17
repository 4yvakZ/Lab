package activity;

import people.Human;

import java.io.Serializable;


public final class DoublePacket implements Serializable {
    private String commad;
    private Human human;
    private User user;

    public DoublePacket(String commad, Human human, User user) {
        this.commad = commad;
        this.human = human;
        this.user = user;
    }

    public String getCommad() {
        return commad;
    }

    public void setCommad(String commad) {
        this.commad = commad;
    }

    public Human getHuman() {
        return human;
    }

    public void setHuman(Human human) {
        this.human = human;
    }

    public User getUser() {
        return user;
    }
}

