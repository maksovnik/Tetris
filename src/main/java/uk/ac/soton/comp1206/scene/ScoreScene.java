package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Pair;
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

    private ScoresList ScoreList;
    
    Game game;
    public ScoreScene (GameWindow gameWindow, Game game) {
        super(gameWindow);
        logger.info("Creating Score Scene");
        this.game=game;
    }
    
    private void addSort(String s, int score){

        //scores.add()
    }


    @Override
    public void initialise() {  
    }

    @Override
    public void build() {
        


        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        //gameWindow.setBGMusic("music/menu.mp3");


        var instructionsPane = new StackPane();
        instructionsPane.setMaxWidth(gameWindow.getWidth());
        instructionsPane.setMaxHeight(gameWindow.getHeight());
        instructionsPane.getStyleClass().add("menu-background");
        root.getChildren().add(instructionsPane);

        var mainPane = new BorderPane();
        instructionsPane.getChildren().add(mainPane);


        var elements = new VBox();
        
        elements.setAlignment(Pos.CENTER);

        Text title = new Text("Scores");

        title.getStyleClass().add("score");

        ScoreList = new ScoresList();

        var y = Utility.loadScores();
        wrapper.setAll(y);

        boolean addded = addNewScore("LKK", game.getScore());

        ScoreList.setScore(wrapper);

        if(addded){
            Utility.writeScores(wrapper.get());
        }
        
        elements.getChildren().addAll(title,ScoreList);
        mainPane.setCenter(elements);
        
    }

    private boolean addNewScore(String name, int score){
        var tempScores = new ArrayList<Pair<String, Integer>>(wrapper.get());
        for(Pair<String, Integer> i: tempScores){
            if(score>i.getValue()){
                var x = new Pair<String, Integer>(name,score);
                wrapper.add(x);
                wrapper.remove(i);
                return true;
            }
        }
        return false;
    }

}