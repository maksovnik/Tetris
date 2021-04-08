package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Pair;
import uk.ac.soton.comp1206.component.ScoreBox;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Utility;

public class MultiplayerScene extends ChallengeScene{
    
    
    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
    private ObservableList<Pair<String, Integer>> localScoreList;
    Communicator communicator;
    private TextField sendBox;
    private Text message;
    private ScoreBox r;


    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        this.communicator = gameWindow.getCommunicator();
        
    }



    @Override
    public void setupGame(){
        logger.info("Starting a new Multiplayer game");

        //Start new game
        game = new MultiplayerGame(5, 5,gameWindow);
    }


    @Override
    public void handleKeyPress(KeyEvent e){
        System.out.println("HAHAAHAHA");

        KeyCode k = e.getCode();
        String keyName = k.getName();

        super.handleKeyPress(e);
        System.out.println(keyName);

        if(keyName.equals("T")){
            sendBox.setDisable(false);
            sendBox.requestFocus();
            sendBox.setOpacity(1);
        }
        
    }

    @Override
    public void initialise() {
        System.out.println("adfsdfsdflklhjflkjlshfklsjdfkjasdf");
        
        game.requestPieces(5);
        this.communicator.addListener(message -> Platform.runLater(() -> receiveMessage(message.trim())));

    }

    private void receiveMessage(String s){
        String[] parts = s.split(" ",2);
        String header = parts[0];

        if(header.equals("PIECE")){

            game.addToQueue(parts[1]);

            if((game.getQueueSize()==5) && board.isDisabled()){
                logger.info("Recieved All Good pieces, game starting.");
                game.start();
                board.setDisable(false);
            }
        }

        if(header.equals("MSG")){
            var comps = parts[1].split(":");
            var sender = comps[0];
            var m = comps[1];

            message.setText(sender+" "+m);
            message.setOpacity(1);
        }

        if(header.equals("SCORES")){


            var playerData = parts[1].split("\n");

            //var t = new ArrayList<Pair<String,Integer>>();
            localScoreList.clear();

            for(String i:playerData){
                var x = i.split(":");
                var g = new Pair<String,Integer>(x[0],Integer.parseInt(x[1]));
                
                if(!localScoreList.contains(g))
                localScoreList.add(g);

                if(x[2].equals("DEAD")){
                    r.addLostPlayer(x[0]);
                }
            }
            
            localScoreList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        }

    }

    @Override
    public void build() {
        super.build();

        this.board.setDisable(true);


        
        elements.getChildren().remove(this.level);
        elements.getChildren().remove(this.levelT);
        elements.getChildren().remove(this.multiplier);
        elements.getChildren().remove(this.multiplierT);
        elements.getChildren().remove(this.hscore);
        elements.getChildren().remove(this.hscoreT);

        r = new ScoreBox();

        Utility.reveal(300, r);
        elements.getChildren().add(r);

        game.setGameEndListener(() -> Platform.runLater(() -> gameWindow.startScores(game,localScoreList)));

        this.localScoreList = FXCollections.observableArrayList();
        var wrapper = new SimpleListProperty<Pair<String, Integer>>(this.localScoreList);
        r.getScoreProperty().bind(wrapper);
        


        
        message = new Text();
        sendBox = new TextField();
        
        sendBox.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                String text = sendBox.getText();
                sendBox.clear();
                this.communicator.send("MSG " +text);
                sendBox.setOpacity(0);
                sendBox.setDisable(true);
            }

            if (e.getCode() == KeyCode.ENTER) {
                e.consume();           
            }
        });
        message.setOpacity(0);
        sendBox.setOpacity(0);
        message.getStyleClass().add("messages");
        message.setStyle("-fx-font-size: 20px;");

        sendBox.setDisable(true);
        y.getChildren().add(0,sendBox);
        k.getChildren().add(1,message);


    }
}
