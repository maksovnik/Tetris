package uk.ac.soton.comp1206.scene;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Multimedia{
    private static MediaPlayer musicPlayer;
    private static boolean audioEnabled = true;
    private static final Logger logger = LogManager.getLogger(Multimedia.class);

    public static void playSound(String file) {
        String toPlay = Multimedia.class.getResource("/" + file).toExternalForm();

        logger.info("Playing audio: " + toPlay);

        try {
            
            Media play = new Media(toPlay);
            musicPlayer = new MediaPlayer(play);
            musicPlayer.play();

            musicPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                  musicPlayer.seek(Duration.ZERO);
                }
            });
        } catch (Exception e) {
            audioEnabled = false;
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }
    }

}