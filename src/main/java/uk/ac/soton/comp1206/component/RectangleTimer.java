package uk.ac.soton.comp1206.component;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class RectangleTimer extends Rectangle{
    int initialWidth;

    public RectangleTimer(int x, int y){
        //super(0, 0, 600, 40);
        super(x,y);

        this.initialWidth = x;
        setFill(Color.GREEN);
    }

    public void shrink(int delay){
        setWidth(initialWidth);
        KeyValue widthValue = new KeyValue(widthProperty(), 0);
        KeyFrame frame = new KeyFrame(Duration.millis(delay), widthValue);

        KeyValue greenValue= new KeyValue(fillProperty(), Color.GREEN);
        KeyFrame frame1 = new KeyFrame(Duration.ZERO, greenValue);

        KeyValue yellowValue= new KeyValue(fillProperty(), Color.YELLOW);
        KeyFrame frame2 = new KeyFrame(new Duration(delay*0.5), yellowValue);
    
        KeyValue redValue= new KeyValue(fillProperty(), Color.RED);
        KeyFrame frame3 = new KeyFrame(new Duration(delay*0.75), redValue);

        Timeline timeline = new Timeline(frame,frame1,frame2,frame3);
        timeline.play();
    }


}
