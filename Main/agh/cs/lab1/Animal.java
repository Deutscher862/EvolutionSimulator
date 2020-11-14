package agh.cs.lab1;

import java.util.ArrayList;

public class Animal {
    private MapDirection orientation = MapDirection.NORTH;
    private Vector2d position;
    private final IWorldMap map;
    private ArrayList<IPositionChangeObserver> observers = new ArrayList<>();

    public Animal(IWorldMap map) {
        this(map, new Vector2d(0, 0));
    }

    public Animal(IWorldMap map, Vector2d initialPosition) {
        this.map = map;
        this.position = initialPosition;
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

    public void move(MoveDirection direction) {
        Vector2d newPosition;
        switch (direction) {
            case RIGHT:
                this.orientation = this.orientation.next();
                break;
            case LEFT:
                this.orientation = this.orientation.previous();
                break;
            case FORWARD:
                //tworzę nowy Vector2d o nowych współrzędnych, ponieważ takiego przekazania wymaga metoda canMoveTo
                newPosition = this.position.add(this.orientation.toUnitVector());
                if (map.canMoveTo(newPosition)) {
                    positionChanged(this.position, newPosition);
                    this.position = newPosition;
                }
                break;
            case BACKWARD:
                newPosition = this.position.add(this.orientation.toUnitVector().opposite());
                if (map.canMoveTo(newPosition)) {
                    positionChanged(this.position, newPosition);
                    this.position = newPosition;
                }
                break;
        }
    }

    //dodałem hashMapę oraz obserwatorów, aby móc informować mapę o zmianie pozycji zwierzęcia

    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer){
        this.observers.remove(observer);
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        //wszyscy obserwatorzy zostają powiadomieni o zmianie
        for(IPositionChangeObserver ob : this.observers){
            ob.positionChanged(oldPosition, newPosition);
        }
    }
}