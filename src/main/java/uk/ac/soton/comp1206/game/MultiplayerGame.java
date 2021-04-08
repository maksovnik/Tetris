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
import uk.ac.soton.comp1206.event.MultiMessageListener;
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
    private MultiMessageListener mml;

    public MultiplayerGame(int cols, int rows, GameWindow g) {
        super(cols, rows);
        this.communicator = g.getCommunicator();


        this.communicator.addListener(message -> Platform.runLater(() -> this.handleMessage(message)));
    
        requestPieces(5);


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
        this.score.addListener((c,a,b) -> updateScore(b));
        this.lives.addListener((c,a,b) -> updateLives(b));
    }
    
    private void requestLoop(){
        this.communicator.send("SCORES");
        this.loop = executor.schedule(() -> requestLoop(), 2000, TimeUnit.MILLISECONDS);
    }

    public void setGameStartListener(GameStartListener g){
        this.gsl = g;
    }

    private void handleMessage(String s){
        String[] parts = s.split(" ",2);
        String header = parts[0];
        logger.info("Header is '{}' .. {}",header,(header.equals("MSG")));



        if(header.equals("PIECE")){
            try {
				queue.put(Integer.parseInt(parts[1]));
                System.out.println(queue.size());

                if((queue.size()==5)&&(!recievedInitial)){
                    logger.info("Recieved All Good pieces, gamme starting.");
                    start();
                    gsl.gameStart();
                    recievedInitial = true;
                    
                }

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

        if(header.equals("MSG")){
            var comps = parts[1].split(":");
            var sender = comps[0];
            var message = comps[1];
            mml.newMessage(sender,message);
        }
    }

    public void setMultiMessageListener(MultiMessageListener d){
        this.mml=d;
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
