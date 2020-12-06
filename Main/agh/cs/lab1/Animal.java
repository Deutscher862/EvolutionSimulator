package agh.cs.lab1;

import java.util.ArrayList;

public class Animal {
    private MapDirection orientation = MapDirection.NORTH;
    private Vector2d position;
    private final TorusMap map;
    private final ArrayList<IPositionChangeObserver> observers = new ArrayList<>();
    private final ArrayList<Vector2d> positionHistory = new ArrayList<>();
    private final Genotype genes;
    protected int Energy;
    private int moveEnergy;
    private final int number;

    //konstruktor dla zwierząt stworzonych podczas rozmnażania
    public Animal(TorusMap map, Animal strongerParent, Animal weakerParent, Vector2d upperRight) {
        this.map = map;
        this.number = 0;
        this.genes = new Genotype(strongerParent.getGenes(), weakerParent.getGenes());
        this.Energy = (strongerParent.getEnergy()+weakerParent.getEnergy())/4;
        this.moveEnergy = strongerParent.getMoveEnergy();
    }

    //konstruktor dla pierwszych zwierząt na mapie, bez rodziców
    public Animal(TorusMap map, int startEnergy, int moveEnergy, int number) {
        this.map = map;
        this.genes = new Genotype();
        this.Energy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.number = number;
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
//this.orientation.toString()
    //String.valueOf(this.number);
    public String toString() { return this.orientation.toString(); }

    public Genotype getGenes() {
        return this.genes;
    }

    public int getEnergy() { return this.Energy; }

    public int getMoveEnergy() { return moveEnergy; }

    public void move() {
        int rotate = this.genes.randomDirection();
        for (int i = 0; i < rotate; i++){
            this.orientation = this.orientation.next();
        }
        Vector2d oldPosition = this.position;
        Vector2d newPosition = this.position.add(this.orientation.toUnitVector());
        //jeśli zwierzę nie wychodzi poza brzeg mapy pozycja się aktualizuje
        if(this.map.canMoveTo(newPosition))
            this.position = newPosition;
        // w przeciwnym wypadku zwierzę musi pojawić się na drugiej stronie mapy
        else {
            Vector2d max = this.map.getUpperRight();
            if (newPosition.x < 0) {
                if (newPosition.y < 0) this.position = max;
                else if( newPosition.y > max.y) this.position = new Vector2d(max.x,0 );
                else this.position = new Vector2d(max.x ,newPosition.y);
            }
            else if (newPosition.x > max.x){
                if (newPosition.y < 0) this.position = new Vector2d(0, max.y);
                else if(newPosition.y > max.y) this.position = this.map.getLowerLeft();
                else this.position = new Vector2d(0, newPosition.y);
            }
            else if (newPosition.y < 0) this.position = new Vector2d(newPosition.x, max.y);
            else this.position = new Vector2d(newPosition.x, 0);
        }

        this.Energy -= this.moveEnergy;
        informObservers(oldPosition, this.position);
    }

    public Animal multiplication(Animal secondParent){
        Animal child = new Animal(this.map, this, secondParent, this.map.getUpperRight());
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
            ob.positionChanged(oldPosition, newPosition, this);
        }
    }

    public void generateNewPosition() {
        this.position = this.map.getLowerLeft().randomVector(this.map.getUpperRight());
    }
}