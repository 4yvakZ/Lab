package space.objects;

public abstract class SpaceObject{
    final int radius;

    final String name;
    public SpaceObject(int radius){
        this.radius = radius;
        name = "Космический объект";
    }

    SpaceObject(int radius, String name){
        this.name = name;
        this.radius = radius;
    }

    int getRadius(){
        return radius;
    }

    String getName(){
        return name;
    }

    public String toString(){
        return name;
    }

    public boolean equals(SpaceObject spaceObject){
        return radius == spaceObject.getRadius() && name.equals(spaceObject.getName());
    }
}
