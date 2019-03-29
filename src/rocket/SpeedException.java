package rocket;

public class SpeedException extends Exception{
    private final int velocity;
    public SpeedException(int velosity){
        this.velocity = velosity;
    }
    public String getMessage(){
        return "Скорость ракеты должна быть положительной\n"+ "Введённое значение: "+velocity;
    }
}
