package uk.ac.soton.comp1206.component;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Channel Chat is a chat window that allows the user to communicate while
 * waiting in a lobby for a game to start.
 **/
public class ChannelChat extends VBox {

    private GameWindow gameWindow;
    private Communicator communicator;
    private VBox messages;
    private HBox users;
    private List<String> userList;
    private ScrollPane sp;

    private Button start;
    private Button leave;
    private TextField entry;

    /**
     * Creates a new ChannelChat object
     * 
     * @param g the gameWindow
     **/
    public ChannelChat(GameWindow g) {
        this.gameWindow = g;
        this.communicator = gameWindow.getCommunicator();
        userList = new ArrayList<String>();
        build();
        initialise();
    }

    /**
     * Adds a message to the channel chat
     * 
     * @param sender  the person who sent the message
     * @param message the contents of the message
     **/
    public void addMessage(String sender, String message) {
        var messageObject = new Text(sender + " " + message);
        messageObject.getStyleClass().add("messages");

        // Adds the new message to the list of messages
        messages.getChildren().add(messageObject);

        // Scrolls to bottom of message box
        sp.layout();
        sp.setVvalue(1);
    }

    /**
     * Updates the list of users in a channel
     * 
     * @param list new user list
     **/
    public void updateUsers(List<String> list) {
        
        //Checks if the given list differs from the current list
        if (!list.equals(userList)) {

            // if they differ then update and recreate user Text objects
            users.getChildren().clear();
            for (String i : list) {
                var b = new Text(i);
                b.getStyleClass().add("channelItem");
                users.getChildren().add(b);
            }
            userList.clear();
            userList.addAll(list);
        }

    }

    /**
     * Reveals the start button if the user is the host
     **/
    public void revealStartButton() {
        start.setOpacity(1);
    }

    /**
     * Builds the elements of the channel chat
     **/
    public void build() {

        // Create a new scroll pane so we can scroll messages
        sp = new ScrollPane();
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);

        sp.setStyle("-fx-background-color: rgba(0,0,0,0.5);");

        entry = new TextField();
        messages = new VBox(2);

        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(messages, Priority.ALWAYS);
        VBox.setVgrow(sp, Priority.ALWAYS);

        users = new HBox(4);

        var buttons = new HBox(4);

        leave = new Button("Leave");
        start = new Button("Start");

        // At first, hide the start button - it will be shown
        // if server affirms that the user is the host
        start.setOpacity(0);

        buttons.getChildren().addAll(leave, start);
        sp.setContent(messages);

        getChildren().addAll(users, sp, buttons, entry);
    }

    /**
     * Initialises the elements of the channel chat
     **/
    public void initialise() {
        
        leave.setOnMouseClicked(e -> {
            this.communicator.send("PART");
        });
        start.setOnMouseClicked(e -> {
            this.communicator.send("START");
        });
        entry.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                String text = entry.getText();
                entry.clear();

                //Splits the message by space characters
                String[] parts = text.split(" ", 2);

                //If the first part was /nick then send a change username request
                if (parts[0].equals("/nick")) {
                    if (parts.length > 1) {
                        this.communicator.send("NICK " + parts[1]);
                    }
                    return;
                }

                //Send the message
                this.communicator.send("MSG " + text);
            }
        });
    }

}
