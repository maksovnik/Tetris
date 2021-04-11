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
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.component.Settings;
import uk.ac.soton.comp1206.event.SettingsListener;
import uk.ac.soton.comp1206.event.pieceEventListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.utility.Utility;

/**
 * The Single Player challenge scene. Holds the UI for the single player
 * challenge mode in the game.
 */

public class ChallengeScene extends BaseScene {

    private final Hashtable<String, String> keyMap;

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    protected Game game;
    protected GameBoard board;

    VBox sidePane;

    protected Text levelTitle;
    protected Text level;
    protected Text highscore;
    protected Text hscoreTitle;
    protected Text multiplierTitle;
    protected Text multiplier;

    protected VBox bottomPane;
    protected VBox centerPane;
    private PieceBoard nextPieceBoard;
    private PieceBoard followingPieceBoard;

    private Text livesTitle;

    private Text scoreTitle;

    private StackPane challengePane;

    private Text score;

    private Text lives;

    private BorderPane mainPane;

    private Settings settings;
    private Rectangle rectangle;

    private Timeline g;

    /**
     * Create a new Single Player challenge scene
     * 
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);

        settings = gameWindow.getSettings();

        keyMap = new Hashtable<String, String>();

        keyMap.put("W", "Up");
        keyMap.put("A", "Left");
        keyMap.put("S", "Down");
        keyMap.put("D", "Right");

        logger.info("Creating Challenge Scene");
    }

    private void createElements() {
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
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

        nextPieceBoard = new PieceBoard(3, 3, 100, 100);
        followingPieceBoard = new PieceBoard(3, 3, 75, 75);

        sidePane = new VBox(5);
        centerPane = new VBox(4);
        bottomPane = new VBox(10);

        mainPane = new BorderPane();
        board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);

        rectangle = new Rectangle(gameWindow.getWidth(), 20);

        rectangle.setFill(Color.GREEN);

    }

    private void styleElements() {
        for (Text i : new Text[] { scoreTitle, levelTitle, livesTitle, multiplierTitle, hscoreTitle }) {
            i.getStyleClass().add("heading");
        }

        highscore.getStyleClass().add("hiscore");
        score.getStyleClass().add("score");
        level.getStyleClass().add("level");
        lives.getStyleClass().add("lives");
        multiplier.getStyleClass().add("level");

        nextPieceBoard.setDoCircle(true);
        sidePane.setStyle("-fx-padding: 5;");
    }

    private void positionElements() {

        sidePane.getChildren().addAll(hscoreTitle, highscore, scoreTitle, score, levelTitle, level, livesTitle, lives,
                multiplierTitle, multiplier, nextPieceBoard, followingPieceBoard);

        mainPane.setCenter(centerPane);
        mainPane.setRight(sidePane);
        mainPane.setBottom(bottomPane);

        sidePane.setAlignment(Pos.CENTER_RIGHT);
        centerPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(rectangle, Pos.CENTER_LEFT);

        root.getChildren().add(challengePane);

        centerPane.getChildren().add(board);
        bottomPane.getChildren().addAll(rectangle);

        challengePane.getChildren().add(mainPane);

        BorderPane.setMargin(rectangle, new Insets(5, 5, 5, 5));
    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        Multimedia.startBackgroundMusic("/music/game.wav");

        setupGame();

        createElements();

        styleElements();

        positionElements();

        StackPane.setAlignment(settings, Pos.CENTER);

        challengePane.getChildren().add(settings);
        settings.setParent(mainPane);

        settings.setListener(new SettingsListener() {
            @Override
            public void onHide() {
                Multimedia.play();
                mainPane.setEffect(null);
                mainPane.setDisable(false);
                game.play();
                // rectangle.playAnimation();
            }

            @Override
            public void onShow() {
                Multimedia.pause();
                mainPane.setDisable(true);
                game.pause();
                // rectangle.pauseAnimation();
            }

            @Override
            public void onExit() {
                game.end();
            }
        });

    }

    private void handleKeyRelease(KeyEvent e) {
        KeyCode k = e.getCode();
        String keyName = k.getName();

        if (keyName.equals("U")) {
            game.resetSpeed();
        }
    }

    protected void setupGame() {
        logger.info("Starting a new Multiplayer game");

        // Start new game
        game = new Game(5,5);
    }

    private int getHighScore() {
        Utility.fetchHighScore();
        var highScore = Utility.getHighScore();
        return highScore;

    }

    /**
     * Handle when a block is clicked
     * 
     * @param gameBlock the Game Block that was clocked
     */

    protected void handleKeyPress(KeyEvent e) {

        KeyCode code = e.getCode();
        String keyName = code.getName();

        if (Arrays.asList("W", "A", "S", "D").contains(keyName)) {
            board.moveHover(keyMap.get(keyName));
        }

        if (keyName.equals("U")) {
            game.speedUp();
        }
        if (code.isArrowKey()) {
            board.moveHover(keyName);
        }
        if (code == KeyCode.ESCAPE) {
            System.out.println("Helllo");
            settings.toggle();
        }
        if (Arrays.asList("Enter", "X").contains(keyName)) {
            game.blockClicked(board.getCurrentHoverPiece());
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

        level.textProperty().bind(game.getLevelProperty().asString());
        lives.textProperty().bind(game.getLivesProperty().asString());
        score.textProperty().bind(game.getScoreProperty().asString());
        highscore.textProperty().bind(game.getHScoreProperty().asString());

        game.setOnSingleLoop(delay -> rectangle.setFill(Color.hsb(120 * delay, 1, 1)));
        rectangle.widthProperty().bind(game.getTimeProperty().multiply(gameWindow.getWidth()));

        multiplier.textProperty().bind(game.getMultiplierProperty().asString());

        Platform.runLater(() -> scene.setOnKeyPressed(e -> handleKeyPress(e)));
        Platform.runLater(() -> scene.setOnKeyReleased(e -> handleKeyRelease(e)));

        game.setOnPieceEvent(new pieceEventListener() {

            @Override
            public void playPiece() {
                Multimedia.playAudio("/sounds/explode.wav");
            }

            @Override
            public void rotatePiece() {
                Multimedia.playAudio("/sounds/rotate.wav");
            }

            @Override
            public void swapPiece() {
                Multimedia.playAudio("/sounds/rotate.wav");
            }

            @Override
            public void nextPiece(GamePiece a, GamePiece b) {
                logger.info("Next Piece");
                nextPieceBoard.SetPieceToDisplay(a);
                followingPieceBoard.SetPieceToDisplay(b);
            }

        });

        game.setOnLineCleared(x -> board.fadeOut(x));

        game.setOnGameEnd(() -> Platform.runLater(() -> gameWindow.startScores(game, Utility.loadScores())));

        game.setHighScore(getHighScore());

        board.setOnBlockClicked((e, g) -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                game.blockClicked(g);
            }
            if (e.getButton() == MouseButton.SECONDARY) {
                game.rotateCurrentPiece(1);
            }

        });

        nextPieceBoard.setOnBlockClicked((m, b) -> {
            if (m.getButton() == MouseButton.PRIMARY) {
                game.rotateCurrentPiece(-1);
            }

            if (m.getButton() == MouseButton.SECONDARY) {
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

    protected void startGame() {
        game.start();
    }

}
