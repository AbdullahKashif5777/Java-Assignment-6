import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon("icons8-tic-tac-toe-96.png");
        frame.setIconImage(icon.getImage());
        frame.setBounds(400, 150, 800, 500);
        frame.setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(Color.darkGray);
        frame.add(gamePanel, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel();
        statsPanel.setPreferredSize(new Dimension(300, 500));
        statsPanel.setBackground(Color.white);
        statsPanel.setLayout(new BorderLayout());
        frame.add(statsPanel, BorderLayout.EAST);

        // Add the icon image to the stats panel
        ImageIcon gameIcon = new ImageIcon("icons8-tic-tac-toe-96.png"); // Use the correct icon file path
        JLabel iconLabel = new JLabel(gameIcon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the icon
        statsPanel.add(iconLabel, BorderLayout.NORTH); // Add the icon to the top of the panel

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(null);
        bottomPanel.setPreferredSize(new Dimension(300, 150));
        bottomPanel.setBackground(Color.white);

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBackground(Color.DARK_GRAY);
        startButton.setForeground(Color.WHITE);
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.setBounds(50, 100, 200, 50); // Position at the bottom of bottomPanel
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startNewGame();
            }
        });

        JLabel player1WinsLabel = new JLabel("Player 1 Wins: 0");
        JLabel player2WinsLabel = new JLabel("Player 2 Wins: 0");

        player1WinsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        player1WinsLabel.setForeground(Color.BLACK);
        player2WinsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        player2WinsLabel.setForeground(Color.BLACK);

        bottomPanel.add(player2WinsLabel);
        bottomPanel.add(player1WinsLabel);

        player2WinsLabel.setBounds(10, 10, 150, 30);
        player1WinsLabel.setBounds(10, 50, 150, 30);

        bottomPanel.add(startButton);

        statsPanel.add(bottomPanel, BorderLayout.CENTER);

        TicTacToeLogic.initializeLabels(player1WinsLabel, player2WinsLabel);

        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 1, 1));
        boardPanel.setBackground(Color.darkGray);
        boardPanel.setVisible(false); // Initially hide the game board

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JButton button = new JButton("");
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                button.setOpaque(true);
                button.setBackground(Color.WHITE);
                button.setFont(new Font("Calibri", Font.BOLD, 50));
                TicTacToeLogic.tiles[row][col] = button;
                boardPanel.add(button);
                TicTacToeLogic.setupTile(button);
            }
        }

        gamePanel.add(boardPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static void startNewGame() {
        JPanel gamePanel = (JPanel) ((JFrame) SwingUtilities.getWindowAncestor((Component) TicTacToeLogic.tiles[0][0])).getContentPane().getComponent(0);
        JPanel boardPanel = (JPanel) gamePanel.getComponent(0);
        boardPanel.setVisible(true); // Show the game board
        TicTacToeLogic.resetBoard(); // Reset the board to start a new game
    }
}

class TicTacToeLogic {
    public static JButton[][] tiles = new JButton[3][3];
    public static boolean isPlayerOneTurn = true; // true -> player 1 | false -> player 2
    public static Color player1Color = Color.BLACK;
    public static Color player2Color = Color.GRAY;
    public static String player1Symbol = "X";
    public static String player2Symbol = "O";
    public static boolean isGameOver = false;
    public static short turnCount = 0;
    private static JLabel player1WinsLabel;
    private static JLabel player2WinsLabel;
    private static int player1Wins = 0;
    private static int player2Wins = 0;

    public static void setupTile(JButton button) {
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setOpaque(true);
        button.setBackground(Color.WHITE);
        button.setFont(new Font("Calibri", Font.BOLD, 50));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isGameOver) {
                    if ("".equals(button.getText())) {
                        if (isPlayerOneTurn) { // PLAYER 1
                            button.setForeground(player1Color);
                            button.setText(player1Symbol);
                        } else { // PLAYER 2
                            button.setForeground(player2Color);
                            button.setText(player2Symbol);
                        }
                        turnCount++;
                        isPlayerOneTurn = !isPlayerOneTurn;
                        evaluateGameState();
                        if (isGameOver) {
                            promptPlayAgain();
                        }
                    }
                }
            }
        });
    }

    public static void resetBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                tiles[row][col].setText("");
                tiles[row][col].setBackground(Color.WHITE);
            }
        }
        turnCount = 0;
        isGameOver = false;
    }

    public static void promptPlayAgain() {
        String message = "Game Over! ";
        if (isGameOver) {
            message += "Do you want to play again?";
        } else {
            message += "It's a draw! Do you want to play again?";
        }
        int restart = JOptionPane.showConfirmDialog(null, message);

        if (restart == JOptionPane.YES_OPTION) {
            resetBoard();
        } else if (restart == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    public static void evaluateGameState() {
        // Check rows
        for (int row = 0; row < 3; row++) {
            if (!tiles[row][0].getText().equals("") &&
                    tiles[row][0].getText().equals(tiles[row][1].getText()) &&
                    tiles[row][1].getText().equals(tiles[row][2].getText())) {
                markWinningTiles(tiles[row][0]);
                isGameOver = true;
                updateWinCounts();
                return;
            }
        }
        // Check columns
        for (int col = 0; col < 3; col++) {
            if (!tiles[0][col].getText().equals("") &&
                    tiles[0][col].getText().equals(tiles[1][col].getText()) &&
                    tiles[1][col].getText().equals(tiles[2][col].getText())) {
                markWinningTiles(tiles[0][col]);
                isGameOver = true;
                updateWinCounts();
                return;
            }
        }
        // Check diagonals
        if (!tiles[0][0].getText().equals("") &&
                tiles[0][0].getText().equals(tiles[1][1].getText()) &&
                tiles[1][1].getText().equals(tiles[2][2].getText())) {
            markWinningTiles(tiles[0][0]);
            isGameOver = true;
            updateWinCounts();
            return;
        }
        if (!tiles[0][2].getText().equals("") &&
                tiles[0][2].getText().equals(tiles[1][1].getText()) &&
                tiles[1][1].getText().equals(tiles[2][0].getText())) {
            markWinningTiles(tiles[0][2]);
            isGameOver = true;
            updateWinCounts();
        }

        if (!isGameOver && turnCount == 9) {
            isGameOver = true;
            promptPlayAgain();
        }
    }

    public static void markWinningTiles(JButton button) {
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.GREEN);
    }

    public static void updateWinCounts() {
        if (isPlayerOneTurn) {
            player2Wins++;
            player2WinsLabel.setText("Player 2 Wins: " + player2Wins);
        } else {
            player1Wins++;
            player1WinsLabel.setText("Player 1 Wins: " + player1Wins);
        }
    }

    public static void initializeLabels(JLabel p1Label, JLabel p2Label) {
        player1WinsLabel = p1Label;
        player2WinsLabel = p2Label;
    }
}
