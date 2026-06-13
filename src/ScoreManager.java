/**
 * Tracks score, coins, and crystal health.
 */
public class ScoreManager {
    // Main player stats.
    private int coins;
    private int score;
    private int crystalHealth;
    private int enemiesDefeated;

    public ScoreManager() {
        reset();
    }

    public void reset() {
        // Starting values for a new run.
        coins = 120;
        score = 0;
        crystalHealth = 20;
        enemiesDefeated = 0;
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
}
