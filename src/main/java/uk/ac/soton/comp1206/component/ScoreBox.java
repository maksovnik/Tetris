package uk.ac.soton.comp1206.component;

import java.util.ArrayList;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;

public class ScoreBox extends VBox {

    ObservableList<Pair<String, Integer>> scoreList;
    SimpleListProperty<Pair<String, Integer>> scores;
    StringProperty name;
    HBox scoreBox;
    ArrayList<String> lostPlayers;

    public ScoreBox() {

        lostPlayers = new ArrayList<String>();
        this.scores = new SimpleListProperty<Pair<String, Integer>>();

        scores.addListener((ListChangeListener<Pair<String, Integer>>) (c -> updateScores()));
    }

    public void updateScores() {
        this.getChildren().clear();

        for (Pair<String, Integer> i : scores) {
            this.scoreBox = makeScoreBox(i);
            this.getChildren().add(scoreBox);
        }
    }

    public void addLostPlayer(String s) {
        lostPlayers.add(s);
    }

    public HBox makeScoreBox(Pair<String, Integer> x) {
        var h = new HBox(8);

        h.setAlignment(Pos.CENTER);

        var n = new Text(x.getKey());

        if (lostPlayers.contains(x.getKey())) {
            n.setStyle("-fx-strikethrough: true;");
        }

        var s = new Text(Integer.toString(x.getValue()));

        n.getStyleClass().add("scorelist");
        s.getStyleClass().add("scorelist");
        h.getChildren().addAll(n, s);

        return h;
    }

    public ListProperty<Pair<String, Integer>> getScoreProperty() {
        return this.scores;
    }

    public StringProperty getNameProperty() {
        return this.name;
    }

}
