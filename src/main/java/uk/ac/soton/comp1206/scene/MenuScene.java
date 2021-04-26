package uk.ac.soton.comp1206.scene;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.soton.comp1206.component.Settings;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.utility.Utility;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    Settings settings;
    private Text error;
    private Text single;
    private Text multi;
    private Text how;
    private Text exit;
    private ImageView image;

    /**
     * Create a new menu scene
     * 
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");

        settings = gameWindow.getSettings();
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());


        //Ends any sound effects playing
        Multimedia.stopEffects();
        //Starts the menu music
        Multimedia.startBackgroundMusic("/music/menu.mp3");

        var menuPane = new StackPane();

        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        VBox menuItems = new VBox(5);
        menuItems.setAlignment(Pos.CENTER);
        single = new Text("Single Player");
        multi = new Text("Multi Player");
        how = new Text("How to Play");
        exit = new Text("Exit");
        error = new Text("No connection to server");

        error.setOpacity(0);
        for (Text i : new Text[] { single, multi, how, exit }) {
            i.getStyleClass().add("menuItem");
        }
        error.getStyleClass().add("error");
        menuItems.setStyle("-fx-padding: 0 0 40 0;-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,1.5), 50, 0, 0, 0);");
        menuItems.setOpacity(0);
        menuItems.getChildren().addAll(single, multi, how, exit, error);

        //Reveals the menu items over a duration of 3 seconds
        Platform.runLater(() -> Utility.reveal(menuItems, 3000));

        mainPane.setBottom(menuItems);
        error.setStrokeWidth(1);

        image = new ImageView(MenuScene.class.getResource("/images/TetrECS.png").toExternalForm());
        image.setFitWidth(this.gameWindow.getHeight());
        image.setPreserveRatio(true);

        image.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,1.5), 30, 0, 0, 0);");
        mainPane.setCenter(image);
        settings.setParent(mainPane);
        menuPane.getChildren().add(settings);

    }

    /**
     * Plays the Menu image flip animation
     */
    public void playStartAnimation() {
        var rt = new RotateTransition(Duration.millis(1000), image);
        var st = new ScaleTransition(Duration.millis(1000), image);
        var pt = new ParallelTransition();
        var sst = new SequentialTransition();
        var rt2 = new RotateTransition(Duration.millis(1000), image);

        st.setFromX(4f);
        st.setToX(1f);

        rt.setByAngle(360);

        rt2.setCycleCount(-1);
        rt2.setFromAngle(5);
        rt2.setInterpolator(Interpolator.EASE_BOTH);
        rt2.setToAngle(-5);
        rt2.setCycleCount(-1);
        rt2.setAutoReverse(true);

        pt.getChildren().addAll(rt, st);
        sst.getChildren().addAll(pt, rt2);
        sst.play();
    }

    /**
     * Shows the connection failed message
     */
    public void showError() {
        Platform.runLater(() -> Utility.reveal(error, 2000));
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

        //Plays the menu start animation
        playStartAnimation();

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                settings.toggle();
            }
        });

        if (gameWindow.isNotConnected()) {
            // Reveals the communication error if the gameWindow is not connected
            Platform.runLater(() -> Utility.reveal(error, 2000));
        } else {
            //Otherwise(connection not yet complete) set a new listener that waits for a connection error with the same result
            gameWindow.addErrorListener(new WebSocketAdapter() {
                @Override
                public void onConnectError(WebSocket arg0, WebSocketException arg1) throws Exception {
                    Platform.runLater(() -> Utility.reveal(error, 2000));
                }
            });

            error.setOpacity(0);
        }

        single.setOnMouseClicked(e -> gameWindow.startChallenge());

        multi.setOnMouseClicked(e -> {
            if (error.getOpacity() == 0) {
                gameWindow.startLobby();
            } else {
                //If error message is shown then not connected to server so fail connection and bounce
                Utility.bounce(175, error, 1.2);
            }
        });
        
        how.setOnMouseClicked(e -> gameWindow.startInstructions());
        exit.setOnMouseClicked(e -> gameWindow.close());
        single.setOnMouseEntered(e -> Utility.bounce(100, single, 1.1));
        multi.setOnMouseEntered(e -> Utility.bounce(100, multi, 1.1));
        how.setOnMouseEntered(e -> Utility.bounce(100, how, 1.1));
        exit.setOnMouseEntered(e -> Utility.bounce(100, exit, 1.1));

    }
}
