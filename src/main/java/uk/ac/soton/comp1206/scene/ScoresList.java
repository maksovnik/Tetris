package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;

public class ScoresList extends VBox{

    ObservableList<Pair<String, Integer>> scoreList = FXCollections.observableArrayList();
    SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<Pair<String, Integer>>(scoreList);
    StringProperty name;
    HBox scoreBox;
    
    public ScoresList(){

        scores.addListener(new ListChangeListener<Pair<String,Integer>>() {
            @Override
            public void onChanged(Change<? extends Pair<String,Integer>> change) {
                updateScores();
            }
        });
    }

    public void setScore(ListProperty<Pair<String, Integer>> s){
        scores.set(s);
    }

    public void updateScores(){
        //scoreList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
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
