package uk.ac.soton.comp1206.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

public class Utility {

    static String highName;
    static int highScore;

    public static ObservableList<Pair<String, Integer>> loadScores(){
        ObservableList<Pair<String, Integer>> x = FXCollections.observableArrayList();
       // ObservableList<Pair<String, Integer>> x = new ArrayList<Pair<String, Integer>>();
        File f = new File("scores.txt");

        try {
            var q = f.createNewFile();

            if(q){
                for(int i=0;i<10;i++){
                    x.add(new Pair<String,Integer>("Oli",5000));
                }
                writeScores(x);
            }
            else{
    
                BufferedReader reader = new BufferedReader(new FileReader(f));
    
                Scanner s = new Scanner(reader);
                while (s.hasNext()){
                    String[] parts = s.next().split(":");
                    var p = new Pair<String, Integer>(parts[0],Integer.parseInt(parts[1]));
                    x.add(p);
                }
                s.close();
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return x;
       
    }

    public static ObservableList<Pair<String, Integer>> getScoreArrayList(String scores){
        ObservableList<Pair<String, Integer>> x = FXCollections.observableArrayList();
        
        String[] w = scores.split(" ",2);
        String header = w[0];
        
        if(!header.equals("HISCORES")){
            return null;
        }

        String[] parts = scores.split(" ",2);
        
        String[] newScores = parts[1].split("\n");
        for(String i: newScores){
            String[] newParts = i.split(":");
            var p = new Pair<String, Integer>(newParts[0],Integer.parseInt(newParts[1]));
            x.add(p);
        }

        return x;
    }




    public static void writeScores(List<Pair<String, Integer>> x){
        var file = new File("scores.txt");
        try {
            file.createNewFile();   
            FileWriter fw = new FileWriter(file);

            var bw= new BufferedWriter(fw);
        
            for(Pair<String,Integer> i: x){
                String s = i.getKey() + ":"+i.getValue()+"\n";
                bw.write(s);
            }
            bw.close();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }

    public static void fetchHighScore(){
        var x = loadScores();
        x.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        highName = x.get(0).getKey();
        highScore = x.get(0).getValue();
    }

    public static String getHighName(){
        return highName;
    }
    public static int getHighScore(){
        return highScore;
    }
}
