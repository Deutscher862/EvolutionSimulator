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

public class TorusMap implements IWorldMap, IPositionChangeObserver {
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

       /*for (int i = 0; i < numberOfGrass/2; i++){
            growGrass();
        }*/
    }

    public Vector2d getUpperRight() {
        return upperRight;
    }

    public Vector2d getLowerLeft() {
        return lowerLeft;
    }

    public String toString(){
        return this.visualize.draw(this.lowerLeft, this.upperRight);
    }

    public void move(){
        for(Animal animal : listOfAnimals){
            animal.move();
        }
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.follows(this.lowerLeft) && position.precedes(this.upperRight);
    }

    @Override
    public boolean place(Animal animal) {
        if (!isOccupied(animal.getPosition())){
            animal.addObserver(this);
            List<Animal> list = new ArrayList<>();
            list.add(animal);
            mapOfAnimals.put(animal.getPosition(), list);
            listOfAnimals.add(animal);
            System.out.println("Dodane na" + list.get(0).toString());
            System.out.println(list.get(0).getPosition().toString());
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
        List<Animal> list = mapOfAnimals.get(position);
        if(list != null) return list.get(0);
        else return mapOfGrass.get(position);
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

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animalToMove) {
        //usuwam zwierzę z hashmapy na starym polu
        List<Animal> oldPositionList = this.mapOfAnimals.get(oldPosition);
        for (Object animal : oldPositionList){
            if(animal.equals(animalToMove)){
                oldPositionList.remove(animalToMove);
                break;
            }
        }
        //jeśli lista zwierząt na danym jest pusta to ją usuwam
        if(oldPositionList.size() == 0)
            this.mapOfAnimals.replace(oldPosition, null);

        //jeśli na nowej pozycji nie ma listy to ją tworzę i dodaję tam zwierzę
        if(this.mapOfAnimals.get(newPosition) == null){
            List<Animal> newList = new ArrayList<>();
            newList.add(animalToMove);
            mapOfAnimals.put(newPosition, newList);
        }
        //jeśli na nowej pozycji jest już zwierzę to sortuję listę zwierząt po ich energii
        else{
            List<Animal> newPositionList = this.mapOfAnimals.get(newPosition);
            newPositionList.add(animalToMove);
            newPositionList.sort(comparator);
        }
    }
}