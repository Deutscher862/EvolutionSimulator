package agh.cs.lab1;

import java.util.ArrayList;

public class Animal {
    private MapDirection orientation;
    private Vector2d position;
    private final IWorldMap map;
    private final ArrayList<IPositionChangeObserver> observers = new ArrayList<>();
    private final ArrayList<Vector2d> positionHistory = new ArrayList<>();
    private final Genotype genes;
    protected int Energy;
    private int moveEnergy;

    //konstruktor dla zwierząt stworzonych podczas rozmnażania
    public Animal(IWorldMap map, Animal strongerParent, Animal weakerParent) {
        this.map = map;
        this.genes = new Genotype(strongerParent.getGenes(), weakerParent.getGenes());
        this.Energy = (strongerParent.getEnergy()+weakerParent.getEnergy())/4;
        this.moveEnergy = strongerParent.getMoveEnergy();
    }

    //konstruktor dla pierwszych zwierząt na mapie, bez rodziców
    public Animal(IWorldMap map, int startEnergy, int moveEnergy) {
        this.map = map;
        this.genes = new Genotype();
        this.Energy = startEnergy;
        this.moveEnergy = moveEnergy;
    }

    public MapDirection getOrientation() {
        return this.orientation;
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public String toString() {
        return this.orientation.toString();
    }

    public Genotype getGenes() {
        return this.genes;
    }

    public int getEnergy() { return this.Energy; }

    public int getMoveEnergy() { return moveEnergy; }

    public void move(MoveDirection direction) {
        int rotate = this.genes.randomDirection();
        for (int i = 0; i < rotate; i++){
            this.orientation = this.orientation.next();
        }
        Vector2d oldPosition = this.position;
        this.position = this.position.add(this.orientation.toUnitVector());
        this.Energy -= this.moveEnergy;
        informObservers(oldPosition, this.position);
    }

    public Animal multiplication(Animal secondParent){
        Animal child = new Animal(this.map, this, secondParent);
        this.Energy -= this.Energy/4;
        secondParent.Energy -= secondParent.Energy/4;
        return child;
    }

    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer){
        this.observers.remove(observer);
    }

    private void informObservers(Vector2d oldPosition, Vector2d newPosition){
        //wszyscy obserwatorzy zostają powiadomieni o zmianie
        for(IPositionChangeObserver ob : this.observers){
            ob.positionChanged(oldPosition, newPosition);
        }
    }
}