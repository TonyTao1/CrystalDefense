import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A bullet fired by a tower toward an enemy target.
 */
public class Bullet {
    // Current position and target.
    private double x;
    private double y;
    private Enemy target;
    private int damage;
    private double speed;
    private boolean active;
    private Color color;

    public Bullet(int startX, int startY, Enemy target, int damage, Color color) {
        // Bullets start from the tower position.
        x = startX;
        y = startY;
        this.target = target;
        this.damage = damage;
        speed = 9.0;
        active = true;
        this.color = color;
    }

    public void update() {
        // Remove the bullet if the target is no longer valid.
        if (!active || target == null || target.isDefeated() || target.hasReachedCrystal()) {
            active = false;
            return;
        }

        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Hit when the bullet gets close enough.
        if (distance <= speed || distance < 8) {
            target.takeDamage(damage);
            active = false;
        } else {
            x += dx / distance * speed;
            y += dy / distance * speed;
        }
    }

    public void draw(Graphics2D g) {
        // Inactive bullets are skipped.
        if (!active) {
            return;
        }

        // Draw a small circle as the bullet.
        g.setColor(color);
        g.fillOval((int) x - 4, (int) y - 4, 8, 8);
        g.setColor(new Color(45, 45, 45));
        g.drawOval((int) x - 4, (int) y - 4, 8, 8);
    }

    public boolean isActive() {
        // GamePanel uses this to remove old bullets.
        return active;
    }
}
