package uk.ac.soton.comp1206.scene;

import com.neovisionaries.ws.client.WebSocketState;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.soton.comp1206.component.Settings;
import uk.ac.soton.comp1206.event.SettingsListener;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    Settings settings;
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

        

        Multimedia.startBackgroundMusic("/music/menu.mp3");

        var menuPane = new StackPane();
        
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        VBox b = new VBox();
        b.setAlignment(Pos.CENTER);
        Text single = new Text("Single Player");
        Text multi = new Text("Multi Player");
        Text how = new Text("How to Play");
        Text exit = new Text("Exit");

        for (Text i : new Text[] {single, multi, how, exit }) {
            i.getStyleClass().add("menuItem");
        }
        b.setStyle("-fx-padding: 0 0 60 0;");
        b.getChildren().addAll(single, multi, how, exit);
        mainPane.setBottom(b);

        single.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> gameWindow.startChallenge());
        multi.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> gameWindow.startLobby());
        how.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> gameWindow.startInstructions());

        final ImageView image = new ImageView(MenuScene.class.getResource("/images/TetrECS.png").toExternalForm());
        image.setFitWidth(this.gameWindow.getHeight());
        image.setPreserveRatio(true);

        image.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,1.5), 30, 0, 0, 0);");

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

        mainPane.setCenter(image);
        sst.getChildren().addAll(pt, rt2);
        sst.play();

        settings.setParent(mainPane);

        menuPane.getChildren().add(settings);

        Platform.runLater(() -> scene.setOnKeyPressed(e -> {
            if(e.getCode()==KeyCode.ESCAPE){
                settings.toggle();
            }
        }));

        settings.setListener(new SettingsListener(){
            @Override
            public void onExit(){
                gameWindow.close();
            }
            @Override
            public void onHide() {};

            @Override
            public void onShow() {}
        });
        

        
    }

    

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
    }

}
