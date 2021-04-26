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

/**
 * A ScoreBox is a component that lists scores in a box
 */
public class ScoreBox extends VBox {

    ObservableList<Pair<String, Integer>> scoreList;
    SimpleListProperty<Pair<String, Integer>> scores;
    StringProperty name;
    HBox scoreBox;
    ArrayList<String> lostPlayers;

    /**
     * Creates a new empty Score Box
     */
    public ScoreBox() {
        lostPlayers = new ArrayList<String>();
        this.scores = new SimpleListProperty<Pair<String, Integer>>();

        // Sets a change listener on scores that calls updateScores if it changes
        scores.addListener((ListChangeListener<Pair<String, Integer>>) (c -> updateScores()));
    }

    /**
     * Forces an update on the score box
     */
    public void updateScores() {
        // Clear all scores
        this.getChildren().clear();

        // Add them back again
        for (Pair<String, Integer> i : scores) {
            this.scoreBox = makeScoreItem(i);
            this.getChildren().add(scoreBox);
        }
    }

    /**
     * Adds a player to the list of dead players
     * 
     * @param s name of the player
     */
    public void addLostPlayer(String s) {
        lostPlayers.add(s);
    }

    /**
     * Makes a single score item
     * 
     * @param x pair containing a name and a score
     * @return score item
     */
    public HBox makeScoreItem(Pair<String, Integer> x) {
        var box = new HBox(8);

        box.setAlignment(Pos.CENTER);

        var name = new Text(x.getKey());
        var score = new Text(Integer.toString(x.getValue()));

        // If the current player is in the list of lost players then strike through them
        if (lostPlayers.contains(x.getKey())) {
            name.setStyle("-fx-strikethrough: true;");
        }

        name.getStyleClass().add("scorelist");
        score.getStyleClass().add("scorelist");

        box.getChildren().addAll(name, score);

        return box;
    }

    /**
     * Gets the scores property
     * 
     * @return list of scores
     */
    public ListProperty<Pair<String, Integer>> getScoresProperty() {
        return this.scores;
    }

}
