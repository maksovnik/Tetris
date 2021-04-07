package uk.ac.soton.comp1206.event;

import java.util.List;

import javafx.util.Pair;

public interface MultiScoreListener {
    public void setScores(List<Pair<String, Integer>> x);
}
