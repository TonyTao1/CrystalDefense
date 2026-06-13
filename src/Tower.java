
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;

/**
 * Represents a tower placed by the player
 */
public class Tower {

    private static BufferedImage basicImage = loadImage("resources/tower_basic.png");
    private static BufferedImage rapidImage = loadImage("resources/tower_rapid.png");
    private static BufferedImage heavyImage = loadImage("resources/tower_heavy.png");

    private int x;
    private int y;
    private String type;
    private int range;
    private int damage;
    private int fireDelay;
    private int cooldown;
    private int cost;
    private Color baseColor;
    private Color bulletColor;

    public Tower(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        cooldown = 0;

        if (type.equals("Rapid")) {
            range = 115;
            damage = 9;
            fireDelay = 24;
            cost = 65;
            baseColor = new Color(255, 193, 7);
            bulletColor = new Color(255, 152, 0);
        } else if (type.equals("Heavy")) {
            range = 155;
            damage = 30;
            fireDelay = 65;
            cost = 90;
            baseColor = new Color(38, 166, 154);
            bulletColor = new Color(0, 121, 107);
        } else {
            range = 135;
            damage = 18;
            fireDelay = 42;
            cost = 40;
            baseColor = new Color(77, 182, 172);
            bulletColor = new Color(0, 150, 136);
        }
    }

    private static BufferedImage loadImage(String fileName) {
        try {
            return ImageIO.read(new File(fileName));
        } catch (IOException e) {
            return null;
        }
    }

    public void update(ArrayList<Enemy> enemies, ArrayList<Bullet> bullets) {
        if (cooldown > 0) {
            cooldown--;
        }

        Enemy target = findTarget(enemies);
        if (target != null && cooldown <= 0) {
            bullets.add(new Bullet(x, y, target, damage, bulletColor));
            cooldown = fireDelay;
        }
    }

    private Enemy findTarget(ArrayList<Enemy> enemies) {
        ArrayList<Enemy> targetsInRange = new ArrayList<Enemy>();

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (!enemy.isDefeated() && !enemy.hasReachedCrystal() && enemy.distanceTo(x, y) <= range) {
                targetsInRange.add(enemy);
            }
        }

        if (targetsInRange.isEmpty()) {
            return null;
        }

        Collections.sort(targetsInRange, new EnemyProgressComparator());
        return targetsInRange.get(0);
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 25));
        g.fillOval(x - range, y - range, range * 2, range * 2);

        BufferedImage image = basicImage;
        if (type.equals("Rapid")) {
            image = rapidImage;
        } else if (type.equals("Heavy")) {
            image = heavyImage;
        }

        if (image != null) {
            int imageHeight = 48;
            int imageWidth = imageHeight * image.getWidth() / image.getHeight();
            g.drawImage(image, x - imageWidth / 2, y - imageHeight / 2, imageWidth, imageHeight, null);
        } else {
            g.setColor(baseColor);
            g.fillRoundRect(x - 16, y - 16, 32, 32, 8, 8);
            g.setColor(new Color(35, 45, 50));
            g.drawRoundRect(x - 16, y - 16, 32, 32, 8, 8);

            g.setColor(Color.WHITE);
            String label = type.substring(0, 1);
            g.drawString(label, x - 4, y + 5);
        }
    }

    public boolean overlaps(int otherX, int otherY) {
        double dx = x - otherX;
        double dy = y - otherY;
        return Math.sqrt(dx * dx + dy * dy) < 36;
    }

    public int getCost() {
        return cost;
    }

    public String getType() {
        return type;
    }
}
