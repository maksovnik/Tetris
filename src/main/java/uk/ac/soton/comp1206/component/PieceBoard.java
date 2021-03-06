package uk.ac.soton.comp1206.component;

import javafx.scene.paint.Color;
import uk.ac.soton.comp1206.game.GamePiece;

/**
 * PieceBoard is a visual component to represent a single piece. It
 * extends a GameBoard.
 */
public class PieceBoard extends GameBoard {
    private boolean doCircle;

    public PieceBoard(int cols, int rows, double width, double height, boolean doCircle) {
        super(cols, rows, width, height);
        this.doCircle = doCircle;
    }

    /**
     * Sets the Piece to display on the board
     * 
     * @param g the piece to display
     */
    public void displayPiece(GamePiece g) {
        if (g == null) {
            return;
        }

        //Sets the grid to be equal to the blocks
        int[][] blocks = g.getBlocks();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid.set(i, j, blocks[i][j]);
            }
        }

        //If a circle indicator should be drawn then draw it in the center block
        if (doCircle) {
            GameBlock x = getBlock(1, 1);
            x.setShowCenter(true);
            x.paint();
        }

    }

    /**
     * "Hovers" the piece at the given coordinates
     * 
     * @param x the x coordinate wanting to be hovered
     * @param y the y coordinate wanting to be hovered
     */
    @Override
    public void hover(int x, int y) {
        GameBlock.setHoverColor(Color.color(0.0, 0.0, 0.0, 0.5));
        getBlock(x, y).setHoverX(true);
    }

}