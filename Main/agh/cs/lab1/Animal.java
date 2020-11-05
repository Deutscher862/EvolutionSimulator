package agh.cs.lab1;

public class Animal {
    private MapDirection orientation = MapDirection.NORTH;
    private Vector2d position;
    private IWorldMap map;
    private Vector2d BottomLeft  = new Vector2d(0, 0);
    private Vector2d TopRight = new Vector2d(4, 4);

    public Animal() {
        this.position = new Vector2d(2, 2);
    }

    public Animal(Vector2d position) {
        this.position = position;
    }

    public Animal(IWorldMap map){
        this.map = map;
    }

    public Animal(IWorldMap map, Vector2d initialPosition){
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
        return this.orientation.toString() + this.position.toString();
    }

    public void move(MoveDirection direction) {
        switch (direction) {
            case RIGHT:
                this.orientation = this.orientation.next();
                break;
            case LEFT:
                this.orientation = this.orientation.previous();
                break;
            case FORWARD:
                if (this.orientation == MapDirection.SOUTH || this.orientation == MapDirection.WEST)
                    this.position = this.position.add(this.orientation.toUnitVector()).upperRight(BottomLeft);
                else
                    this.position = this.position.add(this.orientation.toUnitVector()).lowerLeft(TopRight);
                break;
            case BACKWARD:
                if (this.orientation == MapDirection.SOUTH || this.orientation == MapDirection.WEST)
                    this.position = this.position.add(this.orientation.toUnitVector().opposite()).lowerLeft(TopRight);
                else
                    this.position = this.position.add(this.orientation.toUnitVector().opposite()).upperRight(BottomLeft);
                break;
        }
    }
}