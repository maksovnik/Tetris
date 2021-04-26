package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Piece event listener is used to handle piece events
 */
public interface pieceEventListener {
    /**
     * Handle a event that a piece is played
     */
    public void playPiece();
    /**
     * Handle a event that a piece is rotated
     */
    public void rotatePiece();
        /**
     * Handle a event that the next piece and following piece are swapped
     */
    public void swapPiece();
    /**
     * Handle a event that the next piece is created
     * @param piece the next piece
     * @param followingP the following piece
     */
    public void nextPiece(GamePiece piece, GamePiece followingP);
}
