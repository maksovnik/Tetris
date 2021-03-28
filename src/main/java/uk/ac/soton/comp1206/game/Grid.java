package uk.ac.soton.comp1206.game;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     * @param cols number of columns
     * @param rows number of rows
     */

    public boolean canPlayPiece(GamePiece z, int x, int y){
        
        logger.info("Checking if piece can be played");
        int[][] blocks = z.getBlocks();

        for(int i=-1;i<2;i++){
            for(int j=-1;j<2;j++){
                if(get(x+i,y+j)!=0 && blocks[i+1][j+1]>0){
                    return false;
                }
            }
        }
        return true;

    }

    public void playPiece(GamePiece z, int x, int y){
        
        logger.info("Playing a piece");
        int[][] blocks = z.getBlocks();

        logger.info(Arrays.deepToString(blocks));


        for(int i=-1;i<2;i++){
            for(int j=-1;j<2;j++){
                if(blocks[i+1][j+1]>0){
                    set(x+i,y+j,z.getValue());
                    System.out.println("This is "+(x+i)+" "+(y+j));
                }
            }
        }
        
        
    }
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[rows][cols];

        //Add a SimpleIntegerProperty to every block in the grid
        for(var x = 0; x < rows; x++) {
            for(var y = 0; y < cols; y++) {
             
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }

    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
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
    
    public SimpleIntegerProperty[][] getGrid(){
        return grid;
    }

}
