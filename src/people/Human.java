package people;

import rocket.room.*;



import static timeline.Timeline.*;
import java.util.Date;

public class Human implements Comparable<Human>{
    final String name;
    final int timeUntilHunger;
    int lastMealTime = 0;
    Room room;
    private Date date;
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
    public void goTo(Room room){
        if(!this.room.equals(room)){
            this.room = room;
            System.out.println(getCurrentTime() + " часов: " + name + ": пошёл в " + room.toString() + ".");
        }
    }

    public void eat(){
        if(room.getType() == Type.FOODSTORAGE){
            System.out.println(getCurrentTime() + " часов: " + name + ": ест, находясь в " + room.toString() + ".");
            lastMealTime = getCurrentTime();
        }
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
}