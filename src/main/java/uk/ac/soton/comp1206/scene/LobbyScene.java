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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.component.ChannelChat;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Lobby Scene displays a Lobby and serves as a place
 * for the player to wait until their game start
 */
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

    /**
     * Handles a key press in the lobby scene
     */
    public void handleKeyPress(KeyEvent e) {
        KeyCode k = e.getCode();
        if (k == KeyCode.ESCAPE) {
            communicator.send("PART");
            executor.shutdownNow();
            gameWindow.startMenu();
        }
    }

    /**
     * Creates a new Lobby Scene
     * @param gameWindow the game window
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        this.communicator = gameWindow.getCommunicator();
        logger.info("Creating Lobby Scene");
        this.executor = Executors.newSingleThreadScheduledExecutor();
        x = new ArrayList<String>();
    }


    
    /**
     * Requests the channel list from the server
     */
    private void requestChannels() {
        if (inChannel) {
            this.communicator.send("USERS");
        }
        this.communicator.send("LIST");
        this.loop = executor.schedule(() -> requestChannels(), 2000, TimeUnit.MILLISECONDS);
    }

    /**
     * Initialise the Lobby
     */
    @Override
    public void initialise() {
        this.communicator.addListener(message -> Platform.runLater(() -> this.handleMessage(message)));
        requestChannels();
    }

    /**
     * Handles a new message from the server
     * @param s the received message
     */
    private void handleMessage(String s) {
        String[] parts = s.split(" ", 2);
        String header = parts[0];

        if (header.equals("CHANNELS")) {
            if (parts.length == 1) {
                channelList.getChildren().clear();
                return;
            }
            String message = parts[1];
            List<String> list = Arrays.asList(message.split("\\s+"));

            if (!list.equals(x)) {
                channelList.getChildren().clear();

                for (String i : list) {
                    var q = new Text(i);
                    q.getStyleClass().add("channelItem");
                    q.setOnMouseClicked(e -> this.communicator.send("JOIN " + i));
                    channelList.getChildren().add(q);

                }
                x.clear();
                x.addAll(list);
            }
        }
        if (header.equals("JOIN")) {
            inChannel = true;

            channelChat = new ChannelChat(gameWindow);

            main.getChildren().add(channelChat);
        }
        if (header.equals("ERROR")) {
            String message = parts[1];
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(message);
            logger.error(message);
            alert.showAndWait();
        }
        if (header.equals("HOST")) {
            channelChat.revealStartButton();
        }
        if (header.equals("PARTED")) {
            main.getChildren().remove(channelChat);
            inChannel = false;
        }
        if (header.equals("USERS")) {
            String message = parts[1];
            List<String> list = Arrays.asList(message.split("\\s+"));
            channelChat.updateUsers(list);
        }
        if (header.equals("NICK")) {
            String message = parts[1];
        }
        if (header.equals("START")) {
            executor.shutdownNow();
            communicator.clearListeners();
            gameWindow.startMultiChallenge();
            // start game
        }
        if (header.equals("QUIT")) {
            // quit
        }
        if (header.equals("MSG")) {
            String[] subParts = parts[1].split(":");
            if (subParts.length == 2) {
                String sender = subParts[0];
                String message = subParts[1];
                channelChat.addMessage(sender, message);
            }

        }

    }

    /**
     * Builds the Lobby
     */
    @Override
    public void build() {

        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

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
                this.communicator.send("CREATE " + name);
            }
        });

        leftPane.getChildren().addAll(currentGames, hostNew, g, channelList);
        main.getChildren().add(leftPane);

        main.setMaxWidth(gameWindow.getWidth());
        main.setMaxHeight(gameWindow.getHeight());
        main.getStyleClass().add("menu-background");

        root.getChildren().add(main);

        Platform.runLater(() -> scene.setOnKeyPressed(e -> handleKeyPress(e)));
    }
}
