package uk.ac.soton.comp1206.scene;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class InstructionsScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
    private GridPane gridpane;

    public InstructionsScene(GameWindow gameWindow) {

        super(gameWindow);
        logger.info("Creating Instructions Scene");
    }

    @Override
    public void initialise() {
    }

    /**
     * Builds the Instructions scene
     */
    @Override
    public void build() {

        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var mainPane = new BorderPane();
        var instructionsPane = new StackPane();
        Text title = new Text("Instructions");
        Text title2 = new Text("Game Pieces");
        gridpane = new GridPane();
        var elements = new VBox();

        final ImageView image = new ImageView(
                MenuScene.class.getResource("/images/Instructions2.png").toExternalForm());

        instructionsPane.setMaxWidth(gameWindow.getWidth());
        instructionsPane.setMaxHeight(gameWindow.getHeight());
        instructionsPane.getStyleClass().add("menu-background");

        image.setFitWidth(this.gameWindow.getHeight());
        image.setPreserveRatio(true);

        generatePieces();

        title.getStyleClass().add("instructions");
        title2.getStyleClass().add("instructions");
        elements.setAlignment(Pos.CENTER);
        gridpane.setAlignment(Pos.CENTER);

        Platform.runLater(() -> scene.setOnKeyPressed(e -> handleKeyPress(e)));

        gridpane.setHgap(6);
        gridpane.setVgap(6);
        root.getChildren().add(instructionsPane);
        instructionsPane.getChildren().add(mainPane);
        elements.getChildren().addAll(title, image, title2, gridpane);
        mainPane.setCenter(elements);

    }

    /**
     * Generates the pieces to display on instructions
     */
    private void generatePieces() {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                PieceBoard p = new PieceBoard(3, 3, 50, 50, false);
                var b = GamePiece.createPiece(count);
                p.displayPiece(b);
                gridpane.add(p, j, i);
                count++;
            }

        }
    }

    /**
     * Handles Key press in Instructions scene
     */
    private void handleKeyPress(KeyEvent e) {
        KeyCode k = e.getCode();
        if (k == KeyCode.ESCAPE) {
            gameWindow.startMenu();
        }
    }
}
