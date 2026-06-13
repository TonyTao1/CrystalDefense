
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

    public void addScore(int score) {
        scores.add(score);
        sortScores();

        while (scores.size() > 5) {
            scores.remove(scores.size() - 1);
        }

        lastScoreKept = binarySearchScore(score) >= 0;

        saveScores();
    }

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

    private int binarySearchScore(int score) {
        int low = 0;
        int high = scores.size() - 1;

        while (low <= high) {
            int middle = (low + high) / 2;
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
