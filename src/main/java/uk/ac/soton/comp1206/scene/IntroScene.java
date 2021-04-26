package uk.ac.soton.comp1206.scene;

import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.utility.Utility;

/**
 * The IntroScene displays the games logo and proceeds to open the Menu and intr
 */
public class IntroScene extends BaseScene {

    /**
     * Creates a new Intro Scene
     */
    public IntroScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * Initialise this scene
     */
    @Override
    public void initialise() {
        //If any key is pressed start the menu and stop animation
        this.scene.setOnKeyPressed(e -> {

            gameWindow.startMenu();
            //Stop intro fade animation
            Utility.fader.stop();

        });
    }

    /**
     * Build the layout of this scene
     */
    @Override
    public void build() {

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var n = new BorderPane();
        final ImageView image = new ImageView(GameWindow.class.getResource("/images/ECSGames.png").toExternalForm());
        image.setFitWidth(gameWindow.getHeight());
        image.setPreserveRatio(true);
        n.setCenter(image);
        root.getChildren().addAll(n);
        image.setOpacity(0);

        // Reveal the intro image over 4 seconds
        Utility.reveal(image, 4000);
        // When done, start the menu
        Utility.fader.setOnFinished(e -> gameWindow.startMenu());
        // Play intro sound effect
        Multimedia.playSoundEffect("/sounds/intro.mp3");

    }

}
