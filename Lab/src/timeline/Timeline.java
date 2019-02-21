package timeline;

public abstract class Timeline {
    static private int currentTime;

    static public void setTime(int Time){
        currentTime = Time;
    }

    static public void increaseTime(){
        currentTime++;
    }

    static public int getCurrentTime(){
        return currentTime;
    }
}
