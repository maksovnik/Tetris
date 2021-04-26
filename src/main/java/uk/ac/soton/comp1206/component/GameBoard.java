package uk.ac.soton.comp1206.component;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A GameBoard is a visual component to represent the visual GameBoard. It
 * extends a GridPane to hold a grid of GameBlocks.
 *
 * The GameBoard can hold an internal grid of it's own, for example, for
 * displaying an upcoming block. It also be linked to an external grid, for the
 * main game board.
 *
 * The GameBoard is only a visual representation and should not contain game
 * logic or model logic in it, which should take place in the Grid.
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
     * Color used when not possible to place a piece
     */
    Color notPossibleToPlace = Color.color(0.5, 1.0, 1.0, 0.5);
    /**
     * Color used when possible to place a piece
     */
    Color possibleToPlace = Color.color(1.0, 0, 0, 0.5);

    /**
     * The listener to call when a specific block is clicked
     */
    protected BlockClickedListener blockClickedListener;

    /**
     * Tracks current coordinates of hovered block
     */
    private int[] currentHoverCords = { 0, 0 };

    /**
     * Tracks current piece about to be placed on the board
     */
    protected GamePiece currentPiece;

    /**
     * Create a new GameBoard, based off a given grid, with a visual width and
     * height.
     * 
     * @param grid   linked grid
     * @param width  the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;

        // Build the GameBoard
        build();
    }

    /**
     * Sets the current piece height.
     * 
     * @param g the current piece
     */
    public void setCurrentPiece(GamePiece g) {
        this.currentPiece = g;
    }

    /**
     * Gets the current position being hovered over
     * 
     * @return current position being hovered over
     */
    public int[] getCurrentCenterHoverCoords() {
        return currentHoverCords;
    }

    /**
     * Create a new GameBoard with it's own internal grid, specifying the number of
     * columns and rows, along with the visual width and height.
     *
     * @param cols   number of columns for internal grid
     * @param rows   number of rows for internal grid
     * @param width  the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols, rows);

        // Build the GameBoard
        build();
    }

    /**
     * Get a specific block from the GameBoard, specified by it's row and column
     * 
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Fades out a set of Game Blocks
     * 
     * @param x set of game blocks
     */
    public void fadeOut(Set<GameBlockCoordinate> x) {
        for (GameBlockCoordinate i : x) {
            getBlock(i.getX(), i.getY()).fadeOut();
        }
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}", rows, cols);

        setMaxWidth(width);
        setMaxHeight(height);

        setGridLinesVisible(true);

        blocks = new GameBlock[rows][cols];

        for (var x = 0; x < rows; x++) {
            for (var y = 0; y < cols; y++) {
                GameBlock b = createBlock(x, y);

                final var c = x;
                final var d = y;

                //Sets hover to occur when mouse enters it
                b.setOnMouseEntered(e -> hover(c, d));

                //Clears hovered blocks when mouse exits
                b.setOnMouseExited(e -> clearHover());
            }
        }
    }

    /**
     * Gets the Game Blocks inside the board
     * 
     * @return game blocks inside board
     */
    public GameBlock[][] getBlocks() {
        return blocks;
    }

    /**
     * Removes the hover effect from every block
     */
    public void clearHover() {
        for (GameBlock[] q : blocks) {
            for (GameBlock l : q) {
                l.setHoverX(false);
            }
        }
    }

    /**
     * Creates a new Game Block at the given coordinate
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the new game block
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        // Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        // Add to the GridPane
        add(block, y, x);

        // Add to our block directory
        blocks[x][y] = block;

        // Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x, y));

        // Add a mouse click handler to the block to trigger GameBoard blockClicked
        // method
        block.setOnMouseClicked(e -> {
            if (blockClickedListener != null) {
                blockClickedListener.blockClicked(e, block);
            }
        });

        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     * 
     * @param listener listener to add
     */
    public void setOnBlockClicked(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Calls an update to hover() to reposition the hover indicator
     */
    public void updateHover() {
        var coords = getCurrentCenterHoverCoords();
        hover(coords[0], coords[1]);
    }

    /**
     * Lights up the piece at the given coordinates
     * 
     * @param x the x coordinate wanting to be hovered
     * @param y the y coordinate wanting to be hovered
     */
    public void hover(int x, int y) {

        if (currentPiece == null) {
            //If there is no current piece then we can not hover
            return;
        }

        //Get the current pieces blocks and find if it can be played at the current hover coordinates
        var pieceBlocks = currentPiece.getBlocks();
        var canPlayPiece = grid.canPlayPiece(currentPiece, x, y);

        //Remove hover effect from all game blocks
        clearHover();


        //Assign two different colours based on if the current piece can be played or not
        if (canPlayPiece) {
            GameBlock.setHoverColor(notPossibleToPlace);
        } else {
            GameBlock.setHoverColor(possibleToPlace);
        }

        //Loop through the pieceBlocks array but starting at -1
        // so that relative piece position can be calculated
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                //If the block of that piece exists at the current (i+1,j+1) coordinate 
                if (pieceBlocks[i + 1][j + 1] > 0) {
                    //If it is is in the bounds of the board
                    if ((x + i >= 0) && (x + i < rows) && (y + j >= 0) && (y + j < cols)) {
                        getBlock(x + i, y + j).setHoverX(true);
                    }

                }
            }
        }

        // Keeps track of the current center hovered piece
        currentHoverCords[0] = x;
        currentHoverCords[1] = y;
    }
}
