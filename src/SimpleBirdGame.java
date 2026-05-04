import edu.macalester.graphics.*;
import edu.macalester.graphics.Point;
import edu.macalester.graphics.Rectangle;

import java.awt.*;
import java.awt.Image;
import java.util.*;

public class SimpleBirdGame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private static final int PLAYER_SIZE = 30;
    private static final int OBSTACLE_SIZE = 40;

    private CanvasWindow canvas;
    private edu.macalester.graphics.Image player;
    private ArrayList<Obstacle> obstacles;

    private boolean isGameOver = false;
    private boolean isGameRunning = false;
    private int frameCount = 0;
    private int score = 0;

    private GraphicsText scoreText;
    private GraphicsText gameOverText;

    private GraphicsText startMenuText;
    private Rectangle startButton;
    private GraphicsText startButtonText;

    private boolean loopStarted = false;

    public SimpleBirdGame() {
        canvas = new CanvasWindow("Bird Demo", WIDTH, HEIGHT);

        showStartMenu();
        setupMouseControl();
        startGameLoop();
    }

    private void showStartMenu() {
        canvas.removeAll();
        isGameRunning = false;
        
        startMenuText = new GraphicsText("Bird Game");
        startMenuText.setFontSize(48);
        startMenuText.setPosition(WIDTH / 2 - startMenuText.getBounds().getWidth() / 2, 120);
        canvas.add(startMenuText);

        startButton = new Rectangle(WIDTH / 2 - 80, 200, 160, 50);
        startButton.setFillColor(new Color(50, 150, 50));
        startButton.setStrokeColor(Color.BLACK);
        canvas.add(startButton);
        
        startButtonText = new GraphicsText("Start Game");
        startButtonText.setFontSize(24);
        startButtonText.setPosition(
            WIDTH / 2 - startButtonText.getBounds().getWidth() / 2,
            200 + startButton.getHeight() / 2 + startButtonText.getBounds().getHeight() / 2 - 5
        );
        startButtonText.setFillColor(Color.WHITE);
        canvas.add(startButtonText);

        GraphicsText tip = new GraphicsText("Click button to start");
        tip.setFontSize(14);
        tip.setPosition(WIDTH / 2 - tip.getBounds().getWidth() / 2, 290);
        tip.setFillColor(Color.GRAY);
        canvas.add(tip);
    }

    private void setupGame() {
        canvas.removeAll();

        player = new edu.macalester.graphics.Image("bird.png");
        player.setPosition(100, HEIGHT / 2);
        player.setMaxWidth(50);
        player.setMaxHeight(50);
        canvas.add(player);

        obstacles = new ArrayList<>();

        isGameOver = false;
        isGameRunning = true;
        frameCount = 0;
        score = 0;

        scoreText = new GraphicsText("Score: 0");
        scoreText.setFontSize(16);
        scoreText.setPosition(10, 20);
        canvas.add(scoreText);

        startGameLoop();
    }

    private void setupMouseControl() {
        canvas.onMouseMove(event -> {
            if (isGameRunning && !isGameOver && player != null) {
                double newY = event.getPosition().getY() - PLAYER_SIZE / 2;
                newY = Math.max(0, Math.min(newY, HEIGHT - PLAYER_SIZE));
                player.setPosition(player.getX(), newY);
            }
        });

        canvas.onMouseDown(event -> {
            Point clickPos = event.getPosition();
            
            if (!isGameRunning && startButton != null) {
                if (clickPos.getX() >= startButton.getX() && 
                    clickPos.getX() <= startButton.getX() + startButton.getWidth() &&
                    clickPos.getY() >= startButton.getY() && 
                    clickPos.getY() <= startButton.getY() + startButton.getHeight()) {
                    
                    setupGame();
                }
                return;
            }
            
            if (isGameOver) {
                showStartMenu(); 
            }
        });
    }

    private void startGameLoop() {
        canvas.animate(() -> {
            if (isGameRunning && !isGameOver) {
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
    if (Math.random() < 0.02) {
        double wallWidth = 40;
        double gap = 120 + Math.random() * 60;

        double gapY = Math.random() * (HEIGHT - gap - 100) + 50;

        Obstacle top = new Obstacle(WIDTH + 200, 0, wallWidth, gapY, canvas);
        obstacles.add(top);

        double bottomY = gapY + gap;
        Obstacle bottom = new Obstacle(WIDTH + 200, bottomY, wallWidth, HEIGHT - bottomY, canvas);
        obstacles.add(bottom);
    }
}

    private void moveObstacles() {
        for (Obstacle o : obstacles) {
            o.move();
        }
    }


    private void checkCollision() {
    for (Obstacle o : obstacles) {
        if (!o.canCollide()) continue;
        if (!o.isNearPlayer()) continue;

        double px = player.getX();
        double py = player.getY();
        double pw = player.getWidth();
        double ph = player.getHeight();

        double ox = o.getShape().getX();
        double oy = o.getShape().getY();
        double ow = o.getShape().getWidth();
        double oh = o.getShape().getHeight();

        boolean xOverlap = px < ox + ow && px + pw > ox;
        boolean yOverlap = py < oy + oh && py + ph > oy;
        boolean hit = xOverlap && yOverlap;

        if (hit) {
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
        isGameRunning = false;

        gameOverText = new GraphicsText("Game Over! Click to Restart");
        gameOverText.setFontSize(30);
        gameOverText.setPosition(WIDTH / 2 - 200, HEIGHT / 2);
        canvas.add(gameOverText);
    }

    public static void main(String[] args) {
        new SimpleBirdGame();
    }
}