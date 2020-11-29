package agh.cs.lab1;

public class RectangularMap extends AbstractWorldMap{
    private final Vector2d lowerLeft = new Vector2d(0, 0);
    private final Vector2d upperRight;

    public RectangularMap(int width, int height) {
        this.upperRight = new Vector2d(width-1, height-1);
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.follows(this.lowerLeft) && position.precedes(this.upperRight) && super.canMoveTo(position);
    }
}