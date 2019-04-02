package rocket.room;

public class Room implements Comparable<Room>{
    private final Type type;
    private final String name;
    public Room(Type type, String name){
        this.type = type;
        this.name = name;
    }
    public String toString(){
        return name;
    }
    public Type getType(){
        return type;
    }
    public boolean equals(Room room){
        return type == room.getType() && name.equals(room.toString());
    }

    @Override
    public int compareTo(Room o) {
        return type.compareTo(o.getType());
    }
}