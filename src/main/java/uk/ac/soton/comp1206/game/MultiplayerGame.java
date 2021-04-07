package uk.ac.soton.comp1206.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.util.Pair;
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

    public MultiplayerGame(int cols, int rows, GameWindow g) {
        super(cols, rows);
        this.communicator = g.getCommunicator();



        this.communicator.addListener(message -> Platform.runLater(() -> this.handleMessage(message)));
        this.executor = Executors.newSingleThreadScheduledExecutor();
        System.out.println("testingtesting");

        this.score.addListener((c,a,b) -> updateScore(b));
        this.lives.addListener((c,a,b) -> updateLives(b));


        requestPieces(5);
        
        requestLoop();
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
    
    private void requestLoop(){
        this.communicator.send("SCORES");
        this.loop = executor.schedule(() -> requestLoop(), 2000, TimeUnit.MILLISECONDS);
    }
    private void handleMessage(String s){
        String[] parts = s.split(" ",2);
        String header = parts[0];
        System.out.println(s);
        if(header.equals("PIECE")){
            logger.info("Here is the number: {}",parts[1]);
            try {
				queue.put(Integer.parseInt(parts[1]));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        if(header.equals("SCORES")){
            String[] playerData = parts[1].split("\n");
            var dead = new ArrayList<String>();
            var t = new ArrayList<Pair<String,Integer>>();
            for(String i:playerData){
                String[] x = i.split(":");
                var g = new Pair<String,Integer>(x[0],Integer.parseInt(x[1]));
                t.add(g);
                if(x[2].equals("DEAD")){
                    dead.add(x[0]);
                }
            }
            t.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            msl.setScores(t);
            ppl.lostPlayers(dead);
        }
    }

    public void requestPieces(int num){
        for(int i=0; i<num;i++){
            System.out.println("REQUESTING");
            communicator.send("PIECE");
        }
    }

    @Override
    public GamePiece spawnPiece(){
        int val;
        try {
            val = queue.take();
            var piece = GamePiece.createPiece(val);
            return piece;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void nextPiece(){
        requestPieces(2);
        super.nextPiece();
        
        
    }
    private void updateScore(Number b){
        this.communicator.send("SCORE "+b.intValue());
    }

    private void updateLives(Number b){
        this.communicator.send("LIVES "+b.intValue());
    }

    @Override
    public void afterPiece(){
        super.afterPiece();
    }
}