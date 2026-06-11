# Crystal Defense - Initial Design

Student: Taoboyu
Course: ICS4U
Date: June 3, 2026
Project Type: Option 1 - Java Game

## 1. Program Overview

Crystal Defense is a small tower defense game made with Java JFrame and JPanel.
The player protects a crystal at the end of a fixed path. Enemies enter from the
left side of the map, follow the path, and try to reach the crystal. The player
uses coins to place towers beside the path. Towers automatically shoot enemies.
The player earns coins and score by defeating enemies.

The player wins by surviving all waves. The player loses if the crystal health
reaches 0.

The game is inspired by tower defense games such as Arknights, Carrot Fantasy,
and Plants vs Zombies, but this version uses my own simplified map, tower types,
enemy types, and rules.

## 2. Main Screens

### Title Screen

```
+------------------------------------------------------------+
|                                                            |
|                     CRYSTAL DEFENSE                       |
|                                                            |
|                  [ Start Game ]                            |
|                  [ Instructions ]                          |
|                  [ About ]                                 |
|                                                            |
|        Small non-violent tower defense Java game           |
|                                                            |
+------------------------------------------------------------+
```

Input:
- Mouse click on menu buttons.

Output:
- Title, buttons, and game name.

### Instructions Screen

```
+------------------------------------------------------------+
|                      INSTRUCTIONS                          |
|                                                            |
|  Protect the crystal at the end of the path.               |
|  Click a tower type from the top bar.                      |
|  Click an empty grass tile to place the tower.             |
|  Towers shoot automatically.                               |
|  Defeat enemies to earn coins.                             |
|  Survive all waves to win.                                 |
|                                                            |
|                         [ Back ]                           |
+------------------------------------------------------------+
```

Input:
- Mouse click on Back.

Output:
- Game rules and basic controls.

### About Screen

```
+------------------------------------------------------------+
|                         ABOUT                              |
|                                                            |
|  Game: Crystal Defense                                     |
|  Student: Taoboyu                                          |
|  Course: ICS4U                                             |
|  Date: June 2026                                           |
|  Built with Java JFrame/JPanel                             |
|                                                            |
|                         [ Back ]                           |
+------------------------------------------------------------+
```

Input:
- Mouse click on Back.

Output:
- Student information and project information.

### Main Game Screen

```
+------------------------------------------------------------+
| Coins: 100   Health: 20   Score: 0   Wave: 1/4             |
| [Basic $40] [Rapid $65] [Heavy $90]        Esc: menu       |
+------------------------------------------------------------+
|                                                            |
|  Start ---> ======= path =======                           |
|                         |                                  |
|                         |        tower                     |
|      tower              ======= path =======               |
|                                              |             |
|                                              |             |
|                         ======= path ======= Crystal       |
|                                                            |
+------------------------------------------------------------+
```

Input:
- Mouse click tower buttons to select a tower type.
- Mouse click on a valid map location to place a tower.
- Keyboard shortcut R to restart after game over or win.

Output:
- Map, path, towers, bullets, enemies, crystal health, coins, score, and wave.

### Game Over Screen

```
+------------------------------------------------------------+
|                       GAME OVER                            |
|                                                            |
|                 The crystal was destroyed.                 |
|                         [ Retry ]                          |
|                         [ Menu ]                           |
+------------------------------------------------------------+
```

### Win Screen

```
+------------------------------------------------------------+
|                         YOU WIN                            |
|                                                            |
|                The crystal survived all waves.             |
|                         [ Play Again ]                     |
|                         [ Menu ]                           |
+------------------------------------------------------------+
```

## 3. Classes, Variables, and Methods

### Main

Purpose:
- Starts the program.
- Creates the JFrame.
- Adds the GamePanel to the frame.

Important methods:

| Method | Return Type | Parameters | Description |
| --- | --- | --- | --- |
| main | void | String[] args | Creates the window and starts the game panel. |

### GamePanel

Purpose:
- Controls the game loop.
- Draws all screens.
- Handles mouse and keyboard input.
- Stores the main lists of enemies, towers, and bullets.

Important instance variables:

| Variable | Type | Description |
| --- | --- | --- |
| FPS | int | Target frames per second for the game loop. |
| thread | Thread | Runs the game loop. |
| screen | Screen | Current screen: title, instructions, about, playing, game over, or win. |
| path | ArrayList<Point> | Waypoints that enemies follow. |
| enemies | ArrayList<Enemy> | All enemies currently on the map. |
| towers | ArrayList<Tower> | All towers placed by the player. |
| bullets | ArrayList<Bullet> | All active bullets fired by towers. |
| towerCosts | HashMap<String, Integer> | Stores the cost of each tower type. |
| scoreManager | ScoreManager | Tracks coins, score, health, and defeated enemies. |
| selectedTowerType | String | The tower type currently selected by the player. |
| waveNumber | int | Current wave number. |
| enemiesLeftToSpawn | int | Number of enemies left to spawn in the current wave. |

Important methods:

| Method | Return Type | Parameters | Description |
| --- | --- | --- | --- |
| run | void | none | Runs the update and repaint loop. |
| initializeGame | void | none | Sets up the starting values and path. |
| resetGame | void | none | Clears the game and returns to wave 1. |
| update | void | none | Updates enemies, towers, bullets, waves, and win/loss status. |
| paintComponent | void | Graphics g | Draws the current screen. |
| drawPlayingScreen | void | Graphics2D g | Draws the main game. |
| startNextWave | void | none | Starts the next wave. |
| spawnEnemy | void | none | Adds a new enemy to the map. |
| placeTower | void | int mouseX, int mouseY | Places a tower if the location and coins are valid. |
| isValidTowerLocation | boolean | int x, int y | Checks if a tower can be placed at a position. |
| mousePressed | void | MouseEvent e | Handles menu clicks and tower placement. |
| keyPressed | void | KeyEvent e | Handles keyboard shortcuts. |

### Enemy

Purpose:
- Stores enemy position, health, speed, type, and path progress.
- Moves along the path.
- Implements Comparable so enemies can be sorted by path progress.

Important instance variables:

| Variable | Type | Description |
| --- | --- | --- |
| x, y | double | Enemy position. |
| health, maxHealth | int | Current and maximum health. |
| speed | double | Movement speed. |
| reward | int | Coins gained when defeated. |
| scoreValue | int | Score gained when defeated. |
| type | String | Enemy type name. |
| path | ArrayList<Point> | Path waypoints. |
| targetWaypoint | int | Index of the waypoint the enemy is moving toward. |
| distanceTravelled | double | Total distance moved along the path. |
| reachedCrystal | boolean | Whether the enemy reached the end. |

Important methods:

| Method | Return Type | Parameters | Description |
| --- | --- | --- | --- |
| update | void | none | Moves the enemy toward the next waypoint. |
| draw | void | Graphics2D g | Draws the enemy and health bar. |
| takeDamage | void | int damage | Reduces enemy health. |
| isDefeated | boolean | none | Returns true if health is 0 or less. |
| hasReachedCrystal | boolean | none | Returns true if enemy reached the crystal. |
| compareTo | int | Enemy other | Sorts enemies by path progress. |

### EnemyProgressComparator

Purpose:
- Sorts enemies by how close they are to the crystal.
- Used by towers to target the most dangerous enemy.

Important methods:

| Method | Return Type | Parameters | Description |
| --- | --- | --- | --- |
| compare | int | Enemy a, Enemy b | Compares enemies by path progress. |

### Tower

Purpose:
- Stores tower position, range, damage, firing speed, and type.
- Finds a target and fires bullets.

Important instance variables:

| Variable | Type | Description |
| --- | --- | --- |
| x, y | int | Tower center position. |
| type | String | Tower type name. |
| range | int | Attack range in pixels. |
| damage | int | Damage per bullet. |
| fireDelay | int | Frames between shots. |
| cooldown | int | Frames until the next shot is allowed. |
| cost | int | Coin cost to place the tower. |

Important methods:

| Method | Return Type | Parameters | Description |
| --- | --- | --- | --- |
| createTower | Tower | String type, int x, int y | Creates a tower with stats based on type. |
| update | void | ArrayList<Enemy> enemies, ArrayList<Bullet> bullets | Attacks enemies if possible. |
| findTarget | Enemy | ArrayList<Enemy> enemies | Finds the closest enemy to the crystal within range. |
| draw | void | Graphics2D g | Draws the tower. |
| getCost | int | none | Returns the tower cost. |

### Bullet

Purpose:
- Represents bullets fired by towers.
- Moves toward a target enemy and deals damage on contact.

Important instance variables:

| Variable | Type | Description |
| --- | --- | --- |
| x, y | double | Bullet position. |
| target | Enemy | Enemy being targeted. |
| damage | int | Damage dealt on hit. |
| speed | double | Bullet movement speed. |
| active | boolean | Whether the bullet should stay in the game. |

Important methods:

| Method | Return Type | Parameters | Description |
| --- | --- | --- | --- |
| update | void | none | Moves the bullet and checks if it hits the target. |
| draw | void | Graphics2D g | Draws the bullet. |
| isActive | boolean | none | Returns true if the bullet is still active. |

### ScoreManager

Purpose:
- Tracks score, coins, health, and defeated enemies.
- Uses a HashMap to store how many of each enemy type was defeated.

Important instance variables:

| Variable | Type | Description |
| --- | --- | --- |
| coins | int | Coins available for tower placement. |
| score | int | Player score. |
| crystalHealth | int | Remaining crystal health. |
| enemiesDefeated | int | Total enemies defeated. |
| defeatedByType | HashMap<String, Integer> | Number defeated for each enemy type. |

Important methods:

| Method | Return Type | Parameters | Description |
| --- | --- | --- | --- |
| reset | void | none | Resets health, coins, score, and enemy counts. |
| spendCoins | boolean | int amount | Subtracts coins if the player has enough. |
| addCoins | void | int amount | Adds coins. |
| damageCrystal | void | int amount | Reduces crystal health. |
| recordDefeat | void | Enemy enemy | Adds reward, score, and enemy defeat count. |
| isCrystalDestroyed | boolean | none | Returns true if crystal health is 0 or less. |

## 4. Collections and Course Concepts

The project will use these course concepts:

- Object-oriented programming with multiple useful classes.
- JFrame and JPanel GUI.
- Game loop using Thread and Runnable.
- MouseListener for button clicks and tower placement.
- KeyListener for keyboard shortcuts.
- ArrayList for enemies, towers, bullets, and path points.
- HashMap for tower costs and defeated enemy statistics.
- Comparable in the Enemy class.
- Comparator in EnemyProgressComparator.
- 2D coordinate/grid logic for map placement.

## 5. Current Development Plan

1. Create JFrame window and JPanel game loop.
2. Create title, instructions, about, game over, and win screens.
3. Create fixed map path and crystal.
4. Create Enemy movement along waypoints.
5. Create Tower placement with coins.
6. Create automatic tower targeting.
7. Create Bullet movement and collision.
8. Add waves, score, coins, and crystal health.
9. Improve graphics and add original images.
10. Add sound effects and background music.
11. Add leaderboard if time allows.
12. Test, fix bugs, and write final README.

## 6. Current Challenges

- Making enemy movement turn smoothly along the fixed path.
- Making towers target the correct enemy.
- Preventing towers from being placed on the path.
- Balancing coins, enemy health, tower damage, and wave difficulty.
- Adding sound and music without making the program hard to run on school computers.
- Keeping the code organized enough to explain during the demo.

## 7. Current Progress

At this stage, the project has about 30 percent of the final game framework
completed. The project already has the main window, menu screens, game loop,
fixed path, enemy movement, tower placement, bullets, collision, basic waves,
score, coins, health, and win/loss screens.

The next stage should focus on improving the graphics, adding sound/music,
adding more polish to waves and balancing, and preparing the final README and
demo video.
