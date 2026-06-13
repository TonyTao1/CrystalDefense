import javax.swing.JFrame;

/**
 * Starts Crystal Defense
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Crystal Defense");
        GamePanel gamePanel = new GamePanel();

        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        gamePanel.startGameThread();
    }
}
