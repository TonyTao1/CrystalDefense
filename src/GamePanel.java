
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
 * Main panel for Crystal Defense. Handles the screens, game loop, input,
 * updates, and drawing.
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, MouseListener, KeyListener {

    // Main screen and gameplay settings.
    private static final int SCREEN_WIDTH = 900;
    private static final int SCREEN_HEIGHT = 640;
    private static final int FPS = 60;
    private static final int TOP_BAR_HEIGHT = 82;
    private static final int PATH_WIDTH = 48;
    private static final int TOTAL_LEVELS = 3;
    private static final int WAVES_PER_LEVEL = 3;
    private static final int GRID_SIZE = 40;

    private enum Screen {
        TITLE, LEVEL_SELECT, INSTRUCTIONS, ABOUT, PLAYING, GAME_OVER, WIN
    }

    // Thread and screen state.
    private Thread thread;
    private boolean running;
    private Screen screen;

    // Main game objects and collections.
    private ArrayList<Point> path;
    private ArrayList<ArrayList<Point>> levelPaths;
    private ArrayList<Enemy> enemies;
    private ArrayList<Tower> towers;
    private ArrayList<Bullet> bullets;
    private HashMap<String, Integer> towerCosts;
    private ScoreManager scoreManager;
    private SoundManager soundManager;
    private BufferedImage menuBackgroundImage;
    private BufferedImage levelBackgroundImage;

    // Wave and level state.
    private String selectedTowerType;
    private int levelNumber;
    private int waveNumber;
    private int enemiesLeftToSpawn;
    private int spawnTimer;
    private int spawnDelay;
    private int wavePauseTimer;
    private boolean waveInProgress;
    private String statusMessage;

    // Button hitboxes.
    private Rectangle startButton;
    private Rectangle selectLevelButton;
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
        // Set up the panel before the game starts.
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
        soundManager = new SoundManager();
        menuBackgroundImage = loadImage("resources/menu_background.png");
        levelBackgroundImage = loadImage("resources/level_background.png");

        createButtons();
        initializeGame();
        screen = Screen.TITLE;
        soundManager.playMenuMusic();
    }

    private BufferedImage loadImage(String fileName) {
        // Return null if the image is missing, so the game can still run.
        try {
            return ImageIO.read(new File(fileName));
        } catch (IOException e) {
            return null;
        }
    }

    public void startGameThread() {
        // Only start one game thread.
        if (thread == null) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        // Simple game loop: update, draw, wait.
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
        // These rectangles are used for mouse click detection.
        startButton = new Rectangle(350, 235, 200, 45);
        selectLevelButton = new Rectangle(350, 295, 200, 45);
        instructionsButton = new Rectangle(350, 355, 200, 45);
        aboutButton = new Rectangle(350, 415, 200, 45);
        levelOneButton = new Rectangle(350, 230, 200, 45);
        levelTwoButton = new Rectangle(350, 295, 200, 45);
        levelThreeButton = new Rectangle(350, 360, 200, 45);
        backButton = new Rectangle(350, 500, 200, 45);
        retryButton = new Rectangle(350, 350, 200, 45);
        menuButton = new Rectangle(350, 410, 200, 45);
        basicButton = new Rectangle(190, 42, 105, 30);
        rapidButton = new Rectangle(305, 42, 105, 30);
        heavyButton = new Rectangle(420, 42, 105, 30);
    }

    private void initializeGame() {
        // Store tower prices in a map for easy display.
        towerCosts.put("Basic", Tower.createTower("Basic", 0, 0).getCost());
        towerCosts.put("Rapid", Tower.createTower("Rapid", 0, 0).getCost());
        towerCosts.put("Heavy", Tower.createTower("Heavy", 0, 0).getCost());

        resetGame();
    }

    private void setupLevel(int level) {
        // Each level can have one or more possible enemy paths.
        path.clear();
        levelPaths.clear();

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
        // Convert pairs of numbers into Point objects.
        ArrayList<Point> newPath = new ArrayList<Point>();

        for (int i = 0; i < coordinates.length; i += 2) {
            newPath.add(new Point(coordinates[i], coordinates[i + 1]));
        }

        levelPaths.add(newPath);
    }

    private void resetGame() {
        // Default reset starts at level 1.
        resetGame(1);
    }

    private void resetGame(int startLevel) {
        // Clear the board and prepare a fresh run.
        soundManager.stopBattleMusic();
        enemies.clear();
        towers.clear();
        bullets.clear();
        scoreManager.reset();
        levelNumber = startLevel;
        setupLevel(levelNumber);
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
        // Start from the beginning.
        startLevel(1);
    }

    private void startLevel(int level) {
        // Used by the level select menu.
        resetGame(level);
        soundManager.stopMenuMusic();
        screen = Screen.PLAYING;
        startNextWave();
        requestFocusInWindow();
    }

    private void update() {
        // Menu screens do not need gameplay updates.
        if (screen != Screen.PLAYING) {
            return;
        }

        updateWaveSpawning();
        updateEnemies();
        updateTowers();
        updateBullets();
        checkWaveFinished();
    }

    private void updateWaveSpawning() {
        // Spawn enemies one at a time instead of all at once.
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
        // Loop backwards because enemies may be removed.
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
                    screen = Screen.GAME_OVER;
                }
            }
        }

        // Keep the most advanced enemies first.
        Collections.sort(enemies);
    }

    private void updateTowers() {
        // Towers decide if they can shoot this frame.
        for (int i = 0; i < towers.size(); i++) {
            towers.get(i).update(enemies, bullets);
        }
    }

    private void updateBullets() {
        // Remove bullets after they hit or lose their target.
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            if (!bullet.isActive()) {
                bullets.remove(i);
            }
        }
    }

    private void checkWaveFinished() {
        // Wait until all spawned enemies are gone.
        if (screen != Screen.PLAYING || enemiesLeftToSpawn > 0 || !enemies.isEmpty()) {
            return;
        }

        waveInProgress = false;

        if (waveNumber >= WAVES_PER_LEVEL) {
            // Move to the next level, or win after level 3.
            soundManager.stopBattleMusic();
            if (levelNumber >= TOTAL_LEVELS) {
                screen = Screen.WIN;
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

    private void startNextWave() {
        // Later levels and waves spawn more enemies faster.
        soundManager.playBattleMusic();
        waveNumber++;
        enemiesLeftToSpawn = 5 + waveNumber * 2 + (levelNumber - 1) * 3;
        spawnDelay = Math.max(28, 82 - waveNumber * 8 - levelNumber * 6);
        spawnTimer = 20;
        wavePauseTimer = 120;
        waveInProgress = true;
        statusMessage = "Level " + levelNumber + " Wave " + waveNumber + " started.";
    }

    private void spawnEnemy() {
        // Pick enemy type based on level and remaining spawn count.
        String type = "Scout";

        if (levelNumber == 2 && enemiesLeftToSpawn % 6 == 0) {
            type = "Elite";
        } else if (levelNumber >= 3 && enemiesLeftToSpawn % 4 == 0) {
            type = "Elite";
        } else if (levelNumber >= 3 && enemiesLeftToSpawn % 5 == 0) {
            type = "Tank";
        } else if ((levelNumber >= 2 || waveNumber >= 2) && enemiesLeftToSpawn % 3 == 0) {
            type = "Runner";
        }

        // Level 2 and 3 use random paths.
        int pathIndex = (int) (Math.random() * levelPaths.size());
        enemies.add(new Enemy(type, levelPaths.get(pathIndex)));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the correct screen.
        if (screen == Screen.TITLE) {
            drawTitleScreen(g2);
        } else if (screen == Screen.LEVEL_SELECT) {
            drawLevelSelectScreen(g2);
        } else if (screen == Screen.INSTRUCTIONS) {
            drawInstructionsScreen(g2);
        } else if (screen == Screen.ABOUT) {
            drawAboutScreen(g2);
        } else if (screen == Screen.PLAYING) {
            drawPlayingScreen(g2);
        } else if (screen == Screen.GAME_OVER) {
            drawEndScreen(g2, "GAME OVER", "The crystal was destroyed.");
        } else if (screen == Screen.WIN) {
            drawEndScreen(g2, "YOU WIN", "The crystal survived all waves.");
        }
    }

    private void drawTitleScreen(Graphics2D g) {
        // The image already has the logo, so text is only drawn as backup.
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
        drawButton(g, instructionsButton, "Instructions", false);
        drawButton(g, aboutButton, "About", false);
    }

    private void drawLevelSelectScreen(Graphics2D g) {
        // Lets the player start from any level.
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
        // Basic rules and controls.
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

    private void drawAboutScreen(Graphics2D g) {
        // Project information for the required about page.
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
        // Draw order matters: map first, then objects, then UI.
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
        // Shared screen for win and game over.
        drawMenuBackground(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 44));
        drawCenteredString(g, title, 185);
        g.setFont(new Font("Arial", Font.PLAIN, 21));
        drawCenteredString(g, subtitle, 245);
        drawCenteredString(g, "Score: " + scoreManager.getScore(), 285);

        drawButton(g, retryButton, "Play Again", false);
        drawButton(g, menuButton, "Menu", false);
    }

    private void drawMenuBackground(Graphics2D g) {
        // Use the image background if it is available.
        if (menuBackgroundImage != null) {
            drawCoverImage(g, menuBackgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            g.setColor(new Color(0, 0, 0, 80));
            g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            return;
        }

        // Backup background if the image file is missing.
        g.setColor(new Color(225, 236, 228));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(new Color(183, 214, 204));
        g.fillOval(75, 70, 220, 220);
        g.setColor(new Color(209, 228, 221));
        g.fillOval(620, 360, 250, 250);
        g.setColor(new Color(112, 160, 154));
        g.fillPolygon(new int[]{450, 490, 450, 410}, new int[]{420, 470, 520, 470}, 4);
    }

    private void drawCoverImage(Graphics2D g, BufferedImage image, int x, int y, int width, int height) {
        // Scale the image like a full-screen background.
        double imageRatio = (double) image.getWidth() / image.getHeight();
        double targetRatio = (double) width / height;
        int drawWidth = width;
        int drawHeight = height;

        if (imageRatio > targetRatio) {
            drawHeight = height;
            drawWidth = (int) Math.round(height * imageRatio);
        } else {
            drawWidth = width;
            drawHeight = (int) Math.round(width / imageRatio);
        }

        int drawX = x + (width - drawWidth) / 2;
        int drawY = y + (height - drawHeight) / 2;
        g.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
    }

    private void drawMap(Graphics2D g) {
        // Draw the level background and grid.
        if (levelBackgroundImage != null) {
            drawCoverImage(g, levelBackgroundImage, 0, TOP_BAR_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT - TOP_BAR_HEIGHT);
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
        // Draw every path for the current level.
        g.setStroke(new BasicStroke(PATH_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
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
        // Crystal is placed at the end of the main path.
        Point crystal = getCrystalPoint();
        int x = crystal.x;
        int y = crystal.y;

        g.setColor(new Color(0, 188, 212));
        g.fillPolygon(new int[]{x, x + 28, x, x - 28}, new int[]{y - 38, y, y + 38, y}, 4);
        g.setColor(Color.WHITE);
        g.drawLine(x - 8, y - 18, x + 10, y + 8);
        g.setColor(new Color(20, 86, 100));
        g.drawPolygon(new int[]{x, x + 28, x, x - 28}, new int[]{y - 38, y, y + 38, y}, 4);
    }

    private Point getCrystalPoint() {
        // The first path always ends at the crystal.
        if (!path.isEmpty()) {
            return path.get(path.size() - 1);
        }

        return new Point(870, 230);
    }

    private void drawTopBar(Graphics2D g) {
        // Top bar shows game stats and tower choices.
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
        g.drawString("Wave: " + waveNumber + "/" + WAVES_PER_LEVEL, 245, 28);

        drawButton(g, basicButton, "Basic $" + towerCosts.get("Basic"), selectedTowerType.equals("Basic"));
        drawButton(g, rapidButton, "Rapid $" + towerCosts.get("Rapid"), selectedTowerType.equals("Rapid"));
        drawButton(g, heavyButton, "Heavy $" + towerCosts.get("Heavy"), selectedTowerType.equals("Heavy"));

        g.setFont(new Font("Arial", Font.PLAIN, 15));
        g.setColor(new Color(55, 65, 70));
        g.drawString(statusMessage, 555, 32);
        g.drawString("Esc: menu", 555, 58);
    }

    private void drawButton(Graphics2D g, Rectangle button, String text, boolean selected) {
        // Same button style is reused across screens.
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
        // Helper for menu text.
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (SCREEN_WIDTH - textWidth) / 2, y);
    }

    private void placeTower(int mouseX, int mouseY) {
        // Snap tower placement to the grid.
        int towerX = mouseX / GRID_SIZE * GRID_SIZE + GRID_SIZE / 2;
        int towerY = mouseY / GRID_SIZE * GRID_SIZE + GRID_SIZE / 2;

        if (!isValidTowerLocation(towerX, towerY)) {
            statusMessage = "Place towers on empty grass, not on the path.";
            return;
        }

        Tower tower = Tower.createTower(selectedTowerType, towerX, towerY);
        if (!scoreManager.spendCoins(tower.getCost())) {
            statusMessage = "Not enough coins for " + selectedTowerType + ".";
            return;
        }

        towers.add(tower);
        statusMessage = selectedTowerType + " tower placed.";
    }

    private boolean isValidTowerLocation(int x, int y) {
        // Towers cannot be placed off screen or on the top bar.
        if (y < TOP_BAR_HEIGHT + 20 || x < 20 || x > SCREEN_WIDTH - 20 || y > SCREEN_HEIGHT - 20) {
            return false;
        }

        // Towers also cannot block any enemy path.
        for (int pathIndex = 0; pathIndex < levelPaths.size(); pathIndex++) {
            ArrayList<Point> currentPath = levelPaths.get(pathIndex);

            for (int i = 0; i < currentPath.size() - 1; i++) {
                Point a = currentPath.get(i);
                Point b = currentPath.get(i + 1);
                if (distanceToSegment(x, y, a.x, a.y, b.x, b.y) < PATH_WIDTH / 2 + 22) {
                    return false;
                }
            }
        }

        // Keep towers from sitting on top of each other.
        for (int i = 0; i < towers.size(); i++) {
            if (towers.get(i).overlaps(x, y)) {
                return false;
            }
        }

        return true;
    }

    private double distanceToSegment(int px, int py, int x1, int y1, int x2, int y2) {
        // Finds the shortest distance from a point to a path line.
        double lineDX = x2 - x1;
        double lineDY = y2 - y1;
        double lengthSquared = lineDX * lineDX + lineDY * lineDY;

        if (lengthSquared == 0) {
            double dx = px - x1;
            double dy = py - y1;
            return Math.sqrt(dx * dx + dy * dy);
        }

        double t = ((px - x1) * lineDX + (py - y1) * lineDY) / lengthSquared;
        t = Math.max(0, Math.min(1, t));
        double closestX = x1 + t * lineDX;
        double closestY = y1 + t * lineDY;
        double dx = px - closestX;
        double dy = py - closestY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Mouse input changes screens or places towers.
        int x = e.getX();
        int y = e.getY();

        if (screen == Screen.TITLE) {
            if (startButton.contains(x, y)) {
                startNewRun();
            } else if (selectLevelButton.contains(x, y)) {
                screen = Screen.LEVEL_SELECT;
            } else if (instructionsButton.contains(x, y)) {
                screen = Screen.INSTRUCTIONS;
            } else if (aboutButton.contains(x, y)) {
                screen = Screen.ABOUT;
            }
        } else if (screen == Screen.LEVEL_SELECT) {
            if (levelOneButton.contains(x, y)) {
                startLevel(1);
            } else if (levelTwoButton.contains(x, y)) {
                startLevel(2);
            } else if (levelThreeButton.contains(x, y)) {
                startLevel(3);
            } else if (backButton.contains(x, y)) {
                screen = Screen.TITLE;
            }
        } else if (screen == Screen.INSTRUCTIONS || screen == Screen.ABOUT) {
            if (backButton.contains(x, y)) {
                screen = Screen.TITLE;
            }
        } else if (screen == Screen.GAME_OVER || screen == Screen.WIN) {
            if (retryButton.contains(x, y)) {
                if (screen == Screen.GAME_OVER) {
                    startLevel(levelNumber);
                } else {
                    startNewRun();
                }
            } else if (menuButton.contains(x, y)) {
                resetGame();
                screen = Screen.TITLE;
                soundManager.playMenuMusic();
            }
        } else if (screen == Screen.PLAYING) {
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
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Keyboard shortcuts for retry and menu.
        int key = e.getKeyCode();

        if ((screen == Screen.GAME_OVER || screen == Screen.WIN) && key == KeyEvent.VK_R) {
            if (screen == Screen.GAME_OVER) {
                startLevel(levelNumber);
            } else {
                startNewRun();
            }
        } else if (screen == Screen.PLAYING && key == KeyEvent.VK_ESCAPE) {
            resetGame();
            screen = Screen.TITLE;
            soundManager.playMenuMusic();
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
