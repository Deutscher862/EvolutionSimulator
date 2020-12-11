package agh.cs.lab1;

import java.util.ArrayList;

public class Animal {
    private MapDirection orientation = MapDirection.NORTH;
    private Vector2d position;
    private final TorusMap map;
    private final ArrayList<IPositionChangeObserver> observers = new ArrayList<>();
    private final ArrayList<Vector2d> positionHistory = new ArrayList<>();
    private final Genotype genes;
    private final Animal firstParent;
    private final Animal secondParent;
    private int lifeLength = 0;
    private int numberOfChildren = 0;
    private int numberOfDescendants = 0;
    private final int startEnergy;
    private int energy;
    private final int moveEnergy;

    //konstruktor dla zwierząt stworzonych podczas rozmnażania
    public Animal(TorusMap map, Animal strongerParent, Animal weakerParent, Vector2d position) {
        this.map = map;
        this.position = position;
        this.firstParent = strongerParent;
        this.secondParent = weakerParent;
        this.genes = new Genotype(strongerParent.getGenes(), weakerParent.getGenes());
        this.startEnergy = (strongerParent.getEnergy()+weakerParent.getEnergy())/4;
        this.energy = this.startEnergy;
        this.moveEnergy = strongerParent.getMoveEnergy();
        informAboutDescendant();
    }

    //konstruktor dla pierwszych zwierząt na mapie, bez rodziców
    public Animal(TorusMap map, int startEnergy, int moveEnergy) {
        this.map = map;
        this.firstParent = null;
        this.secondParent = null;
        this.genes = new Genotype();
        this.startEnergy = startEnergy;
        this.energy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.position = this.map.getLowerLeft().randomVector(this.map.getUpperRight());
        int rotate = this.genes.randomDirection();
        for (int i = 0; i < rotate; i++){
            this.orientation = this.orientation.next();
        }
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

    public int getNumberOfChildren() { return numberOfChildren; }

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
        changedPositionInform(oldPosition, this.position);
        if(this.energy <= 0) energyRunOutInform();
    }

    public void eat(int grassEnergy){
        this.energy += grassEnergy;
    }

    public Animal reproduce(Animal secondParent, Vector2d childPosition){
        Animal child = new Animal(this.map,this, secondParent, childPosition);
        this.energy -= this.energy/4;
        secondParent.energy -= secondParent.energy/4;
        this.numberOfChildren += 1;
        secondParent.numberOfChildren += 1;
        return child;
    }

    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer){
        this.observers.remove(observer);
    }

    private void changedPositionInform(Vector2d oldPosition, Vector2d newPosition){
        for(IPositionChangeObserver ob : this.observers){
            ob.positionChanged(oldPosition, newPosition, this);
        }
    }

    private void informAboutDescendant(){
        this.numberOfDescendants += 1;
        if(this.firstParent != null) this.firstParent.informAboutDescendant();
        if(this.secondParent != null) this.secondParent.informAboutDescendant();
    }

    private void energyRunOutInform(){
        for(IPositionChangeObserver ob : this.observers){
            ob.EnergyRunOut(this);
        }
    }

    public void generateNewPosition() {
        this.position = this.map.getLowerLeft().randomVector(this.map.getUpperRight());
    }
}