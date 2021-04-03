package uk.ac.soton.comp1206.scene;

import java.util.Arrays;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.ClickListener;
import uk.ac.soton.comp1206.event.GameEndListener;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.ScoreListener;
import uk.ac.soton.comp1206.event.pieceEventListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.utility.Utility;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */



public class ChallengeScene extends BaseScene{

    private final Hashtable<String, String> keyMap = new Hashtable<String, String>();

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;
    private GameBoard board;


    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);

        keyMap.put("W", "Up");
        keyMap.put("A", "Left");
        keyMap.put("S", "Down");
        keyMap.put("D", "Right");

        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        
        Multimedia.startBackgroundMusic("/music/game.wav");
        
        setupGame();
        

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        
        
        Text scoreT = new Text("Score");
        Text score = new Text("0");

        Text hscoreT = new Text("High Score");
        Text hscore = new Text();

        Text levelT = new Text("Level");
        Text level = new Text();
        
        Text livesT = new Text("Lives");
        Text lives = new Text();

        Text multiplierT = new Text("Multiplier");
        Text multiplier = new Text();

        for(Text i: new Text[] {scoreT,levelT,livesT,multiplierT,hscoreT}){
            i.getStyleClass().add("heading");
        }

        hscore.getStyleClass().add("hiscore");
        score.getStyleClass().add("score");
        level.getStyleClass().add("level");
        lives.getStyleClass().add("lives");
        multiplier.getStyleClass().add("level");


        PieceBoard p = new PieceBoard(3, 3, 100, 100);
        PieceBoard f = new PieceBoard(3, 3, 75, 75);
        p.setDoCircle(true);
        game.setNextPieceListener(new NextPieceListener(){
            @Override
            public void nextPiece(GamePiece piece, GamePiece followingP) {
                logger.info("Next Piece");
                p.SetPieceToDisplay(piece);
                f.SetPieceToDisplay(followingP);
            }
        });

        game.setPieceEventListener(new pieceEventListener(){
            @Override
            public void playSound(String type){
                if(type=="playPiece"){
                    Multimedia.playAudio("/sounds/explode.wav");
                    game.restartLoop();
                }
                if(type=="rotate"){
                    Multimedia.playAudio("/sounds/rotate.wav");
                }
                if(type=="swap"){
                    Multimedia.playAudio("/sounds/rotate.wav");
                }
            }
        });

        game.setLineClearedListener(new LineClearedListener(){
            @Override
            public void linesCleared(GameBlockCoordinate[] x){
                board.fadeOut(x);
            }
        });

        game.setGameEndListener(new GameEndListener(){
            @Override
            public void endGame(Game g){
                game.endGameLoop();
                Platform.runLater(() -> gameWindow.startScores(game));
            }
        });



        VBox v = new VBox();
        v.setStyle("-fx-padding: 5;");
        v.setSpacing(5);
        v.setAlignment(Pos.CENTER_RIGHT);
        
        v.getChildren().addAll(hscoreT,hscore,scoreT,score,levelT,level,livesT,lives,multiplierT,multiplier,p,f);

        root.getChildren().addAll(challengePane);
        var mainPane = new BorderPane();
        mainPane.setRight(v);
        challengePane.getChildren().add(mainPane); //choose this but broken


        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        this.board=board;

        board.setOnClick(new ClickListener(){
            @Override
            public void Click(MouseButton m) {
                if(m==MouseButton.SECONDARY){
                    game.rotateCurrentPiece(1);
                }
            }
        });

        p.setOnClick(new ClickListener(){
            @Override
            public void Click(MouseButton m) {
                if(m==MouseButton.PRIMARY){
                    game.rotateCurrentPiece(1);
                }
            }
        });

        f.setOnClick(new ClickListener(){
            @Override
            public void Click(MouseButton m) {
                if(m==MouseButton.PRIMARY){
                    game.swapCurrentPiece();
                }
            }
        });

        game.setScoreListener(new ScoreListener(){
            @Override
            public void setScore(int scoreC){
                score.setText(Integer.toString(scoreC));
                if(Integer.parseInt(hscore.getText()) < scoreC){
                    hscore.setText(Integer.toString(scoreC));
                
                }
            }
        });


        Rectangle rectangle = new Rectangle(0, 0, 600, 40);

        double x = gameWindow.getWidth()-10;
        game.setGameLoopListener(new GameLoopListener(){
            @Override
            public void timerEnd(int delay){
                //delay = 1000;
                rectangle.setWidth(x);
                KeyValue widthValue = new KeyValue(rectangle.widthProperty(), 0);
                KeyFrame frame = new KeyFrame(Duration.millis(delay), widthValue);
                Timeline timeline = new Timeline(frame);
                timeline.play();
            }
        });

        hscore.setText(Integer.toString(getHighScore()));
    
        level.textProperty().bind(game.getLevelProperty().asString());
        lives.textProperty().bind(game.getLivesProperty().asString());
        
        multiplier.textProperty().bind(game.getMultiplierProperty().asString());
        mainPane.setCenter(board);
        mainPane.setBottom(rectangle);

        BorderPane.setAlignment(rectangle, Pos.CENTER_LEFT);
        BorderPane.setMargin(rectangle, new Insets(5,5,5,5)); // optional
        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
        Platform.runLater(() -> scene.setOnKeyPressed(e -> handleKeyPress(e)));

    }
     
    private int getHighScore(){
        Utility.fetchHighScore();
        String name = Utility.getHighName();
        var highScore = Utility.getHighScore();
        return highScore;

    }
    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */

    private void handleKeyPress(KeyEvent e){

        KeyCode k = e.getCode();
        String keyName = k.getName();

        if(Arrays.asList("W","A","S","D").contains(keyName)){
            board.moveHover(keyMap.get(keyName));
        }
        
        if(k.isArrowKey()){
            board.moveHover(keyName);
        }
        if(k==KeyCode.ESCAPE){
            gameWindow.startMenu();
        }
        if(Arrays.asList("Enter","X").contains(keyName)){
            blockClicked(board.getCurrentHoverPiece());
        }
        if(Arrays.asList("Q","Z","Open Bracket").contains(keyName)){
            game.rotateCurrentPiece(-1);
        }
        if(Arrays.asList("E","C","Close Bracket").contains(keyName)){
            game.rotateCurrentPiece(1);
        }
        if(Arrays.asList("Space","R").contains(keyName)){
            game.swapCurrentPiece();
        }

    }
    
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
