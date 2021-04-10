package uk.ac.soton.comp1206.scene;

import java.util.List;

import com.neovisionaries.ws.client.WebSocketState;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import uk.ac.soton.comp1206.component.ScoreBox;
import uk.ac.soton.comp1206.event.highScoreListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Utility;



public class ScoreScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoreScene.class);

    private ObservableList<Pair<String, Integer>> localScoreList;
    private ObservableList<Pair<String, Integer>> remoteScoreList;

    private ScoreBox localHiScoresBox;
    private ScoreBox remoteHiScoresBox;

    private ObservableList<Pair<String, Integer>> remoteScores;

    Game game;
    int score;
    String name;
    Communicator communicator;

    highScoreListener HighScoreListener;

    private VBox elements;

    ObservableList<Pair<String, Integer>> localItems;

    private HBox scoreBoxes;

    private Text title;

    public ScoreScene(GameWindow gameWindow, Game game, ObservableList<Pair<String, Integer>> localItems) {
        super(gameWindow);
        logger.info("Creating Score Scene");
        this.game = game;
        this.score = game.getScore();
        this.communicator = gameWindow.getCommunicator();
        this.localItems = localItems;
        this.remoteScores = FXCollections.observableArrayList();
    }

    @Override
    public void initialise() {
        
        if(communicator.getState()==WebSocketState.OPEN){
            this.communicator.addListener(message -> Platform.runLater(() -> this.handleMessage(message.trim())));
            this.communicator.send("HISCORES");
        }
        else{
            handleMessage("");
        }

    }

    private void seth(highScoreListener h) {
        this.HighScoreListener = h;
    }

    private void removeLowestItem(List<Pair<String, Integer>> x) {
        Pair<String, Integer> lowest = x.get(x.size() - 1);
        x.remove(lowest);
    }

    private void sortListByScore(List<Pair<String, Integer>> x) {
        x.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    }

    private void handleMessage(final String message) {


        remoteScores = Utility.getScoreArrayList(message);
        title.setOpacity(1);

        var lowestRemote = Double.POSITIVE_INFINITY;

        if(remoteScores!= null){
            remoteScores.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            remoteScoreList.addAll(remoteScores);
    
            lowestRemote = remoteScores.get(this.remoteScores.size() - 1).getValue();
        }


        var lowestLocal = this.localScoreList.get(this.localScoreList.size() - 1).getValue();

        if (game instanceof MultiplayerGame) {
            Utility.reveal(300, scoreBoxes);

            return;
        }

        if ((score > lowestLocal) && (score > lowestRemote)) {

            seth(() -> {
                removeLowestItem(localScoreList);
                removeLowestItem(remoteScoreList);

                var newScore = new Pair<String, Integer>(name, score);

                remoteScoreList.add(newScore);
                localScoreList.add(newScore);

                String sendString = "HISCORE " + name + ":" + score;
                logger.info("The system would send '{}'", sendString);
                // communicator.send("HISCORE "+name+":"+score);

                sortListByScore(remoteScoreList);
                sortListByScore(localScoreList);
            });

            letEnterName();

        } else if (score > lowestLocal) {
            seth(() -> {
                removeLowestItem(localScoreList);
                var newScore = new Pair<String, Integer>(name, score);
                localScoreList.add(newScore);
                sortListByScore(localScoreList);
            });
            letEnterName();
        } else if (score > lowestRemote) {
            seth(() -> {
                removeLowestItem(remoteScoreList);

                var newScore = new Pair<String, Integer>(name, score);
                remoteScoreList.add(newScore);
                sortListByScore(localScoreList);

            });
            letEnterName();
        } else {
            Utility.reveal(300, scoreBoxes);
        }

    }

    private void letEnterName() {
        var name = new TextField();
        var enter = new Button("Add");

        enter.setOnAction(event -> {
            this.name = name.getText();

            elements.getChildren().remove(name);
            elements.getChildren().remove(enter);

            HighScoreListener.handleIt();

            Utility.writeScores(localScoreList);
            Utility.reveal(300, scoreBoxes);

        });

        elements.getChildren().addAll(name, enter);
    }

    @Override
    public void build() {

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var instructionsPane = new StackPane();
        instructionsPane.setMaxWidth(gameWindow.getWidth());
        instructionsPane.setMaxHeight(gameWindow.getHeight());
        instructionsPane.getStyleClass().add("scores-background");
        // instructionsPane.getStyleClass().add("menu-background");
        root.getChildren().add(instructionsPane);

        Platform.runLater(() -> scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();
            }
        }));

        var mainPane = new BorderPane();
        instructionsPane.getChildren().add(mainPane);

        scoreBoxes = new HBox(12);
        scoreBoxes.setOpacity(0);
        scoreBoxes.setAlignment(Pos.CENTER);

        elements = new VBox();
        elements.setAlignment(Pos.CENTER);

        title = new Text("Scores");
        title.setOpacity(0);
        title.getStyleClass().add("score");

        localHiScoresBox = new ScoreBox();
        remoteHiScoresBox = new ScoreBox();

        this.localScoreList = FXCollections.observableArrayList(this.localItems);
        var wrapper = new SimpleListProperty<Pair<String, Integer>>(this.localScoreList);
        this.localHiScoresBox.getScoreProperty().bind(wrapper);

        this.remoteScoreList = FXCollections.observableArrayList(this.remoteScores);
        var wrapper2 = new SimpleListProperty<Pair<String, Integer>>(this.remoteScoreList);
        this.remoteHiScoresBox.getScoreProperty().bind(wrapper2);

        scoreBoxes.getChildren().addAll(localHiScoresBox, remoteHiScoresBox);

        elements.getChildren().addAll(title, scoreBoxes);
        mainPane.setCenter(elements);

    }

}

