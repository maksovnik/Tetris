package uk.ac.soton.comp1206.component;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
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

public class ChannelChat extends VBox {

    String name;
    GameWindow gameWindow;
    Communicator communicator;
    private VBox messages;
    HBox users;
    List<String> x;
    ScrollPane sp;

    private Node start;

    public ChannelChat(GameWindow g, String name) {
        this.gameWindow = g;
        this.name = name;
        this.communicator = gameWindow.getCommunicator();
        x = new ArrayList<String>();
        build();
    }

    public void addMessage(String sender, String message) {
        var messageObject = new Text(sender + " " + message);
        messageObject.getStyleClass().add("messages");
        messages.getChildren().add(messageObject);
        sp.layout();
        sp.setVvalue(1);
    }

    public void updateUsers(List<String> list) {

        if (!list.equals(x)) {
            users.getChildren().clear();
            for (String i : list) {
                var b = new Text(i);
                b.getStyleClass().add("channelItem");
                users.getChildren().add(b);
            }
            x.clear();
            x.addAll(list);
        }

    }

    public void revealStartButton() {
        start.setOpacity(1);
    }

    public void build() {

        sp = new ScrollPane();
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);

        sp.setStyle("-fx-background-color: rgba(0,0,0,0.5);");

        var entry = new TextField();
        messages = new VBox(2);

        entry.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                String text = entry.getText();
                entry.clear();
                String[] parts = text.split(" ", 2);
                if (parts[0].equals("/nick")) {
                    if (parts.length > 1) {
                        this.communicator.send("NICK " + parts[1]);
                    }
                    return;
                }
                this.communicator.send("MSG " + text);
            }
        });
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(messages, Priority.ALWAYS);
        VBox.setVgrow(sp, Priority.ALWAYS);

        users = new HBox(4);

        var buttons = new HBox(4);

        var leave = new Button("Leave");

        leave.setOnMouseClicked(e -> {
            this.communicator.send("PART");
        });

        start = new Button("Start");

        start.setOnMouseClicked(e -> {
            this.communicator.send("START");
        });

        start.setOpacity(0);

        buttons.getChildren().addAll(leave, start);
        sp.setContent(messages);

        getChildren().addAll(users, sp, buttons, entry);
    }

}
