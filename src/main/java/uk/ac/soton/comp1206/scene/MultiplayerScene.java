package uk.ac.soton.comp1206.scene;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Pair;
import uk.ac.soton.comp1206.component.ScoreBox;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Utility;

/**
 * The Multi Player challenge scene holds the UI for the multi player challenge
 * mode in the game.
 */
public class MultiplayerScene extends ChallengeScene {

    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
    private ObservableList<Pair<String, Integer>> localScoreList;
    Communicator communicator;
    private TextField sendBox;
    private Text message;
    private ScoreBox leaderboard;
    ScheduledFuture<?> loop;
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Creates a new Multiplayer Scene
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        this.communicator = gameWindow.getCommunicator();
        this.localScoreList = FXCollections.observableArrayList();
    }

    /**
     * Sets up the Multiplayer game
     */
    @Override
    public void setupGame() {
        logger.info("Starting a new Multiplayer game");

        // Start new game
        game = new MultiplayerGame(5, 5, gameWindow);
    }

    /**
     * Request pieces from the server
     * 
     * @param num the number of pieces to request
     */
    public void requestPieces(int num) {
        for (int i = 0; i < num; i++) {
            logger.info("Requesting piece from Server");
            communicator.send("PIECE");
        }
    }

    /**
     * Handle key press in Multiplayer Scene
     * 
     * @param e key event of keypress
     */
    @Override
    public void handleKeyPress(KeyEvent e) {

        KeyCode k = e.getCode();
        String keyName = k.getName();

        super.handleKeyPress(e);

        if (keyName.equals("T")) {
            sendBox.setDisable(false);
            sendBox.setOpacity(1);
        }

    }

    /**
     * Runs when game starts
     */
    @Override
    public void startGame() {
    }

    /**
     * Requests pieces in loop
     */
    private void requestLoop() {
        this.communicator.send("SCORES");
        this.loop = executor.schedule(() -> requestLoop(), 2000, TimeUnit.MILLISECONDS);
    }

    /**
     * Initialise Multiplayer Scene
     */
    @Override
    public void initialise() {

        super.initialise();

        requestLoop();

        game.setOnGameEnd(() -> Platform.runLater(() -> {
            this.executor.shutdownNow();
            this.communicator.send("DIE");
            game.removeChangeListener();
            gameWindow.getCommunicator().clearListeners();
            gameWindow.startScores(game, localScoreList);
        }));

        game.getScoreProperty().addListener((c, a, b) -> communicator.send("SCORE " + b.intValue()));
        game.getLivesProperty().addListener((c, a, b) -> communicator.send("LIVES " + b.intValue()));

        Utility.reveal(leaderboard, 300);
        requestPieces(5);

        this.communicator.addListener(message -> Platform.runLater(() -> handleMessage(message.trim())));

        sendBox.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                String text = sendBox.getText();
                sendBox.clear();
                this.communicator.send("MSG " + text);
                sendBox.setOpacity(0);
                sendBox.setDisable(true);
            }

            if (e.getCode() == KeyCode.ENTER) {
                e.consume();
            }
        });

    }

    /**
     * Gets next piece and requests a new piece from the server
     */
    protected void onNextPiece() {
        super.onNextPiece();
        requestPieces(1);
    }

    /**
     * Handles a message from the server
     * @param s the message
     */
    private void handleMessage(String s) {
        String[] parts = s.split(" ", 2);
        String header = parts[0];

        if (header.equals("PIECE")) {

            ((MultiplayerGame) game).addToQueue(parts[1]);

            if ((((MultiplayerGame) game).getQueueSize() == 5) && board.isDisabled()) {
                logger.info("Received initial pieces successfully, game starting.");
                game.start();
                board.setDisable(false);
            }
        }

        if (header.equals("MSG")) {
            var comps = parts[1].split(":");
            var sender = comps[0];
            var m = comps[1];

            message.setText(sender + " " + m);
            message.setOpacity(1);
        }

        if (header.equals("SCORES")) {

            var playerData = parts[1].split("\n");
            // var t = new ArrayList<Pair<String,Integer>>();
            localScoreList.clear();

            for (String i : playerData) {
                var x = i.split(":");
                var g = new Pair<String, Integer>(x[0], Integer.parseInt(x[1]));
                

                localScoreList.add(g);

                if (x[2].equals("DEAD")) {
                    leaderboard.addLostPlayer(x[0]);
                }
            }

            localScoreList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        }

    }

    /**
     * Builds the multiplayer scene
     */
    @Override
    public void build() {
        super.build();

        var c = new Text[] { level, levelTitle, multiplier, multiplierTitle, highscore, hscoreTitle };
        sidePane.getChildren().removeAll(c);

        board.setDisable(true);

        leaderboard = new ScoreBox();
        var wrapper = new SimpleListProperty<Pair<String, Integer>>(this.localScoreList);
        message = new Text();
        sendBox = new TextField();

        leaderboard.getScoresProperty().bind(wrapper);

        sidePane.getChildren().add(leaderboard);

        message.setOpacity(0);
        sendBox.setOpacity(0);
        sendBox.setDisable(true);

        message.getStyleClass().add("messages");
        message.setStyle("-fx-font-size: 20px;");

        bottomPane.getChildren().add(0, sendBox);
        centerPane.getChildren().add(1, message);

    }

}
