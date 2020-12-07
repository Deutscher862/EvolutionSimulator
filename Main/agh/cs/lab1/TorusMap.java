package agh.cs.lab1;

import java.util.*;

/*
W konstruktorze pojawia się pierwsza trawa
Następuje pętla, a w niej:
1.Wszystkie zwierzęta ruszają się i tracą energię
2.Następuje przejście po całej mapie:
    - sprawdzenie czy jakieś zwierzę umarło - jest usuwane z listy i hashmapy
    - sprawdzenie czy jakaś trawa i zwierzę jest na jednym polu - jeśli tak to trawa jest usuwana z hashmapy i zwierze odzyskuje energię
    - sprawdzenie czy kilka zwierząt nie stoi na jednej pozycji i czy mogą się rozmnażać - następuje stworzenie nowego zwierzęta
3. Rośnie nowa trawa
 */

public class TorusMap implements IWorldMap, IPositionChangeObserver {
    private final Random rand  = new Random();
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
    private int numberOfAnimals = 0;
    private int numberOfGrass = 0;

    public TorusMap(Vector2d upperRight, int grassEnergy, float jungleRatio){
        this.upperRight = upperRight;
        this.grassEnergy = grassEnergy;
        this.jungleLowerLeft = new Vector2d(Math.round(jungleRatio*this.upperRight.x), Math.round(jungleRatio*this.upperRight.y));
        this.jungleUpperRight = new Vector2d(Math.round(this.upperRight.x - this.upperRight.x*jungleRatio), Math.round(this.upperRight.y - this.upperRight.y*jungleRatio));
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
        //wszystkie zwierzęta się ruszają
        for(Animal animal : listOfAnimals){
            animal.move();
        }
        //przejście po całej mapie
        Vector2d checkingPosition;
        for(int i = 0; i < this.upperRight.x; i++){
            for(int j = 0; j < this.upperRight.y; j++){
                checkingPosition = new Vector2d(i, j);
                if(objectAt(checkingPosition) instanceof Animal){
                    List<Animal> list = this.mapOfAnimals.get(checkingPosition);
                    if(list.get(list.size()-1).getEnergy() <= 0) removeDeadAnimals(checkingPosition);
                    if(this.mapOfGrass.get(checkingPosition) != null && this.mapOfAnimals.get(checkingPosition) != null) grassEating(checkingPosition);
                    if(list.size() > 1) reproduce(list);
                }
            }
        }
        growGrass();
    }

    //zwraca true, jeśli podany wektor jest w dżungli
    public boolean inJungle(Vector2d position){
        return position.precedes(this.jungleUpperRight) && position.follows(this.jungleLowerLeft);
    }

    public ArrayList<ArrayList<Vector2d>> searchForFreeSpace(){
        //search[0] - savannahFreeSpace, search[1] - jungleFreeSpace
        Vector2d checkPosition;
        ArrayList<ArrayList<Vector2d> > search =
                new ArrayList<>(2);
        ArrayList<Vector2d> jungleFreeSpace = new ArrayList<>();
        ArrayList<Vector2d> savannahFreeSpace = new ArrayList<>();

        for (int i = 0; i <= this.upperRight.x; i++){
            for(int j = 0; j <= this.upperRight.y; j++){
                checkPosition = new Vector2d(i, j);
                if(!isOccupied(checkPosition)){
                    if(inJungle(checkPosition)) jungleFreeSpace.add(checkPosition);
                    else savannahFreeSpace.add(checkPosition);
                }
            }
        }
        search.add(savannahFreeSpace);
        search.add(jungleFreeSpace);
        return search;
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return false;
    }

    @Override
    public boolean place(Animal animal) {
        if (!isOccupied(animal.getPosition())){
            animal.addObserver(this);
            List<Animal> list = new ArrayList<>();
            list.add(animal);
            mapOfAnimals.put(animal.getPosition(), list);
            listOfAnimals.add(animal);
            this.numberOfAnimals += 1;
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
        Vector2d newPosition;
        ArrayList<ArrayList<Vector2d>> freeSpace = searchForFreeSpace();
        ArrayList<Vector2d> savannahFreeSpace = freeSpace.get(0);
        ArrayList<Vector2d> jungleFreeSpace = freeSpace.get(1);

        // w sawannie
        if(savannahFreeSpace.size() > 0){
            Grass newGrass;
            newPosition = savannahFreeSpace.get(rand.nextInt(savannahFreeSpace.size()));
            newGrass = new Grass(newPosition, this.grassEnergy);
            this.mapOfGrass.put(newPosition, newGrass);
        }

        // w dżungli
        if(jungleFreeSpace.size() > 0){
            Grass newGrass;
            newPosition = jungleFreeSpace.get(rand.nextInt(jungleFreeSpace.size()));
            newGrass = new Grass(newPosition, this.grassEnergy);
            this.mapOfGrass.put(newPosition, newGrass);
        }
    }

    private void grassEating(Vector2d position){
        //gdy na polu z trawą pojawią się zwierzę
        Grass grass = mapOfGrass.get(position);
        List<Animal> list = this.mapOfAnimals.get(position);
        Animal strongestAnimal = list.get(0);
        //counter liczy ilość zwierząt które zjedzą trawę
        int sameEnergyCounter = 1;
        while(sameEnergyCounter < list.size() && list.get(sameEnergyCounter).getEnergy() == strongestAnimal.getEnergy())
            sameEnergyCounter += 1;
        // następnie wszystkie zwięrzęta o tej samej energii dzielą się rośliną
        for(int i = 0; i < sameEnergyCounter; i++){
            list.get(i).eat(grass.getEnergy()/sameEnergyCounter);
        }
        //trawa znika z mapy
        numberOfGrass -= 1;
        mapOfGrass.replace(position, null);
    }

    private void reproduce(List<Animal> list){
        Animal strongerParent = list.get(0);
        Animal weakerParent = list.get(1);
        //sprawdzam czy zwierzęta mają wystarczająco dużo energii do rozmnażania
        if(strongerParent.getEnergy() > strongerParent.getStartEnergy()/2 && weakerParent.getEnergy() > weakerParent.getStartEnergy()/2 ){
            //szukam czy dookoła rodziców jest jakieś wolne pole
            ArrayList<Vector2d> freeSpace = new ArrayList<>();
            Vector2d nearestPosition;
            MapDirection lookForFreeSpace = strongerParent.getOrientation();
            for (int i = 0; i < 8; i++){
                nearestPosition = strongerParent.getPosition().add(lookForFreeSpace.toUnitVector()).getBackToMap(getUpperRight());
                if(!isOccupied(nearestPosition)){
                    freeSpace.add(nearestPosition);
                }
                lookForFreeSpace = lookForFreeSpace.next();
            }
            //jeśli istnieje jakieś puste miejsce, to je losuję i dodaję do metody reproduce
            Vector2d childPosition;
            if(freeSpace.size() > 0)
                childPosition = freeSpace.get(rand.nextInt(freeSpace.size()));
            //jeśli nie, to losuje zajęte miejsce
            else{
                int spin = rand.nextInt(8);
                for (int i = 0; i< spin; i ++)
                    lookForFreeSpace = lookForFreeSpace.next();
                childPosition = strongerParent.getPosition().add(lookForFreeSpace.toUnitVector()).getBackToMap(getUpperRight());
            }
            Animal child = strongerParent.reproduce(weakerParent, childPosition);
            this.place(child);
        }
    }

    private void removeDeadAnimals(Vector2d position) {
        List<Animal> list = this.mapOfAnimals.get(position);
        //lista jest posortowana, więc usuwam ostatni element dopóki są tam martwe zwierzęta
        while(list.size() > 0 && list.get(list.size()-1).getEnergy() <= 0){
            Animal deadAnimal = list.get(list.size()-1);

            this.listOfAnimals.remove(deadAnimal);
            this.numberOfAnimals -= 1;

            list.remove(deadAnimal);
            System.out.println("umarło :c");
        }
        if (list.size() == 0) this.mapOfAnimals.replace(position, null);
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