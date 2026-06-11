import java.util.HashMap;

/**
 * Tracks score, coins, crystal health, and defeated enemy statistics.
 */
public class ScoreManager {
    // Main player stats.
    private int coins;
    private int score;
    private int crystalHealth;
    private int enemiesDefeated;
    private HashMap<String, Integer> defeatedByType;

    public ScoreManager() {
        // Map stores how many of each enemy type was defeated.
        defeatedByType = new HashMap<String, Integer>();
        reset();
    }

    public void reset() {
        // Starting values for a new run.
        coins = 120;
        score = 0;
        crystalHealth = 20;
        enemiesDefeated = 0;
        defeatedByType.clear();
    }

    public boolean spendCoins(int amount) {
        // Return false if the player cannot afford the tower.
        if (coins < amount) {
            return false;
        }

        coins -= amount;
        return true;
    }

    public void addCoins(int amount) {
        // Used for rewards and level bonuses.
        coins += amount;
    }

    public void damageCrystal(int amount) {
        // Crystal health should not go below zero.
        crystalHealth -= amount;
        if (crystalHealth < 0) {
            crystalHealth = 0;
        }
    }

    public void recordDefeat(Enemy enemy) {
        // Add rewards when an enemy is defeated.
        enemiesDefeated++;
        addCoins(enemy.getReward());
        score += enemy.getScoreValue();

        // Update the enemy type count in the map.
        String enemyType = enemy.getType();
        Integer oldCount = defeatedByType.get(enemyType);
        if (oldCount == null) {
            defeatedByType.put(enemyType, 1);
        } else {
            defeatedByType.put(enemyType, oldCount + 1);
        }
    }

    public boolean isCrystalDestroyed() {
        // Losing condition.
        return crystalHealth <= 0;
    }

    public int getCoins() {
        return coins;
    }

    public int getScore() {
        return score;
    }

    public int getCrystalHealth() {
        return crystalHealth;
    }

    public int getEnemiesDefeated() {
        return enemiesDefeated;
    }

    public HashMap<String, Integer> getDefeatedByType() {
        return defeatedByType;
    }
}
