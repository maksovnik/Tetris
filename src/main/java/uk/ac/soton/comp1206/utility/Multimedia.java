package uk.ac.soton.comp1206.utility;

import java.util.concurrent.SynchronousQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.media.AudioClip;

public class Multimedia{



    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    private AudioClip audio;
    private boolean loop;
    private SynchronousQueue<String> queue;


    public Multimedia(boolean loop){

        this.queue = new SynchronousQueue<String>();
        this.loop = loop;

        new Thread(() -> {
            while(true){
                get();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void playSound(String file) {
        String toPlay = Multimedia.class.getResource("/" + file).toExternalForm();

        logger.info("Playing audio: " + toPlay);

        try {

            if(audio != null){
                audio.stop();
            }

            audio = new AudioClip(toPlay);

            audio.setVolume(0.5f);

            if(loop){
                audio.setCycleCount(-1);
            }
        
            audio.play();

        
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }
    }

    public void get(){
        String file;
        try {
            file = (String) queue.take();
            playSound(file);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       
    }

    public void put(String file){
        try {
            queue.put(file);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}