import people.*;
import rocket.*;
import rocket.room.*;
import space.objects.*;

import static timeline.Timeline.*;
import static activity.Activity.*;

import java.util.Scanner;
import java.util.TreeSet;


class Lab {
    public static void main(String[] args) {
        try{
            Scanner scanner = new Scanner(System.in);
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
            start(passengers, args[0], cabin, rocket);
//            show(passengers);
//            info(passengers);
//            fool.seems(moon.getOrbit(), rocket.getVelocity());
            boolean shutDown = false;
            String help = "List of Commands:\nhelp\nadd\nshow\ninfo\nremove_lower {element}\nremove {element}\nload\nadd_if_max\nnext_hour\nshutdown\n";
            System.out.print(help+">");
            while (true){
                switch (scanner.next()){
                    case "add":
                        add(passengers,cabin,scanner.nextLine());
                        break;
                    case "shutdown":
                        shutdown(passengers);
                        shutDown = true;
                        break;
                    case "show":
                        show(passengers);
                        break;
                    case "info":
                        info(passengers);
                        break;
                    case "help":
                        System.out.println(help);
                        break;
                    case "remove_lower":
                        removeLower(passengers, scanner.nextLine(), cabin);
                        break;
                    case "remove":
                        remove(passengers,scanner.nextLine(), cabin);
                        break;
                    case "load":
                        load(passengers, scanner.nextLine(),cabin,rocket);
                        break;
                    case "add_if_max":
                        addIfMax(passengers, scanner.nextLine(), cabin);
                        break;
                    case "next_hour":
                        for(Human human : passengers){
                            if(human.isHungryNow()){
                                human.goTo(foodStorage);
                                human.eat();
                            }
                        }
                        increaseTime();
                        break;
                    default:
                        System.out.println("Error: Wrong command!!! Please try again!");
                        break;
                }
                if(shutDown)break;
                System.out.print(">");
            }
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

