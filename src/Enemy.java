
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * Represents one enemy moving along the fixed path toward the crystal.
 */
public class Enemy implements Comparable<Enemy> {

    // Images are shared by all enemies.
    private static BufferedImage monsterImage = loadImage("resources/enemy_monster.png");
    private static BufferedImage eliteImage = loadImage("resources/enemy_elite.png");

    // Movement and stat values for one enemy.
    private double x;
    private double y;
    private int health;
    private int maxHealth;
    private double speed;
    private int reward;
    private int scoreValue;
    private int size;
    private String type;
    private Color color;
    private ArrayList<Point> path;
    private int targetWaypoint;
    private double distanceTravelled;
    private boolean reachedCrystal;

    public Enemy(String type, ArrayList<Point> path) {
        // Start at the first point of the chosen path.
        this.type = type;
        this.path = path;
        Point start = path.get(0);
        x = start.x;
        y = start.y;
        targetWaypoint = 1;
        distanceTravelled = 0;
        reachedCrystal = false;
        setStatsForType(type);
    }

    private void setStatsForType(String type) {
        // Each enemy type has different speed, health, and reward.
        if (type.equals("Elite")) {
            maxHealth = 230;
            speed = 0.75;
            reward = 30;
            scoreValue = 320;
            size = 50;
            color = new Color(94, 64, 110);
        } else if (type.equals("Runner")) {
            maxHealth = 45;
            speed = 2.2;
            reward = 10;
            scoreValue = 90;
            size = 22;
            color = new Color(229, 115, 115);
        } else if (type.equals("Tank")) {
            maxHealth = 120;
            speed = 1.0;
            reward = 18;
            scoreValue = 180;
            size = 30;
            color = new Color(126, 87, 194);
        } else {
            maxHealth = 70;
            speed = 1.5;
            reward = 12;
            scoreValue = 120;
            size = 26;
            color = new Color(66, 165, 245);
        }

        health = maxHealth;
    }

    private static BufferedImage loadImage(String fileName) {
        // Missing images are allowed; the enemy will use shapes instead.
        try {
            return ImageIO.read(new File(fileName));
        } catch (IOException e) {
            return null;
        }
    }

    public void update() {
        // Stop moving once the enemy is finished.
        if (reachedCrystal || isDefeated()) {
            return;
        }

        if (targetWaypoint >= path.size()) {
            reachedCrystal = true;
            return;
        }

        Point target = path.get(targetWaypoint);
        double dx = target.x - x;
        double dy = target.y - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Move to the next waypoint when close enough.
        if (distance <= speed) {
            x = target.x;
            y = target.y;
            distanceTravelled += distance;
            targetWaypoint++;

            if (targetWaypoint >= path.size()) {
                reachedCrystal = true;
            }
        } else {
            x += dx / distance * speed;
            y += dy / distance * speed;
            distanceTravelled += speed;
        }
    }

    public void draw(Graphics2D g) {
        // Elite enemies are drawn bigger than normal enemies.
        BufferedImage image = getImage();
        int imageHeight = size + 28;
        if (type.equals("Elite")) {
            imageHeight = 88;
        }

        int imageWidth = imageHeight;

        if (image != null) {
            imageWidth = imageHeight * image.getWidth() / image.getHeight();
        }

        int drawX = (int) x - imageWidth / 2;
        int drawY = (int) y - imageHeight / 2;
        if (type.equals("Elite")) {
            drawY -= 14;
        }

        // Draw the sprite if possible, otherwise draw a simple fallback.
        if (image != null) {
            g.drawImage(image, drawX, drawY, imageWidth, imageHeight, null);
        } else {
            int fallbackX = (int) x - size / 2;
            int fallbackY = (int) y - size / 2;
            g.setColor(color);
            g.fillOval(fallbackX, fallbackY, size, size);
            g.setColor(new Color(40, 45, 50));
            g.drawOval(fallbackX, fallbackY, size, size);
        }

        // Health bar.
        int barWidth = size + 8;
        if (type.equals("Elite")) {
            barWidth = imageWidth;
        }

        int barHeight = 5;
        int barX = (int) x - barWidth / 2;
        int barY = drawY - 10;
        double healthRatio = (double) health / maxHealth;

        g.setColor(new Color(95, 95, 95));
        g.fillRect(barX, barY, barWidth, barHeight);
        g.setColor(new Color(76, 175, 80));
        g.fillRect(barX, barY, (int) (barWidth * healthRatio), barHeight);
        g.setColor(new Color(35, 35, 35));
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    private BufferedImage getImage() {
        // Use the elite sprite only for elite enemies.
        if (type.equals("Elite") && eliteImage != null) {
            return eliteImage;
        }

        return monsterImage;
    }

    public void takeDamage(int damage) {
        // Called by bullets when they hit.
        health -= damage;
    }

    public boolean isDefeated() {
        // Enemy is removed when health reaches zero.
        return health <= 0;
    }

    public boolean hasReachedCrystal() {
        // Used to damage the crystal.
        return reachedCrystal;
    }

    public double distanceTo(int otherX, int otherY) {
        // Used by towers to check range.
        double dx = x - otherX;
        double dy = y - otherY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public int compareTo(Enemy other) {
        // Farther enemies come first.
        if (distanceTravelled > other.distanceTravelled) {
            return -1;
        } else if (distanceTravelled < other.distanceTravelled) {
            return 1;
        }

        return 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getReward() {
        return reward;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public String getType() {
        return type;
    }

    public double getDistanceTravelled() {
        return distanceTravelled;
    }
}
