package activity;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import people.Donut;
import people.Fool;
import people.Human;
import rocket.Rocket;
import rocket.room.Room;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

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
            System.out.println("File " + file + " does not exist, but anyway");
        }
    }

    /**<p>Convert String to JSON and generate Human</p>
     * @param string JSON string
     * @return human from JSON
     */
    private Human readJSON(String string) throws ParseException, NullPointerException {
        Human human;

        JSONObject jo = (JSONObject) new JSONParser().parse(string);
        String name;
        if(jo.get("name") instanceof String) {
            name = (String) jo.get("name");
        }else{
            throw new ParseException(0);
        }
        String foodName;
        try {
            if(jo.get("foodName") instanceof String) {
                foodName = (String) jo.get("foodName");
            }else{
                throw new ParseException(0);
            }
        }catch (NullPointerException e){
            foodName = "";
        }
        int timeUntilHunger;
        if(jo.get("timeUntilHunger") instanceof Long) {
            timeUntilHunger = ((Long) jo.get("timeUntilHunger")).intValue();
        }else{
            throw new ParseException(0);
        }
        int thumbLength;
        try {
            if(jo.get("thumbLength") instanceof Long) {
                thumbLength = ((Long)jo.get("thumbLength")).intValue();
            }else {
                throw new ParseException(0);
            }
        }catch (NullPointerException e){
            thumbLength = 0;
        }
        /*int timeUntilHunger = 0,thumbLength = 0;
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
        }*/
        if (timeUntilHunger < 1) throw new ParseException(1);
        if (name.isEmpty()){
            human = new Human(timeUntilHunger, room);
        }else if (thumbLength > 0){
            if (!foodName.isEmpty()) {
                human = new  Fool(name, timeUntilHunger, room, foodName, thumbLength);
            }else{
                human = new  Fool(name, timeUntilHunger, room, thumbLength);
            }
        }else if (!foodName.isEmpty()){
            human = new Donut(name, timeUntilHunger, room, foodName);
        }else{
            human = new Human(name, timeUntilHunger, room);
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

    /**<p>Read from saves</p>
     * @param startRoom start room
     * @param rocket start rocket
     */
    public void start(Room startRoom, Rocket rocket){
        room = startRoom;
        readCSV(saveFile, rocket);
    }

    /**<p>Add new element to Human collection</p>
     * @param passengers Human collection
     * @param string JSON string
     * @return action message
     * @throws NullPointerException
     * @throws ParseException
     */
    public String add(ConcurrentSkipListSet<Human> passengers, String string, Room startRoom) throws NullPointerException, ParseException {
        room = startRoom;
        Human human = readJSON(string);
        if(!passengers.add(human)){
            return "Object with same name has already exist";
        }
        return "Object was successfully added";
    }

    /**<p>Show all elements from Human collection in terminal</p>
     * @param passengers Human collection
     * @return action message
     */
    public String show(ConcurrentSkipListSet<Human> passengers){
        StringBuilder out = new StringBuilder();
        passengers.stream().forEach(x-> out.append(x.toString()+ "\n"));
        out.deleteCharAt(out.length()-1);
        return out.toString();
    }

    /**<p>Show info about Human collection in terminal</p>
     * @param passengers Human collection
     * @return action message
     */
    public String info(ConcurrentSkipListSet<Human> passengers){
        return "Тип коллекции ConcurrentSkipListSet<Human>, кол-во элементов коллекции "+passengers.size();
    }

    /**<p>Remove all elements lower than written</p>
     * @param passengers Human collection
     * @param string JSON string
     * @return action message
     * @throws ParseException
     * @throws NullPointerException
     */
    public String removeLower(ConcurrentSkipListSet<Human> passengers, String string) throws ParseException, NullPointerException {
        Human human;
        human = readJSON(string);
        if(passengers.retainAll(passengers.stream()
                .filter(x -> x.compareTo(human) >= 0)
                .collect(Collectors.toCollection(ConcurrentSkipListSet::new)))){
            return "Some objects were removed";
        }
        return "Nothing happened";
    }

    /**<p>Reload Human collection from argument file</p>
     * @param passengers Human collection
     * @param file name of csv
     * @param rocket start rocket
     */
    public void load(ConcurrentSkipListSet<Human> passengers, String file,  Rocket rocket){
        passengers.clear();
        readCSV(file, rocket);
    }

    /**<p>Remove element from Human collection</p>
     * @param passengers Human collection
     * @param string JSON string
     * @return action message
     * @throws NullPointerException
     * @throws ParseException
     */
    public String remove(ConcurrentSkipListSet<Human> passengers, String string) throws NullPointerException, ParseException {
        Human human = readJSON(string);
        if(passengers.remove(human)){
            return human.toString() + " was successfully removed";
        }
        return "Nothing was removed";
    }

    /**<p>Add element to Human collection if it bigger then biggest one in collection</p>
     * @param passengers Human collection
     * @param string JSON string
     * @return action message
     * @throws NullPointerException
     * @throws ParseException
     */
    public String addIfMax(ConcurrentSkipListSet<Human> passengers, String string,Room startRoom) throws NullPointerException, ParseException {
        room = startRoom;
        Human human = readJSON(string);
        if(passengers.higher(human)==null){
            passengers.add(human);
            return "Objects was successfully added";
        }
        return "Objects isn't bigger then maximum one, so nothing was added";
    }

    /**<p>Saves collection to save.csv and ends program</p>
     * @param passengers Human collection
     */
    public void save(ConcurrentSkipListSet<Human> passengers){
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
