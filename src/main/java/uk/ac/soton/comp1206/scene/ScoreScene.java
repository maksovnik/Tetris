package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
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
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Utility;

public class ScoreScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(ScoreScene.class);

    SimpleListProperty<Pair<String,Integer>> localScores;
    ArrayList<Pair<String,Integer>> scores = new ArrayList<Pair<String,Integer>>();
    ObservableList<Pair<String,Integer>> scoresList = FXCollections.observableArrayList(scores);
    ListProperty<Pair<String,Integer>> wrapper = new SimpleListProperty<Pair<String,Integer>>(scoresList);

    SimpleListProperty<Pair<String,Integer>> localScores2;
    ArrayList<Pair<String,Integer>> scores2 = new ArrayList<Pair<String,Integer>>();
    ObservableList<Pair<String,Integer>> scoresList2 = FXCollections.observableArrayList(scores2);
    ListProperty<Pair<String,Integer>> remoteScores = new SimpleListProperty<Pair<String,Integer>>(scoresList2);

    private ScoresList ScoreList;
    private ScoresList RemoteScoreList;
    
    Game game;
    int score;

    private VBox elements;
    public ScoreScene (GameWindow gameWindow, Game game) {
        super(gameWindow);
        logger.info("Creating Score Scene");
        this.game=game;
        this.score=game.getScore();
    }
    

    @Override
    public void initialise() {  
    }

    private void loadOnlineScores(){
        gameWindow.getCommunicator().send("HISCORES");
    }

    @Override
    public void build() {
        


        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var instructionsPane = new StackPane();
        instructionsPane.setMaxWidth(gameWindow.getWidth());
        instructionsPane.setMaxHeight(gameWindow.getHeight());
        instructionsPane.getStyleClass().add("menu-background");
        root.getChildren().add(instructionsPane);

        var mainPane = new BorderPane();
        instructionsPane.getChildren().add(mainPane);

        var boxes = new HBox(12);
        boxes.setAlignment(Pos.CENTER);
        
        elements = new VBox();
        loadOnlineScores();
        
        elements.setAlignment(Pos.CENTER);

        Text title = new Text("Scores");

        title.getStyleClass().add("score");

        ScoreList = new ScoresList();
        
        RemoteScoreList = new ScoresList();

        gameWindow.getCommunicator().addListener(new CommunicationsListener(){
            @Override
            public void receiveCommunication(String communication){
                var x = new ArrayList<Pair<String, Integer>>();
                String[] parts = communication.split(" ");
                String[] newScores = parts[1].split("\n");
                for(String i: newScores){
                    String[] newParts = i.split(":");
                    var p = new Pair<String, Integer>(newParts[0],Integer.parseInt(newParts[1]));
                    x.add(p);
                }
                x.sort((a, b) -> b.getValue().compareTo(a.getValue()));
                remoteScores.setAll(x);
                Platform.runLater(() -> RemoteScoreList.setScore(remoteScores));
                RemoteScoreList.reveal();
            }
        });

        
        

        

        ScoreList.setOpacity(0);

        var y = Utility.loadScores();
        wrapper.setAll(y);

        
        int lastScore = (y.get(y.size() - 1).getValue());

        if(score > lastScore){
            var r = wrapper.get(wrapper.size()-1);
            wrapper.remove(r);
            newHighScore();
        }
        else{
            ScoreList.reveal();
            ScoreList.setScore(wrapper);
        }
        
        
        boxes.getChildren().addAll(ScoreList,RemoteScoreList);
        elements.getChildren().addAll(title,boxes);
        mainPane.setCenter(elements);
        
    }

    private void newHighScore(){
        var name =  new TextField();
        var enter =  new Button("Add");

        enter.setOnAction(event -> {
            String nameS = name.getText();
            
            name.setOpacity(0);
            enter.setOpacity(0);

            addNewScore(nameS, score);
            ScoreList.reveal();
        });

        elements.getChildren().addAll(name,enter);


    }

    private void addNewScore(String name, int score){
        var tempScores = new ArrayList<Pair<String, Integer>>(wrapper.get());
        for(Pair<String, Integer> i: tempScores){
            if(i.getValue()<score){
                add(new Pair<String, Integer>(name,score));
                ScoreList.setScore(wrapper);
                Utility.writeScores(wrapper.get());
                break;
            }
        } 

    }

    private void add(Pair<String, Integer> c){
        wrapper.add(c);
        wrapper.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    }
}