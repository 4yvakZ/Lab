import people.*;
import rocket.*;
import rocket.room.*;
import space.objects.*;

import static timeline.Timeline.*;
import static activity.Activity.*;
import java.util.TreeSet;


class Lab {
    public static void main(String[] args) {
        try{
            setTime(0);
            Room cabin = new Room(Type.CABIN, "Кабина");
            Room foodStorage = new Room(Type.FOODSTORAGE, "Пищевой блок");
            Room space = null;
            SpaceObject earth = new Earth(6400);
            Moon moon = new Moon(1740, 400000, earth);
            Rocket rocket = new Rocket(12, moon);
            rocket.addRoom(cabin);
            rocket.addRoom(foodStorage);
            Fool fool = new Fool("Незнайка",3, cabin,10 );
            Donut donut = new Donut("Пончик",2, cabin, "Пышка");
            rocket.addPassenger(fool);
            rocket.addPassenger(donut);
            System.out.println(rocket.toString() + ".");
            moon.orbitInfo();
            TreeSet<Human> passengers = rocket.getPassengers();
            load(passengers, args[0], cabin, rocket);
            show(passengers);
            info(passengers);
            fool.seems(moon.getOrbit(), rocket.getVelocity());
            /*for(int i = 0; i<=3; i++){
                donut.sleep();
                fool.lookAt(moon);
                for(Human human : passengers){
                    if(human.isHungryNow()){
                        human.goTo(foodStorage);
                        human.eat();
                    }
                }
                increaseTime();
            }*/
        }catch(SpeedException e){
            System.out.println(e.getMessage());
        }
    }


}

