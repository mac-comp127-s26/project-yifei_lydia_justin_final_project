import edu.macalester.graphics.*;
import java.awt.*;
import java.util.*;

public class SimpleBirdGame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private static final int PLAYER_SIZE = 30;
    private static final int OBSTACLE_SIZE = 40;

    private CanvasWindow canvas;
    private Ellipse player;
    private ArrayList<Ellipse> obstacles;

    public SimpleBirdGame() {
        canvas = new CanvasWindow("Bird Demo", WIDTH, HEIGHT);

        player = new Ellipse(100, HEIGHT / 2, PLAYER_SIZE, PLAYER_SIZE);
        player.setFillColor(Color.BLUE);
        canvas.add(player);

        obstacles = new ArrayList<>();

        setupMouseControl();
        runGameLoop();
    }

    private void setupMouseControl() {
        canvas.onMouseMove(event -> {
            double newY = event.getPosition().getY() - PLAYER_SIZE / 2;
            player.setPosition(player.getX(), newY);
        });
    }

    private void runGameLoop() {
        canvas.animate(() -> {
            moveObstacles();
            maybeAddObstacle();
        });
    }

    private void moveObstacles() {
        for (Ellipse e : obstacles) {
            e.moveBy(-3, 0);
        }
    }

    private void maybeAddObstacle() {
        if (Math.random() < 0.02) {
            double y = Math.random() * (HEIGHT - OBSTACLE_SIZE);

            Ellipse obstacle = new Ellipse(WIDTH, y, OBSTACLE_SIZE, OBSTACLE_SIZE);
            obstacle.setFillColor(Color.RED);

            obstacles.add(obstacle);
            canvas.add(obstacle);
        }
    }

    public static void main(String[] args) {
        new SimpleBirdGame();
    }
}