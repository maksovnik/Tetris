package uk.ac.soton.comp1206.game;

import java.util.concurrent.LinkedBlockingQueue;

import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The MultiplayerGame class handles the main multiplayer game logic, state and
 * properties of the game. Methods to manipulate the game state and to handle
 * actions take place within this class.
 */

public class MultiplayerGame extends Game {

    LinkedBlockingQueue<Integer> pieceQueue = new LinkedBlockingQueue<Integer>();

    /**
     * Creates a Multiplayer game
     */
    public MultiplayerGame(int cols, int rows, GameWindow g) {
        super(cols, rows);
    }

    /**
     * Adds a new piece to the queue
     * 
     * @param c piece id number
     */
    public void addToQueue(String c) {
        try {
            // Puts a new piece into the piece queue
            pieceQueue.put(Integer.parseInt(c));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the size of the queue
     * 
     * @return size of queue
     */
    public int getQueueSize() {
        return pieceQueue.size();
    }

    /**
     * Spawns a new piece
     * 
     * @return the new piece
     */
    @Override
    public GamePiece spawnPiece() {
        try {
            // Take a piece from the queue
            int val = pieceQueue.take();
            // Create a game piece from it
            var piece = GamePiece.createPiece(val);
            return piece;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }
}
