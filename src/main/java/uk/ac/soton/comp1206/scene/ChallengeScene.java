package uk.ac.soton.comp1206.scene;

import java.util.Arrays;

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.component.Settings;
import uk.ac.soton.comp1206.event.SettingsListener;
import uk.ac.soton.comp1206.event.PieceEventListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.utility.Utility;

/**
 * The Single Player challenge scene holds the UI for the single player
 * challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);

    protected Game game;
    protected GameBoard board;

    private PieceBoard nextPieceBoard;
    private PieceBoard followingPieceBoard;

    private StackPane challengePane;

    protected Text levelTitle;
    protected Text level;
    protected Text highscore;
    protected Text hscoreTitle;
    protected Text multiplierTitle;
    protected Text multiplier;
    private Text livesTitle;
    private Text scoreTitle;
    private Text score;
    private Text lives;

    protected VBox sidePane;
    protected VBox bottomPane;
    protected VBox centerPane;

    private BorderPane mainPane;
    private Settings settings;
    private Rectangle rectangle;

    /**
     * Create a new Single Player challenge scene
     * 
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);

        settings = gameWindow.getSettings();

        logger.info("Creating Challenge Scene");
    }

    /**
     * Creates the game elements
     */
    private void createElements() {
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        System.out.println(gameWindow.getHeight());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("challenge-background");

        scoreTitle = new Text("Score");
        livesTitle = new Text("Lives");
        hscoreTitle = new Text("High Score");
        levelTitle = new Text("Level");
        multiplierTitle = new Text("Multiplier");

        highscore = new Text();
        score = new Text();
        level = new Text();
        lives = new Text();
        multiplier = new Text();

        nextPieceBoard = new PieceBoard(3, 3, 100, 100, true);
        followingPieceBoard = new PieceBoard(3, 3, 75, 75, false);

        sidePane = new VBox(5);
        centerPane = new VBox(4);
        bottomPane = new VBox(10);

        mainPane = new BorderPane();
        board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);

        rectangle = new Rectangle(gameWindow.getWidth(), 20);

        rectangle.setFill(Color.GREEN);
    }

    /**
     * Styles the game elements
     */
    private void styleElements() {
        for (Text i : new Text[] { scoreTitle, levelTitle, livesTitle, multiplierTitle, hscoreTitle }) {
            i.getStyleClass().add("heading");
        }

        highscore.getStyleClass().add("hiscore");
        score.getStyleClass().add("score");
        level.getStyleClass().add("level");
        lives.getStyleClass().add("lives");
        multiplier.getStyleClass().add("level");

        sidePane.setStyle("-fx-padding: 5;");
    }

    /**
     * Positions the game elements
     */
    private void positionElements() {

        sidePane.getChildren().addAll(hscoreTitle, highscore, scoreTitle, score, levelTitle, level, livesTitle, lives,
                multiplierTitle, multiplier, nextPieceBoard, followingPieceBoard);

        sidePane.setAlignment(Pos.CENTER_RIGHT);
        centerPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(rectangle, Pos.CENTER_LEFT);

        root.getChildren().add(challengePane);

        centerPane.getChildren().add(board);
        bottomPane.getChildren().add(rectangle);

        challengePane.getChildren().add(mainPane);

        mainPane.setCenter(centerPane);
        mainPane.setRight(sidePane);
        mainPane.setBottom(bottomPane);

        BorderPane.setMargin(rectangle, new Insets(5, 5, 5, 5));
    }

    /**
     * Positions the game elements
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        Multimedia.startBackgroundMusic("/music/game.mp3");

        setupGame();

        createElements();

        styleElements();

        positionElements();

        StackPane.setAlignment(settings, Pos.CENTER);

        challengePane.getChildren().add(settings);
        settings.setParent(mainPane);

    }

    /**
     * Handles a key release event
     * 
     * @param e key event
     */
    private void handleKeyRelease(KeyEvent e) {
        KeyCode k = e.getCode();
        String keyName = k.getName();

        if (keyName.equals("U")) {
            game.resetSpeed();
        }
    }

    /**
     * Creates a new game
     */
    protected void setupGame() {
        logger.info("Starting a new game");

        // Start new game
        game = new Game(5, 5);
    }

    /**
     * Fetches the highest score from the file
     */
    private int getHighScore() {
        Utility.fetchHighScore();
        var highScore = Utility.getHighScore();
        return highScore;
    }

    /**
     * Handles a key press event
     * 
     * @param e key event
     */
    protected void handleKeyPress(KeyEvent e) {

        KeyCode code = e.getCode();
        String keyName = code.getName();

        if (keyName.equals("U")) {
            game.speedUp();
        }
        if (Arrays.asList("W", "A", "S", "D", "Up", "Down", "Left", "Right").contains(keyName)) {

            var coords = board.getCurrentCenterHoverCoords();
            if (keyName.equals("Up") || keyName.equals("W")) {
                // Move the displayed hover 1 row up
                board.hover(coords[0] - 1, coords[1]);
            }
            if (keyName.equals("Down") || keyName.equals("S")) {
                // Move the displayed hover 1 row down
                board.hover(coords[0] + 1, coords[1]);
            }
            if (keyName.equals("Left") || keyName.equals("A")) {
                // Move the displayed hover 1 column left
                board.hover(coords[0], coords[1] - 1);
            }
            if (keyName.equals("Right") || keyName.equals("D")) {
                // Move the displayed hover 1 column right
                board.hover(coords[0], coords[1] + 1);
            }

        }
        if (code == KeyCode.ESCAPE) {
            //toggle displaying the settings screen
            settings.toggle();
        }
        if (Arrays.asList("Enter", "X").contains(keyName)) {
            //Gets the currently hovered on position and "clicks" the block at that position
            var c = board.getCurrentCenterHoverCoords();
            game.blockClicked(board.getBlock(c[0], c[1]));
        }
        if (Arrays.asList("Q", "Z", "Open Bracket").contains(keyName)) {
            game.rotateCurrentPiece(-1);
        }
        if (Arrays.asList("E", "C", "Close Bracket").contains(keyName)) {
            game.rotateCurrentPiece(1);
        }
        if (Arrays.asList("Space", "R").contains(keyName)) {
            game.swapCurrentPiece();
        }
    }

    /**
     * Initialise the scene and start the game
     */
    public void initialise() {
        logger.info("Initialising Challenge");

        //Shows button allowing user to quit their current game
        settings.showEndGame();

        settings.setListener(new SettingsListener() {
            @Override
            public void onHide() {
                //Music resumed
                Multimedia.play();
                //mainPane effects removed
                mainPane.setEffect(null);
                mainPane.setDisable(false);
                //game unpaused
                game.play();
            }

            @Override
            public void onShow() {
                // music paused
                Multimedia.pause();
                // main pane is disabled so user cant accidentally interact with game UI
                mainPane.setDisable(true);
                // game is paused
                game.pause();
            }

            @Override
            public void onExit() {
                // end game button is hidden
                settings.hideEndGame();
                // game end instructed
                game.end();
            }
        });

        //Binds the UI properties to the values inside of Game
        level.textProperty().bind(game.getLevelProperty().asString());
        lives.textProperty().bind(game.getLivesProperty().asString());
        score.textProperty().bind(game.getScoreProperty().asString());
        highscore.textProperty().bind(game.getHScoreProperty().asString());
        multiplier.textProperty().bind(game.getMultiplierProperty().asString());

        
        // proud. Takes the current time of the game set set's the fill of the rectangle to be equal to the 120*
        // the hue of the newTime (ranging from 1 to 0), this has the effect of the bar going from green
        // to red as the game time reduces
        game.setOnTimerChange(newTime -> rectangle.setFill(Color.hsb(120 * newTime, 1, 1)));
        
        // Binds the width property of the rectangle to the time property in game (ranging from 1 to 0)
        // multiplied by the width of the window, this has the effect of the rectangle starting at full
        // width and reducing in width to 0 when the timer is up.
        rectangle.widthProperty().bind(game.getTimeProperty().multiply(root.getWidth()));

        //Registers handleKeyPress methods with listeners
        Platform.runLater(() -> scene.setOnKeyPressed(e -> handleKeyPress(e)));
        Platform.runLater(() -> scene.setOnKeyReleased(e -> handleKeyRelease(e)));

        game.setOnPieceEvent(new PieceEventListener() {
            @Override
            public void playPiece() {
                Multimedia.playSoundEffect("/sounds/place.wav");
            }

            @Override
            public void rotatePiece() {
                Multimedia.playSoundEffect("/sounds/rotate.wav");
            }

            @Override
            public void swapPiece() {
                Multimedia.playSoundEffect("/sounds/transition.wav");
            }

            @Override
            public void nextPiece(GamePiece a, GamePiece b) {
                logger.info("Next Piece");
                //Display the next piece on the nextPieceboard
                nextPieceBoard.displayPiece(a);
                //Display the following piece on the followingPieceboard
                followingPieceBoard.displayPiece(b);
                //Set the current piece on the board for hovering purposes
                board.setCurrentPiece(a);
                //Allows action for subclasses
                onNextPiece();
                //A new piece has been generated so the hovered piece must be updated
                board.updateHover();
            }

        });

        game.setOnLineCleared(x -> {
            Multimedia.playSoundEffect("/sounds/clear.wav");
            board.fadeOut(x);
        });

        game.setOnGameEnd(() -> Platform.runLater(() -> {
            gameWindow.getCommunicator().clearListeners();
            gameWindow.startScores(game, Utility.loadScores());
        }));

        //Sets the high score in the game to the loaded score from file
        game.setHighScore(getHighScore());

        board.setOnBlockClicked((e, g) -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                game.blockClicked(g);
            }
            if (e.getButton() == MouseButton.SECONDARY) {
                //rotate clockwise
                game.rotateCurrentPiece(1);
                //When a rotate happens we need to update the blocks hovered because
                // the piece has rotated
                var coords = board.getCurrentCenterHoverCoords();
                board.hover(coords[0], coords[1]);
            }

        });

        nextPieceBoard.setOnBlockClicked((m, b) -> {
            if (m.getButton() == MouseButton.PRIMARY) {
                //rotate anticlockwise
                game.rotateCurrentPiece(-1);
            }

            if (m.getButton() == MouseButton.SECONDARY) {
                //rotate clockwise
                game.rotateCurrentPiece(1);
            }
        });

        followingPieceBoard.setOnBlockClicked((m, b) -> {
            if (m.getButton() == MouseButton.PRIMARY) {
                game.swapCurrentPiece();
            }
        });

        startGame();

    }

    /**
     * On next piece
     */
    protected void onNextPiece() {
    }

    /**
     * Starts the game
     */
    protected void startGame() {
        game.start();
    }

}
