package rocket;


import people.Human;
import rocket.room.Room;
import space.objects.SpaceObject;

import java.util.TreeSet;


public class Rocket {
    private int velocity;
    private final TreeSet<Human> passengers = new TreeSet<>();
    private Room[] rooms;
    private int numberOfRooms = 0;
    private SpaceObject target;

    public Rocket(int velocity, SpaceObject target) throws SpeedException{
        if(velocity < 0){
            throw new SpeedException(velocity);
        }
        this.velocity = velocity;
        this.target = target;
    }

    public void addPassenger(Human human){
        passengers.add(human);
    }

    public TreeSet<Human> getPassengers(){
        return passengers;
    }

    public int getVelocity(){
        return velocity;
    }

    public int getNumberOfRooms(){
        return numberOfRooms;
    }

    public SpaceObject getTarget(){
        return target;
    }

    public void addRoom(Room room){
        numberOfRooms ++;
        if(numberOfRooms > 1){
            Room[] rooms1 = rooms;
            rooms = new Room[numberOfRooms];
            System.arraycopy(rooms1, 0, rooms, 0, numberOfRooms - 1);
            rooms[numberOfRooms - 1] = room;
        } else {
            rooms = new Room[1];
            rooms[0] = room;
        }
    }

    private Room[] getRooms(){
        return rooms;
    }

    public String toString(){
        return "Ракета летит со скоростью " + velocity + " км/с";
    }

    public boolean equals(Rocket rocket){
        if(velocity == rocket.getVelocity() && numberOfRooms == rocket.getNumberOfRooms() && target.equals(rocket.getTarget())){
            if (!passengers.equals(rocket.passengers)) return false;
            Room[] rooms1 = rocket.getRooms();
            for(int i = 0; i<numberOfRooms; i++){
                if(!rooms[i].equals(rooms1[i])) return false;
            }
            return true;
        }
        return false;
    }
}
