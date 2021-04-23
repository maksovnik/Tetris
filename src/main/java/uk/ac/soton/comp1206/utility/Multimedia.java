package uk.ac.soton.comp1206.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Multimedia {

    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    private static boolean audioEnabled;
    private static MediaPlayer mediaPlayer;
    private static MediaPlayer backgroundPlayer;

    static String file;

    private static boolean fadeIn = true;

    private static DoubleProperty effectVolume = new SimpleDoubleProperty(0.5);
    private static DoubleProperty backgroundVolume = new SimpleDoubleProperty(0.5);


    public static void startBackgroundMusic(String file) {

        if ((Multimedia.file == file) && fadeIn) {
            return;
        }

        if (audioEnabled) {
            backgroundPlayer.stop();
            audioEnabled = false;
        }

        Multimedia.file = file;

        String toPlay = Multimedia.class.getResource(file).toExternalForm();
        logger.info("Playing Music: " + toPlay);
        Media play = new Media(toPlay);
        backgroundPlayer = new MediaPlayer(play);
        backgroundPlayer.setOnEndOfMedia(() -> loopBackground(file));

        backgroundPlayer.volumeProperty().bind(backgroundVolume);

        // if (fadeIn) {
        //     //backgroundPlayer.setVolume(0);
        //     Timeline timeline = new Timeline(
        //             new KeyFrame(Duration.seconds(4), new KeyValue(backgroundPlayer.volumeProperty(), 0.2)));
        //     timeline.play();
        // }

        backgroundPlayer.play();

        audioEnabled = true;
    }

    public static DoubleProperty getMusicVolumeProperty(){
        return backgroundVolume;
    }

    public static DoubleProperty getFXVolumeProperty(){
        return effectVolume;
    }
    public static void loopBackground(String file) {
        fadeIn = false;
        startBackgroundMusic(file);
        fadeIn = true;
    }

    public static void playAudio(final String file) {

        String toPlay = Multimedia.class.getResource(file).toExternalForm();
        logger.info("Playing Sound: " + toPlay);
        Media play = new Media(toPlay);
        mediaPlayer = new MediaPlayer(play);
        mediaPlayer.volumeProperty().bind(effectVolume);
        mediaPlayer.play();
    }

    public static void pause(){
        backgroundPlayer.pause();
    }

    public static void play(){
        backgroundPlayer.play();
    }

}