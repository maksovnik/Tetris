package uk.ac.soton.comp1206.event;

import javafx.scene.input.MouseButton;

/**
 * The Block Clicked listener is used to handle the event when a block in a GameBoard is clicked. It passes the
 * GameBlock that was clicked in the message
 */
public interface ClickListener{
    public void Click(MouseButton m);
}