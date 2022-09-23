import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import java.util.Random;

public class Panel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    
    // the higher the number the slower the game goes
    static final int DELAY = 75;

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    int snakeLength = 6;
    int targetsTaken;
    int targetX;
    int targetY;

    char direction = 'R';
    boolean running = false;

    Timer timer;
    Random random;

    Panel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new SnakeKeyAdapter());

        startGame();
    }

    public void startGame() {
        newTarget();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            for (int i = 0; i < (SCREEN_HEIGHT/UNIT_SIZE); i++) {
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }

            g.setColor(Color.red);
            g.fillRect(targetX, targetY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < snakeLength; i++) {
                g.setColor(Color.green);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }    

            g.setColor(Color.white);
            g.setFont(new Font("Monospaced", Font.BOLD, UNIT_SIZE));
            g.drawString("Score: " + targetsTaken, SCREEN_WIDTH - 145, g.getFont().getSize() - 4);
        }

        else {
            gameOver(g);
        }
    }

    public void newTarget() {
        targetX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        targetY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }

    public void move() {
        for (int i = snakeLength; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;

            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;

            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;

            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkTarget() {
        if ((x[0] == targetX) && (y[0] == targetY)) {
            snakeLength++;
            targetsTaken++;
            newTarget();
        }
    }

    public void checkCollisions() {
        // check if head collides with body
        for (int i = snakeLength; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        //check if head touches border
        if ((x[0] < 0) || (x[0] > SCREEN_WIDTH) || (y[0] < 0) || (y[0] > SCREEN_HEIGHT)) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Monospaced", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH-metrics1.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);

        g.setColor(Color.white);
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + targetsTaken, (SCREEN_WIDTH-metrics2.stringWidth("Score: " + targetsTaken))/2, (SCREEN_HEIGHT/2) + g.getFont().getSize());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkTarget();
            checkCollisions();
        }

        repaint();
    }

    public class SnakeKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R')
                        direction = 'L';
                    break;

                case KeyEvent.VK_RIGHT:
                    if (direction != 'L')
                        direction = 'R';
                    break;

                case KeyEvent.VK_UP:
                    if (direction != 'D')
                        direction = 'U';
                    break;

                case KeyEvent.VK_DOWN:
                    if (direction != 'U')
                        direction = 'D';
                    break;
            }
        }
    }
    
}
