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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 *
 */
public class Activity {
    private final String saveFile = "save.csv";
    private Room room;

    /**<p>Read collection from SQL database</p>
     * @param connection connection to SQL database
     * @param rocket start rocket
     */
    private void readSQL(Connection connection, Rocket rocket){
        Statement statement;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM HUMANS;");
            while (resultSet.next()){
                int timeUntilHunger = resultSet.getInt("time_until_hunger");
                int thumbLength = resultSet.getInt("thumb_length");
                String name = resultSet.getString("name");
                String foodName = resultSet.getString("food_name");
                if (name.isEmpty()){
                    rocket.addPassenger(new Human(timeUntilHunger, room));
                }else if (thumbLength != 0){
                    if (foodName !=null) {
                        rocket.addPassenger(new Fool(name, timeUntilHunger, room, foodName, thumbLength));
                    }else{
                        rocket.addPassenger(new Fool(name, timeUntilHunger, room, thumbLength));
                    }
                }else if (foodName != null){
                    rocket.addPassenger((new Donut(name, timeUntilHunger, room, foodName)));
                }else {
                    rocket.addPassenger(new Human(name, timeUntilHunger, room));
                }
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**<p>Read csv file and add it to Human collection</p>
     * @param file name of csv
     * @param rocket start rocket
     */
    private void readCSVFile(String file, Rocket rocket){
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

    private void readCSV(String data, Rocket rocket) throws ParseException {
        int timeUntilHunger = 0;
        int thumbLength = 0;
        String name = new String();
        String foodName = new String();
        String[] lines = data.split("\n");
        for(int i = 0; i < lines.length; i++) {
            String[] csvObject = lines[i].split(",");
            if (csvObject.length != 4) {
                throw new ParseException(0);
            } else {
                name = csvObject[0];
                if (!csvObject[1].isEmpty()) {
                    thumbLength = Integer.parseInt(csvObject[1]);
                }else{
                    thumbLength = 0;
                }
                foodName = csvObject[2];
                if (!csvObject[3].isEmpty()) {
                    timeUntilHunger = Integer.parseInt(csvObject[3]);
                }else{
                    timeUntilHunger = 0;
                }
            }
            if (name.isEmpty()) {
                rocket.addPassenger(new Human(timeUntilHunger, room));
            } else if (thumbLength != 0) {
                if (!foodName.isEmpty()) {
                    rocket.addPassenger(new Fool(name, timeUntilHunger, room, foodName, thumbLength));
                } else {
                    rocket.addPassenger(new Fool(name, timeUntilHunger, room, thumbLength));
                }
            } else if (!foodName.isEmpty()) {
                rocket.addPassenger((new Donut(name, timeUntilHunger, room, foodName)));
            } else {
                rocket.addPassenger(new Human(name, timeUntilHunger, room));
            }
        }
    }

    /**<p>Reload Human collection from argument file</p>
     * @param passengers Human collection
     * @param data with csv
     * @param rocket start rocket
     */
    public String load(ConcurrentSkipListSet<Human> passengers, String data,  Rocket rocket, Room room) throws ParseException {
        this.room = room;
        readCSV(data, rocket);
        return "Uniq objects from files were added";
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
        readCSVFile(saveFile, rocket);
        readCSVFile(file, rocket);
    }

    /**<p>Read from saves</p>
     * @param startRoom start room
     * @param rocket start rocket
     */
    public void start(Room startRoom, Rocket rocket){
        room = startRoom;
        readCSVFile(saveFile, rocket);
    }

    /**<p>Read from saves from SQL database</p>
     * @param startRoom start room
     * @param rocket start rocket
     * @param connection connection to SQL database
     */
    public void start(Room startRoom, Rocket rocket, Connection connection){
        room = startRoom;
        readSQL(connection, rocket);
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

    /**<p>Add new element to Human collection</p>
     * @param passengers Human collection
     * @param human to add
     * @return action message
     * @throws NullPointerException
     */
    public String add(ConcurrentSkipListSet<Human> passengers, Human human)  {
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

    /**<p>Remove all elements lower than written</p>
     * @param passengers Human collection
     * @param human to remove lower
     * @return action message
     * @throws ParseException
     * @throws NullPointerException
     */
    public String removeLower(ConcurrentSkipListSet<Human> passengers, Human human) throws NullPointerException {
        if(passengers.retainAll(passengers.stream()
                .filter(x -> x.compareTo(human) >= 0)
                .collect(Collectors.toCollection(ConcurrentSkipListSet::new)))){
            return "Some objects were removed";
        }
        return "Nothing happened";
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

    /**<p>Remove element from Human collection</p>
     * @param passengers Human collection
     * @param human to remove
     * @return action message
     * @throws NullPointerException
     * @throws ParseException
     */
    public String remove(ConcurrentSkipListSet<Human> passengers, Human human) throws NullPointerException {
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

    /**<p>Add element to Human collection if it bigger then biggest one in collection</p>
     * @param passengers Human collection
     * @param human to remove
     * @throws NullPointerException
     * @throws ParseException
     */
    public String addIfMax(ConcurrentSkipListSet<Human> passengers, Human human) throws NullPointerException{
        if(passengers.higher(human)==null){
            passengers.add(human);
            return "Objects was successfully added";
        }
        return "Objects isn't bigger then maximum one, so nothing was added";
    }

    /**<p>Saves collection to save.csv</p>
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

    /**<p>Saves collection to SQL database</p>
     * @param passengers Human collection
     * @param connection connection to SQL database
     */
    public void save(ConcurrentSkipListSet<Human> passengers, Connection connection){
        Statement statement;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM HUMANS *");
            String table = "INSERT INTO HUMANS ";
            String colums, value;
            for (Human human: passengers) {
                String name = human.getName();
                int timeUntilHunger = human.getTimeUntilHunger();
                if(human instanceof Fool){
                    Fool fool = (Fool) human;
                    String foodName = fool.getFoodName();
                    int thumbLength = fool.getThumbLength();
                    colums = "(name, time_until_hunger, food_name, thumb_length, username) ";
                    value = "VALUES ('" + name +"'," + timeUntilHunger +",'"+foodName+"',"+thumbLength+",'admin');";
                }else if (human instanceof Donut){
                    Donut donut = (Donut) human;
                    String foodName = donut.getFoodName();
                    colums = "(name, time_until_hunger, food_name, username) ";
                    value = "VALUES ('" + name +"'," + timeUntilHunger +",'"+foodName+"','admin');";
                }else{
                    colums = "(name, time_until_hunger, username) ";
                    value = "VALUES ('" + name +"'," + timeUntilHunger +",'admin');";
                }
                statement.executeUpdate(table+colums+value);
            }
            statement.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
