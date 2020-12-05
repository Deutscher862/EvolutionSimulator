package agh.cs.lab1;

import java.util.*;

/*
W konstruktorze pojawia się pierwsza trawa
Następuje pętla, a w niej:
1.Wszystkie zwierzęta ruszają się i tracą energię
2.Następuje przejście po całej mapie:
    - sprawdzenie czy jakaś trawa i zwierzę jest na jednym polu - jeśli tak to trawa jest usuwana z hashmapy i zwierze odzyskuje energię
    - sprawdzenie czy jakieś zwierzę umarło - jest usuwane z listy i hashmapy
    - sprawdzenie czy kilka zwierząt nie stoi na jednej pozycji i czy mogą się rozmnażać - następuje stworzenie nowego zwierzęta
3. Rośnie nowa trawa
 */

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

    public TorusMap(Vector2d upperRight, int numberOfGrass, int grassEnergy, float jungleRatio){
        this.upperRight = upperRight;
        this.grassEnergy = grassEnergy;
        this.jungleLowerLeft = new Vector2d(Math.round(jungleRatio*this.upperRight.x), Math.round(jungleRatio*this.upperRight.y));
        this.jungleUpperRight = new Vector2d(Math.round(this.upperRight.x - this.upperRight.x*jungleRatio), Math.round(this.upperRight.y - this.upperRight.y*jungleRatio));

        for (int i = 0; i < numberOfGrass/2; i++){
            growGrass();
        }
    }

    public Vector2d getUpperRight() {
        return upperRight;
    }

    public Vector2d getLowerLeft() {
        return lowerLeft;
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.follows(this.lowerLeft) && position.precedes(this.upperRight);
    }

    @Override
    public boolean place(Animal animal) {
        if (!isOccupied(animal.getPosition())){
            List<Animal> list = new ArrayList<>();
            list.add(animal);
            //list.sort(comparator);
            mapOfAnimals.put(animal.getPosition(), list);
            listOfAnimals.add(animal);
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