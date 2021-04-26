package uk.ac.soton.comp1206.game;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The MultiplayerGame class handles the main multiplayer game logic, state and
 * properties of the game. Methods to manipulate the game state and to handle
 * actions take place within this class.
 */

public class MultiplayerGame extends Game {

    LinkedBlockingQueue<Integer> pieceQueue = new LinkedBlockingQueue<Integer>();
    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);

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
     * @return the new piece
     */
    @Override
    public GamePiece spawnPiece() {
        int val;
        try {
            val = pieceQueue.take();
            var piece = GamePiece.createPiece(val);
            return piece;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }
}
