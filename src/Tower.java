import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a tower placed by the player.
 */
public class Tower {
    // Position and attack stats.
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

    private Tower(String type, int x, int y, int range, int damage, int fireDelay, int cost,
            Color baseColor, Color bulletColor) {
        // Private constructor keeps tower stats in createTower.
        this.type = type;
        this.x = x;
        this.y = y;
        this.range = range;
        this.damage = damage;
        this.fireDelay = fireDelay;
        this.cost = cost;
        this.baseColor = baseColor;
        this.bulletColor = bulletColor;
        cooldown = 0;
    }

    public static Tower createTower(String type, int x, int y) {
        // Builds a tower with stats based on its type.
        if (type.equals("Rapid")) {
            return new Tower("Rapid", x, y, 115, 9, 24, 65,
                    new Color(255, 193, 7), new Color(255, 152, 0));
        } else if (type.equals("Heavy")) {
            return new Tower("Heavy", x, y, 155, 30, 65, 90,
                    new Color(38, 166, 154), new Color(0, 121, 107));
        }

        return new Tower("Basic", x, y, 135, 18, 42, 40,
                new Color(77, 182, 172), new Color(0, 150, 136));
    }

    public void update(ArrayList<Enemy> enemies, ArrayList<Bullet> bullets) {
        // Cooldown controls how fast the tower shoots.
        if (cooldown > 0) {
            cooldown--;
        }

        // Shoot if an enemy is in range and the tower is ready.
        Enemy target = findTarget(enemies);
        if (target != null && cooldown <= 0) {
            bullets.add(new Bullet(x, y, target, damage, bulletColor));
            cooldown = fireDelay;
        }
    }

    private Enemy findTarget(ArrayList<Enemy> enemies) {
        // Collect enemies that are close enough to attack.
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

        // Attack the enemy closest to the crystal.
        Collections.sort(targetsInRange, new EnemyProgressComparator());
        return targetsInRange.get(0);
    }

    public void draw(Graphics2D g) {
        // Transparent circle shows attack range.
        g.setColor(new Color(0, 0, 0, 25));
        g.fillOval(x - range, y - range, range * 2, range * 2);

        // Simple tower body for now.
        g.setColor(baseColor);
        g.fillRoundRect(x - 16, y - 16, 32, 32, 8, 8);
        g.setColor(new Color(35, 45, 50));
        g.drawRoundRect(x - 16, y - 16, 32, 32, 8, 8);

        g.setColor(Color.WHITE);
        String label = type.substring(0, 1);
        g.drawString(label, x - 4, y + 5);
    }

    public boolean overlaps(int otherX, int otherY) {
        // Prevents towers from being placed too close.
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
