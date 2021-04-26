package uk.ac.soton.comp1206.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.util.Pair;

/**
 * The Utility class provides a collection of static methods used for utility
 * tasks
 */
public class Utility {

    static String highName;
    static int highScore;
    public static FadeTransition fader;

    /**
     * Loads scores from a file
     * 
     * @return List of scores
     */
    public static ObservableList<Pair<String, Integer>> loadScores() {
        ObservableList<Pair<String, Integer>> x = FXCollections.observableArrayList();
        // ObservableList<Pair<String, Integer>> x = new ArrayList<Pair<String,
        // Integer>>();
        File f = new File("scores.txt");

        try {
            var q = f.createNewFile();

            if (q) {
                for (int i = 0; i < 10; i++) {
                    x.add(new Pair<String, Integer>("Oli", 5000));
                }
                writeScores(x);
            } else {

                BufferedReader reader = new BufferedReader(new FileReader(f));

                Scanner s = new Scanner(reader);
                while (s.hasNext()) {
                    String[] parts = s.next().split(":");
                    var p = new Pair<String, Integer>(parts[0], Integer.parseInt(parts[1]));
                    x.add(p);
                }
                s.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return x;

    }

    /**
     * Formats a string containing scores into an ObservableList of Pairs
     * 
     * @return ObservableList of scores
     */
    public static ObservableList<Pair<String, Integer>> getScoreList(String scores) {
        ObservableList<Pair<String, Integer>> x = FXCollections.observableArrayList();

        String[] w = scores.split(" ", 2);
        String header = w[0];

        if (!header.equals("HISCORES")) {
            return null;
        }

        String[] parts = scores.split(" ", 2);

        String[] newScores = parts[1].split("\n");
        for (String i : newScores) {
            String[] newParts = i.split(":");
            var p = new Pair<String, Integer>(newParts[0], Integer.parseInt(newParts[1]));
            x.add(p);
        }

        return x;
    }

    /**
     * Reveals a given node over a given period of time
     * 
     * @param node   a JavaFX Node
     * @param millis duration in millis
     */
    public static void reveal(Node node, double millis) {

        if (node == null || node.getOpacity() != 0) {
            return;
        }

        fader = new FadeTransition(new Duration(millis), node);
        fader.setFromValue(0.0);
        fader.setToValue(1.0);

        fader.play();
    }

    /**
     * Writes scores to a file
     * 
     * @param x List of Scores
     */
    public static void writeScores(List<Pair<String, Integer>> x) {
        var file = new File("scores.txt");
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);

            var bw = new BufferedWriter(fw);

            for (Pair<String, Integer> i : x) {
                String s = i.getKey() + ":" + i.getValue() + "\n";
                bw.write(s);
            }
            bw.close();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Fetches the high score from file
     */
    public static void fetchHighScore() {
        var x = loadScores();
        x.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        highName = x.get(0).getKey();
        highScore = x.get(0).getValue();
    }

    /**
     * Gets the name of the highest scoring player
     * 
     * @return the name of the highest scoring player
     */
    public static String getHighName() {
        return highName;
    }

    /**
     * Gets the highest score
     * 
     * @return the high score
     */
    public static int getHighScore() {
        return highScore;
    }

    /**
     * Bounce animation on a node for a given duration and amount
     * 
     * @param duration duraton in millis
     * @param node     a JavaFX Node
     * @param amount   the amount by which to "bounce"
     */
    public static void bounce(int duration, Node node, double amount) {
        var st = new ScaleTransition(Duration.millis(duration), node);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(amount);
        st.setToY(amount);
        st.setAutoReverse(true);
        st.setCycleCount(2);

        st.play();
    }

    /**
     * Writes settings to a file
     * 
     * @param ip     the ip address
     * @param port   the port
     * @param bgVol  the background volume
     * @param fxVol  the sound effects volume
     * @param width  the game width
     * @param height the game height
     */
    public static void writeSettings(String ip, String port, double bgVol, double fxVol, String width, String height) {
        var file = new File("settings.txt");

        FileWriter fw;
        try {
            file.createNewFile();
            fw = new FileWriter(file);

            var bw = new BufferedWriter(fw);
            bw.write("ip " + ip + "\n");
            bw.write("serverPort " + port + "\n");
            bw.write("musicVol " + String.valueOf(bgVol) + "\n");
            bw.write("soundFXVol " + String.valueOf(fxVol) + "\n");
            bw.write("width " + String.valueOf(width) + "\n");
            bw.write("height " + String.valueOf(height) + "\n");
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads settings from file into a HashMap
     * 
     * @return hashmap containing loaded settings
     */
    public static HashMap<String, String> loadSettings() {

        HashMap<String, String> hmap = new HashMap<String, String>();
        File f = new File("settings.txt");
        var x = new ArrayList<String[]>();
        try {

            BufferedReader reader = new BufferedReader(new FileReader(f));

            Scanner s = new Scanner(reader);
            while (s.hasNext()) {
                String y = s.nextLine();
                System.out.println(y);
                String[] parts = y.split(" ", 2);
                hmap.put(parts[0], parts[1]);
                x.add(parts);
            }
            s.close();

        } catch (IOException e) {
        }

        System.out.println(x);
        return hmap;
    }

    /**
     * Checks if the value of a given String is an integer
     * 
     * @param g string to be checked
     */
    public static boolean isInteger(String g) {
        try {
            Integer.parseInt(g);
            return true;
        } catch (NumberFormatException d) {
            return false;
        }
    }
}
