package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;

public class ScoreBox extends VBox{

    ObservableList<Pair<String, Integer>> scoreList;
    SimpleListProperty<Pair<String, Integer>> scores;
    StringProperty name;
    HBox scoreBox;
    
    public ScoreBox(){
        this.scores=new SimpleListProperty<Pair<String, Integer>>();
        setOpacity(0);

        scores.addListener((ListChangeListener<Pair<String,Integer>>)(c -> updateScores()));
    }

    public void updateScores(){
        System.out.println("MMUAHAAHAHAHHAAHAH");
        this.getChildren().clear();
        
        for(Pair<String, Integer> i : scores){
            this.scoreBox = makeScoreBox(i);
            this.getChildren().add(scoreBox);
        }
    }

    public HBox makeScoreBox(Pair<String, Integer> x){
        var h = new HBox(8);

        h.setAlignment(Pos.CENTER);
        var n = new Text(x.getKey());
        var s = new Text(Integer.toString(x.getValue()));

        n.getStyleClass().add("scorelist");
        s.getStyleClass().add("scorelist");
        h.getChildren().addAll(n,s);

        return h;
    }

    public ListProperty<Pair<String, Integer>> getScoreProperty() {
        return this.scores;
    }
    
    public void reveal(){
        final FadeTransition fader = new FadeTransition(new Duration(300.0), this);
        fader.setFromValue(0.0);
        fader.setToValue(1.0);
        
        fader.play();
    }
    public StringProperty getNameProperty() {
        return this.name;
    }

}
