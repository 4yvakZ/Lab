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
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 *
 */
public final class Activity {
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
                ZonedDateTime time = ZonedDateTime.parse(resultSet.getString("zoned_time"));
                String user = resultSet.getString("username");
                if (name.isEmpty()){
                    rocket.addPassenger(new Human(timeUntilHunger, user, room, time));
                }else if (thumbLength != 0){
                    if (foodName !=null) {
                        rocket.addPassenger(new Fool(name, timeUntilHunger, room, foodName, thumbLength, time, user));
                    }else{
                        rocket.addPassenger(new Fool(name, timeUntilHunger, room, thumbLength, time, user));
                    }
                }else if (foodName != null){
                    rocket.addPassenger((new Donut(name, timeUntilHunger, room, foodName, time, user)));
                }else {
                    rocket.addPassenger(new Human(name, timeUntilHunger, user, room, time));
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
     * @param username active username
     */
    private void readCSVFile(String file, Rocket rocket, String username){
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
                    rocket.addPassenger(new Human(timeUntilHunger, username, room));
                }else if (thumbLength != 0){
                    if (!foodName.toString().isEmpty()) {
                        rocket.addPassenger(new Fool(name.toString(), timeUntilHunger, room, foodName.toString(), thumbLength, username));
                    }else{
                        rocket.addPassenger(new Fool(name.toString(), timeUntilHunger, room, thumbLength, username));
                    }
                }else if (!foodName.toString().isEmpty()){
                    rocket.addPassenger((new Donut(name.toString(), timeUntilHunger, room, foodName.toString(), username)));
                }else {
                    rocket.addPassenger(new Human(name.toString(), timeUntilHunger, username, room));
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("File " + file + " does not exist, but anyway");
        }
    }

    private void readCSV(String data, Rocket rocket, String username) throws ParseException {
        int timeUntilHunger;
        int thumbLength;
        String name;
        String foodName;
        String[] lines = data.split("\n");
        for (String line : lines) {
            String[] csvObject = line.split(",");
            if (csvObject.length != 4) {
                throw new ParseException(0);
            } else {
                name = csvObject[0];
                if (!csvObject[1].isEmpty()) {
                    thumbLength = Integer.parseInt(csvObject[1]);
                } else {
                    thumbLength = 0;
                }
                foodName = csvObject[2];
                if (!csvObject[3].isEmpty()) {
                    timeUntilHunger = Integer.parseInt(csvObject[3]);
                } else {
                    timeUntilHunger = 0;
                }
            }
            if (name.isEmpty()) {
                rocket.addPassenger(new Human(timeUntilHunger, username, room));
            } else if (thumbLength != 0) {
                if (!foodName.isEmpty()) {
                    rocket.addPassenger(new Fool(name, timeUntilHunger, room, foodName, thumbLength, username));
                } else {
                    rocket.addPassenger(new Fool(name, timeUntilHunger, room, thumbLength, username));
                }
            } else if (!foodName.isEmpty()) {
                rocket.addPassenger((new Donut(name, timeUntilHunger, room, foodName, username)));
            } else {
                rocket.addPassenger(new Human(name, timeUntilHunger, username, room));
            }
        }
    }

    /**<p>Reload Human collection from argument file</p>
     * @param data with csv
     * @param rocket start rocket
     * @param username active username
     */
    public String load(String data, Rocket rocket, Room room, String username) throws ParseException {
        this.room = room;
        readCSV(data, rocket, username);
        return "Uniq objects from files were added";
    }

    /**<p>Convert String to JSON and generate Human</p>
     * @param string JSON string
     * @param user active user
     * @return human from JSON
     */
    private Human readJSON(String string, String user) throws ParseException, NullPointerException {
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
        if (timeUntilHunger < 1) throw new ParseException(1);
        if (name.isEmpty()){
            human = new Human(timeUntilHunger, user, room);
        }else if (thumbLength > 0){
            if (!foodName.isEmpty()) {
                human = new  Fool(name, timeUntilHunger, room, foodName, thumbLength, user);
            }else{
                human = new  Fool(name, timeUntilHunger, room, thumbLength, user);
            }
        }else if (!foodName.isEmpty()){
            human = new Donut(name, timeUntilHunger, room, foodName, user);
        }else{
            human = new Human(name, timeUntilHunger, user, room);
        }
        return human;
    }

    /**<p>Read from saves and argument file</p>
     * @param file name of csv
     * @param startRoom start room
     * @param rocket start rocket
     * @param username active username
     */
    public void start(String file, Room startRoom, Rocket rocket, String username){
        room = startRoom;
        readCSVFile(saveFile, rocket, username);
        readCSVFile(file, rocket, username);
    }

    /**<p>Read from saves</p>
     * @param startRoom start room
     * @param rocket start rocket
     * @param username active username
     */
    public void start(Room startRoom, Rocket rocket, String username){
        room = startRoom;
        readCSVFile(saveFile, rocket, username);
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
     * @param username active username
     * @return action message
     * @throws NullPointerException wrong format exception
     * @throws ParseException wrong format exception
     */
    public String add(ConcurrentSkipListSet<Human> passengers, String string, Room startRoom, String username) throws NullPointerException, ParseException {
        room = startRoom;
        Human human = readJSON(string, username);
        if(!passengers.add(human)){
            return "Object with same name has already exist";
        }
        return "Object was successfully added";
    }

    /**<p>Add new element to Human collection</p>
     * @param passengers Human collection
     * @param human to add
     * @return action message
     * @throws NullPointerException wrong format exception
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
        passengers.forEach(x-> out.append(x.getInfo()+ "\n"));
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
     * @param human to remove lower
     * @return action message
     * @throws NullPointerException wrong format exception
     */
    public String removeLower(ConcurrentSkipListSet<Human> passengers, Human human) throws NullPointerException {
        if(passengers.retainAll(passengers.stream()
                .filter(x -> x.compareTo(human) >= 0).filter(x-> x.getUsername().equals(human.getUsername()))
                .collect(Collectors.toCollection(ConcurrentSkipListSet::new)))){
            return "Some objects were removed";
        }
        return "Nothing happened";
    }

    /**<p>Remove element from Human collection</p>
     * @param passengers Human collection
     * @param human to remove
     * @return action message
     * @throws NullPointerException wrong format exception
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
     * @param username active username
     * @return action message
     * @throws NullPointerException wrong format exception
     * @throws ParseException wrong format exception
     */
    public String addIfMax(ConcurrentSkipListSet<Human> passengers, String string, Room startRoom, String username) throws NullPointerException, ParseException {
        room = startRoom;
        Human human = readJSON(string, username);
        if(passengers.higher(human)==null){
            passengers.add(human);
            return "Objects was successfully added";
        }
        return "Objects isn't bigger then maximum one, so nothing was added";
    }

    /**<p>Add element to Human collection if it bigger then biggest one in collection</p>
     * @param passengers Human collection
     * @param human to remove
     * @throws NullPointerException wrong format exception
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
        try {
            Statement statement;
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM HUMANS *");
            String table = "INSERT INTO HUMANS ";
            String colums, value;
            for (Human human: passengers) {
                String name = human.getName();
                ZonedDateTime time = human.getTime();
                String username = human.getUsername();
                int timeUntilHunger = human.getTimeUntilHunger();
                if(human instanceof Fool){
                    Fool fool = (Fool) human;
                    String foodName = fool.getFoodName();
                    int thumbLength = fool.getThumbLength();
                    colums = "(name, time_until_hunger, food_name, thumb_length, username, zoned_time) ";
                    value = "VALUES ('" + name +"'," + timeUntilHunger +",'"+foodName+"',"+thumbLength+",'"+username +"','"+time+"');";
                }else if (human instanceof Donut){
                    Donut donut = (Donut) human;
                    String foodName = donut.getFoodName();
                    colums = "(name, time_until_hunger, food_name, username, zoned_time) ";
                    value = "VALUES ('" + name +"'," + timeUntilHunger +",'"+foodName+"','"+username +"','"+time+"');";
                }else {
                    colums = "(name, time_until_hunger, username, zoned_time) ";
                    value = "VALUES ('" + name + "'," + timeUntilHunger + ",'"+username +"','"+time+"');";
                }
                statement.executeUpdate(table+colums+value);
            }
            if (statement!=null) statement.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
