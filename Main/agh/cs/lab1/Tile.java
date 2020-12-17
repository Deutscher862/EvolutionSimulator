package agh.cs.lab1;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Tile extends StackPane{
    private final int size;
    private final Rectangle rectangle;
    private final int x;
    private final int y;
    public Color color;

    public Tile(int size, int x, int y, Color color){
        this.size = size;
        this.x = x;
        this.y = y;
        this.rectangle = new Rectangle(size, size );
        this.rectangle.setStroke(Color.BLACK);
        this.rectangle.setFill(color);
        this.getChildren().add(rectangle);
        this.setTranslateX(x*size+10);
        this.setTranslateY(y*size+10);
    }

    public void setColor(Color color) {
        this.color = color;
        this.rectangle.setFill(this.color);
    }

}
