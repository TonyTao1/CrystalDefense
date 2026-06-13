
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Main panel for Crystal Defense
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, MouseListener, KeyListener {

    private static int SCREEN_WIDTH = 900;
    private static int SCREEN_HEIGHT = 640;
    private static int FPS = 60;
    private static int TOP_BAR_HEIGHT = 82;
    private static int PATH_WIDTH = 48;
    private static int TOTAL_LEVELS = 3;
    private static int WAVES_PER_LEVEL = 3;
    private static int GRID_SIZE = 40;

    private static int TITLE = 0;
    private static int LEVEL_SELECT = 1;
    private static int INSTRUCTIONS = 2;
    private static int ABOUT = 3;
    private static int SCORE_BOARD = 4;
    private static int PLAYING = 5;
    private static int GAME_OVER = 6;
    private static int WIN = 7;

    private Thread thread;
    private boolean running;
    private int screen;

    private ArrayList<Point> path;
    private ArrayList<ArrayList<Point>> levelPaths;
    private ArrayList<Enemy> enemies;
    private ArrayList<Tower> towers;
    private ArrayList<Bullet> bullets;
    private HashMap<String, Integer> towerCosts;
    private ScoreManager scoreManager;
    private ScoreBoard scoreBoard;
    private SoundManager soundManager;
    private BufferedImage menuBackgroundImage;
    private BufferedImage levelBackgroundImage;

    private String selectedTowerType;
    private int levelNumber;
    private int waveNumber;
    private int enemiesLeftToSpawn;
    private int spawnTimer;
    private int spawnDelay;
    private int wavePauseTimer;
    private boolean waveInProgress;
    private String statusMessage;

    private Rectangle startButton;
    private Rectangle selectLevelButton;
    private Rectangle scoreBoardButton;
    private Rectangle levelOneButton;
    private Rectangle levelTwoButton;
    private Rectangle levelThreeButton;
    private Rectangle instructionsButton;
    private Rectangle aboutButton;
    private Rectangle backButton;
    private Rectangle retryButton;
    private Rectangle menuButton;
    private Rectangle basicButton;
    private Rectangle rapidButton;
    private Rectangle heavyButton;

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(new Color(232, 238, 232));
        setFocusable(true);
        addMouseListener(this);
        addKeyListener(this);

        scoreManager = new ScoreManager();
        path = new ArrayList<Point>();
        levelPaths = new ArrayList<ArrayList<Point>>();
        enemies = new ArrayList<Enemy>();
        towers = new ArrayList<Tower>();
        bullets = new ArrayList<Bullet>();
        towerCosts = new HashMap<String, Integer>();
        scoreBoard = new ScoreBoard();
        soundManager = new SoundManager();
        menuBackgroundImage = loadImage("resources/menu_background.png");
        levelBackgroundImage = loadImage("resources/level_background.png");

        createButtons();
        initializeGame();
        screen = TITLE;
        soundManager.playMenuMusic();
    }

    private BufferedImage loadImage(String fileName) {
        try {
            return ImageIO.read(new File(fileName));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Starts the game loop if it is not already running
     */
    public void startGameThread() {
        // The window can ask twice, so keep the thread guard in one place
        if (thread == null) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        while (running) {
            update();
            repaint();

            try {
                Thread.sleep(1000 / FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createButtons() {
        startButton = new Rectangle(350, 220, 200, 45);
        selectLevelButton = new Rectangle(350, 275, 200, 45);
        scoreBoardButton = new Rectangle(350, 330, 200, 45);
        instructionsButton = new Rectangle(350, 385, 200, 45);
        aboutButton = new Rectangle(350, 440, 200, 45);
        levelOneButton = new Rectangle(350, 230, 200, 45);
        levelTwoButton = new Rectangle(350, 295, 200, 45);
        levelThreeButton = new Rectangle(350, 360, 200, 45);
        backButton = new Rectangle(350, 500, 200, 45);
        retryButton = new Rectangle(350, 350, 200, 45);
        menuButton = new Rectangle(350, 410, 200, 45);
        basicButton = new Rectangle(530, 42, 105, 30);
        rapidButton = new Rectangle(645, 42, 105, 30);
        heavyButton = new Rectangle(760, 42, 105, 30);
    }

    private void initializeGame() {
        towerCosts.put("Basic", 40);
        towerCosts.put("Rapid", 65);
        towerCosts.put("Heavy", 90);

        resetGame();
    }

    /**
     * Builds the enemy paths for the chosen level
     */
    private void setupLevel(int level) {
        path.clear();
        levelPaths.clear();

        // Level 2 and 3 get more than one path so the map changes a bit
        if (level == 1) {
            addLevelPath(new int[]{
                -20, 320,
                170, 320,
                170, 150,
                410, 150,
                410, 455,
                690, 455,
                690, 230,
                870, 230
            });
        } else if (level == 2) {
            addLevelPath(new int[]{
                -20, 320,
                150, 320,
                150, 150,
                380, 150,
                380, 260,
                650, 260,
                650, 180,
                760, 180,
                760, 240,
                870, 240
            });
            addLevelPath(new int[]{
                -20, 320,
                150, 320,
                150, 500,
                390, 500,
                390, 390,
                635, 390,
                635, 300,
                760, 300,
                760, 240,
                870, 240
            });
        } else {
            addLevelPath(new int[]{
                -20, 180,
                120, 180,
                120, 110,
                330, 110,
                330, 300,
                550, 300,
                550, 170,
                760, 170,
                760, 240,
                870, 240
            });
            addLevelPath(new int[]{
                -20, 420,
                220, 420,
                220, 520,
                430, 520,
                430, 400,
                690, 400,
                690, 500,
                820, 500,
                820, 360,
                870, 360,
                870, 240
            });
            addLevelPath(new int[]{
                455, 660,
                455, 585,
                300, 585,
                300, 500,
                630, 500,
                630, 100,
                870, 100,
                870, 240
            });
        }

        if (!levelPaths.isEmpty()) {
            path.addAll(levelPaths.get(0));
        }
    }

    private void addLevelPath(int[] coordinates) {
        ArrayList<Point> newPath = new ArrayList<Point>();

        for (int i = 0; i < coordinates.length; i += 2) {
            newPath.add(new Point(coordinates[i], coordinates[i + 1]));
        }

        levelPaths.add(newPath);
    }

    private void resetGame() {
        resetGame(1);
    }

    /**
     * Clears the board and sets up a fresh run from the requested level
     */
    private void resetGame(int startLevel) {
        soundManager.stopBattleMusic();
        enemies.clear();
        towers.clear();
        bullets.clear();
        scoreManager.reset();
        levelNumber = startLevel;
        setupLevel(levelNumber);
        // Later levels start with a small coin boost
        scoreManager.addCoins((startLevel - 1) * 80);
        selectedTowerType = "Basic";
        waveNumber = 0;
        enemiesLeftToSpawn = 0;
        spawnTimer = 0;
        spawnDelay = 70;
        wavePauseTimer = 90;
        waveInProgress = false;
        statusMessage = "Select a tower and place it beside the path.";
    }

    private void startNewRun() {
        startLevel(1);
    }

    private void startLevel(int level) {
        resetGame(level);
        soundManager.stopMenuMusic();
        screen = PLAYING;
        startNextWave();
        requestFocusInWindow();
    }

    private void update() {
        // Menu screens stay still, only the play screen keeps ticking
        if (screen != PLAYING) {
            return;
        }

        updateWaveSpawning();
        updateEnemies();
        updateTowers();
        updateBullets();
        checkWaveFinished();
    }

    private void updateWaveSpawning() {
        if (!waveInProgress || enemiesLeftToSpawn <= 0) {
            return;
        }

        spawnTimer--;
        if (spawnTimer <= 0) {
            spawnEnemy();
            enemiesLeftToSpawn--;
            spawnTimer = spawnDelay;
        }
    }

    private void updateEnemies() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update();

            if (enemy.isDefeated()) {
                soundManager.playDefeatSound();
                scoreManager.recordDefeat(enemy);
                enemies.remove(i);
            } else if (enemy.hasReachedCrystal()) {
                scoreManager.damageCrystal(1);
                enemies.remove(i);
                statusMessage = "An enemy reached the crystal.";

                if (scoreManager.isCrystalDestroyed()) {
                    soundManager.stopBattleMusic();
                    screen = GAME_OVER;
                }
            }
        }

        Collections.sort(enemies);
    }

    private void updateTowers() {
        for (int i = 0; i < towers.size(); i++) {
            towers.get(i).update(enemies, bullets);
        }
    }

    private void updateBullets() {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            if (!bullet.isActive()) {
                bullets.remove(i);
            }
        }
    }

    private void checkWaveFinished() {
        if (screen != PLAYING || enemiesLeftToSpawn > 0 || !enemies.isEmpty()) {
            return;
        }

        waveInProgress = false;

        if (waveNumber >= WAVES_PER_LEVEL) {
            // Last wave done, either move up or finish the run
            soundManager.stopBattleMusic();
            if (levelNumber >= TOTAL_LEVELS) {
                scoreBoard.addScore(scoreManager.getScore());
                screen = WIN;
                return;
            }

            levelNumber++;
            setupLevel(levelNumber);
            towers.clear();
            bullets.clear();
            waveNumber = 0;
            wavePauseTimer = 150;
            scoreManager.addCoins(80);
            statusMessage = "Level " + levelNumber + " begins. New paths and +80 coins.";
            return;
        }

        wavePauseTimer--;
        statusMessage = "Next wave begins soon.";
        if (wavePauseTimer <= 0) {
            startNextWave();
        }
    }

    /**
     * Starts the next wave and adjusts the pacing for the current level
     */
    private void startNextWave() {
        soundManager.playBattleMusic();
        waveNumber++;
        enemiesLeftToSpawn = getEnemyCount(levelNumber, waveNumber);
        spawnDelay = 82 - waveNumber * 8 - levelNumber * 6;
        if (spawnDelay < 28) {
            spawnDelay = 28;
        }
        spawnTimer = 20;
        wavePauseTimer = 120;
        waveInProgress = true;
        statusMessage = "Level " + levelNumber + " Wave " + waveNumber + " started.";
    }

    /**
     * Spawns one enemy using the current level rules
     */
    private void spawnEnemy() {
        String type = getEnemyType(levelNumber, waveNumber, enemiesLeftToSpawn);

        int pathIndex = (int) (Math.random() * levelPaths.size());
        enemies.add(new Enemy(type, levelPaths.get(pathIndex)));
    }

    private int getEnemyCount(int level, int wave) {
        return 5 + wave * 2 + (level - 1) * 3;
    }

    private String getEnemyType(int level, int wave, int countLeft) {
        String type = "Scout";

        if (level == 2 && countLeft % 6 == 0) {
            type = "Elite";
        } else if (level >= 3 && countLeft % 4 == 0) {
            type = "Elite";
        } else if (level >= 3 && countLeft % 5 == 0) {
            type = "Tank";
        } else if ((level >= 2 || wave >= 2) && countLeft % 3 == 0) {
            type = "Runner";
        }

        return type;
    }

    private int getScoreForType(String type) {
        if (type.equals("Elite")) {
            return 320;
        } else if (type.equals("Tank")) {
            return 180;
        } else if (type.equals("Runner")) {
            return 90;
        }

        return 120;
    }

    private int getRewardForType(String type) {
        if (type.equals("Elite")) {
            return 30;
        } else if (type.equals("Tank")) {
            return 18;
        } else if (type.equals("Runner")) {
            return 10;
        }

        return 12;
    }

    /**
     * Teacher shortcut that awards the rest of the level immediately
     */
    private void teacherClearLevel() {
        int scoreBonus = 0;
        int coinBonus = 0;

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            scoreBonus += enemy.getScoreValue();
            coinBonus += enemy.getReward();
        }

        for (int i = enemiesLeftToSpawn; i >= 1; i--) {
            String type = getEnemyType(levelNumber, waveNumber, i);
            scoreBonus += getScoreForType(type);
            coinBonus += getRewardForType(type);
        }

        for (int wave = waveNumber + 1; wave <= WAVES_PER_LEVEL; wave++) {
            int total = getEnemyCount(levelNumber, wave);
            for (int i = total; i >= 1; i--) {
                String type = getEnemyType(levelNumber, wave, i);
                scoreBonus += getScoreForType(type);
                coinBonus += getRewardForType(type);
            }
        }

        scoreManager.addScore(scoreBonus);
        scoreManager.addCoins(coinBonus);
        enemies.clear();
        bullets.clear();
        enemiesLeftToSpawn = 0;
        waveInProgress = false;
        soundManager.stopBattleMusic();

        if (levelNumber >= TOTAL_LEVELS) {
            scoreBoard.addScore(scoreManager.getScore());
            screen = WIN;
        } else {
            levelNumber++;
            setupLevel(levelNumber);
            towers.clear();
            waveNumber = 0;
            wavePauseTimer = 150;
            scoreManager.addCoins(80);
            statusMessage = "Teacher shortcut cleared the level.";
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (screen == TITLE) {
            drawTitleScreen(g2);
        } else if (screen == LEVEL_SELECT) {
            drawLevelSelectScreen(g2);
        } else if (screen == INSTRUCTIONS) {
            drawInstructionsScreen(g2);
        } else if (screen == ABOUT) {
            drawAboutScreen(g2);
        } else if (screen == SCORE_BOARD) {
            drawScoreBoardScreen(g2);
        } else if (screen == PLAYING) {
            drawPlayingScreen(g2);
        } else if (screen == GAME_OVER) {
            drawEndScreen(g2, "GAME OVER", "The crystal was destroyed.");
        } else if (screen == WIN) {
            drawEndScreen(g2, "YOU WIN", "The crystal survived all waves.");
        }
    }

    private void drawTitleScreen(Graphics2D g) {
        drawMenuBackground(g);

        if (menuBackgroundImage == null) {
            g.setColor(new Color(31, 45, 48));
            g.setFont(new Font("Arial", Font.BOLD, 46));
            drawCenteredString(g, "CRYSTAL DEFENSE", 150);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            drawCenteredString(g, "Protect the crystal with towers.", 205);
        }

        drawButton(g, startButton, "Start Game", false);
        drawButton(g, selectLevelButton, "Select Level", false);
        drawButton(g, scoreBoardButton, "Score Board", false);
        drawButton(g, instructionsButton, "Instructions", false);
        drawButton(g, aboutButton, "About", false);
    }

    private void drawLevelSelectScreen(Graphics2D g) {
        drawMenuBackground(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 38));
        drawCenteredString(g, "SELECT LEVEL", 110);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        drawCenteredString(g, "Choose a starting level. Later levels give extra starting coins.", 165);

        drawButton(g, levelOneButton, "Level 1", false);
        drawButton(g, levelTwoButton, "Level 2", false);
        drawButton(g, levelThreeButton, "Level 3", false);
        drawButton(g, backButton, "Back", false);
    }

    private void drawInstructionsScreen(Graphics2D g) {
        drawMenuBackground(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 38));
        drawCenteredString(g, "INSTRUCTIONS", 95);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        int y = 165;
        g.drawString("1. Protect the crystal at the end of the path.", 185, y);
        g.drawString("2. Click a tower type from the top bar.", 185, y + 45);
        g.drawString("3. Click an empty grass tile to place it.", 185, y + 90);
        g.drawString("4. Towers shoot automatically.", 185, y + 135);
        g.drawString("5. Level 2 adds random branch paths.", 185, y + 180);
        g.drawString("6. Level 3 adds a second enemy spawn point.", 185, y + 225);
        g.drawString("7. Survive all 3 levels to win.", 185, y + 270);

        drawButton(g, backButton, "Back", false);
    }

    private void drawScoreBoardScreen(Graphics2D g) {
        drawMenuBackground(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 38));
        drawCenteredString(g, "SCORE BOARD", 100);

        ArrayList<Integer> scores = scoreBoard.getScores();

        if (scores.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            drawCenteredString(g, "No winning scores yet.", 245);
        } else {
            g.setFont(new Font("Arial", Font.BOLD, 24));
            drawCenteredString(g, "Top 5 Scores", 165);

            g.setFont(new Font("Arial", Font.PLAIN, 24));
            for (int i = 0; i < scores.size(); i++) {
                String line = (i + 1) + ". " + scores.get(i);
                drawCenteredString(g, line, 220 + i * 45);
            }
        }

        drawButton(g, backButton, "Back", false);
    }

    private void drawAboutScreen(Graphics2D g) {
        drawMenuBackground(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 38));
        drawCenteredString(g, "ABOUT", 100);

        g.setFont(new Font("Arial", Font.PLAIN, 21));
        g.drawString("Game: Crystal Defense", 290, 180);
        g.drawString("Student: Taoboyu", 290, 225);
        g.drawString("Course: ICS4U", 290, 270);
        g.drawString("Date: June 2026", 290, 315);
        g.drawString("Built with Java JFrame and JPanel", 290, 360);

        drawButton(g, backButton, "Back", false);
    }

    private void drawPlayingScreen(Graphics2D g) {
        drawMap(g);
        drawPath(g);
        drawCrystal(g);

        for (int i = 0; i < towers.size(); i++) {
            towers.get(i).draw(g);
        }

        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g);
        }

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }

        drawTopBar(g);
    }

    private void drawEndScreen(Graphics2D g, String title, String subtitle) {
        drawMenuBackground(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 44));
        drawCenteredString(g, title, 185);
        g.setFont(new Font("Arial", Font.PLAIN, 21));
        drawCenteredString(g, subtitle, 245);
        drawCenteredString(g, "Score: " + scoreManager.getScore(), 285);
        if (screen == WIN) {
            if (scoreBoard.wasLastScoreKept()) {
                drawCenteredString(g, "Score saved in top 5.", 325);
            } else {
                drawCenteredString(g, "Score was not high enough for top 5.", 325);
            }
        }

        drawButton(g, retryButton, "Play Again", false);
        drawButton(g, menuButton, "Menu", false);
    }

    private void drawMenuBackground(Graphics2D g) {
        if (menuBackgroundImage != null) {
            g.drawImage(menuBackgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
            g.setColor(new Color(0, 0, 0, 80));
            g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            return;
        }

        g.setColor(new Color(225, 236, 228));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(new Color(183, 214, 204));
        g.fillOval(75, 70, 220, 220);
        g.setColor(new Color(209, 228, 221));
        g.fillOval(620, 360, 250, 250);
        g.setColor(new Color(112, 160, 154));
        g.fillPolygon(new int[]{450, 490, 450, 410}, new int[]{420, 470, 520, 470}, 4);
    }

    private void drawMap(Graphics2D g) {
        if (levelBackgroundImage != null) {
            g.drawImage(levelBackgroundImage, 0, TOP_BAR_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT - TOP_BAR_HEIGHT, null);
            g.setColor(new Color(0, 0, 0, 45));
            g.fillRect(0, TOP_BAR_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT - TOP_BAR_HEIGHT);
        } else {
            g.setColor(new Color(212, 230, 205));
            g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        }

        g.setColor(new Color(255, 255, 255, 35));
        for (int x = 0; x < SCREEN_WIDTH; x += GRID_SIZE) {
            g.drawLine(x, TOP_BAR_HEIGHT, x, SCREEN_HEIGHT);
        }

        for (int y = TOP_BAR_HEIGHT; y < SCREEN_HEIGHT; y += GRID_SIZE) {
            g.drawLine(0, y, SCREEN_WIDTH, y);
        }
    }

    private void drawPath(Graphics2D g) {
        // The thick stroke gives the path a painted look
        g.setStroke(new BasicStroke(PATH_WIDTH));
        g.setColor(new Color(176, 151, 112));

        for (int pathIndex = 0; pathIndex < levelPaths.size(); pathIndex++) {
            ArrayList<Point> currentPath = levelPaths.get(pathIndex);

            for (int i = 0; i < currentPath.size() - 1; i++) {
                Point current = currentPath.get(i);
                Point next = currentPath.get(i + 1);
                g.drawLine(current.x, current.y, next.x, next.y);
            }
        }

        g.setStroke(new BasicStroke(2));
        g.setColor(new Color(127, 104, 76));
        for (int pathIndex = 0; pathIndex < levelPaths.size(); pathIndex++) {
            ArrayList<Point> currentPath = levelPaths.get(pathIndex);

            for (int i = 0; i < currentPath.size() - 1; i++) {
                Point current = currentPath.get(i);
                Point next = currentPath.get(i + 1);
                g.drawLine(current.x, current.y, next.x, next.y);
            }
        }
    }

    private void drawCrystal(Graphics2D g) {
        Point crystal;
        if (!path.isEmpty()) {
            crystal = path.get(path.size() - 1);
        } else {
            crystal = new Point(870, 230);
        }

        int x = crystal.x;
        int y = crystal.y;

        g.setColor(new Color(0, 188, 212));
        g.fillPolygon(new int[]{x, x + 28, x, x - 28}, new int[]{y - 38, y, y + 38, y}, 4);
        g.setColor(Color.WHITE);
        g.drawLine(x - 8, y - 18, x + 10, y + 8);
        g.setColor(new Color(20, 86, 100));
        g.drawPolygon(new int[]{x, x + 28, x, x - 28}, new int[]{y - 38, y, y + 38, y}, 4);
    }

    private void drawTopBar(Graphics2D g) {
        g.setColor(new Color(245, 248, 247));
        g.fillRect(0, 0, SCREEN_WIDTH, TOP_BAR_HEIGHT);
        g.setColor(new Color(69, 92, 94));
        g.drawLine(0, TOP_BAR_HEIGHT, SCREEN_WIDTH, TOP_BAR_HEIGHT);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(35, 45, 50));
        g.drawString("Coins: " + scoreManager.getCoins(), 20, 28);
        g.drawString("Health: " + scoreManager.getCrystalHealth(), 20, 55);
        g.drawString("Score: " + scoreManager.getScore(), 140, 28);
        g.drawString("Level: " + levelNumber + "/" + TOTAL_LEVELS, 140, 55);
        g.drawString("Wave: " + waveNumber + "/" + WAVES_PER_LEVEL, 270, 28);

        drawButton(g, basicButton, "Basic $" + towerCosts.get("Basic"), selectedTowerType.equals("Basic"));
        drawButton(g, rapidButton, "Rapid $" + towerCosts.get("Rapid"), selectedTowerType.equals("Rapid"));
        drawButton(g, heavyButton, "Heavy $" + towerCosts.get("Heavy"), selectedTowerType.equals("Heavy"));

        g.setFont(new Font("Arial", Font.PLAIN, 15));
        g.setColor(new Color(55, 65, 70));
        g.drawString(statusMessage, 420, 28);
        g.drawString("Esc: menu", 420, 58);
    }

    private void drawButton(Graphics2D g, Rectangle button, String text, boolean selected) {
        // Reuse one button style so the menus still feel related
        if (selected) {
            g.setColor(new Color(94, 151, 145));
        } else {
            g.setColor(new Color(246, 248, 246));
        }

        g.fillRoundRect(button.x, button.y, button.width, button.height, 8, 8);
        g.setColor(new Color(49, 71, 74));
        g.drawRoundRect(button.x, button.y, button.width, button.height, 8, 8);

        g.setFont(new Font("Arial", Font.BOLD, 15));
        if (selected) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(new Color(31, 45, 48));
        }

        int textWidth = g.getFontMetrics().stringWidth(text);
        int textX = button.x + (button.width - textWidth) / 2;
        int textY = button.y + (button.height + g.getFontMetrics().getAscent()) / 2 - 4;
        g.drawString(text, textX, textY);
    }

    private void drawCenteredString(Graphics2D g, String text, int y) {
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (SCREEN_WIDTH - textWidth) / 2, y);
    }

    /**
     * Tries to place a tower at the clicked spot
     */
    private void placeTower(int mouseX, int mouseY) {
        int towerX = mouseX / GRID_SIZE * GRID_SIZE + GRID_SIZE / 2;
        int towerY = mouseY / GRID_SIZE * GRID_SIZE + GRID_SIZE / 2;

        if (!isValidTowerLocation(towerX, towerY)) {
            statusMessage = "Place towers on empty grass, not on the path.";
            return;
        }

        Tower tower = new Tower(selectedTowerType, towerX, towerY);
        if (!scoreManager.spendCoins(tower.getCost())) {
            statusMessage = "Not enough coins for " + selectedTowerType + ".";
            return;
        }

        towers.add(tower);
        statusMessage = selectedTowerType + " tower placed.";
    }

    private void returnToTitle() {
        resetGame();
        screen = TITLE;
        soundManager.playMenuMusic();
    }

    private void restartFromCurrentLevel() {
        startLevel(levelNumber);
    }

    private void startFromTitleButtons(int x, int y) {
        // Title buttons just route to the next screen
        if (startButton.contains(x, y)) {
            startNewRun();
        } else if (selectLevelButton.contains(x, y)) {
            screen = LEVEL_SELECT;
        } else if (scoreBoardButton.contains(x, y)) {
            screen = SCORE_BOARD;
        } else if (instructionsButton.contains(x, y)) {
            screen = INSTRUCTIONS;
        } else if (aboutButton.contains(x, y)) {
            screen = ABOUT;
        }
    }

    private void startFromLevelSelectButtons(int x, int y) {
        // Level select is the same idea, just with different starting points
        if (levelOneButton.contains(x, y)) {
            startLevel(1);
        } else if (levelTwoButton.contains(x, y)) {
            startLevel(2);
        } else if (levelThreeButton.contains(x, y)) {
            startLevel(3);
        } else if (backButton.contains(x, y)) {
            screen = TITLE;
        }
    }

    private void handleBackButtonOnly(int x, int y) {
        if (backButton.contains(x, y)) {
            screen = TITLE;
        }
    }

    private void handleEndScreenButtons(int x, int y) {
        // End screens only need retry or a way back to the menu
        if (retryButton.contains(x, y)) {
            if (screen == GAME_OVER) {
                restartFromCurrentLevel();
            } else {
                startNewRun();
            }
        } else if (menuButton.contains(x, y)) {
            returnToTitle();
        }
    }

    private void handlePlayingButtons(int x, int y) {
        // In play, a click either picks a tower or places one
        if (basicButton.contains(x, y)) {
            selectedTowerType = "Basic";
        } else if (rapidButton.contains(x, y)) {
            selectedTowerType = "Rapid";
        } else if (heavyButton.contains(x, y)) {
            selectedTowerType = "Heavy";
        } else {
            placeTower(x, y);
        }
    }

    /**
     * Checks whether a tower can be placed at the given grid position
     */
    private boolean isValidTowerLocation(int x, int y) {
        if (y < TOP_BAR_HEIGHT + 20 || x < 20 || x > SCREEN_WIDTH - 20 || y > SCREEN_HEIGHT - 20) {
            return false;
        }

        for (int pathIndex = 0; pathIndex < levelPaths.size(); pathIndex++) {
            ArrayList<Point> currentPath = levelPaths.get(pathIndex);

            for (int i = 0; i < currentPath.size() - 1; i++) {
                Point a = currentPath.get(i);
                Point b = currentPath.get(i + 1);
                if (isNearPathSegment(x, y, a, b)) {
                    return false;
                }
            }
        }

        for (int i = 0; i < towers.size(); i++) {
            if (towers.get(i).overlaps(x, y)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a point sits too close to a straight path segment
     */
    private boolean isNearPathSegment(int x, int y, Point a, Point b) {
        int buffer = PATH_WIDTH / 2 + 22;

        if (a.x == b.x) {
            int top = a.y;
            int bottom = b.y;
            if (top > bottom) {
                int temp = top;
                top = bottom;
                bottom = temp;
            }
            top = top - buffer;
            bottom = bottom + buffer;
            return Math.abs(x - a.x) < buffer && y >= top && y <= bottom;
        } else {
            int left = a.x;
            int right = b.x;
            if (left > right) {
                int temp = left;
                left = right;
                right = temp;
            }
            left = left - buffer;
            right = right + buffer;
            return Math.abs(y - a.y) < buffer && x >= left && x <= right;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Each screen handles clicks a little differently
        if (screen == TITLE) {
            startFromTitleButtons(x, y);
        } else if (screen == LEVEL_SELECT) {
            startFromLevelSelectButtons(x, y);
        } else if (screen == INSTRUCTIONS || screen == ABOUT || screen == SCORE_BOARD) {
            handleBackButtonOnly(x, y);
        } else if (screen == GAME_OVER || screen == WIN) {
            handleEndScreenButtons(x, y);
        } else if (screen == PLAYING) {
            handlePlayingButtons(x, y);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Keyboard shortcuts only matter during the end screens and play
        if ((screen == GAME_OVER || screen == WIN) && key == KeyEvent.VK_R) {
            if (screen == GAME_OVER) {
                restartFromCurrentLevel();
            } else {
                startNewRun();
            }
        } else if (screen == PLAYING && key == KeyEvent.VK_T) {
            teacherClearLevel();
        } else if (screen == PLAYING && key == KeyEvent.VK_ESCAPE) {
            returnToTitle();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
