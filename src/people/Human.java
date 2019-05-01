package people;

import rocket.room.*;
import java.io.Serializable;
import static timeline.Timeline.*;
import java.util.Date;

public class Human implements Comparable<Human>, Serializable {
    final String name;
    final int timeUntilHunger;
    int lastMealTime = 0;
    Room room;
    private Date date;
    ZoneId zone = ZoneId.of("Europe/Moscow");
    ZonedDateTime zonedDateTime = ZonedDateTime.now(zone);
    public Human(int timeUntilHunger, Room room){
        date = new Date();
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
    public Human(String name, int timeUntilHunger, Room room){
        date = new Date();
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

    private Room getRoom(){
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
                && timeUntilHunger == human.getTimeUntilHunger();
    }

    @Override
    public int compareTo(Human human) {
        if (room.compareTo(human.getRoom()) == 0){
            return name.compareTo(human.name);
        }
        return room.compareTo(human.getRoom());
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
