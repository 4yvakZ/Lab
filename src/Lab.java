import activity.Activity;
import org.json.simple.parser.ParseException;
import people.Donut;
import people.Fool;
import people.Human;
import rocket.Rocket;
import rocket.SpeedException;
import rocket.room.Room;
import rocket.room.Type;
import space.objects.Earth;
import space.objects.Moon;
import space.objects.SpaceObject;

import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListSet;

import static timeline.Timeline.increaseTime;
import static timeline.Timeline.setTime;


class Lab {
    public static void main(String[] args) {
        try{
            Activity activity = new Activity();
            Scanner scanner = new Scanner(System.in);
            setTime(0);
            Room cabin = new Room(Type.CABIN, "Кабина");
            Room foodStorage = new Room(Type.FOODSTORAGE, "Пищевой блок");
//            Room space = null;
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
            ConcurrentSkipListSet<Human> passengers = rocket.getPassengers();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                activity.save(passengers);
            }));

            activity.start(args[0], cabin, rocket);
            boolean shutDown = false;
            String help = "List of Commands:\nhelp\nadd\nshow\ninfo\nremove_lower {element}\nremove {element}\nload\nadd_if_max\nnext_hour\nshutdown\n";
            System.out.print(help+">");
            while (true){
                try {
                    switch (scanner.next()) {
                        case "add":
                            activity.add(passengers, scanner.nextLine());
                            break;
                        case "shutdown":
                            shutDown = true;
                            break;
                        case "show":
                            activity.show(passengers);
                            break;
                        case "info":
                            activity.info(passengers);
                            break;
                        case "help":
                            System.out.print(help);
                            break;
                        case "remove_lower":
                            activity.removeLower(passengers, scanner.nextLine());
                            break;
                        case "remove":
                            activity.remove(passengers, scanner.nextLine());
                            break;
                        case "load":
                            activity.load(passengers, args[0], rocket);
                            break;
                        case "add_if_max":
                            activity.addIfMax(passengers, scanner.nextLine());
                            break;
                        case "next_hour":
                            for (Human human : passengers) {
                                if (human.isHungryNow()) {
                                    human.goTo(foodStorage);
                                    human.eat();
                                }
                            }
                            increaseTime();
                            break;
                        default:
                            System.out.println("Error: Wrong command!!! Please try again!");
                            scanner.nextLine();
                            break;
                    }
                    if (shutDown) break;
                }catch (NullPointerException e){
                    System.out.println("Wrong format");
                }catch (ParseException e){
                    System.out.println("Wrong format");
                }
                System.out.print(">");
            }
        }catch(SpeedException e){
            System.out.println(e.getMessage());
        }
    }
}

