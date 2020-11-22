package agh.cs.lab1;

public class Grass {
    private final Vector2d position;

    public Grass(Vector2d grass){
        this.position = grass;
    }

    public String toString(){
        return "*";
    }

    public Vector2d getPosition() {
        return position;
    }
}
