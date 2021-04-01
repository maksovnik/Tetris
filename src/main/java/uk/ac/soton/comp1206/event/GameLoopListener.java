package uk.ac.soton.comp1206.event;

/**
 * The Block Clicked listener is used to handle the event when a block in a GameBoard is clicked. It passes the
 * GameBlock that was clicked in the message
 */
public interface GameLoopListener {

    public void timerEnd(int delay);
}
