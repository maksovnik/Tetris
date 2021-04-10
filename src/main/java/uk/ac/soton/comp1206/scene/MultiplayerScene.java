package uk.ac.soton.comp1206.scene;

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

public class MultiplayerScene extends ChallengeScene {

    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
    private ObservableList<Pair<String, Integer>> localScoreList;
    Communicator communicator;
    private TextField sendBox;
    private Text message;
    private ScoreBox leaderboard;

    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        this.communicator = gameWindow.getCommunicator();
        this.localScoreList = FXCollections.observableArrayList();
    }

    @Override
    public void setupGame() {
        logger.info("Starting a new Multiplayer game");

        // Start new game
        game = new MultiplayerGame(5, 5, gameWindow);
    }

    @Override
    public void handleKeyPress(KeyEvent e) {

        KeyCode k = e.getCode();
        String keyName = k.getName();

        super.handleKeyPress(e);

        if (keyName.equals("T")) {
            sendBox.setDisable(false);
            sendBox.requestFocus();
            sendBox.setOpacity(1);
        }

    }

    @Override
    public void startGame() {
    }

    @Override
    public void initialise() {

        super.initialise();

        Utility.reveal(300, leaderboard);
        game.requestPieces(5);

        this.communicator.addListener(message -> Platform.runLater(() -> receiveMessage(message.trim())));

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

        
        System.out.println("Multiscnee");
        game.setOnGameEnd(() -> Platform.runLater(() -> {
            System.out.println("helloo");
            rectangle.stopAnimation();
            gameWindow.startScores(game, localScoreList);
        }));

    }

    private void receiveMessage(String s) {
        String[] parts = s.split(" ", 2);
        String header = parts[0];

        if (header.equals("PIECE")) {

            game.addToQueue(parts[1]);

            if ((game.getQueueSize() == 5) && board.isDisabled()) {
                logger.info("Recieved All Good pieces, game starting.");
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

                if (!localScoreList.contains(g))
                    localScoreList.add(g);

                if (x[2].equals("DEAD")) {
                    leaderboard.addLostPlayer(x[0]);
                }
            }

            localScoreList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        }

    }

    public void removeUnneeded() {
        sidePane.getChildren().remove(this.level);
        sidePane.getChildren().remove(this.levelTitle);
        sidePane.getChildren().remove(this.multiplier);
        sidePane.getChildren().remove(this.multiplierTitle);
        sidePane.getChildren().remove(this.highscore);
        sidePane.getChildren().remove(this.hscoreTitle);
    }

    @Override
    public void build() {
        super.build();

        removeUnneeded();

        board.setDisable(true);

        leaderboard = new ScoreBox();
        var wrapper = new SimpleListProperty<Pair<String, Integer>>(this.localScoreList);
        message = new Text();
        sendBox = new TextField();

        leaderboard.getScoreProperty().bind(wrapper);

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
