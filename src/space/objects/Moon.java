package space.objects;

public class Moon extends SpaceObject implements OrbitalObject{
    private final int orbit;//Радиус орбиты
    private final SpaceObject orbitCenter;//Центр орбиты
    public Moon(int radius, int orbit, SpaceObject orbitCenter){
        super(radius, "Луна");
        this.orbit = orbit;
        this.orbitCenter = orbitCenter;
    }

    public int getOrbit(){
        return orbit;
    }

    public SpaceObject getOrbitCenter(){
        return orbitCenter;
    }

    public void orbitInfo(){
        System.out.println(name + " имеет орбиту вокруг " + orbitCenter.toString() + " радиуса " + orbit + ".");
    }

    public boolean equals(Moon moon){
        return radius == moon.getRadius() && name.equals(moon.getName()) && orbit == moon.getOrbit() && orbitCenter.equals(moon.getOrbitCenter());
    }
}