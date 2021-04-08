package uk.ac.soton.comp1206.game;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.util.Pair;
import uk.ac.soton.comp1206.event.GameStartListener;
import uk.ac.soton.comp1206.event.MultiScoreListener;
import uk.ac.soton.comp1206.event.PlayerLostListener;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;


public class MultiplayerGame extends Game{

    Communicator communicator;
    LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer> ();
    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);
    ScheduledExecutorService executor;
    ScheduledFuture<?> loop;
    MultiScoreListener msl;
    PlayerLostListener ppl;

    boolean recievedInitial;
    GameStartListener gsl;

    public MultiplayerGame(int cols, int rows, GameWindow g) {
        super(cols, rows);
        this.communicator = g.getCommunicator();



        this.executor = Executors.newSingleThreadScheduledExecutor();
        
        
    }

    public void setPlayerLostListener(PlayerLostListener m){
        this.ppl = m;
    }

    public void setMultiScoreListener(MultiScoreListener m){
        this.msl = m;
    }
    public void end(){
        this.communicator.send("DIE");
        this.communicator.clearListeners();
        this.executor.shutdown();
        this.executor.shutdownNow();
        super.end();
    }

    @Override
    public void start(){
        super.start();
        requestLoop();
        this.score.addListener((c,a,b) -> communicator.send("SCORE "+b.intValue()));
        this.lives.addListener((c,a,b) -> communicator.send("LIVES "+b.intValue()));
    }
    
    private void requestLoop(){
        this.communicator.send("SCORES");
        this.loop = executor.schedule(() -> requestLoop(), 2000, TimeUnit.MILLISECONDS);
    }

    public void setGameStartListener(GameStartListener g){
        this.gsl = g;
    }

    public void addToQueue(String c){
        try {
            queue.put(Integer.parseInt(c));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getQueueSize(){
        return queue.size();
    }
    public void requestPieces(int num){
        for(int i=0; i<num;i++){
            System.out.println("REQUESTING PIECE");
            communicator.send("PIECE");
        }
    }

    @Override
    public GamePiece spawnPiece(){
        int val;
        try {
            val = queue.take();
            requestPieces(1);
            var piece = GamePiece.createPiece(val);
            return piece;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }



    @Override
    public void afterPiece(){
        super.afterPiece();
    }
}
