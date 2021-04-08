package uk.ac.soton.comp1206.scene;

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

public class MultiplayerScene extends ChallengeScene{
    
    
    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
    private ObservableList<Pair<String, Integer>> localScoreList;
    Communicator communicator;
    private TextField sendBox;
    private Text message;

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

        var r = new ScoreBox();
        r.reveal();
        elements.getChildren().add(r);

        game.setGameEndListener(() -> Platform.runLater(() -> gameWindow.startScores(game,localScoreList)));

        this.localScoreList = FXCollections.observableArrayList();
        var wrapper = new SimpleListProperty<Pair<String, Integer>>(this.localScoreList);
        r.getScoreProperty().bind(wrapper);
        
        ((MultiplayerGame) game).setGameStartListener(() -> {
            this.board.setDisable(false);
        });

        ((MultiplayerGame) game).setMultiScoreListener(x -> localScoreList.setAll(x));

        
        ((MultiplayerGame) game).setPlayerLostListener(x -> {
            r.addLostPlayer(x);
        });

        ((MultiplayerGame) game).setMultiMessageListener((s,m) -> {
            System.out.println("New message");
            message.setText(s+" "+m);
            message.setOpacity(1);
        });

        
        
        message = new Text("Hellllo");
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
        sendBox.setOpacity(0);
        message.getStyleClass().add("messages");
        message.setStyle("-fx-font-size: 20px;");

        sendBox.setDisable(true);
        y.getChildren().add(0,sendBox);
        k.getChildren().add(1,message);


    }
}
