package uk.ac.soton.comp1206.ui;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.component.Settings;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.scene.BaseScene;
import uk.ac.soton.comp1206.scene.ChallengeScene;
import uk.ac.soton.comp1206.scene.InstructionsScene;
import uk.ac.soton.comp1206.scene.IntroScene;
import uk.ac.soton.comp1206.scene.LobbyScene;
import uk.ac.soton.comp1206.scene.MenuScene;
import uk.ac.soton.comp1206.scene.MultiplayerScene;
import uk.ac.soton.comp1206.scene.ScoreScene;
import uk.ac.soton.comp1206.utility.Utility;

/**
 * The GameWindow is the single window for the game where everything takes
 * place. To move between screens in the game, we simply change the scene.
 *
 * The GameWindow has methods to launch each of the different parts of the game
 * by switching scenes. You can add more methods here to add more screens to the
 * game.
 */
public class GameWindow {

    private static final Logger logger = LogManager.getLogger(GameWindow.class);

    // private Multimedia bgPlayer = new Multimedia(true);
    // private Multimedia soundPlayer = new Multimedia(false);

    private final Stage stage;

    private BaseScene currentScene;
    private Scene scene;
    private MenuScene menu;

    Communicator communicator;

    private Settings settings;

    public boolean notConnected;

    public static final String ip = "discord.ecs.soton.ac.uk";
    public static final String port = "9700";
    public static final String width = "800";

    public static final String height = "800";
    public static final String bgVol = "0.5";
    public static final String fxVol = "0.5";

    /**
     * Create a new GameWindow attached to the given stage with the specified width
     * and height
     * 
     * @param stage  stage
     * @param width  width
     * @param height height
     */

    public GameWindow(Stage stage) {


        this.stage = stage;


        // Setup windo
        //communicator = new Communicator("ws://discord.ecs.soton.ac.uk:9700");


        var b = Utility.loadSettings();
        settings = new Settings(500,400);
        if(!b.isEmpty()){
            var ip = b.get("ip");
            var port = b.get("serverPort");
            var bgVol = b.get("musicVol");
            var fxVol = b.get("soundFXVol");
            var width = b.get("width");
            var height = b.get("height");
            Utility.writeSettings(ip, port, Double.parseDouble(bgVol), Double.parseDouble(fxVol), width, height);
            settings.setSettings(ip,port,bgVol,fxVol,width,height);
        }
        else{
            Utility.writeSettings(ip, port, Double.parseDouble(bgVol), Double.parseDouble(fxVol), width, height);
            settings.setSettings(ip,port,bgVol,fxVol,width,height);
        }


        stage.setMinHeight(Double.parseDouble(height));
        stage.setMinWidth(Double.parseDouble(width));

        communicator = new Communicator("ws://"+ip+":"+port);
        communicator.setOnError(new WebSocketAdapter(){
            @Override
            public void onConnectError(WebSocket arg0, WebSocketException arg1) throws Exception {
                setNotConnected(true);
                menu.checkConnected();
            }
        });

        setupStage();

        // Setup resources
        setupResources();

        // Setup default scene
        setupDefaultScene();

        startIntro();

    }
    

    public Settings getSettings(){
        return settings;
    }

    public void addListener(WebSocketAdapter e){
        communicator.setOnError(e);
    }


    public boolean isNotConnected() {
        return notConnected;
    }
    
    public void setNotConnected(boolean c){
        notConnected = c;
    }


    /**
     * Setup the font and any other resources we need
     */
    private void setupResources() {
        logger.info("Loading resources");
        Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-Regular.ttf"), 32);
        Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-Bold.ttf"), 32);
        Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-ExtraBold.ttf"), 32);
    }

    /**
     * Display the main menu
     */

    public MenuScene getMenu() {
        return menu;
    }

    public void startMenu() {
        scene.setOnKeyPressed(null);
        this.menu = new MenuScene(this);
        loadScene(this.menu);
    }

    public void startInstructions() {
        loadScene(new InstructionsScene(this));
    }

    public void startScores(Game g, ObservableList<Pair<String, Integer>> localScoreList) {
        loadScene(new ScoreScene(this, g, localScoreList));
    }

    public void startLobby() {
        loadScene(new LobbyScene(this));
        // if(communicator.getState()==WebSocketState.OPEN){
            
        // }
        // else{
        //     notifyFailedConnection();
        // }
        
    }

    /**
     * Display the single player challenge
     */

    public void startIntro() {
        loadScene(new IntroScene(this));
    }

    public void startChallenge() {
        loadScene(new ChallengeScene(this));
    }

    public void startMultiChallenge() {
        loadScene(new MultiplayerScene(this));
    }

    /**
     * Setup the default settings for the stage itself (the window), such as the
     * title and minimum width and height.
     */
    public void setupStage() {
        stage.setTitle("TetrECS");
        stage.setMinWidth(Double.parseDouble(width));
        stage.setMinHeight(Double.parseDouble(height));
        stage.setOnCloseRequest(ev -> App.getInstance().shutdown());
    }

    /**
     * Load a given scene which extends BaseScene and switch over.
     * 
     * @param newScene new scene to load
     */

    public void close(){
        App.getInstance().shutdown();
    }
    public void loadScene(BaseScene newScene) {
        // Cleanup remains of the previous scene
        cleanup();

        // Create the new scene and set it up
        newScene.build();
        currentScene = newScene;
        scene = newScene.setScene();
        stage.setScene(scene);

        // Initialise the scene when ready
        Platform.runLater(() -> currentScene.initialise());
    }

    /**
     * Setup the default scene (an empty black scene) when no scene is loaded
     */

    public void setupDefaultScene() {
        this.scene = new Scene(new Pane(), Double.parseDouble(width), Double.parseDouble(height), Color.BLACK);
        stage.setScene(this.scene);
    }

    /**
     * When switching scenes, perform any cleanup needed, such as removing previous
     * listeners
     */
    public void cleanup() {
        logger.info("Clearing up previous scene");
        communicator.clearListeners();
    }

    /**
     * Get the current scene being displayed
     * 
     * @return scene
     */
    public Scene getScene() {
        return scene;
    }

    public BaseScene getBScene() {
        return currentScene;
    }

    /**
     * Get the width of the Game Window
     * 
     * @return width
     */
    public int getWidth() {
        return Integer.parseInt(this.width);
    }

    /**
     * Get the height of the Game Window
     * 
     * @return height
     */
    public int getHeight() {
        return Integer.parseInt(this.height);
    }

    /**
     * Get the communicator
     * 
     * @return communicator
     */
    public Communicator getCommunicator() {
        return communicator;
    }


}