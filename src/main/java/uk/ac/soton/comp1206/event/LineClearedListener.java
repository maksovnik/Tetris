package uk.ac.soton.comp1206.event;

import java.util.Set;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * The Line cleared listener is used to handle the event that a line is cleared.
 * It takes a set of game block coordinates where block clearing has occured.
 */
public interface LineClearedListener {
    /**
     * Handle a line cleared event
     * 
     * @param c A set of cleared blocks
     */
    public void linesCleared(Set<GameBlockCoordinate> c);
}
