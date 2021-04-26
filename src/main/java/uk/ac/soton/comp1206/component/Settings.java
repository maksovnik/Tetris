package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.event.SettingsListener;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.utility.Utility;

/**
 * Settings is a Pane that appears and allows the user to change certain
 * properties such as volume and screen resolution. When the Pane is closed the
 * changes are written to a file.
 */
public class Settings extends BorderPane {

    SettingsListener sl;
    Pane parent;

    private Slider musicSlider;
    private Slider fxSlider;

    private static final Logger logger = LogManager.getLogger(Settings.class);

    private TextField ip;
    private TextField port;
    private TextField resolution;

    private Text afterRestart;
    private Text resetLabel;
    private Text save;
    private Text endGame;

    GameWindow gameWindow;

    /**
     * Create a new Settings window attached to the given stage with the specified
     * width and height
     * 
     * @param i          width of pane
     * @param j          height of pane
     * @param gameWindow gameWindow
     **/
    public Settings(GameWindow gameWindow, int i, int j) {
        this.gameWindow = gameWindow;

        //Initially hide the settings window
        hide();

        //Sets dimensions
        setMaxHeight(i);
        setMaxWidth(j);

        getStyleClass().add("settings");

        build();

    }

    public void build(){

        var inner = new VBox();
        var musicVolLabel = new Text("Music Volume");
        var soundFXVolLabel = new Text("Sound Effect Volume");
        var ipLabel = new Text("Server IP");
        var portLabel = new Text("Server Port");
        var resLabel = new Text("Resolution");
        var g = new VBox(3);

        endGame = new Text("End Game");
        save = new Text("Save");
        resetLabel = new Text("Reset Settings");
        afterRestart = new Text("Settings will be applied after restart");
        ip = new TextField();
        port = new TextField();
        resolution = new TextField();
        musicSlider = new Slider(0, 1, 0.5);
        fxSlider = new Slider(0, 1, 0.5);
        

        hideEndGame();

        for (Text o : new Text[] { musicVolLabel, soundFXVolLabel, save, ipLabel, portLabel, resetLabel, portLabel, resLabel, endGame }) {
            o.getStyleClass().add("settingsItem");
        }

        afterRestart.getStyleClass().add("error");
        afterRestart.setOpacity(0);

        inner.getChildren().addAll(musicVolLabel, musicSlider, soundFXVolLabel, fxSlider, ipLabel, ip, portLabel, port, resLabel, resolution, resetLabel);
        inner.setAlignment(Pos.CENTER);

        setAlignment(save, Pos.CENTER);
        setCenter(inner);


        g.setAlignment(Pos.CENTER);
        g.getChildren().addAll(afterRestart, save, endGame);
        setBottom(g);
    }
    /**
     * Initialises the Settings pane. Sets up bindings and listeners.
     **/
    public void initialise() {

        // Bind the static volume properties to the property of the sliders
        Multimedia.getMusicVolumeProperty().bind(musicSlider.valueProperty());
        Multimedia.getFXVolumeProperty().bind(fxSlider.valueProperty());

        //If any of the text changes reveal the message telling the player a restart is required
        ip.textProperty().addListener((a, b, c) -> Utility.reveal(afterRestart, 200));
        port.textProperty().addListener((a, b, c) -> Utility.reveal(afterRestart, 200));
        resolution.textProperty().addListener((a, b, c) -> Utility.reveal(afterRestart, 200));

        //Reset the settings
        resetLabel.setOnMouseClicked(e -> setSettings(GameWindow.ipD, GameWindow.portD, GameWindow.bgVolD, GameWindow.fxVolD,
                GameWindow.widthD, GameWindow.heightD));
        
        //Save and close settings window
        save.setOnMouseClicked(e -> hide());
        
        //Save and close + call listener
        endGame.setOnMouseClicked(e -> {
            hide();
            if (sl != null) {
                sl.onExit();
            }
        });

    }

    /**
     * Sets each TextField object to the respective passed parameter
     * 
     * @param ipD     ip address
     * @param portD   port
     * @param bgVol  background volume
     * @param fxVol  special effects volume
     * @param width  width of game
     * @param height height of game
     **/
    public void setSettings(String ipAddress, String portD, String bgVol, String fxVol, String width, String height) {
        musicSlider.setValue(Double.parseDouble(bgVol));
        fxSlider.setValue(Double.parseDouble(fxVol));

        ip.setText(ipAddress);
        port.setText(portD);
        resolution.setText(width + "x" + height);
    }

    /**
     * Toggles the visibility of this pane
     * 
     **/
    public void toggle() {
        //If the settings window is disabled then show otherwise hide
        if (isDisabled()) {
            show();
        } else {
            hide();
        }
    }

    /**
     * Set's behaviour for pressing the Escape key
     * 
     **/
    public void onKeyPress(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            hide();
        }
    }

    /**
     * Hides window
     **/
    public void hide() {

        logger.info("Settings window hidden");

        if ((musicSlider != null) && (fxSlider != null)) {
            //Gets the value of the fields and sliders
            var first = musicSlider.getValue();
            var second = fxSlider.getValue();
            var th = ip.getText();
            var th1 = port.getText();

            var c = resolution.getText().split("x");
            if ((c.length == 2) && (Utility.isInteger(c[0])) && (Utility.isInteger(c[1]))) {
                // Saves the new data to a file
                Utility.writeSettings(th, th1, first, second, c[0], c[1]);
            }

        }

        if (parent != null) {
            //Removes blur from behind settings
            parent.setEffect(null);
        }

        if (sl != null) {
            sl.onHide();
        }
        
        //Disable and hide pane
        setOpacity(0);
        setDisable(true);
    }

    /**
     * Sets the parent pane of this window
     * 
     * @param p parent pane
     **/
    public void setParent(Pane p) {
        this.parent = p;
    }

    /**
     * Shows settings window
     **/
    public void show() {
        logger.info("Settings window shown");
        // Blurs the Parent pane when settings is show
        GaussianBlur blur = new GaussianBlur(55);
        parent.setEffect(blur);

        if (sl != null) {
            sl.onShow();
        }

        //Enable and show pane
        setOpacity(1);
        setDisable(false);
    }

    /**
     * Sets the SettingsListener to allow for action on events
     * 
     * @param s SettingsListener
     **/
    public void setListener(SettingsListener s) {
        this.sl = s;
    }

    /**
     * Shows the End Game label
     **/
    public void showEndGame() {
        this.endGame.setDisable(false);
        this.endGame.setOpacity(1);
    }

    /**
     * Hides the End Game label
     **/
    public void hideEndGame() {
        this.endGame.setDisable(true);
        this.endGame.setOpacity(0);
    }

}
