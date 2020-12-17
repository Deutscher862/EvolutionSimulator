package agh.cs.lab1;

import java.util.*;

public class TorusMap implements IWorldMap, IPositionChangeObserver {
    private final Random rand  = new Random();
    private final ArrayList<Animal> listOfAnimals = new ArrayList<>();
    private final ArrayList<Animal> animalsToRemove = new ArrayList<>();
    private final Comparator<Animal> comparator = new EnergyCompare();
    protected final Map<Vector2d, List<Animal>> mapOfAnimals = new HashMap<>();
    private final Map<Vector2d,Grass> mapOfGrass = new HashMap<>();
    private final MapVisualizer visualize = new MapVisualizer(this);
    private final int grassEnergy;
    private final Vector2d lowerLeft = new Vector2d(0, 0);
    private final Vector2d upperRight;
    private final Vector2d jungleLowerLeft;
    private final Vector2d jungleUpperRight;
    public final Statistics stats = new Statistics();

    public TorusMap(Vector2d upperRight, int grassEnergy, float jungleRatio){
        this.upperRight = upperRight;
        this.grassEnergy = grassEnergy;
        int jungleWidth = Math.round(upperRight.x * jungleRatio);
        int jungleHeight = Math.round(upperRight.y * jungleRatio);

        this.jungleLowerLeft = new Vector2d(upperRight.x/2-jungleWidth/2, upperRight.y/2-jungleHeight/2);
        this.jungleUpperRight = new Vector2d(this.jungleLowerLeft.x+jungleWidth, this.jungleLowerLeft.y+jungleHeight);
        System.out.println(this.lowerLeft.toString() + this.upperRight.toString());
        System.out.println(this.jungleLowerLeft.toString() + this.jungleUpperRight.toString());

    }

    public Vector2d getUpperRight() {
        return upperRight;
    }

    public Vector2d getLowerLeft() {
        return lowerLeft;
    }

    public ArrayList<Animal> getListOfAnimals() { return listOfAnimals; }

    public Map<Vector2d, List<Animal>> getMapOfAnimals() {
        return mapOfAnimals;
    }

    public Map<Vector2d, Grass> getMapOfGrass() {
        return mapOfGrass;
    }

    public String toString(){
        return this.visualize.draw(this.lowerLeft, this.upperRight);
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

        for (int i = 0; i < this.upperRight.x; i++){
            for(int j = 0; j < this.upperRight.y; j++){
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
        if (this.mapOfAnimals.get(animal.getPosition()) == null){
            List<Animal> list = new ArrayList<>();
            list.add(animal);
            mapOfAnimals.put(animal.getPosition(), list);
        }
        //tylko jeśli chcę zplace'ować dziecko a nie ma pustych miejsc
        else{
            List<Animal> list = this.mapOfAnimals.get(animal.getPosition());
            list.add(animal);
            list.sort(comparator);
        }
        this.stats.addToHashmap(animal);
        animal.addObserver(this);
        listOfAnimals.add(animal);
        this.stats.numberOfAnimals += 1;
        return true;
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

    public void growGrass(){
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
            this.stats.numberOfGrass += 1;
        }

        // w dżungli
        if(jungleFreeSpace.size() > 0){
            Grass newGrass;
            newPosition = jungleFreeSpace.get(rand.nextInt(jungleFreeSpace.size()));
            newGrass = new Grass(newPosition, this.grassEnergy);
            this.mapOfGrass.put(newPosition, newGrass);
            this.stats.numberOfGrass += 1;
        }
    }

    public void grassEating(){
        //iteruję po wszystkich polach z roślinami, szukając na nich zwierząt
        Iterator<Map.Entry<Vector2d,Grass>> iter = this.mapOfGrass.entrySet().iterator();
        while (iter.hasNext()) {
            Vector2d currentGrassPosition = iter.next().getKey();
            if(objectAt(currentGrassPosition) instanceof Animal) {
                //gdy na polu z trawą pojawią się zwierzę
                Grass grass = mapOfGrass.get(currentGrassPosition);
                List<Animal> list = this.mapOfAnimals.get(currentGrassPosition);
                Animal strongestAnimal = list.get(0);
                //counter liczy ilość zwierząt które zjedzą trawę
                int sameEnergyCounter = 1;
                while (sameEnergyCounter < list.size() && list.get(sameEnergyCounter).getEnergy() == strongestAnimal.getEnergy())
                    sameEnergyCounter += 1;
                // następnie wszystkie najsilniejsze zwięrzęta o tej samej energii dzielą się rośliną
                for (int i = 0; i < sameEnergyCounter; i++) {
                    list.get(i).eat(grass.getEnergy() / sameEnergyCounter);
                }
                //trawa znika z mapy
                this.stats.numberOfGrass -= 1;
                iter.remove();
            }
        }
    }

    public void reproduce(){
        //iteruję po wszystkich polach z mapy, na których znajdują się zwierzęta
        ArrayList<Animal> childrenToPlace = new ArrayList<>();
        for (Map.Entry<Vector2d, List<Animal>> vector2dListEntry : this.mapOfAnimals.entrySet()) {
            List<Animal> currentList = vector2dListEntry.getValue();
            if (currentList.size() >= 2) {
                Animal strongerParent;
                Animal weakerParent;
                //wybieranie rodziców
                int j = 1;
                while (j < currentList.size() && currentList.get(j).getEnergy() == currentList.get(0).getEnergy())
                    j += 1;
                if (j == 1) {
                    strongerParent = currentList.get(0);
                    int k = 2;
                    while (k < currentList.size() && currentList.get(k).getEnergy() == currentList.get(1).getEnergy())
                        k += 1;
                    if (k == 2) weakerParent = currentList.get(1);
                    else {
                        weakerParent = currentList.get(rand.nextInt(k - 1) + 1);
                    }
                } else {
                    strongerParent = currentList.get(rand.nextInt(j));
                    weakerParent = currentList.get(rand.nextInt(j));
                    while (strongerParent.equals(weakerParent))
                        weakerParent = currentList.get(rand.nextInt(j));
                }

                //sprawdzam czy zwierzęta mają wystarczająco dużo energii do rozmnażania
                if (strongerParent.getEnergy() > strongerParent.getStartEnergy() / 2 && weakerParent.getEnergy() > weakerParent.getStartEnergy() / 2) {
                    //szukam czy dookoła rodziców jest jakieś wolne pole
                    ArrayList<Vector2d> freeSpace = new ArrayList<>();
                    Vector2d nearestPosition;
                    MapDirection lookForFreeSpace = strongerParent.getOrientation();
                    for (int i = 0; i < 8; i++) {
                        nearestPosition = strongerParent.getPosition().add(lookForFreeSpace.toUnitVector()).getBackToMap(getUpperRight());
                        if (!isOccupied(nearestPosition)) {
                            freeSpace.add(nearestPosition);
                        }
                        lookForFreeSpace = lookForFreeSpace.next();
                    }
                    //jeśli istnieje jakieś puste miejsce, to je losuję i dodaję do metody reproduce
                    Vector2d childPosition;
                    if (freeSpace.size() > 0)
                        childPosition = freeSpace.get(rand.nextInt(freeSpace.size()));
                        //jeśli nie, to losuje zajęte miejsce
                    else {
                        int spin = rand.nextInt(8);
                        for (int i = 0; i < spin; i++)
                            lookForFreeSpace = lookForFreeSpace.next();
                        childPosition = strongerParent.getPosition().add(lookForFreeSpace.toUnitVector()).getBackToMap(getUpperRight());
                    }
                    Animal child = strongerParent.reproduce(weakerParent, childPosition);
                    childrenToPlace.add(child);
                }
            }
        }
        //gdy wszystkie dzieci zostają stworzone, dodaję je do mapy
        for(Animal child : childrenToPlace) this.place(child);
    }

    public void removeDeadAnimals() {
        //usuwam wszystkie martwe zwierzęta z mapy
        this.stats.numberOfAnimals -= this.animalsToRemove.size();
        this.stats.numberOfDeadAnimals += this.animalsToRemove.size();
        for(Animal animal : this.animalsToRemove){
            this.stats.sumOfLifeLengths += animal.getLifeLength();
            this.listOfAnimals.remove(animal);
            this.stats.removeFromHashmap(animal);
        }
        this.animalsToRemove.clear();
    }

    @Override
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
            this.mapOfAnimals.remove(oldPosition);

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

    @Override
    public void EnergyRunOut(Animal animal) {
        List<Animal> list = this.mapOfAnimals.get(animal.getPosition());
        list.remove(animal);
        if (list.size() == 0) this.mapOfAnimals.remove(animal.getPosition());
        //ponieważ martwe zwierzę wykonuje ruch w SimulationEngine, nie mogę usunąc go z listy zwierząt
        // zapisuję je więc do listy zwierząt do usunięcia, którą wywołam po zakończenia ruchu zwierzęcia
        this.animalsToRemove.add(animal);
    }
}