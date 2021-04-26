package uk.ac.soton.comp1206.event;


/**
 * The Game end listener is used to handle the event 
 * that the game ends.
 */
public interface GameEndListener {
    /**
     * Handle a game end event
     */
    public void endGame();
}