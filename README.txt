Crystal Defense
ICS4U Initial Framework

How to run from the CrystalDefense folder:

1. Compile:
   javac -d bin src/*.java

2. Run:
   java -cp bin Main

Current status:
- Title screen, instructions screen, about screen, game screen, win screen, and game over screen exist.
- The title screen includes a Select Level menu, so the player can start from Level 1, 2, or 3.
- Enemies move along a fixed path.
- The game now has 3 levels.
- All levels use a dungeon-style background image.
- Level 2 has branching paths and enemies randomly choose a route.
- Level 3 has more complex paths and a second enemy spawn point.
- Level 2 and Level 3 include Elite enemies with more health and slower movement.
- Menu music loops on the title, level select, instructions, and about screens.
- Battle music loops during each level and stops when the level ends.
- A sound effect plays when an enemy is defeated.
- Player can select tower types and place towers with coins.
- Towers automatically shoot enemies in range.
- Bullets damage enemies.
- Coins, score, wave number, and crystal health are tracked.
- ArrayList, HashMap, Comparable, and Comparator are already included in the code.

Not finished yet:
- Original image assets.
- More balancing.
- Final polished README details.
- Optional leaderboard.

Controls:
- Click Start Game to play.
- Click a tower button at the top to choose a tower type.
- Click on grass beside the path to place the selected tower.
- Press R after winning or losing to restart.
- Press Esc during the game to return to the title screen.
