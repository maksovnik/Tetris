package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

public interface pieceEventListener {
    public void playPiece();
    public void rotatePiece();
    public void swapPiece();
    public void nextPiece(GamePiece piece, GamePiece followingP);
}
