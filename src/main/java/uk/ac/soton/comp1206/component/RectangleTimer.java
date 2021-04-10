package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import uk.ac.soton.comp1206.event.TimerFinishedListener;

public class RectangleTimer extends Rectangle {
    int initialWidth;
    int speed = 4;

    private static final Logger logger = LogManager.getLogger(RectangleTimer.class);
    Timeline timeline;

    double speedMult = 1;
    TimerFinishedListener gcl;

    public RectangleTimer(int x, int y) {
        // super(0, 0, 600, 40);
        super(x, y);

        this.initialWidth = x;
        setFill(Color.GREEN);
    }

    public int fastSpeed() {
        if (timeline != null) {
            timeline.setRate(speed * speedMult);
            if (speedMult < 2) {
                speedMult = speedMult + 0.2;
            }
            logger.info("Speedmult is: '{}'", speed * speedMult);
        }
        return 0;
    }

    public void pauseAnimation(){
        if(timeline!=null){
            timeline.pause();
        }
        
    }

    public void playAnimation(){
        if(timeline!=null){
            timeline.play();
        }
        
    }
    
    public void resetSpeed() {
        speed = 4;
        speedMult = 1;
        timeline.setRate(1);
    }

    public void stopAnimation() {
        timeline.stop();
    }

    public void shrink(int delay) {

        if (timeline != null) {
            timeline.stop();
        }

        setWidth(initialWidth);
        KeyValue widthValue = new KeyValue(widthProperty(), 0);
        KeyFrame frame = new KeyFrame(Duration.millis(delay), widthValue);

        KeyValue greenValue = new KeyValue(fillProperty(), Color.GREEN);
        KeyFrame frame1 = new KeyFrame(Duration.ZERO, greenValue);

        KeyValue yellowValue = new KeyValue(fillProperty(), Color.YELLOW);
        KeyFrame frame2 = new KeyFrame(new Duration(delay * 0.5), yellowValue);

        KeyValue redValue = new KeyValue(fillProperty(), Color.RED);
        KeyFrame frame3 = new KeyFrame(new Duration(delay * 0.75), redValue);

        timeline = new Timeline(frame, frame1, frame2, frame3);
        timeline.play();
        timeline.setOnFinished(e -> gcl.setTimerToFull(0));
    }

    public void setOnAnimationEnd(TimerFinishedListener g) {
        this.gcl = g;
    }

}
