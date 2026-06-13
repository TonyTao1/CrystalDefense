import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A bullet fired by a tower toward an enemy target
 */
public class Bullet {
    private double x;
    private double y;
    private Enemy target;
    private int damage;
    private double speed;
    private boolean active;
    private Color color;

    public Bullet(int startX, int startY, Enemy target, int damage, Color color) {
        x = startX;
        y = startY;
        this.target = target;
        this.damage = damage;
        speed = 9.0;
        active = true;
        this.color = color;
    }

    public void update() {
        if (!active || target == null || target.isDefeated() || target.hasReachedCrystal()) {
            active = false;
            return;
        }

        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= speed || distance < 8) {
            target.takeDamage(damage);
            active = false;
        } else {
            x += dx / distance * speed;
            y += dy / distance * speed;
        }
    }

    public void draw(Graphics2D g) {
        if (!active) {
            return;
        }

        g.setColor(color);
        g.fillOval((int) x - 4, (int) y - 4, 8, 8);
        g.setColor(new Color(45, 45, 45));
        g.drawOval((int) x - 4, (int) y - 4, 8, 8);
    }

    public boolean isActive() {
        return active;
    }
}
