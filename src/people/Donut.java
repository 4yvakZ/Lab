package people;

import rocket.room.*;
import security.User;

import java.time.ZonedDateTime;

import static timeline.Timeline.*;

public class Donut extends Human{
    private String foodName = "";
    public Donut(String name, int timeUntilHunger, Room room, String username){
        super(name, timeUntilHunger, username, room);
    }
    public Donut(String name, int timeUntilHunger, Room room, String foodName, ZonedDateTime time, String username){
        super(name, timeUntilHunger, username, room, time);
        this.foodName = foodName;
    }
    public Donut(String name, int timeUntilHunger, Room room, ZonedDateTime time, String username){
        super(name, timeUntilHunger, username, room, time);
    }
    public Donut(String name, int timeUntilHunger, Room room, String foodName, String username){
        super(name, timeUntilHunger, username, room);
        this.foodName = foodName;
    }
    public String getFoodName(){
        return foodName;
    }
    public void sleep(){
        Dream dream = new Dream(){
            public void sleep(){
                System.out.println(getCurrentTime() + " часов: " + name + ": спит, находясь в " + room.toString() + ".");
            }
            public void wakeUp(){
                System.out.println(getCurrentTime() + " часов: " + name + ": проснулся, находясь в " + room.toString() + ".");
            }
        };
        if (!(getCurrentTime() - lastMealTime + 1 > timeUntilHunger)){
            dream.sleep();
        } else {
            dream.wakeUp();
        }
    }
    public String eat(){
        class DonutFood{
            private String name;
            private DonutFood(){
                name = "Пышка";
            }
            private DonutFood(String name){
                this.name = name;
            }
            private String getName(){
                return name;
            }
            public String toString(){
                return name;
            }
            public boolean equals(DonutFood food){
                return name.equals(food.getName());
            }
        }
        DonutFood food;
        if(!foodName.isEmpty()){
            food = new DonutFood(foodName);
        }else {
            food = new DonutFood();
        }
        if(room.getType() == Type.FOODSTORAGE){
            lastMealTime = getCurrentTime();
            return getCurrentTime() + " часов: " + name + ": ест "+ food.toString() + ", находясь в " + room.toString() + ".";
        }
        return getCurrentTime() + " часов: " + name + ": не нашёл "+ food.toString() + ", находясь в " + room.toString() + ".";
    }
}