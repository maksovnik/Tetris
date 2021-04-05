package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.event.handleHighscore;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Utility;

public class ScoreScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(ScoreScene.class);

    private ObservableList<Pair<String, Integer>> localScoreList;
    private ObservableList<Pair<String, Integer>> remoteScoreList;

    private ScoreBox localHiScoresBox;
    private ScoreBox remoteHiScoresBox;

    private ArrayList<Pair<String, Integer>> remoteScores;


    Game game;
    int score;
    String name;
    Communicator communicator;

    handleHighscore h;

    private VBox elements;

    public ScoreScene (GameWindow gameWindow, Game game) {
        super(gameWindow);
        logger.info("Creating Score Scene");
        this.game=game;
        this.score=game.getScore();
        this.communicator = gameWindow.getCommunicator();
        this.remoteScores = new ArrayList<Pair<String, Integer>>();

    }

    

    @Override
    public void initialise() { 
        this.communicator.addListener(message -> Platform.runLater(() -> this.handleMessage(message.trim())));
        this.communicator.send("HISCORES");
    }

    private void seth(handleHighscore h){
        this.h=h;
    }

    private void removeLowestItem(List<Pair<String, Integer>> x){
        Pair<String,Integer> lowest = x.get(x.size() - 1);
        x.remove(lowest);
    }

    private void sortListByScore(List<Pair<String, Integer>> x){
        x.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    }
    private void handleMessage(final String message) {

        remoteScores = Utility.getScoreArrayList(message);
        remoteScores.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        remoteScoreList.addAll(remoteScores);

        var lowestRemote = remoteScores.get(this.remoteScores.size() - 1).getValue();
        var lowestLocal = this.localScoreList.get(this.localScoreList.size() - 1).getValue();
        

        if((score>lowestLocal)&&(score>lowestRemote)){

            seth(() -> {
                removeLowestItem(localScoreList);
                removeLowestItem(remoteScoreList);

                var newScore = new Pair<String, Integer>(name,score);
                
                remoteScoreList.add(newScore);
                localScoreList.add(newScore);

                sortListByScore(remoteScoreList);
                sortListByScore(localScoreList);
            });

            letEnterName();

        }
        else if(score>lowestLocal){
            seth(() -> {
                removeLowestItem(localScoreList);
                var newScore = new Pair<String, Integer>(name,score);
                localScoreList.add(newScore);
                sortListByScore(localScoreList);
            });
            letEnterName();
        }
        else if(score>lowestRemote){
            seth(() -> {
                removeLowestItem(remoteScoreList);

                var newScore = new Pair<String, Integer>(name,score);
                remoteScoreList.add(newScore);
                sortListByScore(localScoreList);

            });
            letEnterName();
        }
        else{
            localHiScoresBox.reveal();
            remoteHiScoresBox.reveal();
        }

    }

    private void letEnterName(){
        var name =  new TextField();
        var enter =  new Button("Add");

        enter.setOnAction(event -> {
            this.name = name.getText();
            
            elements.getChildren().remove(name);
            elements.getChildren().remove(enter);

            h.handleIt();

            Utility.writeScores(localScoreList);
            localHiScoresBox.reveal();
            remoteHiScoresBox.reveal();

        });
        
        elements.getChildren().addAll(name,enter);
    }

    @Override
    public void build() {
        
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var instructionsPane = new StackPane();
        instructionsPane.getStyleClass().add("menu-background");
        root.getChildren().add(instructionsPane);

        var mainPane = new BorderPane();
        instructionsPane.getChildren().add(mainPane);

        var scoreBoxes = new HBox(12);
        scoreBoxes.setAlignment(Pos.CENTER);
        
        elements = new VBox();
        elements.setAlignment(Pos.CENTER);

        Text title = new Text("Scores");
        title.getStyleClass().add("score");

        localHiScoresBox = new ScoreBox();
        remoteHiScoresBox = new ScoreBox();

        this.localScoreList = FXCollections.observableArrayList(Utility.loadScores());
        var wrapper = new SimpleListProperty<Pair<String, Integer>>(this.localScoreList);
        this.localHiScoresBox.getScoreProperty().bind(wrapper);

        this.remoteScoreList = FXCollections.observableArrayList(this.remoteScores);
        var wrapper2 = new SimpleListProperty<Pair<String, Integer>>(this.remoteScoreList);
        this.remoteHiScoresBox.getScoreProperty().bind(wrapper2);

        
        scoreBoxes.getChildren().addAll(localHiScoresBox,remoteHiScoresBox);
                
        elements.getChildren().addAll(title,scoreBoxes);
        mainPane.setCenter(elements);

        
    }




}