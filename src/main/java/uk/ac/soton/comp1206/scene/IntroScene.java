package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Utility;

public class IntroScene extends BaseScene {

    public IntroScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {
        this.scene.setOnKeyPressed(e -> {
            gameWindow.startMenu();
            Utility.fader.stop();

        });
    }

    @Override
    public void build() {

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        final ImageView image = new ImageView(GameWindow.class.getResource("/images/ECSGames.png").toExternalForm());
        image.setFitWidth(gameWindow.getHeight());
        image.setPreserveRatio(true);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(image);

        Utility.reveal(4000, image);
        Utility.fader.setOnFinished(e -> {
            gameWindow.startMenu();
        });

    }

}
