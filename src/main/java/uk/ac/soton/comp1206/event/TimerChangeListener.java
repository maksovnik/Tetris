package uk.ac.soton.comp1206.event;

/**
 * The Timer Event Listener is used to notify the GUI of
 * the current status of the Game loop.
 */
public interface TimerChangeListener {
    /**
     * Handle the event that the timer has changed.
     * @param delay the current delay on the game loop between 0 and 1 where 1 is full and 0 implies a life lost.
     */
    public void onTimerChange(double delay);
}
