package uk.ac.soton.comp1206.game;

import java.util.Random;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.scene.Multimedia;


/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    GamePiece currentPiece;
    
    private static final Logger logger = LogManager.getLogger(Game.class);

    private SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty level = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty lives = new SimpleIntegerProperty(3);
    private SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);
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

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */

    public int getScore(){
        return score.get();
    }

    public int getLevel(){
        return level.get();
    }
    public int getLives(){
        return lives.get();
    }
    public int getMultiplier(){
        return multiplier.get();
    }
    // Define a getter for the property itself
    public IntegerProperty getScoreProperty() {
        return score;
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

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);

        Multimedia.playSound("music/game.wav");
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        currentPiece = spawnPiece();
    }

    public void nextPiece(){
        currentPiece = spawnPiece();
    }

    public GamePiece spawnPiece(){
        logger.info("Spawning a piece");
        Random rn = new Random();
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
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        //Get the new value for this block
        int previousValue = grid.get(x,y);
        int newValue = previousValue + 1;
        if (newValue  > GamePiece.PIECES) {
            newValue = 0;
        }

        //Update the grid with the new value
        grid.set(x,y,newValue);
        afterPiece();
        nextPiece();
    }

    public void afterPiece(){
        logger.info("Checking for lines");
        int first= 0; //Sets the number of cleared rows to 0
        int second= 0; //Sets the number of cleared lines to 0

        for(int i=0;i<grid.getRows();i++){ //Loops through each row
            if(fullRow(i)){ //Checks if specific row is clear
                logger.info("Row found - clearing");
                setRowZero(i); //Clears that row
                first++;
            }
        }

        for(int i=0;i<grid.getCols();i++){
            if(fullColumn(i)){
                logger.info("Line found - clearing");
                setColZero(i);
                second++;
            }
        }

        int numLines = first+second;
        int blocksCleared = (5*first)+(5*second)-(first*second); //Calculates number of blocks cleared
        
        if(numLines == 0){
            multiplier.set(1);
            return;
        }
        this.score.set(getScore()+score(numLines,blocksCleared)); //Increases score
        
        level.set((int)this.score.get()/1000); //Sets level
        multiplier.set(multiplier.get()+1); //Increases multiplier
        
    }

    public int score(int lines, int blocks){
        return lines*blocks*10*getMultiplier();
    }
    public void setRowZero(int row){
        // Set's each item in a row to 0
        for(int i=0;i<grid.getCols();i++){
            grid.set(row, i, 0);
        } 
    }

    public void setColZero(int col){
        // Set's each item in a column to 0
        for(int i=0;i<grid.getRows();i++){
            grid.set(i, col, 0);
        } 
    }

    public boolean fullColumn(int col){
        //Checks if a specific column is full
        for(int i =0;i < grid.getCols();i++){
            if(grid.get(i,col) == 0){
                return false;
            }
        }
        return true;
    }

    public boolean fullRow(int row){
        //Checks if a specific row is full
        for(int i =0;i < grid.getRows();i++){
            if(grid.get(row,i) == 0){
                return false;
            }
        }
        return true;
    }
    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }


}
