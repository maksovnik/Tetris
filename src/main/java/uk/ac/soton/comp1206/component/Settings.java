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

    private Slider slider1;
    private Slider slider2;

    private static final Logger logger = LogManager.getLogger(Settings.class);

    private TextField g1;
    private TextField g2;
    private TextField g3;

    private Text t7;
    private Text t6;
    private Text t3;
    private Text t0;

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
        hide();
        setMaxHeight(i);
        setMaxWidth(j);

        getStyleClass().add("settings");

        var inner = new VBox();

        var t1 = new Text("Music Volume");
        var t2 = new Text("Sound Effect Volume");
        t0 = new Text("End Game");
        t3 = new Text("Save");
        var t4 = new Text("Server IP");
        var t5 = new Text("Server Port");
        var t8 = new Text("Resolution");
        t6 = new Text("Reset Settings");
        t7 = new Text("Settings will be applied after restart");
        g1 = new TextField();
        g2 = new TextField();

        g3 = new TextField();

        // setAlignment(Pos.BOTTOM_CENTER);

        slider1 = new Slider(0, 1, 0.5);
        slider2 = new Slider(0, 1, 0.5);

        hideEndGame();

        for (Text o : new Text[] { t1, t2, t3, t4, t5, t6, t5, t8, t0 }) {
            o.getStyleClass().add("settingsItem");
        }

        t7.getStyleClass().add("error");

        t7.setOpacity(0);

        inner.getChildren().addAll(t1, slider1, t2, slider2, t4, g1, t5, g2, t8, g3, t6);
        inner.setAlignment(Pos.CENTER);
        setAlignment(t3, Pos.CENTER);
        setCenter(inner);

        var g = new VBox(3);
        g.setAlignment(Pos.CENTER);

        g.getChildren().addAll(t7, t3, t0);
        setBottom(g);

    }

    /**
     * Initialises the Settings pane. Sets up bindings and listeners.
     **/
    public void initialise() {

        Multimedia.getMusicVolumeProperty().bind(slider1.valueProperty());
        Multimedia.getFXVolumeProperty().bind(slider2.valueProperty());

        g1.textProperty().addListener((a, b, c) -> Utility.reveal(t7, 200));
        g2.textProperty().addListener((a, b, c) -> Utility.reveal(t7, 200));
        g3.textProperty().addListener((a, b, c) -> Utility.reveal(t7, 200));

        t6.setOnMouseClicked(e -> setSettings(GameWindow.ip, GameWindow.port, GameWindow.bgVol, GameWindow.fxVol,
                GameWindow.width, GameWindow.height));
        t3.setOnMouseClicked(e -> hide());
        t0.setOnMouseClicked(e -> {
            hide();
            if (sl != null) {
                sl.onExit();
            }
        });

    }

    /**
     * Sets each TextField object to the respective passed parameter
     * 
     * @param ip     ip address
     * @param port   port
     * @param bgVol  background volume
     * @param fxVol  special effects volume
     * @param width  width of game
     * @param height height of game
     **/
    public void setSettings(String ip, String port, String bgVol, String fxVol, String width, String height) {
        slider1.setValue(Double.parseDouble(bgVol));
        slider2.setValue(Double.parseDouble(fxVol));

        g1.setText(ip);
        g2.setText(port);
        g3.setText(width + "x" + height);

    }

    /**
     * Toggles the visibility of this pane
     * 
     **/
    public void toggle() {
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
        if (sl != null) {
            sl.onHide();
        }

        if ((slider1 != null) && (slider2 != null)) {
            var first = slider1.getValue();
            var second = slider2.getValue();
            var th = g1.getText();
            var th1 = g2.getText();

            var c = g3.getText().split("x");
            logger.info("First:{}  Second:{}");
            if ((c.length == 2) && (Utility.isInteger(c[0])) && (Utility.isInteger(c[1]))) {
                Utility.writeSettings(th, th1, first, second, c[0], c[1]);
            }

        }

        if (parent != null) {
            parent.setEffect(null);
        }

        if (sl != null) {
            sl.onHide();
        }

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

        GaussianBlur blur = new GaussianBlur(55); // 55 is just to show edge effect more clearly.
        parent.setEffect(blur);

        if (sl != null) {
            sl.onShow();
        }

        setOpacity(1);
        // isVisible = true;
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
        this.t0.setDisable(false);
        this.t0.setOpacity(1);
    }

    /**
     * Hides the End Game label
     **/
    public void hideEndGame() {
        this.t0.setDisable(true);
        this.t0.setOpacity(0);
    }

}
