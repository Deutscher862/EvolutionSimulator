package agh.cs.lab1;

import java.util.ArrayList;

public class Animal {
    private MapDirection orientation = MapDirection.NORTH;
    private Vector2d position;
    private final TorusMap map;
    private final ArrayList<IEnergyRunOutObserver> observers = new ArrayList<>();
    private final ArrayList<Vector2d> positionHistory = new ArrayList<>();
    private final Genotype genes;
    private final Animal firstParent;
    private final Animal secondParent;
    private int lifeLength = 0;
    protected int deadAge = -1;
    private int aliveChildren = 0;
    private final int startEnergy;
    private int energy;
    private final int moveEnergy;
    //typ zwierzęcia - wykorzystywany podczas śledzenia jego historii
    protected AnimalType type;

    public Animal(TorusMap map, int startEnergy, int moveEnergy, Animal firstParent, Animal secondParent, Vector2d position, AnimalType type){
        this.map = map;
        this.firstParent = firstParent;
        this.secondParent = secondParent;
        this.startEnergy = startEnergy;
        this.energy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.position = position;
        this.type = type;
        if (firstParent == null)
            this.genes = new Genotype();
        else this.genes = new Genotype(firstParent.getGenes(), secondParent.getGenes());
    }

    public MapDirection getOrientation() {
        return this.orientation;
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public String toString() { return this.orientation.toString(); }

    public Genotype getGenes() {
        return this.genes;
    }

    public int getStartEnergy() { return startEnergy; }

    public int getEnergy() { return this.energy; }

    public int getMoveEnergy() { return moveEnergy; }

    public int getLifeLength() { return lifeLength; }

    public int getAliveChildren() { return aliveChildren; }

    public int getDeadAge() {
        return deadAge;
    }

    public AnimalType getType() {
        return type;
    }

    public void move() {
        int rotate = this.genes.randomDirection();
        for (int i = 0; i < rotate; i++){
            this.orientation = this.orientation.next();
        }
        Vector2d oldPosition = this.position;
        Vector2d newPosition = this.position.add(this.orientation.toUnitVector());
        //dzięki metodzie getBackToMap zwierzę zawsze zostaje na mapie
        this.position = newPosition.getBackToMap(this.map.getUpperRight());

        this.energy -= this.moveEnergy;
        this.lifeLength += 1;
        this.map.positionChanged(oldPosition, this.position, this);
        if(this.energy <= 0) {
            //gdy zwierzę umarło przekazuję informację o tym do mapy, engine i wszystkich przodków
            informAboutDeath();
            if(this.firstParent != null)
                this.firstParent.aliveChildren -= 1;
            if(this.secondParent != null)
                this.secondParent.aliveChildren -= 1;
        }
    }

    public void eat(int grassEnergy){
        this.energy += grassEnergy;
    }

    public Animal reproduce(Animal secondParent, Vector2d childPosition){
        this.energy -= this.energy/4;
        secondParent.energy -= secondParent.energy/4;
        AnimalType childType;
        if(this.type == AnimalType.SELECTED || secondParent.type == AnimalType.SELECTED) childType = AnimalType.CHILD;
        else if(this.type == AnimalType.CHILD || secondParent.type == AnimalType.CHILD || this.type == AnimalType.DESCENDANT || secondParent.type == AnimalType.DESCENDANT)  childType = AnimalType.DESCENDANT;
        else childType = AnimalType.DEFAULT;
        Animal child = new Animal(this.map,(this.energy+secondParent.energy)/4, this.moveEnergy, this, secondParent, childPosition, childType);
        this.aliveChildren += 1;
        secondParent.aliveChildren += 1;
        return child;
    }

    public void addObserver(IEnergyRunOutObserver observer){
        this.observers.add(observer);
    }

    public void informAboutDeath(){
        for(IEnergyRunOutObserver observer : this.observers){
            observer.EnergyRunOut(this);
        }
    }

    public void generateNewPosition() {
        this.position = this.map.getLowerLeft().randomVector(this.map.getUpperRight());
    }
}