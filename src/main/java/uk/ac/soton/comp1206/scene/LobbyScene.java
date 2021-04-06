package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.component.ChannelChat;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;


public class LobbyScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(LobbyScene.class);
    ScheduledExecutorService executor;
    Communicator communicator;
    VBox channelList;

    VBox topBox;

    List<String> x;
    ScheduledFuture<?> loop;
    private HBox main;
    boolean inChannel;
    
    private ChannelChat channelChat;
    
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        this.communicator = gameWindow.getCommunicator();
        logger.info("Creating Lobby Scene");
        this.executor = Executors.newSingleThreadScheduledExecutor();
        x = new ArrayList<String>();
    }

    private void requestChannels(){
        if(inChannel){
            this.communicator.send("USERS");
        }
        this.communicator.send("LIST");
        this.loop = executor.schedule(() -> requestChannels(), 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void initialise() {
        System.out.println("sjdhfghjsdgfsgdhfjsghfjdg");
        this.communicator.addListener(message -> Platform.runLater(() -> this.handleMessage(message)));
        requestChannels();
    }



    private void handleMessage(String s){
        String[] parts = s.split(" ",2);
        String header = parts[0];


        if(header.equals("CHANNELS")){
            if(parts.length==1){
                channelList.getChildren().clear();
                return;
            }
            System.out.println(Arrays.toString(parts));
            String message = parts[1];
            List<String> list = Arrays.asList(message.split("\\s+"));

            if(!list.equals(x)){
                channelList.getChildren().clear();

                for(String i: list){
                    var q = new Text(i);
                    q.getStyleClass().add("channelItem");
                    q.setOnMouseClicked(e -> this.communicator.send("JOIN "+i));
                    channelList.getChildren().add(q);
                    
                }
                x.clear();
                x.addAll(list);
            }
        }
        if(header.equals("JOIN")){
            inChannel = true;
            String channelName = parts[1];

            channelChat = new ChannelChat(gameWindow,channelName);


            main.getChildren().add(channelChat);
        }
        if(header.equals("HOST")){

        }
        if(header.equals("ERROR")){
            String message = parts[1];
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(message);
            logger.error(message);
            alert.showAndWait();
        }
        if(header.equals("NICK")){
            String message = parts[1];
        }
        if(header.equals("HOST")){
            channelChat.revealStartButton();
        }
        if(header.equals("PARTED")){
            main.getChildren().remove(channelChat);
            inChannel = false;
        }
        if(header.equals("USERS")){
            String message = parts[1];
            List<String> list = Arrays.asList(message.split("\\s+"));
            channelChat.updateUsers(list);
        }
        if(header.equals("NICK")){
            String message = parts[1];
        }
        if(header.equals("START")){
            //start game
        }
        if(header.equals("QUIT")){
            //quit
        }
        if(header.equals("MSG")){
            String[] subParts = parts[1].split(":");
            if(subParts.length ==2){
                String sender=subParts[0];
                String message=subParts[1];
                channelChat.addMessage(sender,message);
            }

        }
    
    }
    @Override
    public void build() {
        
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());


        main = new HBox();
        
        var leftPane = new VBox();
        channelList = new VBox();

        
        var g = new TextField();
        var currentGames = new Text("Current Games");
        currentGames.getStyleClass().add("title");
        var hostNew = new Text("Host New Game");
        hostNew.getStyleClass().add("heading");

        hostNew.setOnMousePressed(e -> g.setOpacity(1));
        
        g.setOpacity(0);

        g.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                g.setOpacity(0);
                String name = g.getText();
                g.clear();
                this.communicator.send("CREATE " +name);
            }
        });

        leftPane.getChildren().addAll(currentGames,hostNew,g,channelList);
        main.getChildren().add(leftPane);

        main.setMaxWidth(gameWindow.getWidth());
        main.setMaxHeight(gameWindow.getHeight());
        main.getStyleClass().add("menu-background");

        root.getChildren().add(main);
    }
}
