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
    private int aliveChildren = 0;
    private final int startEnergy;
    private int energy;
    private final int moveEnergy;
    private int aliveDescendants = 0;
    private boolean visited = false;

    public Animal(TorusMap map, int startEnergy, int moveEnergy, Animal firstParent, Animal secondParent, Vector2d position){
        this.map = map;
        this.firstParent = firstParent;
        this.secondParent = secondParent;
        this.startEnergy = startEnergy;
        this.energy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.position = position;
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
            //gdy zwierzę umarło przekazuję informację o tym do mapy i wszystkich przodków
            this.map.EnergyRunOut(this);
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
        Animal child = new Animal(this.map,(this.energy+secondParent.energy)/4, this.moveEnergy, this, secondParent, childPosition);
        this.aliveChildren += 1;
        secondParent.aliveChildren += 1;
        return child;
    }

    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }

    private void informAboutDescendant(boolean deadInformation){
        //rekurencyjna funkcja odwłująca się do kolejnych przodków zwierzęcia informując o jego stanie
        //deadInformation 0 - nowonarodzony, 1 - martwy
        if (!deadInformation)
            this.aliveDescendants += 1;
        else this.aliveDescendants -= 1;
        this.visited = true;
        if(this.firstParent != null && !firstParent.visited) this.firstParent.informAboutDescendant(deadInformation);
        if(this.secondParent != null && !secondParent.visited) this.secondParent.informAboutDescendant(deadInformation);
        this.visited = false;
    }

    public void generateNewPosition() {
        this.position = this.map.getLowerLeft().randomVector(this.map.getUpperRight());
    }
}