import java.util.Comparator;

/**
 * Sorts enemies by how close they are to the crystal.
 */
public class EnemyProgressComparator implements Comparator<Enemy> {
    @Override
    public int compare(Enemy first, Enemy second) {
        // Larger distance means closer to the crystal.
        return Double.compare(second.getDistanceTravelled(), first.getDistanceTravelled());
    }
}
