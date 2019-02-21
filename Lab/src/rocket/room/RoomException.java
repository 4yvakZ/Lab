package rocket.room;

public class RoomException extends NullPointerException{
    public RoomException(String name, Throwable e){
        System.out.println(name + " оказался в космосе!");
        initCause(e);
    }
}
