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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Pair;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ScoreScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(ScoreScene.class);

    SimpleListProperty<Pair<String,Integer>> localScores;
    ArrayList<Pair<String,Integer>> scores = new ArrayList<Pair<String,Integer>>();
    ObservableList<Pair<String,Integer>> scoresList = FXCollections.observableArrayList(scores);
    ListProperty<Pair<String,Integer>> wrapper = new SimpleListProperty<Pair<String,Integer>>(scoresList);
    

    public ScoreScene (GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Score Scene");
    }
        
    public void loadScores(){
        var x = new ArrayList<Pair<String, Integer>>();
        

            File f = new File("scores.txt");

            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new FileReader(f));

                Scanner s = new Scanner(reader);
                while (s.hasNext()){
                    String[] parts = s.next().split(":");
                    var p = new Pair<String, Integer>(parts[0],Integer.parseInt(parts[1]));
                    x.add(p);
                }
                s.close();
    
                scores.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    
                for(Pair<String, Integer> i : x){
                    wrapper.add(i);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        


    }
    public void writeScores(){
        var file = new File("scores.txt");
        try {
            file.createNewFile();   
            FileWriter fw = new FileWriter(file);

            var bw= new BufferedWriter(fw);
        
            System.out.println(scores.size());
            for(Pair<String,Integer> i:wrapper.get()){
                String s = i.getKey() + ":"+i.getValue()+"\n";
                bw.write(s);
            }
            bw.close();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

            
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

        var sl = new ScoresList();
        wrapper.bind(sl.getScoreProperty());

        loadScores();

        var b = new Pair<String,Integer>("Maks",500);
        var c = new Pair<String,Integer>("Toby",2000);
        var d = new Pair<String,Integer>("Toby",7000);
        sl.makeScoreBox(c);
        
        wrapper.add(d);
        writeScores();



        elements.getChildren().addAll(title,sl);
        mainPane.setCenter(elements);
        
    }

}