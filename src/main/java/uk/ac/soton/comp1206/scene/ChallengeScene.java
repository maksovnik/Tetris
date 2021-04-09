package uk.ac.soton.comp1206.scene;

import java.util.Arrays;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.component.RectangleTimer;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.utility.Utility;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */



public class ChallengeScene extends BaseScene{

    private final Hashtable<String, String> keyMap;

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    protected Game game;
    protected GameBoard board;

    VBox sidePanel;

	protected Text levelTitle;
	protected Text level;
	protected Text hscore;
	protected Text hscoreTitle;
	protected Text multiplierTitle;
	protected Text multiplier;
    
    public VBox y;
    protected VBox k;
    private PieceBoard p;
    private PieceBoard f;
    private RectangleTimer rectangle;


    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        
        keyMap = new Hashtable<String, String>();

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
        challengePane.getStyleClass().add("challenge-background");

        var scoreT = new Text("Score");
        var score = new Text("0");
        hscoreTitle = new Text("High Score");
        hscore = new Text();
        levelTitle = new Text("Level");
        level = new Text();
        var livesT = new Text("Lives");
        var lives = new Text();
        multiplierTitle = new Text("Multiplier");
        multiplier = new Text();
        p = new PieceBoard(3, 3, 100, 100);
        f = new PieceBoard(3, 3, 75, 75);
        sidePanel = new VBox(5);
        var mainPane = new BorderPane();
        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        rectangle = new RectangleTimer(gameWindow.getWidth(), 40);
        k = new VBox(4);
        y = new VBox(10);
        

        

        for(Text i: new Text[] {scoreT,levelTitle,livesT,multiplierTitle,hscoreTitle}){
            i.getStyleClass().add("heading");
        }
        hscore.getStyleClass().add("hiscore");
        score.getStyleClass().add("score");
        level.getStyleClass().add("level");
        lives.getStyleClass().add("lives");
        multiplier.getStyleClass().add("level");

        p.setDoCircle(true);
        
        sidePanel.setStyle("-fx-padding: 5;");
        sidePanel.setAlignment(Pos.CENTER_RIGHT);
        
        sidePanel.getChildren().addAll(hscoreTitle,hscore,scoreT,score,levelTitle,level,livesT,lives,multiplierTitle,multiplier,p,f);

        //root.getChildren().addAll(root);

        
        mainPane.setRight(sidePanel);
        //root.getChildren().add(mainPane); //choose this but broken

        root.getChildren().add(challengePane);
        challengePane.getChildren().add(mainPane);
    
        level.textProperty().bind(game.getLevelProperty().asString());
        lives.textProperty().bind(game.getLivesProperty().asString());
        score.textProperty().bind(game.getScoreProperty().asString());
        hscore.textProperty().bind(game.getHScoreProperty().asString());
        
        multiplier.textProperty().bind(game.getMultiplierProperty().asString());

        k.getChildren().addAll(board);
        k.setAlignment(Pos.CENTER);
        mainPane.setCenter(k);

        y.getChildren().addAll(rectangle);

        mainPane.setBottom(y);
        
        BorderPane.setAlignment(rectangle, Pos.CENTER_LEFT);
        BorderPane.setMargin(rectangle, new Insets(5,5,5,5)); // optional

        Platform.runLater(() -> scene.setOnKeyPressed(e -> handleKeyPress(e)));
        Platform.runLater(() -> scene.setOnKeyReleased(e -> handleKeyRelease(e)));
    }

    private void handleKeyRelease(KeyEvent e) {
        KeyCode k = e.getCode();
        String keyName = k.getName();

        if(keyName.equals("U")){
            rectangle.resetSpeedMult();
            rectangle.setSpeed(1);
        }
    }

    public void setupGame(){
        logger.info("Starting a new Multiplayer game");

        //Start new game
        game = new Game(5, 5);
    }

     
    private int getHighScore(){
        Utility.fetchHighScore();
        var highScore = Utility.getHighScore();
        return highScore;

    }
    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */

    
    public void handleKeyPress(KeyEvent e){

        KeyCode k = e.getCode();
        String keyName = k.getName();

        if(Arrays.asList("W","A","S","D").contains(keyName)){
            board.moveHover(keyMap.get(keyName));
        }
        
        if(keyName.equals("U")){
            rectangle.setSpeed(4);
        }
        if(k.isArrowKey()){
            board.moveHover(keyName);
        }
        if(k==KeyCode.ESCAPE){
            game.end();
        }
        if(Arrays.asList("Enter","X").contains(keyName)){
            game.blockClicked(board.getCurrentHoverPiece());
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
    

    public void endGame(){
        
    }

    /**
     * Initialise the scene and start the game
     */
    
    public void initialise() {
        logger.info("Initialising Challenge");
        game.setNextPieceListener((piece, followingP) -> {
            logger.info("Next Piece");
            p.SetPieceToDisplay(piece);
            f.SetPieceToDisplay(followingP);
        });

        game.setPieceEventListener(type -> {
            if(type=="playPiece"){
                Multimedia.playAudio("/sounds/explode.wav");
            }
            if(type=="rotate"){
                Multimedia.playAudio("/sounds/rotate.wav");
            }
            if(type=="swap"){
                Multimedia.playAudio("/sounds/rotate.wav");
            }
        });

        game.setLineClearedListener(x -> board.fadeOut(x));




        board.setOnBlockClick((e,g) -> {
            if(e.getButton()==MouseButton.SECONDARY){
                game.rotateCurrentPiece(1);
            }
            if(e.getButton()==MouseButton.PRIMARY){
                game.blockClicked(g);
            }
        });

        rectangle.setOnAnimationEnd(e -> game.gameLoop());
        game.setGameLoopListener(delay -> rectangle.shrink(delay));
        game.setGameEndListener(() -> Platform.runLater(() -> {
            rectangle.stopAnimation();
            gameWindow.startScores(game,Utility.loadScores());
        }));

        game.setHScore(getHighScore());

        
        p.setOnBlockClick((m,b) -> {
            if(m.getButton()==MouseButton.PRIMARY){
                game.rotateCurrentPiece(1);
            }
            if(m.getButton()==MouseButton.SECONDARY){
                game.rotateCurrentPiece(-1);
            }
        });
        
        f.setOnBlockClick((m,b) -> {
            if(m.getButton()==MouseButton.PRIMARY){
                game.swapCurrentPiece();
            }
        });
        
        startGame();
        
    }

    public void startGame(){
        game.start();
    }

}
