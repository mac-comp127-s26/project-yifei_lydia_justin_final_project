import edu.macalester.graphics.*;
import edu.macalester.graphics.Image;

import java.awt.*;

public class Obstacle {
    private Image shape;

    public Obstacle(double x, double y, double width, double height, CanvasWindow canvas) {

    shape = new Image("wall.png");

    shape.setPosition(x, y);
    shape.setMaxWidth(width);
    shape.setMaxHeight(height);

    canvas.add(shape);
    }

    private int age = 0;

    public boolean canCollide() {
    return age > 20; // 
    }

    public boolean isNearPlayer() {
    return shape.getX() < 180;
    }

    public void move() {
        shape.moveBy(-3, 0);
        age++;
    }

    public boolean isOffScreen() {
        return shape.getX() < -50;
    }

    public Image getShape() {
        return shape;
    }

  
}