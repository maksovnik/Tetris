package uk.ac.soton.comp1206.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.util.Duration;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameEndListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.TimerChangeListener;
import uk.ac.soton.comp1206.event.PieceEventListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS
 * game. Methods to manipulate the game state and to handle actions made by the
 * player should take place inside this class.
 */
public class Game {

    GamePiece currentPiece;

    PieceEventListener ppl;
    LineClearedListener lcl;
    GameEndListener gel;
    GamePiece followingPiece;

    private static final Logger logger = LogManager.getLogger(Game.class);

    protected SimpleIntegerProperty highScore = new SimpleIntegerProperty(0);
    protected SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty level = new SimpleIntegerProperty(0);
    protected SimpleIntegerProperty lives = new SimpleIntegerProperty(3);
    private SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);

    ChangeListener<Number> changeListener;

    TimerChangeListener tel;
    protected SimpleDoubleProperty time = new SimpleDoubleProperty(1);
    Timeline timer;

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

    double speed = 1;

    /**
     * Creates a new Game object with specified rows and columns
     * 
     * @param cols Number of columns
     * @param rows Number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        // Create a new grid model to represent the game state
        this.grid = new Grid(cols, rows);

        changeListener = (a, b, c) -> onChange(c);
        time.addListener(changeListener);
    }

    /**
     * Handles the game timer reducing
     * 
     * @param newValue the latest timer value
     */
    private void onChange(Number newValue) {

        if (tel != null) {
            // Calls the timer change listener with the new value
            tel.onTimerChange(newValue.doubleValue());
        }

        if (newValue.doubleValue() == 0) {
            // If the timer is 0 then punish and loop again
            punish();
            resetGameLoop();
            nextPiece();
        }
    }

    /**
     * Starts a new game loop
     * 
     * @param delay the time for the game loop
     */
    public void resetGameLoop() {
        logger.info("Game Loop has been reset");
        //Gets timer delay
        var delay = getTimerDelay();

        if (timer != null) {
            // Stop previous loop
            timer.stop();
        }

        // Set starting value to 1
        time.set(1);

        // Reduce value to 0 over a duration passed to the method
        KeyValue timerValue = new KeyValue(time, 0);
        KeyFrame frame = new KeyFrame(Duration.millis(delay), timerValue);

        timer = new Timeline(frame);

        // Start the timer
        timer.play();
    }

    /**
     * Gives the time property for use with binding
     * 
     * @return time property
     */
    public DoubleProperty getTimeProperty() {
        return time;
    }

    /**
     * Rotates the current piece
     * 
     * @param direction anticlockwise is -1, clockwise is 1
     */
    public void rotateCurrentPiece(int direction) {
        if(direction==-1){
            logger.info("Piece rotated anticlockwise");
        }
        if(direction==1){
            logger.info("Piece rotated clockwise");
        }
        
        if (currentPiece == null) {
            // Can't rotate a piece if there isn't one
            return;
        }

        currentPiece.rotate(direction);
        ppl.nextPiece(currentPiece, followingPiece);

        // Calls piece event listener in case the UI wants to do anything on rotate
        ppl.rotatePiece();
    }

    /**
     * Gets the next timer delay
     * 
     * @return next timer delay
     */
    public int getTimerDelay() {
        return Math.max(2500, 12000 - 500 * (level.get()));
    }
    
    /**
     * Pauses the game timer
     */
    public void pause() {
        if(timer!=null){
            timer.pause();
        }

    }

    /**
     * Plays the game timer
     */
    public void play() {
        if(timer!=null){
            timer.play();
        }
    }

    /**
     * Sets the piece event listener
     * 
     * @param ppl piece event listener
     */
    public void setOnPieceEvent(PieceEventListener ppl) {
        this.ppl = ppl;
    }

    /**
     * Gets the score value
     * 
     * @return score
     */
    public int getScore() {
        return score.get();
    }

    /**
     * Gets the level value
     * 
     * @return level
     */
    public int getLevel() {
        return level.get();
    }

    /**
     * Gets the lives value
     * 
     * @return lives
     */
    public int getLives() {
        return lives.get();
    }

    /**
     * Gets the multiplier value
     * 
     * @return multiplier
     */
    public int getMultiplier() {
        return multiplier.get();
    }

    /**
     * Sets the TimerChangeListener
     * 
     * @param t Timer Change Listener
     */
    public void setOnTimerChange(TimerChangeListener t) {
        this.tel = t;
    }

    /**
     * Gets the score property
     * 
     * @return score property
     */
    public IntegerProperty getScoreProperty() {
        return score;
    }

    /**
     * Gets the high score property
     * 
     * @return high score property
     */
    public IntegerProperty getHScoreProperty() {
        return highScore;
    }

    /**
     * Sets the score property
     * 
     * @param high score
     */
    public void setHighScore(int h) {
        highScore.set(h);
    }

    /**
     * Gets the lives property
     * 
     * @return lives property
     */
    public IntegerProperty getLivesProperty() {
        return lives;
    }

    /**
     * Gets the level property
     * 
     * @return level property
     */
    public IntegerProperty getLevelProperty() {
        return level;
    }

    /**
     * Gets the multiplier property
     * 
     * @return multiplier property
     */
    public IntegerProperty getMultiplierProperty() {
        return multiplier;
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        resetGameLoop();
        currentPiece = spawnPiece();
        this.followingPiece = spawnPiece();
        ppl.nextPiece(currentPiece, followingPiece);

    }

    /**
     * Punish the player (timer has reached 0)
     */
    public void punish() {

        // Reset multiplier
        multiplier.set(1);

        // Reduce lives if player not dead
        if (lives.get() > 0) {
            lives.set(lives.get() - 1);
        } else {
            // else end game
            end();
        }

    }

    /**
     * Cleans up everything once game has ended and pings listener
     */
    public void end() {
        logger.info("Game Ended");
        time.removeListener(changeListener);
        if (gel != null) {
            gel.endGame();
        }

    }

    public void removeChangeListener() {
        time.removeListener(changeListener);
    }

    /**
     * Sets the Game end listener
     * 
     * @param g game end listener
     */
    public void setOnGameEnd(GameEndListener g) {
        this.gel = g;
    }

    /**
     * Swaps the current piece and the following piece
     */
    public void swapCurrentPiece() {
        logger.info("Pieces swapped");
        if (currentPiece == null) {
            return;
        }
        if (followingPiece == null) {
            return;
        }

        // Store the following piece temporarily
        GamePiece temp = followingPiece;
        // Set following piece to current piece
        followingPiece = currentPiece;
        // Set current piece to old following piece
        currentPiece = temp;
        // Triggers next piece and swap events
        ppl.nextPiece(currentPiece, followingPiece);
        ppl.swapPiece();
    }

    /**
     * Gets the next piece
     */
    public void nextPiece() {
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        ppl.nextPiece(currentPiece, followingPiece);
    }

    /**
     * Spawns a new piece
     */
    public GamePiece spawnPiece() {
        logger.info("Spawning a piece");
        Random rn = new Random();

        //Generates a random number used to create a random piece
        int number = rn.nextInt(15);
        GamePiece piece = GamePiece.createPiece(number);
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

        // If a piece can be played then play it
        if (grid.canPlayPiece(currentPiece, x, y)) {
            grid.playPiece(currentPiece, x, y);

            afterPiece();
            //Gets next piece
            nextPiece();

            //Notifys of play piece event
            ppl.playPiece();

            //Resets game loop
            resetGameLoop();

        }

    }

    /**
     * Speeds up the game timer by a small factor (recommended to be bound to
     * keyhold)
     */
    public void speedUp() {
        if (timer != null) {
            //Checks that speed is below speed limit
            if (speed < 6) {
                //Increments speed
                speed = speed + 0.5;
            }
            //Sets new speed as the rate of the game timer
            timer.setRate(speed);
        }
    }

    /**
     * Gets the current piece
     * 
     * @return current piece
     */
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }

    /**
     * Resets the speed of the game timer
     */
    public void resetSpeed() {
        timer.setRate(1);
        speed = 1;
    }

    /**
     * Runs after a piece has been played to check for lines
     */
    public void afterPiece() {
        logger.info("Checking for lines");

        //Creates two empty array lists of lines and columns to clear
        ArrayList<Integer> rowsToClear = new ArrayList<Integer>();
        ArrayList<Integer> colsToClear = new ArrayList<Integer>();

        // Checks for full rows, adds them to array list
        for (int i = 0; i < grid.getRows(); i++) { // Loops through each row
            if (fullRow(i)) { // Checks if specific row is clear
                logger.info("Row found - clearing");
                rowsToClear.add(i);
            }
        }

        // Checks for full columns, adds them to array list
        for (int i = 0; i < grid.getCols(); i++) {
            if (fullColumn(i)) {
                logger.info("Line found - clearing");
                colsToClear.add(i);
            }
        }

        //Makes a new Set of GameBlockCoordinates such that each coordinate
        //can only be added a single time and will be cleared only once
        Set<GameBlockCoordinate> c = new HashSet<>();

        //Adds all the row coordinates to the set
        for (int i : rowsToClear) {
            c.addAll(setRowZero(i));
        }
        //Adds all the column coordinates to the set
        for (int i : colsToClear) {
            c.addAll(setColZero(i));
        }

        //Works out the total number of lines cleared
        int numLines = rowsToClear.size() + colsToClear.size();

        // Calculates number of blocks cleared by finding the total sum and subtracting the overlap
        int blocksCleared = 5*(rowsToClear.size() + colsToClear.size())
                - (rowsToClear.size() * colsToClear.size());

        //If no lines were made the multiplier is reset
        if (numLines == 0) {
            multiplier.set(1);
            return;
        }

        // Sets the score to the new score
        setScore(score.get() + score(numLines, blocksCleared));

        level.set((int) this.score.get() / 1000); // Sets level
        multiplier.set(multiplier.get() + 1); // Increases multiplier

        //Pings line cleared listener with the set of coordinates cleared
        lcl.linesCleared(c);

    }

    /**
     * Sets the score
     * 
     * @param newScore the new score
     */
    public void setScore(int newScore) {

        //Replaces current high score if beaten
        if (newScore > highScore.get()) {
            highScore.set(newScore);
        }
        score.set(newScore);
    }

    /**
     * Sets the line cleared listener
     * 
     * @param l line cleared listener
     */
    public void setOnLineCleared(LineClearedListener l) {
        this.lcl = l;
    }

    /**
     * Calculates the increase in score based on lines cleared and blocks cleared
     * 
     * @param lines  number of lines cleared
     * @param blocks number of blocks cleared
     * @return amount to increment players score
     */
    public int score(int lines, int blocks) {
        return lines * blocks * 10 * getMultiplier();
    }

    /**
     * Clears a row of the grid
     * 
     * @param row row index to clear
     * @return arraylist of coordinates cleared
     */
    public ArrayList<GameBlockCoordinate> setRowZero(int row) {
        var coords = new ArrayList<GameBlockCoordinate>();
        // Set's each item in a row to 0
        for (int i = 0; i < grid.getCols(); i++) {
            grid.set(row, i, 0);
            coords.add(new GameBlockCoordinate(row, i));
        }
        return coords;
    }

    /**
     * Clears a column of the grid
     * 
     * @param col column index to clear
     * @return arraylist of coordinates cleared
     */
    public ArrayList<GameBlockCoordinate> setColZero(int col) {
        var coords = new ArrayList<GameBlockCoordinate>();
        // Set's each item in a column to 0
        for (int i = 0; i < grid.getRows(); i++) {
            grid.set(i, col, 0);
            coords.add(new GameBlockCoordinate(i, col));
        }
        return coords;
    }

    /**
     * Detects if a given column is full of blocks
     * 
     * @param col column index to check
     * @return if column is full
     */
    public boolean fullColumn(int col) {
        // Checks if a specific column is full
        for (int i = 0; i < grid.getCols(); i++) {
            if (grid.get(i, col) == 0) {
                // Return false if any of the blocks are 0
                return false;
            }
        }
        return true;
    }

    /**
     * Detects if a given row is full of blocks
     * 
     * @param row row index to check
     * @return if row is full
     */
    public boolean fullRow(int row) {
        // Checks if a specific row is full
        for (int i = 0; i < grid.getRows(); i++) {
            if (grid.get(row, i) == 0) {
                // Return false if any of the blocks are 0
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
