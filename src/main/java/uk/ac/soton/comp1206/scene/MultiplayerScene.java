package uk.ac.soton.comp1206.scene;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;
import uk.ac.soton.comp1206.component.Leaderboard;
import uk.ac.soton.comp1206.event.ScoreListener;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

public class MultiplayerScene extends ChallengeScene{
    
    
    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
    private ObservableList<Pair<String, Integer>> localScoreList;
    Communicator communicator;

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
    

    public void handleKeyPress(KeyEvent e){
        KeyCode k = e.getCode();
        String keyName = k.getName();
        
        super.handleKeyPress(e);

        if(k==KeyCode.ESCAPE){
            game.end();
        }
        
    }

    @Override
    public void build() {
        super.build();

        elements.getChildren().remove(this.level);
        elements.getChildren().remove(this.levelT);
        elements.getChildren().remove(this.multiplier);
        elements.getChildren().remove(this.multiplierT);
        elements.getChildren().remove(this.hscore);
        elements.getChildren().remove(this.hscoreT);

        var r = new Leaderboard();
        r.reveal();
        elements.getChildren().add(r);

        this.localScoreList = FXCollections.observableArrayList();
        var wrapper = new SimpleListProperty<Pair<String, Integer>>(this.localScoreList);
        r.getScoreProperty().bind(wrapper);

        ((MultiplayerGame) game).setMultiScoreListener(x -> {
            System.out.println("Helllo");
            localScoreList.setAll(x);
        });

    }
}
