package uk.ac.soton.comp1206.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * The Multimedia class deals with all sounds in the app.
 */
public class Multimedia {

    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    private static boolean audioEnabled;
    private static MediaPlayer effectsPlayer;
    private static MediaPlayer musicPlayer;

    static String file;

    private static boolean fadeIn = true;

    private static DoubleProperty effectVolume = new SimpleDoubleProperty(0.5);
    private static DoubleProperty backgroundVolume = new SimpleDoubleProperty(0.5);

    /**
     * Stops the sound effects player
     */
    public static void stopEffects() {
        effectsPlayer.stop();
    }

    /**
     * Starts playing the music at the given path
     * 
     * @param file The path to the file that should be played
     */
    public static void startBackgroundMusic(String file) {
        if ((Multimedia.file == file) && fadeIn) {
            return;
        }

        if (audioEnabled) {
            musicPlayer.stop();
            audioEnabled = false;
        }

        Multimedia.file = file;

        String toPlay = Multimedia.class.getResource(file).toExternalForm();
        logger.info("Playing Music: " + toPlay);
        Media play = new Media(toPlay);
        musicPlayer = new MediaPlayer(play);
        musicPlayer.setOnEndOfMedia(() -> loopBackground(file));

        musicPlayer.volumeProperty().bind(backgroundVolume);

        musicPlayer.play();

        audioEnabled = true;
    }

    /**
     * Gets the music volume property
     * 
     * @return music volume property
     */
    public static DoubleProperty getMusicVolumeProperty() {
        return backgroundVolume;
    }

    /**
     * Gets the sound effects volume property
     * 
     * @return sound effects volume property
     */
    public static DoubleProperty getFXVolumeProperty() {
        return effectVolume;
    }

    /**
     * Will temporarily disable music fading so that looping can occur and then play
     * the given music file
     * 
     * @param file path to the file that will be played
     */
    public static void loopBackground(String file) {
        fadeIn = false;
        startBackgroundMusic(file);
        fadeIn = true;
    }

    /**
     * Plays the sound effect at the given path
     * 
     * @param file The path to the file that should be played
     */
    public static void playSoundEffect(final String file) {
        String toPlay = Multimedia.class.getResource(file).toExternalForm();
        logger.info("Playing Sound: " + toPlay);
        Media play = new Media(toPlay);
        effectsPlayer = new MediaPlayer(play);
        effectsPlayer.volumeProperty().bind(effectVolume);
        effectsPlayer.play();
    }

    /**
     * Pauses the music
     */
    public static void pause() {
        musicPlayer.pause();
    }

    /**
     * Plays the music
     */
    public static void play() {
        musicPlayer.play();
    }

}