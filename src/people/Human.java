package people;

import rocket.room.*;

import java.io.Serializable;
import static timeline.Timeline.*;

import java.time.ZonedDateTime;

public class Human implements Comparable<Human>, Serializable {
    protected final String name;
    protected final int timeUntilHunger;
    protected int lastMealTime = 0;
    private final String username;
    protected Room room;
    final private ZonedDateTime time;
    public Human(int timeUntilHunger, String username, Room room){
        this.username = username;
        time= ZonedDateTime.now();
        this.timeUntilHunger = timeUntilHunger;
        this.name = "Эй ты";
        try{
            if(room == null){
                throw new NullPointerException();
            }
            this.room = room;
        }catch(NullPointerException e){
            throw new RoomException(name, e);
        }
    }
    public Human(String name, int timeUntilHunger, String username, Room room){
        this.username = username;
        time= ZonedDateTime.now();
        this.timeUntilHunger = timeUntilHunger;
        this.name = name;
        try{
            if(room == null){
                throw new NullPointerException();
            }
            this.room = room;
        }catch(NullPointerException e){
            throw new RoomException(name, e);
        }
    }
    public Human(int timeUntilHunger, String username, Room room, ZonedDateTime time){
        this.username = username;
        this.time= time;
        this.timeUntilHunger = timeUntilHunger;
        this.name = "Эй ты";
        try{
            if(room == null){
                throw new NullPointerException();
            }
            this.room = room;
        }catch(NullPointerException e){
            throw new RoomException(name, e);
        }
    }
    public Human(String name, int timeUntilHunger, String username, Room room, ZonedDateTime time){
        this.username = username;
        this.time= time;
        this.timeUntilHunger = timeUntilHunger;
        this.name = name;
        try{
            if(room == null){
                throw new NullPointerException();
            }
            this.room = room;
        }catch(NullPointerException e){
            throw new RoomException(name, e);
        }
    }
    public boolean isHungryNow(){
        return getCurrentTime() - lastMealTime + 1 > timeUntilHunger;
    }
    public String goTo(Room room){
        if(!this.room.equals(room)){
            this.room = room;
            return getCurrentTime() + " часов: " + name + ": пошёл в " + room.toString() + ".";
        }
        return getCurrentTime() + " часов: " + name + ": остался в " + room.toString() + ".";
    }

    public String eat(){
        if(room.getType() == Type.FOODSTORAGE){
            lastMealTime = getCurrentTime();
            return getCurrentTime() + " часов: " + name + ": ест, находясь в " + room.toString() + ".";
        }
        return getCurrentTime() + " часов: " + name + ": не нашёл еду в " + room.toString() + ".";
    }

    public Room getRoom(){
        return room;
    }

    public String getName(){
        return name;
    }

    public int getTimeUntilHunger(){
        return timeUntilHunger;
    }

    private int getLastMealTime(){
        return lastMealTime;
    }

    public String toString(){
        return name;
    }


    public boolean equals(Human human){
        return room.equals(human.getRoom())
                && name.equals(human.getName())
                && lastMealTime == human.getLastMealTime()
                && timeUntilHunger == human.getTimeUntilHunger()
                && username.equals(human.username);
    }

    @Override
    public int compareTo(Human human) {
        if (name.compareTo(human.name) == 0){
            return username.compareTo(human.getUsername());
        }
        return name.compareTo(human.name);
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }

    public String getInfo(){
        return name + " находится в "+ room.toString()+ " создан "+ username+ " " + time;
    }
}
