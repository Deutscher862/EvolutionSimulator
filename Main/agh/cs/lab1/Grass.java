package agh.cs.lab1;

public class Grass {
    private final Vector2d position;
    private final int energy;

    public Grass(Vector2d grass, int energy){
        this.position = grass;
        this.energy = energy;
    }

    public String toString(){
        return "*";
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public int getEnergy() {
        return energy;
    }
}
