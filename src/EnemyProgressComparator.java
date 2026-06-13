import java.util.Comparator;

/**
 * Sorts enemies by how far they have moved.
 */
public class EnemyProgressComparator implements Comparator<Enemy> {
    @Override
    public int compare(Enemy first, Enemy second) {
        if (first.getDistanceTravelled() > second.getDistanceTravelled()) {
            return -1;
        } else if (first.getDistanceTravelled() < second.getDistanceTravelled()) {
            return 1;
        }

        return 0;
    }
}
