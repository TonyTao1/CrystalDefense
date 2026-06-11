import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Loads and plays the game sounds.
 */
public class SoundManager {
    private Clip menuMusic;
    private Clip battleMusic;
    private Clip defeatSound;

    public SoundManager() {
        // Load sounds once when the game starts.
        menuMusic = loadClip("resources/sounds/menu.wav");
        battleMusic = loadClip("resources/sounds/battle.wav");
        defeatSound = loadClip("resources/sounds/defeat.wav");
    }

    private Clip loadClip(String fileName) {
        // Return null if the sound file cannot be loaded.
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(fileName));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (IOException e) {
            return null;
        } catch (UnsupportedAudioFileException e) {
            return null;
        } catch (LineUnavailableException e) {
            return null;
        }
    }

    public void playMenuMusic() {
        // Loop music on the menu screens.
        if (menuMusic == null || menuMusic.isRunning()) {
            return;
        }

        menuMusic.setFramePosition(0);
        menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopMenuMusic() {
        // Stop menu music before gameplay starts.
        if (menuMusic == null) {
            return;
        }

        menuMusic.stop();
        menuMusic.setFramePosition(0);
    }

    public void playBattleMusic() {
        // Loop the battle music during a level.
        if (battleMusic == null || battleMusic.isRunning()) {
            return;
        }

        battleMusic.setFramePosition(0);
        battleMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopBattleMusic() {
        // Stop the loop when the level or game ends.
        if (battleMusic == null) {
            return;
        }

        battleMusic.stop();
        battleMusic.setFramePosition(0);
    }

    public void playDefeatSound() {
        // Restart the sound each time an enemy is defeated.
        if (defeatSound == null) {
            return;
        }

        defeatSound.stop();
        defeatSound.setFramePosition(0);
        defeatSound.start();
    }
}
