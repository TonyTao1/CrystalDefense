
/**
 * Tracks score, coins, and crystal health
 */
public class ScoreManager {

    private int coins;
    private int score;
    private int crystalHealth;
    private int enemiesDefeated;

    public ScoreManager() {
        reset();
    }

    public void reset() {
        coins = 120;
        score = 0;
        crystalHealth = 20;
        enemiesDefeated = 0;
    }

    public boolean spendCoins(int amount) {
        if (coins < amount) {
            return false;
        }

        coins -= amount;
        return true;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public void addScore(int amount) {
        score += amount;
    }

    public void damageCrystal(int amount) {
        crystalHealth -= amount;
        if (crystalHealth < 0) {
            crystalHealth = 0;
        }
    }

    public void recordDefeat(Enemy enemy) {
        enemiesDefeated++;
        addCoins(enemy.getReward());
        score += enemy.getScoreValue();
    }

    public boolean isCrystalDestroyed() {
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
