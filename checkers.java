import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class checkers {
    private static boolean x;
    private static JButton[][] board = new JButton[8][8];
    private static JFrame frame = new JFrame();
    private static JFrame choosePLayerFrame = new JFrame();
    private static JDialog win = new JDialog();
    private static JLabel winner = new JLabel();
    public String teamOne;
    public String teamTwo;
    public Color playerColor;

    public checkers() {
        this.makeBoard();
        frame.setLayout(new GridLayout(8, 8));
        frame.setSize(new Dimension(1000, 1000));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setLayout(new GridLayout(1, 1));
        win.setSize(new Dimension(100, 100));
        win.add(winner);
        win.setUndecorated(false);
        this.makePawns();
        this.chooseFirstPlayer();
        frame.setVisible(true);
    }

    public checkers(String t1Name, String t2Name) {
        teamOne = t1Name;
        teamTwo = t2Name;
        makeBoard();
        makePawns();
        chooseFirstPlayer();
        setupFrame();
    }

    public void setupFrame() {
        frame.setLayout(new GridLayout(8, 8));
        frame.setSize(new Dimension(1000, 1000));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setLayout(new GridLayout(1, 1));
        win.setSize(new Dimension(100, 100));
        win.add(winner);
        win.setUndecorated(false);
        frame.setVisible(true);
    }

    public void makeBoard() {
        boolean black = false;
        for (int row = 0; row < board.length; row++) {
            black = (row % 2 == 0);
            for (int col = 0; col < board[row].length; col++) {
                int locationR = row;
                int locationC = col;
                JButton button = new JButton("");
                board[row][col] = button;
                button.setName("");
                if (row % 2 == 0)
                    button.setBackground(Color.black);
                else {
                    button.setBackground(Color.white);
                    button.setEnabled(false);
                }
                black = !black;
                frame.add(button);

                button.addActionListener(e -> actionPerformed(button, locationR, locationC));
            }
        }
    }

    public void actionPerformed(JButton button, int locationR, int locationC) {
        playerColor = x ? Color.red : Color.green;
        checkWin();
        changeAll(false);
        if (button.getText().equals("Pawn") || button.getText().equals("King")) {
            if (button.getForeground().equals(playerColor)) {
                Location[] moves = getMoves(locationR, locationC);
                button.setName("selected");
                if (button.getText().equals("King"))
                    button.setName("King");
                button.setEnabled(true);
                for (Location move : moves) {
                    if (move != null && !board[move.getRow()][move.getCol()].getForeground()
                            .equals(playerColor)) {
                        board[move.getRow()][move.getCol()].setEnabled(true);
                        board[move.getRow()][move.getCol()].setBackground(Color.yellow);
                        button.setFont(new Font("MV Boli", Font.BOLD, 100));
                        button.setText("X");
                    } else {
                        enableTeam(playerColor);
                    }
                }
            } else {
                int newR;
                int newC;
                JButton newButton;
                for (int row = 0; row < board.length; row++) {
                    for (int col = 0; col < board.length; col++) {
                        if (board[row][col].getText().equals("X")) {
                            newR = locationR - row;
                            newC = locationC - col;
                            clearBoard();
                            if (locationC + newC > 7 && row + newR > 7 && locationC + newC < 0
                                    && row + newR < 0
                                    && board[locationR + newR][locationC + newC].getText()
                                            .equals("Pawn")
                                    && board[locationR + newR][locationC + newC].getText()
                                            .equals("King")) {
                                enableTeam(playerColor);
                                board[row][col].setText((board[row][col].getName().equals("King")) ? "King" : "Pawn");
                                board[row][col].setFont(null);
                                break;
                            } else {
                                System.out.println((locationR + newR) + ", " + (locationC + newC));
                                newButton = board[locationR + newR][locationC + newC];
                                newButton.setForeground(board[row][col].getForeground());
                                newButton.setText("Pawn");
                                if (newButton.getForeground().equals(Color.green)
                                        && locationR + newR == 0) {
                                    System.out.println("green kinging");
                                    newButton.setText("King");
                                    newButton.setName("King");
                                } else if (newButton.getForeground().equals(Color.red)
                                        && locationR + newR == 7) {
                                    System.out.println("red kinging");
                                    newButton.setText("King");
                                    newButton.setName("King");
                                } else {
                                    System.out.println("bottom out");
                                }

                                setEmpty(button);
                                setEmpty(board[row][col]);

                                enableTeam(playerColor);
                                // changeTeam();
                                break;
                            }
                        }
                    }
                }
            }

        } else if (button.getText().equals("X")) {
            enableTeam(button.getForeground());
            button.setFont(null);
            button.setText((button.getName().equals("King")) ? "King" : "Pawn");
            for (JButton[] brow : board) {
                for (JButton yellowButton : brow) {
                    if (yellowButton.getBackground().equals(Color.yellow)) {
                        yellowButton.setBackground(Color.black);
                    }
                }
            }

        } else if (button.getText() == "") {
            clearBoard();
            for (JButton[] row : board) {
                for (JButton button2 : row) {
                    if (button2.getText().equals("X")) {
                        button.setForeground(button2.getForeground());
                        button.setText((button2.getName().equals("King")) ? "King" : "Pawn");

                        if (button.getForeground().equals(Color.green)
                                && locationR == 0) {
                            button.setText("King");
                            button.setName("King");
                        } else if (button.getForeground().equals(Color.red)
                                && locationR == 7) {
                            button.setText("King");
                            button.setName("King");
                        }
                        setEmpty(button2);
                        changeTeam();
                        break;
                    }

                }
            }

        }
    }

    public Location[] getMoves(int startRow, int startCol) {
        Location[] moves = new Location[4];
        if (board[startRow][startCol].getForeground() == Color.green || board[startRow][startCol].getName() == "King") {
            if (startCol > 0 && startRow > 0) {
                moves[0] = new Location(startRow - 1, startCol - 1);
            }
            if (startCol < 7 && startRow > 0) {
                moves[1] = new Location(startRow - 1, startCol + 1);
            }
        } else if (board[startRow][startCol].getForeground() == Color.red
                || board[startRow][startCol].getName() == "King") {
            if (startCol > 0 && startRow < 7) {
                moves[2] = new Location(startRow + 1, startCol - 1);
            }
            if (startCol < 7 && startRow < 7) {
                moves[3] = new Location(startRow + 1, startCol + 1);
            }
        }
        return moves;
    }

    public void changeTeam() {
        x = !x;
        if (x) {
            changeAll(false);
            enableTeam(Color.red);
        } else {
            changeAll(false);
            enableTeam(Color.green);
        }
    }

    public void setEmpty(JButton button) {
        button.setName("");
        button.setForeground(null);
        button.setFont(null);
        button.setEnabled(false);
        button.setText("");
    }

    public void makePawns() {
        for (int row = 0; row < board.length; row++) {
            for (JButton button : board[row]) {
                if (button.getBackground() == Color.black && (row < 3)) {
                    button.setText("Pawn");
                    button.setForeground(Color.red);
                } else if (button.getBackground() == Color.black && (row > 4)) {
                    button.setText("Pawn");
                    button.setForeground(Color.green);
                }
            }
        }

        // board[1][5].setText("Pawn");
        // board[1][5].setForeground(Color.green);

    }

    public void chooseFirstPlayer() {
        changeAll(false);
        choosePLayerFrame = new JFrame();
        choosePLayerFrame.setSize(400, 200);
        choosePLayerFrame.setLayout(new GridLayout(1, 2, 10, 10));
        choosePLayerFrame.setTitle("Choose First Player");
        choosePLayerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        choosePLayerFrame.setResizable(false);
        choosePLayerFrame.setLocation(0,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2 - choosePLayerFrame.getSize().height / 2);
        JButton xButton = new JButton();
        JButton oButton = new JButton();
        xButton.setText("Red");
        oButton.setText("Green");
        xButton.setFont(new Font("Arial", Font.PLAIN, 50));
        oButton.setFont(new Font("Arial", Font.PLAIN, 50));
        xButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == xButton) {
                    x = true;
                    enableTeam(Color.red);
                    choosePLayerFrame.dispose();
                }
            }
        });
        oButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == oButton) {
                    x = false;
                    enableTeam(Color.green);
                    choosePLayerFrame.dispose();
                }
            }
        });
        choosePLayerFrame.add(xButton);
        choosePLayerFrame.add(oButton);
        choosePLayerFrame.setVisible(true);
    }

    private void clearBoard() {
        for (JButton[] row : board) {
            for (JButton button : row) {
                if (button.getBackground().equals(Color.yellow))
                    button.setBackground(Color.black);
            }
        }
    }

    private static void changeAll(boolean enabled) {
        for (JButton[] row : board) {
            for (JButton col : row) {
                if (col.getBackground() == Color.black || col.getBackground() == Color.yellow)
                    col.setEnabled(enabled);
            }
        }
    }

    private static void enableTeam(Color color) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col].getForeground() == color) {
                    board[row][col].setEnabled(true);
                }
            }
        }
    }

    private void end(String endText) {
        for (JButton[] brow : board) {
            for (JButton button : brow) {
                button.setEnabled(false);
            }
        }
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });

        win.setLocationRelativeTo(null);
        win.setLayout(new GridLayout(2, 1));
        win.setSize(100, 100);
        win.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        winner.setHorizontalAlignment(JLabel.CENTER);
        win.add(winner);
        win.add(close);
        win.setTitle(endText);
        winner.setText(endText);
        win.setVisible(true);
    }

    private void checkWin() {
        boolean redWins = true;
        boolean greenWins = true;
        for (JButton[] brow : board) {
            for (JButton button : brow) {
                if (button.getText().equals("Pawn") || button.getText().equals("King")) {
                    if (button.getForeground().equals(Color.red)) {
                        greenWins = false;
                    } else if (button.getForeground().equals(Color.green)) {
                        redWins = false;
                    }
                }
            }
        }
        if (redWins) {
            if (teamOne != null)
                end(teamOne + " wins");
            else
                end("Red Wins");
        } else if (greenWins) {
            if (teamTwo != null)
                end(teamTwo + " wins");
            else
                end("Green Wins");
        }
    }

    public static void main(String[] args) {
        checkers test = new checkers();
        // test.end("Rip");
    }

}

class Location {
    private int row;
    private int col;

    public Location(int r, int c) {
        col = c;
        row = r;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int r) {
        row = r;
    }

    public void setCol(int c) {
        row = c;
    }

    public String toString() {
        return "[" + this.getRow() + ", " + this.getCol() + "]";
    }

}
