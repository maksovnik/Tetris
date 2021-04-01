package uk.ac.soton.comp1206.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Multimedia{

    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    private static boolean audioEnabled;
    private static MediaPlayer mediaPlayer;
    private static MediaPlayer backgroundPlayer;


    public static void startBackgroundMusic(String file) {

        if(audioEnabled){
            backgroundPlayer.stop();
            audioEnabled=false;
        }

        String toPlay = Multimedia.class.getResource(file).toExternalForm();
        Media play = new Media(toPlay);
        backgroundPlayer = new MediaPlayer(play);
        backgroundPlayer.setOnEndOfMedia(() -> startBackgroundMusic(file));
        backgroundPlayer.play();
        backgroundPlayer.setVolume(0.3);
        audioEnabled = true;
        
    }

    public static void playAudio(final String file) {

        String toPlay = Multimedia.class.getResource(file).toExternalForm();
        Media play = new Media(toPlay);
        mediaPlayer = new MediaPlayer(play);
        mediaPlayer.setVolume(0.25);
        mediaPlayer.play();

    }
    
}