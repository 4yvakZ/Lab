package activity;

import people.*;
import rocket.Rocket;
import rocket.room.Room;

import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

/**
 *
 */
public abstract class Activity {
    private static final String saveFile = "save.csv";
    /**
     * @param passengers
     * @param file
     * @param cabin
     * @param rocket
     */
    private static void readCSV(TreeSet<Human> passengers, String file, Room cabin, Rocket rocket){
        try {
            FileReader reader = new FileReader(file);
            int c;
            while((c = reader.read())!= -1) //noinspection Duplicates,Duplicates
            {
                int timeUntilHunger = 0;
                int thumbLength = 0;
                StringBuilder name = new StringBuilder();
                StringBuilder foodName = new StringBuilder();
                boolean flag = false;
                if (c == '"'){
                    c = reader.read();
                    flag = true;
                }
                while (c != -1) {
                    if (c == '"'){
                        if((c = reader.read())== '"'){
                            name.append((char) c);
                            c = reader.read();
                            continue;
                        } else {
                            flag = false;
                        }
                    }
                    if (c == ',' && !flag) break;
                    name.append((char) c);
                    c = reader.read();
                }
                c = reader.read();
                while (c != -1) {
                    if (c == ',') break;
                    thumbLength = thumbLength * 10 + c - '0';
                    c = reader.read();
                }
                c = reader.read();
                if (c == '"'){
                    c = reader.read();
                    flag = true;
                }
                while (c != -1) {
                    if (c == '"'){
                        if((c = reader.read())== '"'){
                            foodName.append((char) c);
                            c = reader.read();
                            continue;
                        } else {
                            flag = false;
                        }
                    }
                    if (c == ',' && !flag) break;
                    foodName.append((char) c);
                    c = reader.read();
                }
                c = reader.read();
                while (c != -1) {
                    if (c < '0'||c > '9') break;
                    timeUntilHunger = timeUntilHunger * 10 + c - '0';
                    c = reader.read();
                }
                if(thumbLength != 0){
                    rocket.addPassenger(new Fool(name.toString(), timeUntilHunger, cabin, thumbLength));
                }else if (!foodName.toString().equals("")){
                    rocket.addPassenger((new Donut(name.toString(), timeUntilHunger, cabin, foodName.toString())));
                }else {
                    rocket.addPassenger(new Human(name.toString(), timeUntilHunger, cabin));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param string JSON string
     * @return human from JSON
     */
    private static Human readJSON(String string, Room cabin){
        Human human;
        string.replaceAll(" ", "");
        return human;
    }

    /**
     * @param passengers
     * @param file
     * @param cabin
     * @param rocket
     */
    public static void start(TreeSet<Human> passengers, String file, Room cabin, Rocket rocket){
        readCSV(passengers, saveFile, cabin, rocket);
        readCSV(passengers, file, cabin, rocket);
    }

    /**
     * @param passengers
     * @param human
     */
    public static void add(TreeSet<Human> passengers, Human human, String string){
        //ADD CONSOLE READER
        passengers.add(human);
    }

    /**
     * @param passengers
     */
    public static void show(TreeSet<Human> passengers){
        for (Human human:passengers) {
            System.out.println(human.toString());
        }
    }

    /**
     * @param passengers
     */
    public static void info(TreeSet<Human> passengers){
        System.out.println("Тип коллекции TreeSet<Human>, кол-во элементов коллекции "+passengers.size());
    }

    /**
     * @param passengers
     * @param human
     */
    public static void removeLower(TreeSet<Human> passengers, Human human){
        Human removable;
        while((removable = passengers.lower(human))!=null){
            passengers.remove(removable);
        }
    }

    /**
     * @param passengers
     * @param file
     * @param cabin
     * @param rocket
     */
    public static void load(TreeSet<Human> passengers, String file, Room cabin, Rocket rocket){
        passengers.clear();
        readCSV(passengers,file, cabin, rocket);
    }

    /**
     * @param passengers
     * @param human
     */
    public static void remove(TreeSet<Human> passengers, Human human){
        passengers.remove(human);
    }

    /**
     * @param passengers
     * @param human
     */
    public static void addIfMax(TreeSet<Human> passengers, Human human){
        if(passengers.higher(human)==null)passengers.add(human);
    }

    /**
     * @param passengers
     */
    public static void shutDown(TreeSet<Human> passengers){

    }
}
