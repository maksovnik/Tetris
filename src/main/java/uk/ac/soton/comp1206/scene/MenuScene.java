package uk.ac.soton.comp1206.scene;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        Multimedia.startBackgroundMusic("/music/menu.mp3");

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        
        final ImageView image = new ImageView(MenuScene.class.getResource("/images/TetrECS.png").toExternalForm());
        image.setFitWidth(this.gameWindow.getHeight());
        image.setPreserveRatio(true);
        mainPane.setCenter(image);

        VBox b = new VBox();
        b.setAlignment(Pos.CENTER);
        Text single = new Text("Single Player");
        Text multi = new Text("Multi Player");
        Text how = new Text("How to Play");
        Text exit = new Text("Exit");

        for(Text i: new Text[] {single,multi,how,exit}){
            i.getStyleClass().add("menuItem");
        }   
        b.setStyle("-fx-padding: 0 0 60 0;");
        b.getChildren().addAll(single,multi,how,exit);
        mainPane.setBottom(b);

        single.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->  gameWindow.startChallenge());
        multi.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->  gameWindow.startLobby());
        how.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->  gameWindow.startInstructions());
        
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }

}
