package uk.ac.soton.comp1206.component;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.ClickListener;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 *
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 *
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    private final int cols;

    /**
     * Number of rows in the board
     */
    private final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    private final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    private final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;
    private ClickListener ClickListener;
    private MouseButton rotateKey;

    private int[] currentHoverCords = {0,0};


    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;

        //Build the GameBoard
        build();
    }

    public GameBlock getCurrentHoverPiece(){
        int cx = currentHoverCords[0];
        int cy = currentHoverCords[1];
        return blocks[cx][cy];
    }

    /**
     * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols number of columns for internal grid
     * @param rows number of rows for internal grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols,rows);

        //Build the GameBoard
        build();
    }

    /**
     * Get a specific block from the GameBoard, specified by it's row and column
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}",rows,cols);

        setMaxWidth(width);
        setMaxHeight(height);

        setGridLinesVisible(true);

        blocks = new GameBlock[rows][cols];

        for(var x = 0; x < rows; x++) {
            for (var y = 0; y < cols; y++) {
                GameBlock b = createBlock(x,y);
                b.setOnMouseEntered(e -> this.hover(b));
                b.setOnMouseExited(e -> this.unhover());
            }
        }

        setOnMouseClicked(e -> Click(e));

    }

    public void moveHover(String direction){
        int cx = currentHoverCords[0];
        int cy = currentHoverCords[1];
        unhover();
        try{
            if(direction=="Left"){
                hover(blocks[cx][cy-1]);
            }
            if(direction=="Right"){
                hover(blocks[cx][cy+1]);
            }
            if(direction=="Up"){
                hover(blocks[cx-1][cy]);
            }
            if(direction=="Down"){
                hover(blocks[cx+1][cy]);
            }
        }
        catch(ArrayIndexOutOfBoundsException ignored){
            hover(blocks[cx][cy]);
        }
    }
    private void hover(GameBlock b){
        unhover();
        b.setHoverX(true);
        currentHoverCords[0] = b.getX();
        currentHoverCords[1] = b.getY();
    }
    
    private void unhover(){
        int cx = currentHoverCords[0];
        int cy = currentHoverCords[1];
        blocks[cx][cy].setHoverX(false);
        currentHoverCords[0] = 0;
        currentHoverCords[1] = 0;
    }
    private void Click(MouseEvent e) {
        ClickListener.Click(e.getButton());
    }

    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Add to the GridPane
        add(block,y,x);

        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x,y));

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) -> blockClicked(e, block)); //stops working

        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    public void setOnClick(ClickListener listener) {
        this.ClickListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     * @param event mouse event
     * @param block block clicked on
     */
    private void blockClicked(MouseEvent event, GameBlock block) {
        if(event.getButton()==MouseButton.PRIMARY){
            logger.info("Block clicked: {}", block);
            if(blockClickedListener != null) {
                blockClickedListener.blockClicked(block);
            }
        }
    }
}
