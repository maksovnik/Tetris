package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());


        gameWindow.setBGMusic("music/game.wav");

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        

        Text scoreT = new Text("Score");
        Text score = new Text("a Text");

        Text levelT = new Text("Level");
        Text level = new Text("a Text");
        
        Text livesT = new Text("Lives");
        Text lives = new Text("a Text");

        Text multiplierT = new Text("Multiplier");
        Text multiplier = new Text("a Text");

        for(Text i: new Text[] {scoreT,levelT,livesT,multiplierT}){
            i.getStyleClass().add("heading");
        }


        score.getStyleClass().add("score");
        level.getStyleClass().add("level");
        lives.getStyleClass().add("lives");
        multiplier.getStyleClass().add("level");


        PieceBoard p = new PieceBoard(3, 3, 100, 100);
        
        game.setNextPieceListener(new NextPieceListener(){
            @Override
            public void nextPiece(GamePiece piece) {
                p.SetPieceToDisplay(piece);
            }
        });


        VBox v = new VBox();
        v.setStyle("-fx-padding: 5;");
        v.setSpacing(5);
        v.setAlignment(Pos.CENTER_RIGHT);
        
        v.getChildren().addAll(scoreT,score,levelT,level,livesT,lives,multiplierT,multiplier,p);

        root.getChildren().addAll(challengePane);
        var mainPane = new BorderPane();
        mainPane.setRight(v);
        challengePane.getChildren().add(mainPane); //choose this but broken


        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);


        score.textProperty().bind(game.getScoreProperty().asString());
        level.textProperty().bind(game.getLevelProperty().asString());
        lives.textProperty().bind(game.getLivesProperty().asString());
        multiplier.textProperty().bind(game.getMultiplierProperty().asString());
        mainPane.setCenter(board);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
    * Setup the game object and model
    */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
    }

}
