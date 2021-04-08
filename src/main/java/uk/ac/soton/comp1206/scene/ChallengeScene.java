package uk.ac.soton.comp1206.scene;

import java.util.Arrays;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.utility.Utility;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */



public class ChallengeScene extends BaseScene{

    private final Hashtable<String, String> keyMap = new Hashtable<String, String>();

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    protected Game game;
    protected GameBoard board;

    VBox elements;

	protected Text levelT;

	protected Text level;

	protected Text hscore;

	protected Text hscoreT;

	protected Text multiplierT;

	protected Text multiplier;

    public VBox y;

    protected VBox k;


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

        hscoreT = new Text("High Score");
        hscore = new Text();

        levelT = new Text("Level");
        level = new Text();
        
        Text livesT = new Text("Lives");
        Text lives = new Text();

        multiplierT = new Text("Multiplier");
        multiplier = new Text();

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

        game.setNextPieceListener((piece, followingP) -> {
            logger.info("Next Piece");
            p.SetPieceToDisplay(piece);
            f.SetPieceToDisplay(followingP);
        });

        game.setPieceEventListener(type -> {
            if(type=="playPiece"){
                Multimedia.playAudio("/sounds/explode.wav");
                game.restartLoop();
            }
            if(type=="rotate"){
                System.out.println("hahha");
                Multimedia.playAudio("/sounds/rotate.wav");
            }
            if(type=="swap"){
                Multimedia.playAudio("/sounds/rotate.wav");
            }
        });

        game.setLineClearedListener(x -> board.fadeOut(x));

        game.setGameEndListener(() -> Platform.runLater(() -> gameWindow.startScores(game,Utility.loadScores())));



        elements = new VBox(5);
        elements.setStyle("-fx-padding: 5;");
        elements.setAlignment(Pos.CENTER_RIGHT);
        
        elements.getChildren().addAll(hscoreT,hscore,scoreT,score,levelT,level,livesT,lives,multiplierT,multiplier,p,f);

        root.getChildren().addAll(challengePane);

        var mainPane = new BorderPane();
        mainPane.setRight(elements);
        challengePane.getChildren().add(mainPane); //choose this but broken


        this.board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);

        board.setOnClick(m -> {
            if(m==MouseButton.SECONDARY){
                game.rotateCurrentPiece(1);
            }
        });

        p.setOnClick(m -> {
            if(m==MouseButton.PRIMARY){
                game.rotateCurrentPiece(1);
            }
            if(m==MouseButton.SECONDARY){
                game.rotateCurrentPiece(-1);
            }
        });
        
        f.setOnClick(m -> {
            if(m==MouseButton.PRIMARY){
                game.swapCurrentPiece();
            }
        });

        game.setScoreListener(scoreC -> {
            score.setText(Integer.toString(scoreC));
            if(Integer.parseInt(hscore.getText()) < scoreC){
                hscore.setText(Integer.toString(scoreC));
            
            }
        });

        Rectangle rectangle = new Rectangle(0, 0, 600, 40);

        rectangle.setFill(Color.GREEN);
        
        double x = gameWindow.getWidth()-10;
        rectangle.setWidth(x);

        game.setGameLoopListener(delay -> {
            rectangle.setWidth(x);
            KeyValue widthValue = new KeyValue(rectangle.widthProperty(), 0);
            KeyFrame frame = new KeyFrame(Duration.millis(delay), widthValue);

            KeyValue greenValue= new KeyValue(rectangle.fillProperty(), Color.GREEN);
            KeyFrame frame1 = new KeyFrame(Duration.ZERO, greenValue);

            KeyValue yellowValue= new KeyValue(rectangle.fillProperty(), Color.YELLOW);
            KeyFrame frame2 = new KeyFrame(new Duration(delay*0.5), yellowValue);
        
            KeyValue redValue= new KeyValue(rectangle.fillProperty(), Color.RED);
            KeyFrame frame3 = new KeyFrame(new Duration(delay*0.75), redValue);

            Timeline timeline = new Timeline(frame,frame1,frame2,frame3);
            timeline.play();
        });


        hscore.setText(Integer.toString(getHighScore()));
    
        level.textProperty().bind(game.getLevelProperty().asString());
        lives.textProperty().bind(game.getLivesProperty().asString());
        
        multiplier.textProperty().bind(game.getMultiplierProperty().asString());

        k = new VBox(4);
        k.getChildren().addAll(board);
        k.setAlignment(Pos.CENTER);
        mainPane.setCenter(k);

        
        
        y = new VBox(10);
        y.getChildren().addAll(rectangle);
        //mainPane.setBottom(rectangle);
        mainPane.setBottom(y);

        BorderPane.setAlignment(rectangle, Pos.CENTER_LEFT);
        BorderPane.setMargin(rectangle, new Insets(5,5,5,5)); // optional
        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
        Platform.runLater(() -> scene.setOnKeyPressed(e -> handleKeyPress(e)));
        //Platform.runLater(() -> scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> handleKeyPress(e)));

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

        System.out.println("nnono");
        KeyCode k = e.getCode();
        String keyName = k.getName();

        if(Arrays.asList("W","A","S","D").contains(keyName)){
            board.moveHover(keyMap.get(keyName));
        }
        
        if(k.isArrowKey()){
            board.moveHover(keyName);
        }
        if(k==KeyCode.ESCAPE){
            game.end();
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
        game = new Game(5,5);
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
