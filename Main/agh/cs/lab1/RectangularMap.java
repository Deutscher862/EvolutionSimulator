package agh.cs.lab1;

public class RectangularMap extends AbstractWorldMap{
    private int width;
    private int height;

    public RectangularMap(int width, int height) {
        this.width = width-1;
        this.height = height-1;
        this.upperRight = new Vector2d(this.width, this.height);
    }

    @Override
    public String toString() {
        return super.toString(super.lowerLeft, this.upperRight);
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.x <= this.width && position.y <= this.height && position.x >= 0 && position.y >= 0 && super.canMoveTo(position);
    }
}