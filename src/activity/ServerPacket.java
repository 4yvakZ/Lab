package activity;

import people.Human;
import security.User;

import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListSet;

public class ServerPacket implements Serializable {
    private static final long serialVersionUID = 4L;
    private ConcurrentSkipListSet<String> onlineUsers;
    private ConcurrentSkipListSet<Human> passengers;
    private String answer;
    private boolean ping;

    public ServerPacket(ConcurrentSkipListSet<String> onlineUsers, ConcurrentSkipListSet<Human> passengers){
        this.onlineUsers = onlineUsers;
        this.passengers = passengers;
        ping = true;
    }
    public ServerPacket(String answer){
        onlineUsers = null;
        passengers = null;
        ping = false;
        this.answer = answer;
    }

    public ConcurrentSkipListSet<String> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(ConcurrentSkipListSet<String> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public ConcurrentSkipListSet<Human> getPassengers() {
        return passengers;
    }

    public void setPassengers(ConcurrentSkipListSet<Human> passengers) {
        this.passengers = passengers;
    }

    public boolean isPing() {
        return ping;
    }

    public void setPing(boolean ping) {
        this.ping = ping;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
