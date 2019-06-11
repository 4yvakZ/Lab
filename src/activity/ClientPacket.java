package activity;

import people.Human;
import security.User;

import java.io.Serializable;


public final class ClientPacket implements Serializable {
    private static final long serialVersionUID = 4L;
    private String command;
    private Human human;
    private User user;
    private boolean ping;

    public ClientPacket(User user){
        this.user = user;
        human = null;
        command = null;
        ping = true;
    }

    public ClientPacket(String command, Human human, User user) {
        this.command = command;
        this.human = human;
        this.user = user;
        ping = false;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
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

    public boolean isPing() {
        return ping;
    }

    public void setPing(boolean ping) {
        this.ping = ping;
    }
}

