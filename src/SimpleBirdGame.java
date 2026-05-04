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
    private ArrayList<Obstacle> obstacles;

    private boolean isGameOver = false;
    private int frameCount = 0;
    private int score = 0;

    private GraphicsText scoreText;
    private GraphicsText gameOverText;

    public SimpleBirdGame() {
        canvas = new CanvasWindow("Bird Demo", WIDTH, HEIGHT);

        setupGame();
        setupMouseControl();
        runGameLoop();
    }

    private void setupGame() {
        canvas.removeAll();

        player = new Ellipse(100, HEIGHT / 2, PLAYER_SIZE, PLAYER_SIZE);
        player.setFillColor(Color.BLUE);
        canvas.add(player);

        obstacles = new ArrayList<>();

        isGameOver = false;
        frameCount = 0;
        score = 0;

        scoreText = new GraphicsText("Score: 0");
        scoreText.setFontSize(16);
        scoreText.setPosition(10, 20);
        canvas.add(scoreText);
    }

    private void setupMouseControl() {
        canvas.onMouseMove(event -> {
            if (!isGameOver) {
                double newY = event.getPosition().getY() - PLAYER_SIZE / 2;
                player.setPosition(player.getX(), newY);
            }
        });

        canvas.onMouseDown(event -> {
            if (isGameOver) {
                setupGame();
            }
        });
    }

    private void runGameLoop() {
        canvas.animate(() -> {
            if (!isGameOver) {
                frameCount++;

                moveObstacles();

                if (frameCount > 60) {
                    maybeAddObstacle();
                    checkCollision();
                }

                updateScore();
                removeOffscreenObstacles();
            }
        });
    }

    private void maybeAddObstacle() {
        if (Math.random() < 0.1) {
            double y = Math.random() * (HEIGHT - OBSTACLE_SIZE - 100) + 50;

            Obstacle o = new Obstacle(WIDTH, y, OBSTACLE_SIZE, canvas);
            obstacles.add(o);
        }
    }

    private void moveObstacles() {
        for (Obstacle o : obstacles) {
            o.move();
        }
    }


    private void checkCollision() {
        for (Obstacle o : obstacles) {

            double dx = player.getCenter().getX() - o.getShape().getCenter().getX();
            double dy = player.getCenter().getY() - o.getShape().getCenter().getY();

            double distance = Math.sqrt(dx * dx + dy * dy);

            double radiusSum = PLAYER_SIZE / 2.0 + OBSTACLE_SIZE / 2.0;

            if (!isGameOver && distance < radiusSum) {
                gameOver();
            }
        }
    }

    private void removeOffscreenObstacles() {
        Iterator<Obstacle> iter = obstacles.iterator();
        while (iter.hasNext()) {
            Obstacle o = iter.next();
            if (o.isOffScreen()) {
                canvas.remove(o.getShape());
                iter.remove();
            }
        }
    }


    private void updateScore() {
        score++;
        scoreText.setText("Score: " + score);
    }

    private void gameOver() {
        isGameOver = true;

        gameOverText = new GraphicsText("Game Over! Click to Restart");
        gameOverText.setFontSize(30);
        gameOverText.setPosition(WIDTH / 2 - 200, HEIGHT / 2);
        canvas.add(gameOverText);
    }

    public static void main(String[] args) {
        new SimpleBirdGame();
    }
}