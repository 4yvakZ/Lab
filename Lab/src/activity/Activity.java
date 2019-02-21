package activity;

import people.*;
import rocket.Rocket;
import rocket.room.Room;

import java.io.FileReader;
import java.io.FileWriter;
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
                if (name.equals("")){
                    rocket.addPassenger(new Human(timeUntilHunger, cabin));
                }else if (thumbLength != 0){
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
        Human human = new Human(0,cabin);
        string = string.replace(" ", "");
        string = string.replace("   ", "");
        string = string.replace("{", "");
        string = string.replace("}", "");
        String[] array = string.split(",");
        String name = "", foodName = "";
        int timeUntilHunger = 0,thumbLength = 0;
        for (String line: array) {
            String[] param = line.split(":");
            param[0]= param[0].replace(Character.toString('"'), "");
            switch (param[0]){
                case "timeUntilHunger":
                    timeUntilHunger = Integer.parseInt(param[1]);
                    break;
                case "thumbLength":
                    thumbLength = Integer.parseInt(param[1]);
                    break;
                case "name":
                    name = param[1].replaceFirst(Character.toString('"'), "");
                    name = name.substring(0, name.length() - 1);
                    break;
                case "foodName":
                    foodName= param[1].replaceFirst(Character.toString('"'), "");
                    foodName = foodName.substring(0, foodName.length() - 1);
                    break;
            }
            if (name.equals("")){
                human = new Human(timeUntilHunger, cabin);
            }else if (thumbLength != 0){
                human = new  Fool(name, timeUntilHunger, cabin, thumbLength);
            }else if (!foodName.equals("")){
                human = new Donut(name, timeUntilHunger, cabin, foodName);
            }else {
                human = new Human(name, timeUntilHunger, cabin);
            }
        }
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
     * @param string
     */
    public static void add(TreeSet<Human> passengers, Room cabin,String string){
        Human human = readJSON(string, cabin);
        if(human.getTimeUntilHunger()<1){
            System.out.println("Wrong format");
            return;
        }
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
     * @param string
     * @param cabin
     */
    public static void removeLower(TreeSet<Human> passengers, String string, Room cabin){
        Human removable;
        Human human = readJSON(string, cabin);
        if(human.getTimeUntilHunger()<1){
            System.out.println("Wrong format");
            return;
        }
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
     * @param string
     * @param cabin
     */
    public static void remove(TreeSet<Human> passengers, String string, Room cabin){
        passengers.remove(readJSON(string,cabin));
    }

    /**
     * @param passengers
     * @param string
     * @param cabin
     */
    public static void addIfMax(TreeSet<Human> passengers, String string, Room cabin){
        Human human = readJSON(string, cabin);
        if(human.getTimeUntilHunger()<1){
            System.out.println("Wrong format");
            return;
        }
        if(passengers.higher(human)==null)passengers.add(human);
    }

    /**
     * @param passengers
     */
    public static void shutdown(TreeSet<Human> passengers){
        try {
            FileWriter writer = new FileWriter(saveFile);
            for (Human human: passengers) {
                String name = human.getName();
                char c = '"';
                name = name.replace(Character.toString(c), Character.toString(c)+ c);
                if(name.contains(" ")){
                    writer.write('"');
                    writer.write(name);
                    writer.write('"');
                }else {
                    writer.write(name);
                }
                writer.write(",");
                //thumb
                if(human instanceof Fool){
                    Fool fool = (Fool) human;
                    writer.write(Integer.toString(fool.getThumbLength()));
                    writer.write(",,");
                }else if (human instanceof Donut){
                    writer.write(",");
                    Donut donut = (Donut) human;
                    String foodName = donut.getFoodName();
                    foodName = foodName.replace(Character.toString(c), c +Character.toString(c));
                    if(foodName.contains(" ")){
                        writer.write('"');
                        writer.write(foodName);
                        writer.write('"');
                    }else {
                        writer.write(foodName);
                    }writer.write(",");
                }else {
                    writer.write(",,");
                }
                writer.write(Integer.toString(human.getTimeUntilHunger()));
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
