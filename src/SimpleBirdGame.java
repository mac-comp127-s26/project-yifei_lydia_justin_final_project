// import edu.macalester.graphics.*;
// import java.awt.*;
// import java.util.*;

// public class SimpleBirdGame {
//     private static final int WIDTH = 600;
//     private static final int HEIGHT = 400;

//     private static final int PLAYER_SIZE = 30;
//     private static final int OBSTACLE_SIZE = 40;

//     private CanvasWindow canvas;
//     private Ellipse player;
//     private ArrayList<Ellipse> obstacles;
//     private boolean isGameOver = false;

//     public SimpleBirdGame() {
//         canvas = new CanvasWindow("Bird Demo", WIDTH, HEIGHT);

//         player = new Ellipse(100, HEIGHT / 2, PLAYER_SIZE, PLAYER_SIZE);
//         player.setFillColor(Color.BLUE);
//         canvas.add(player);

//         obstacles = new ArrayList<>();

//         setupMouseControl();
//         runGameLoop();
//     }

//     private void setupMouseControl() {
//         canvas.onMouseMove(event -> {
//             double newY = event.getPosition().getY() - PLAYER_SIZE / 2;
//             player.setPosition(player.getX(), newY);
//         });
//     }

//     private void runGameLoop() {
//         canvas.animate(() -> {
//         if (!isGameOver) {
//             moveObstacles();
//             maybeAddObstacle();
//             checkCollision();
//             }
//         });
//     }

//     private void moveObstacles() {
//         for (Ellipse e : obstacles) {
//             e.moveBy(-3, 0);
//         }
//     }

//     private void maybeAddObstacle() {
//         if (Math.random() < 0.02) {
//             double y = Math.random() * (HEIGHT - OBSTACLE_SIZE);

//             Ellipse obstacle = new Ellipse(WIDTH + 100, y, OBSTACLE_SIZE, OBSTACLE_SIZE);
//             obstacle.setFillColor(Color.RED);

//             obstacles.add(obstacle);
//             canvas.add(obstacle);
//         }
//     }

//     private void checkCollision() {
//     for (Ellipse e : obstacles) {
//         if (player.getBounds().intersects(e.getBounds())) {
//             gameOver();
//         }
//     }
//     }

//     private void gameOver() {
//     isGameOver = true;

//     GraphicsText text = new GraphicsText("Game Over");
//     text.setFontSize(40);
//     text.setPosition(200, 200);
//     canvas.add(text);
//     }

//     public static void main(String[] args) {
//         new SimpleBirdGame();
//     }
// }

import edu.macalester.graphics.*;
import edu.macalester.graphics.Point;
import edu.macalester.graphics.Rectangle;

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

        player = new Ellipse(100, HEIGHT / 2, PLAYER_SIZE, PLAYER_SIZE);
        player.setFillColor(Color.BLUE);
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
            if (o.canCollide()) {
                double dx = player.getCenter().getX() - o.getShape().getCenter().getX();
                double dy = player.getCenter().getY() - o.getShape().getCenter().getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                double radiusSum = PLAYER_SIZE / 2.0 + OBSTACLE_SIZE / 2.0;

                if (distance < radiusSum) {
                    gameOver();
                }
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