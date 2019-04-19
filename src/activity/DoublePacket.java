package activity;

import people.Human;

import java.io.Serializable;


public final class DoublePacket implements Serializable {
    private String commad;
    private Human human;

    public DoublePacket(String commad, Human human) {
        this.commad = commad;
        this.human = human;
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
}

