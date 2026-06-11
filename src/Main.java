import javax.swing.JFrame;

/**
 * Starts Crystal Defense and creates the main JFrame window.
 */
public class Main {
    public static void main(String[] args) {
        // Create the window and the panel that runs the game.
        JFrame frame = new JFrame("Crystal Defense");
        GamePanel gamePanel = new GamePanel();

        // Basic JFrame setup.
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start the game loop after the window is ready.
        gamePanel.startGameThread();
    }
}
