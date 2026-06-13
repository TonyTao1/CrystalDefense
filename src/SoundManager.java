
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Loads and plays the game sounds
 */
public class SoundManager {

    private Clip menuMusic;
    private Clip battleMusic;
    private Clip defeatSound;

    public SoundManager() {
        menuMusic = loadClip("resources/sounds/menu.wav");
        battleMusic = loadClip("resources/sounds/battle.wav");
        defeatSound = loadClip("resources/sounds/defeat.wav");
    }

    private Clip loadClip(String fileName) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(fileName));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (Exception e) {
            return null;
        }
    }

    public void playMenuMusic() {
        if (menuMusic == null || menuMusic.isRunning()) {
            return;
        }

        menuMusic.setFramePosition(0);
        menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopMenuMusic() {
        if (menuMusic == null) {
            return;
        }

        menuMusic.stop();
        menuMusic.setFramePosition(0);
    }

    public void playBattleMusic() {
        if (battleMusic == null || battleMusic.isRunning()) {
            return;
        }

        battleMusic.setFramePosition(0);
        battleMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopBattleMusic() {
        if (battleMusic == null) {
            return;
        }

        battleMusic.stop();
        battleMusic.setFramePosition(0);
    }

    public void playDefeatSound() {
        if (defeatSound == null) {
            return;
        }

        defeatSound.stop();
        defeatSound.setFramePosition(0);
        defeatSound.start();
    }
}
