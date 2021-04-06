package uk.ac.soton.comp1206.component;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ChannelChat extends ScrollPane{

    String name;
    GameWindow gameWindow;
    Communicator communicator;
    private VBox messages;

    public ChannelChat(GameWindow g, String name){
        this.gameWindow = g;
        this.name=name;
        this.communicator = gameWindow.getCommunicator();

        build();
    }

    public void addMessage(String sender, String message){
        var messageObject = new Text(sender + " " + message);
        messageObject.getStyleClass().add("messages");
        messages.getChildren().add(messageObject);
    }
    public void build(){

        this.setFitToHeight(true);
        this.setFitToWidth(true);

        var contents = new VBox();
        
        setStyle("-fx-background-color: rgba(0,0,0,0.5);");


        var entry = new TextField();
        messages = new VBox(2);

        entry.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                String text = entry.getText();
                entry.clear();
                this.communicator.send("MSG " +text);
            }
        });
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(messages, Priority.ALWAYS);

        
        contents.getChildren().addAll(messages,entry);
        setContent(contents);
    }

}
