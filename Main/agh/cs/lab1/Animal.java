package agh.cs.lab1;

public class Animal {
    private MapDirection orientation = MapDirection.NORTH;
    private Vector2d position;
    private IWorldMap map;
    private Vector2d BottomLeft = new Vector2d(0, 0);
    private Vector2d TopRight = new Vector2d(4, 4);

    public Animal(IWorldMap map) {
        this.map = map;
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
        Vector2d checkBeforeMove;
        switch (direction) {
            case RIGHT:
                this.orientation = this.orientation.next();
                break;
            case LEFT:
                this.orientation = this.orientation.previous();
                break;
            case FORWARD:
                //tworzę nowy Vector2d o nowych współrzędnych, ponieważ takiego przekazania wymaga metoda canMoveTo
                checkBeforeMove = this.position.add(this.orientation.toUnitVector());
                if (map.canMoveTo(checkBeforeMove))
                    this.position = checkBeforeMove;
                break;
            case BACKWARD:
                checkBeforeMove = this.position.add(this.orientation.toUnitVector().opposite());
                if (map.canMoveTo(checkBeforeMove))
                    this.position = checkBeforeMove;
                break;
        }
    }
}