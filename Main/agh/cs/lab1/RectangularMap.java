package agh.cs.lab1;

public class RectangularMap extends AbstractWorldMap{
    private final int width;
    private final int height;
    private final Vector2d lowerLeft = new Vector2d(0, 0);
    private final Vector2d upperRight;

    public RectangularMap(int width, int height) {
        this.width = width-1;
        this.height = height-1;
        this.upperRight = new Vector2d(this.width, this.height);
    }

    @Override
    public Vector2d getLowerLeft(){
        return this.lowerLeft;
    }

    @Override
    public Vector2d getUpperRight(){
        return this.upperRight;
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.follows(this.lowerLeft) && position.precedes(this.upperRight) && super.canMoveTo(position);
    }
}