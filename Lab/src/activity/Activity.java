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
public class Activity {
    private final String saveFile = "save.csv";
    private Room room;
    /**<p>Read csv file and add it to Human collection</p>
     * @param file name of csv
     * @param rocket start rocket
     */
    private void readCSV(String file, Rocket rocket){
        try {
            FileReader reader = new FileReader(file);
            int c;
            while((c = reader.read())!= -1) // noinspection Duplicates
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
                if (name.toString().isEmpty()){
                    rocket.addPassenger(new Human(timeUntilHunger, room));
                }else if (thumbLength != 0){
                    if (!foodName.toString().isEmpty()) {
                        rocket.addPassenger(new Fool(name.toString(), timeUntilHunger, room, foodName.toString(), thumbLength));
                    }else{
                        rocket.addPassenger(new Fool(name.toString(), timeUntilHunger, room, thumbLength));
                    }
                }else if (!foodName.toString().isEmpty()){
                    rocket.addPassenger((new Donut(name.toString(), timeUntilHunger, room, foodName.toString())));
                }else {
                    rocket.addPassenger(new Human(name.toString(), timeUntilHunger, room));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**<p>Convert String to JSON and generate Human</p>
     * @param string JSON string
     * @return human from JSON
     */
    private Human readJSON(String string){
        Human human = new Human(0, room);
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
            if (name.isEmpty()){
                human = new Human(timeUntilHunger, room);
            }else if (thumbLength != 0){
                if (!foodName.isEmpty()) {
                    human = new  Fool(name, timeUntilHunger, room, foodName, thumbLength);
                }else{
                    human = new  Fool(name, timeUntilHunger, room, thumbLength);
                }
            }else if (!foodName.isEmpty()){
                human = new Donut(name, timeUntilHunger, room, foodName);
            }else {
                human = new Human(name, timeUntilHunger, room);
            }
        }
        return human;
    }

    /**<p>Read from saves and argument file</p>
     * @param file name of csv
     * @param startRoom start room
     * @param rocket start rocket
     */
    public void start(String file, Room startRoom, Rocket rocket){
        room = startRoom;
        readCSV(saveFile, rocket);
        readCSV(file, rocket);
    }

    /**<p>Add new element to Human collection</p>
     * @param passengers Human collection
     * @param string JSON string
     */
    public void add(TreeSet<Human> passengers,String string){
        Human human = readJSON(string);
        if(human.getTimeUntilHunger()<1){
            System.out.println("Wrong format");
            return;
        }
        if(!passengers.add(human)){
            System.out.println("Object with same name has already exist");
        }
    }

    /**<p>Show all elements from Human collection in terminal</p>
     * @param passengers Human collection
     */
    public void show(TreeSet<Human> passengers){
        for (Human human:passengers) {
            System.out.println(human.toString());
        }
    }

    /**<p>Show info about Human collection in terminal</p>
     * @param passengers Human collection
     */
    public void info(TreeSet<Human> passengers){
        System.out.println("Тип коллекции TreeSet<Human>, кол-во элементов коллекции "+passengers.size());
    }

    /**<p>Remove all elements lower than written</p>
     * @param passengers Human collection
     * @param string JSON string
     */
    public void removeLower(TreeSet<Human> passengers, String string){
        Human removable;
        Human human = readJSON(string);
        if(human.getTimeUntilHunger()<1){
            System.out.println("Wrong format");
            return;
        }
        while((removable = passengers.lower(human))!=null){
            passengers.remove(removable);
        }
    }

    /**<p>Reload Human collection from argument file</p>
     * @param passengers Human collection
     * @param file name of csv
     * @param rocket start rocket
     */
    public void load(TreeSet<Human> passengers, String file,  Rocket rocket){
        passengers.clear();
        readCSV(file, rocket);
    }

    /**<p>Remove element from Human collection</p>
     * @param passengers Human collection
     * @param string JSON string
     */
    public void remove(TreeSet<Human> passengers, String string){
        passengers.remove(readJSON(string));
    }

    /**<p>Add element to Human collection if it bigger then biggest one in collection</p>
     * @param passengers Human collection
     * @param string JSON string
     */
    public void addIfMax(TreeSet<Human> passengers, String string){
        Human human = readJSON(string);
        if(human.getTimeUntilHunger()<1){

            System.out.println("Wrong format");
            return;
        }
        if(passengers.higher(human)==null)passengers.add(human);
        else System.out.println("Objects isn't bigger then maximum one, so nothing was added");
    }

    /**<p>Saves collection to save.csv and ends program</p>
     * @param passengers Human collection
     */
    public void shutdown(TreeSet<Human> passengers){
        try {
            FileWriter writer = new FileWriter(saveFile);
            for (Human human: passengers) //noinspection Duplicates
            {
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

                if(human instanceof Fool){
                    Fool fool = (Fool) human;
                    writer.write(Integer.toString(fool.getThumbLength()));
                }
                writer.write(",");

                if (human instanceof Donut){
                    Donut donut = (Donut) human;
                    String foodName = donut.getFoodName();
                    foodName = foodName.replace(Character.toString(c), c +Character.toString(c));
                    if(foodName.contains(" ")){
                        writer.write('"');
                        writer.write(foodName);
                        writer.write('"');
                    }else {
                        writer.write(foodName);
                    }
                }
                writer.write(",");

                writer.write(Integer.toString(human.getTimeUntilHunger()));
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
