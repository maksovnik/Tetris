package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
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
        this.scene.setOnKeyPressed(e -> {

            gameWindow.startMenu();
            Utility.fader.stop();

        });
    }

    /**
     * Build the layout of this scene
     */
    @Override
    public void build() {

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        final ImageView image = new ImageView(GameWindow.class.getResource("/images/ECSGames.png").toExternalForm());
        image.setFitWidth(gameWindow.getHeight());
        image.setPreserveRatio(true);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(image);
        image.setOpacity(0);
        Utility.reveal(image, 4000);
        Utility.fader.setOnFinished(e -> {
            gameWindow.startMenu();
        });
        Multimedia.playSoundEffect("/sounds/intro.mp3");

    }

}
