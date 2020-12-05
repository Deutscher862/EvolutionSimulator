package agh.cs.lab1;

import java.util.*;

public class TorusMap implements IWorldMap {

    private final ArrayList<Animal> listOfAnimals = new ArrayList<>();
    private final Comparator<Animal> comparator = new EnergyCompare();
    protected final Map<Vector2d, List<Animal>> mapOfAnimals = new HashMap<>();
    private final Map<Vector2d,Grass> mapOfGrass = new HashMap<>();
    private final MapVisualizer visualize = new MapVisualizer(this);
    private final int grassEnergy;
    private final Vector2d lowerLeft = new Vector2d(0, 0);
    private final Vector2d upperRight;
    private final Vector2d jungleLowerLeft;
    private final Vector2d jungleUpperRight;
    private final int width;
    private final int height;

    public TorusMap(int width, int height, int numberOfGrass, int grassEnergy, float jungleRatio){
        this.width = width;
        this.height = height;
        this.upperRight = new Vector2d(width, height);
        this.grassEnergy = grassEnergy;
        this.jungleLowerLeft = new Vector2d(Math.round(jungleRatio*this.width), Math.round(jungleRatio*this.height));
        this.jungleUpperRight = new Vector2d(Math.round(this.width - this.width*jungleRatio), Math.round(this.height - this.height*jungleRatio));

        for (int i = 0; i < numberOfGrass/2; i++){
            growGrass();
        }
    }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return false;
    }

    @Override
    public boolean place(Animal animal) {
        if (!isOccupied(animal.getPosition())){
            List<Animal> list = new ArrayList<>();
            list.add(animal);
            list.sort(comparator);
            mapOfAnimals.put(animal.getPosition(), list);
            return true;
        }
        return false;
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return !(objectAt(position) == null);
    }

    @Override
    public Object objectAt(Vector2d position) {
        Object object = mapOfAnimals.get(position);
        if(object == null)
            return mapOfGrass.get(position);
        else return object;
    }

    private void growGrass(){
        Vector2d newPosition = this.lowerLeft.randomVector(this.upperRight);
        Grass newGrass;
        int placed = 0;
        // poza dżunglą
        /*while(){

        }*/
        newGrass = new Grass(newPosition);
        this.mapOfGrass.put(newPosition, newGrass);

        newPosition = this.jungleLowerLeft.randomVector(this.jungleUpperRight);
        // w dżungli
        /*while(){

        }*/
        newGrass = new Grass(newPosition);
        this.mapOfGrass.put(newPosition, newGrass);

    }
}