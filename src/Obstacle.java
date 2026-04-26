import edu.macalester.graphics.*;
import java.awt.*;

public class Obstacle {
    private Ellipse shape;

    public Obstacle(double x, double y, double size, CanvasWindow canvas) {
        shape = new Ellipse(x, y, size, size);
        shape.setFillColor(Color.RED);
        canvas.add(shape);
    }

    private int age = 0;

    public boolean canCollide() {
    return age > 20; // 
}

    public void move() {
        shape.moveBy(-3, 0);
        age++;
    }

    public boolean isOffScreen() {
        return shape.getX() < -50;
    }

    public Ellipse getShape() {
        return shape;
    }

  
}