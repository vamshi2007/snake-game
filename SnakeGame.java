import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int DOT_SIZE = 20;
    private LinkedList<Point> snake;
    private Point food;
    private char direction;
    private boolean gameRunning;
    private int score;

    public SnakeGame() {
        setTitle("Snake Game");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        snake = new LinkedList<>();
        direction = 'R'; // Starting direction: Right
        spawnFood();
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (direction != 'D') direction = 'U';
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') direction = 'D';
                        break;
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') direction = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') direction = 'R';
                        break;
                    case KeyEvent.VK_R: // Restart the game
                        if (!gameRunning) startGame();
                        break;
                    case KeyEvent.VK_Q: // Quit the game
                        System.exit(0);
                        break;
                }
            }
        });

        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameRunning) {
                    moveSnake();
                    checkCollision();
                    repaint();
                }
            }
        });
        timer.start();

        startGame();
    }

    private void startGame() {
        snake.clear();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2)); // Start position
        direction = 'R';
        gameRunning = true;
        score = 0; // Reset score
        spawnFood();
    }

    private void spawnFood() {
        Random random = new Random();
        int x = random.nextInt(WIDTH / DOT_SIZE) * DOT_SIZE;
        int y = random.nextInt(HEIGHT / DOT_SIZE) * DOT_SIZE;
        food = new Point(x, y);
    }

    private void moveSnake() {
        Point head = snake.getFirst();
        Point newHead = new Point(head);

        switch (direction) {
            case 'U': newHead.translate(0, -DOT_SIZE); break;
            case 'D': newHead.translate(0, DOT_SIZE); break;
            case 'L': newHead.translate(-DOT_SIZE, 0); break;
            case 'R': newHead.translate(DOT_SIZE, 0); break;
        }

        snake.addFirst(newHead);

        if (newHead.equals(food)) {
            spawnFood(); // Grow the snake
            score += 10; // Increase score
        } else {
            snake.removeLast(); // Move the snake
        }
    }

    private void checkCollision() {
        Point head = snake.getFirst();

        // Check wall collision
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            gameRunning = false;
        }

        // Check self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameRunning = false;
                break;
            }
        }

        if (!gameRunning) {
            saveScore();
        }
    }

    private void saveScore() {
        String name = JOptionPane.showInputDialog("Game Over! Enter your name:");
        if (name != null && !name.trim().isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt", true))) {
                writer.write(name + ": " + score);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw food
        g.setColor(Color.RED);
        g.fillRect(food.x, food.y, DOT_SIZE, DOT_SIZE);

        // Draw snake
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x, p.y, DOT_SIZE, DOT_SIZE);
        }

        // Game over
        if (!gameRunning) {
            g.setColor(Color.WHITE);
            g.drawString("Game Over! Press R to Restart or Q to Quit", WIDTH / 2 - 140, HEIGHT / 2);
            g.drawString("Your Score: " + score, WIDTH / 2 - 70, HEIGHT / 2 + 20);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnakeGame game = new SnakeGame();
            game.setVisible(true);
        });
    }
}
