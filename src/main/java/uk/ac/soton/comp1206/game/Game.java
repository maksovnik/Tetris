package uk.ac.soton.comp1206.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameEndListener;
import uk.ac.soton.comp1206.event.TimerFinishedListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.pieceEventListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS
 * game. Methods to manipulate the game state and to handle actions made by the
 * player should take place inside this class.
 */
public class Game {

    GamePiece currentPiece;

    NextPieceListener npl;
    pieceEventListener ppl;
    LineClearedListener lcl;
    GameEndListener gel;
    TimerFinishedListener gll;
    GamePiece followingPiece;

    private static final Logger logger = LogManager.getLogger(Game.class);

    protected SimpleIntegerProperty highScore = new SimpleIntegerProperty(0);
    protected SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty level = new SimpleIntegerProperty(0);
    protected SimpleIntegerProperty lives = new SimpleIntegerProperty(3);
    private SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);

    public void rotateCurrentPiece(int direction) {
        if (currentPiece == null) {
            return;
        }

        currentPiece.rotate(direction);
        npl.nextPiece(currentPiece, followingPiece);
        ppl.eventTrigger("rotate");
    }

    private int getTimerDelay() {
        // return 500;
        return Math.max(2500, 12000 - 500 * (level.get()));
    }

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    int[][] blocks = new int[5][5];

    /**
     * Create a new game with the specified rows and columns. Creates a
     * corresponding grid model.
     * 
     * @param cols number of columns
     * @param rows number of rows
     */

    public void setOnNextPiece(NextPieceListener npl) {
        this.npl = npl;
    }

    public void setOnTimerFinished(TimerFinishedListener gll) {
        this.gll = gll;
    }

    public void setOnPieceEvent(pieceEventListener ppl) {
        this.ppl = ppl;
    }

    public int getScore() {
        return score.get();
    }

    public int getLevel() {
        return level.get();
    }

    public int getLives() {
        return lives.get();
    }

    public int getMultiplier() {
        return multiplier.get();
    }

    // Define a getter for the property itself
    public IntegerProperty getScoreProperty() {
        return score;
    }

    public IntegerProperty getHScoreProperty() {
        return highScore;
    }

    private void resetGameTimer() {
        if (gll != null) {
            int delay = getTimerDelay();
            gll.setTimerToFull(delay);
        }

    }

    public void setHighScore(int h) {
        highScore.set(h);
    }

    public int getQueueSize() {
        return 0;
    }

    public void addToQueue(String c) {
    };

    public void requestPieces(int i) {
    }

    public IntegerProperty getLivesProperty() {
        return lives;
    }

    public IntegerProperty getLevelProperty() {
        return level;
    }

    public IntegerProperty getMultiplierProperty() {
        return multiplier;
    }

    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        // Create a new grid model to represent the game state
        this.grid = new Grid(cols, rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        currentPiece = spawnPiece();
        this.followingPiece = spawnPiece();
        npl.nextPiece(currentPiece, followingPiece);

        resetGameTimer();

    }

    public void gameLoop() {
        if (multiplier.get() > 1) {
            multiplier.set(1);
        }

        if (lives.get() > 0) {
            lives.set(lives.get() - 1);
        } else {
            logger.info("LIVES IS {}", lives.get());
            end();
            return;
        }

        nextPiece();
        resetGameTimer();

    }

    public void end() {
        gel.endGame();
    }

    public void setOnGameEnd(GameEndListener g) {
        this.gel = g;
    }

    public void swapCurrentPiece() {

        if (currentPiece == null) {
            return;
        }
        if (followingPiece == null) {
            return;
        }

        GamePiece temp = followingPiece;
        followingPiece = currentPiece;
        currentPiece = temp;
        npl.nextPiece(currentPiece, followingPiece);
        ppl.eventTrigger("swap");
    }

    public void nextPiece() {
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        npl.nextPiece(currentPiece, followingPiece);
    }

    public GamePiece spawnPiece() {
        logger.info("Spawning a piece");
        Random rn = new Random();
        int number = rn.nextInt(15); // 3 For testing
        GamePiece piece = GamePiece.createPiece(number);
        logger.info(Arrays.deepToString(piece.getBlocks()));
        return piece;
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * 
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        // Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        // Get the new value for this block
        int previousValue = grid.get(x, y);
        int newValue = previousValue + 1;
        if (newValue > GamePiece.PIECES) {
            newValue = 0;
        }

        // Update the grid with the new value
        if (grid.canPlayPiece(currentPiece, x, y)) {
            grid.playPiece(currentPiece, x, y);
            logger.info("THIS PIECE IS ID:{}", currentPiece.getValue());
            afterPiece();
            nextPiece();
            ppl.eventTrigger("playPiece");
            resetGameTimer();
        }
        // grid.set(x,y,newValue);

    }

    public void afterPiece() {
        logger.info("Checking for Rows");

        ArrayList<Integer> rowsToClear = new ArrayList<Integer>();
        ArrayList<Integer> colsToClear = new ArrayList<Integer>();

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                blocks[i][j] = grid.get(i, j);
            }
        }

        for (int i = 0; i < grid.getRows(); i++) { // Loops through each row
            if (fullRow(i)) { // Checks if specific row is clear
                logger.info("Row found - clearing");
                rowsToClear.add(i);
            }
        }

        logger.info("Checking for Lines");
        for (int i = 0; i < grid.getCols(); i++) {
            if (fullColumn(i)) {
                logger.info("Line found - clearing");
                colsToClear.add(i);
            }
        }

        var c = new ArrayList<GameBlockCoordinate>();

        for (int i : rowsToClear) {
            c.addAll(setRowZero(i));
        }

        for (int i : colsToClear) {
            c.addAll(setColZero(i));
        }

        int numLines = rowsToClear.size() + colsToClear.size();
        int blocksCleared = (5 * rowsToClear.size()) + (5 * colsToClear.size())
                - (rowsToClear.size() * colsToClear.size()); // Calculates number of blocks cleared

        if (numLines == 0) {
            multiplier.set(1);
            return;
        }

        setScore(score.get() + score(numLines, blocksCleared));

        level.set((int) this.score.get() / 1000); // Sets level
        multiplier.set(multiplier.get() + 1); // Increases multiplier

        var coords = new ArrayList<GameBlockCoordinate>();

        lcl.linesCleared(c.toArray(new GameBlockCoordinate[coords.size()]));

    }

    public void setScore(int newScore) {
        if (newScore > highScore.get()) {
            highScore.set(newScore);
        }
        score.set(newScore);
    }

    public void setOnLineCleared(LineClearedListener l) {
        this.lcl = l;
    }

    public int score(int lines, int blocks) {
        return lines * blocks * 10 * getMultiplier();
    }

    public ArrayList<GameBlockCoordinate> setRowZero(int row) {
        var coords = new ArrayList<GameBlockCoordinate>();
        // Set's each item in a row to 0
        for (int i = 0; i < grid.getCols(); i++) {
            grid.set(row, i, 0);
            coords.add(new GameBlockCoordinate(row, i));
        }
        return coords;
    }

    public ArrayList<GameBlockCoordinate> setColZero(int col) {
        var coords = new ArrayList<GameBlockCoordinate>();
        // Set's each item in a column to 0
        for (int i = 0; i < grid.getRows(); i++) {
            grid.set(i, col, 0);
            coords.add(new GameBlockCoordinate(i, col));
        }
        return coords;
    }

    public boolean fullColumn(int col) {
        // Checks if a specific column is full
        for (int i = 0; i < grid.getCols(); i++) {
            if (grid.get(i, col) == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean fullRow(int row) {
        // Checks if a specific row is full
        for (int i = 0; i < grid.getRows(); i++) {
            if (grid.get(row, i) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * 
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * 
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * 
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

}
