package agh.cs.lab1;

import java.util.*;

public class TorusMap implements IWorldMap, IPositionChangeObserver, IEnergyRunOutObserver {
    private final Random rand  = new Random();
    private final Comparator<Animal> comparator = new EnergyCompare();
    private final Map<Vector2d, List<Animal>> mapOfAnimals = new HashMap<>();
    private final Map<Vector2d,Grass> mapOfGrass = new HashMap<>();
    private final int grassEnergy;
    private final Vector2d lowerLeft = new Vector2d(0, 0);
    private final Vector2d upperRight;
    private final Vector2d jungleLowerLeft;
    private final Vector2d jungleUpperRight;
    private int numberOfGrass;

    public TorusMap(Vector2d upperRight, int grassEnergy, double jungleRatio){
        this.upperRight = upperRight;
        this.grassEnergy = grassEnergy;
        int jungleWidth = (int) Math.round(upperRight.x * jungleRatio);
        int jungleHeight = (int) Math.round(upperRight.y * jungleRatio);

        this.jungleLowerLeft = new Vector2d(upperRight.x/2-jungleWidth/2, upperRight.y/2-jungleHeight/2);
        this.jungleUpperRight = new Vector2d(this.jungleLowerLeft.x+jungleWidth, this.jungleLowerLeft.y+jungleHeight);
    }

    public Vector2d getUpperRight() {
        return upperRight;
    }

    public Vector2d getLowerLeft() {
        return lowerLeft;
    }

    public Map<Vector2d, List<Animal>> getMapOfAnimals() {
        return mapOfAnimals;
    }

    public Map<Vector2d, Grass> getMapOfGrass() {
        return mapOfGrass;
    }

    public int getNumberOfGrass() {
        return numberOfGrass;
    }

    //zwraca true, jeśli podany wektor jest w dżungli
    private boolean inJungle(Vector2d position){
        return position.precedes(this.jungleUpperRight) && position.follows(this.jungleLowerLeft);
    }

    private ArrayList<ArrayList<Vector2d>> searchForFreeSpace(){
        //sprawdzanie, czy na mapie są puste miejsca na dodanie trawy
        //search[0] - savannahFreeSpace, search[1] - jungleFreeSpace
        Vector2d checkPosition;
        ArrayList<ArrayList<Vector2d> > freeSpace =
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
        freeSpace.add(savannahFreeSpace);
        freeSpace.add(jungleFreeSpace);
        return freeSpace;
    }

    @Override
    public void place(Animal animal) {
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
        animal.addObserver(this);
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
            this.numberOfGrass += 1;
        }

        // w dżungli
        if(jungleFreeSpace.size() > 0){
            Grass newGrass;
            newPosition = jungleFreeSpace.get(rand.nextInt(jungleFreeSpace.size()));
            newGrass = new Grass(newPosition, this.grassEnergy);
            this.mapOfGrass.put(newPosition, newGrass);
            this.numberOfGrass += 1;
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
                //counter liczy ilość najsilniejszych zwierząt które zjedzą trawę
                int sameEnergyCounter = 1;
                while (sameEnergyCounter < list.size() && list.get(sameEnergyCounter).getEnergy() == strongestAnimal.getEnergy())
                    sameEnergyCounter += 1;
                // następnie wszystkie najsilniejsze zwięrzęta o tej samej energii dzielą się rośliną
                for (int i = 0; i < sameEnergyCounter; i++) {
                    list.get(i).eat(grass.getEnergy() / sameEnergyCounter);
                }
                //trawa znika z mapy
                this.numberOfGrass -= 1;
                iter.remove();
                grass=null;
            }
        }
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
    }
}