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
import javafx.animation.StrokeTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.soton.comp1206.component.Settings;
import uk.ac.soton.comp1206.event.SettingsListener;
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

        VBox b = new VBox(5);
        b.setAlignment(Pos.CENTER);
        Text single = new Text("Single Player");
        Text multi = new Text("Multi Player");
        Text how = new Text("How to Play");
        Text exit = new Text("Exit");
        Text error = new Text("No connection to server");

        error.setOpacity(0);
        for (Text i : new Text[] { single, multi, how, exit}) {
            i.getStyleClass().add("menuItem");
        }
        error.getStyleClass().add("error");
        b.setStyle("-fx-padding: 0 0 40 0;-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,1.5), 50, 0, 0, 0);");
        b.setOpacity(0);
        b.getChildren().addAll(single, multi, how, exit,error);
        Platform.runLater(() -> Utility.reveal(3000,b));
        mainPane.setBottom(b);
        
        single.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> gameWindow.startChallenge());

        error.setStrokeWidth(1);  

        multi.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(error.getOpacity()==0){
                gameWindow.startLobby();
            }
            else{
                Utility.bounce(175, error,1.2,1.2);
            }
        });

        single.setOnMouseEntered(e -> Utility.bounce(100, single,1.1,1.1));

        multi.setOnMouseEntered(e -> Utility.bounce(100, multi,1.1,1.1));
        how.setOnMouseEntered(e -> Utility.bounce(100, how,1.1,1.1));
        exit.setOnMouseEntered(e -> Utility.bounce(100, exit,1.1,1.1));

        how.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> gameWindow.startInstructions());
        exit.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> gameWindow.close());


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
            if (e.getCode() == KeyCode.ESCAPE) {
                settings.toggle();
            }
        }));
        
        if(gameWindow.isNotConnected()){
            Platform.runLater(() -> Utility.reveal(2000,error));
        }
        else{
            gameWindow.addListener(new WebSocketAdapter(){
                @Override
                public void onConnectError(WebSocket arg0, WebSocketException arg1) throws Exception {
                    Platform.runLater(() -> Utility.reveal(2000,error));
                }
            });
            
            error.setOpacity(0);
        }
        

        settings.setListener(new SettingsListener() {
            @Override
            public void onExit() {
                gameWindow.close();
            }

            @Override
            public void onHide() {
            };

            @Override
            public void onShow() {
            }
        });

    }   


    public void checkConnected(){
        Platform.runLater(() -> Utility.reveal(2000,error));
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        //Platform.runLater(() -> checkConnected());
    }

}
