
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Stores the top five winning scores
 */
public class ScoreBoard {

    private ArrayList<Integer> scores;
    private String fileName;
    private boolean lastScoreKept;

    public ScoreBoard() {
        scores = new ArrayList<Integer>();
        fileName = "resources/scoreboard.txt";
        lastScoreKept = false;
        loadScores();
    }

    /**
     * Adds a score, keeps only the top five, and saves the result
     */
    public void addScore(int score) {
        scores.add(score);
        sortScores();

        while (scores.size() > 5) {
            scores.remove(scores.size() - 1);
        }

        lastScoreKept = binarySearchScore(score) >= 0;

        saveScores();
    }

    /**
     * Reads saved scores from disk if the file exists
     */
    private void loadScores() {
        scores.clear();

        try {
            Scanner input = new Scanner(new File(fileName));

            while (input.hasNextInt()) {
                scores.add(input.nextInt());
            }

            input.close();
        } catch (IOException e) {
        }

        sortScores();

        while (scores.size() > 5) {
            scores.remove(scores.size() - 1);
        }
    }

    /**
     * Writes the current score list back to disk
     */
    private void saveScores() {
        try {
            FileWriter writer = new FileWriter(fileName);

            for (int i = 0; i < scores.size(); i++) {
                writer.write(scores.get(i) + "\n");
            }

            writer.close();
        } catch (IOException e) {
        }
    }

    /**
     * Sorts scores from highest to lowest
     */
    private void sortScores() {
        for (int i = 0; i < scores.size() - 1; i++) {
            for (int j = i + 1; j < scores.size(); j++) {
                if (scores.get(j) > scores.get(i)) {
                    int temp = scores.get(i);
                    scores.set(i, scores.get(j));
                    scores.set(j, temp);
                }
            }
        }
    }

    /**
     * Checks whether the score still exists after trimming the list
     */
    private int binarySearchScore(int score) {
        int low = 0;
        int high = scores.size() - 1;

        while (low <= high) {
            int middle = low + (high - low) / 2;
            int middleScore = scores.get(middle);

            if (middleScore == score) {
                return middle;
            } else if (score > middleScore) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }

        return -1;
    }

    public ArrayList<Integer> getScores() {
        return scores;
    }

    public boolean wasLastScoreKept() {
        return lastScoreKept;
    }
}
