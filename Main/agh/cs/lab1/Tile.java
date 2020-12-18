package agh.cs.lab1;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends StackPane{
    private final int size;
    private final Rectangle rectangle;
    private final Vector2d position;
    public Color color;

    public Tile(int size, Vector2d position, Color color, MapVizualizerFX vizualizerFX){
        this.size = size;
        this.position = position;
        this.rectangle = new Rectangle(size, size );
        this.rectangle.setStroke(Color.BLACK);
        this.rectangle.setFill(color);
        this.getChildren().add(rectangle);
        this.setTranslateX(this.position.x*size+10);
        this.setTranslateY(this.position.y*size+10);

        setOnMouseClicked(event -> vizualizerFX.selectAnimal(this.position));
    }

    public void setColor(Color color) {
        this.color = color;
        this.rectangle.setFill(this.color);
    }

}