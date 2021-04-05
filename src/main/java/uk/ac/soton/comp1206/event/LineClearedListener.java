package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

public interface LineClearedListener{
    public void linesCleared(GameBlockCoordinate[] x);
}
