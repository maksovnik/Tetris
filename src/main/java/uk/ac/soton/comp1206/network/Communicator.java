package uk.ac.soton.comp1206.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.Alert;
import uk.ac.soton.comp1206.event.CommunicationsListener;

/**
 * Uses web sockets to talk to a web socket server and relays communication to
 * attached listeners
 */
public class Communicator {

    private static final Logger logger = LogManager.getLogger(Communicator.class);

    /**
     * Attached communication listeners listening to messages on this Communicator.
     * Each will be sent any messages.
     */
    private final List<CommunicationsListener> handlers = new ArrayList<>();

    private WebSocket ws = null;

    /**
     * Create a new communicator to the given web socket server
     *
     * @param server server to connect to
     */
    public WebSocketState getState() {
        return ws.getState();
    }

    public void setOnError(WebSocketAdapter e) {
        ws.addListener(e);
    }

    public Communicator(String server) {

        var socketFactory = new WebSocketFactory().setConnectionTimeout(1000);

        // Connect to the server
        try {
            ws = socketFactory.createSocket(server);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        ws.connectAsynchronously();

        // Add new listener with event handlers for socket communication
        ws.addListener(new WebSocketAdapter() {

            @Override
            public void onTextMessage(WebSocket websocket, String message) throws Exception {
                Communicator.this.receive(websocket, message);
            }

            @Override
            public void onPingFrame(WebSocket webSocket, WebSocketFrame webSocketFrame) throws Exception {
                logger.info("Ping? Pong!");
            }

            @Override
            public void onError(WebSocket webSocket, WebSocketException e) throws Exception {
                logger.error("Error:" + e.getMessage());
                // e.printStackTrace();
            }
        });

    }

    /**
     * Send a message to the server
     *
     * @param message Message to send
     */
    public void send(String message) {
        logger.info("Sending message: " + message);
        ws.sendText(message);
    }

    public void silentSend(String message) {
        ws.sendText(message);
    }

    /**
     * Add a new listener to receive messages from the server
     * 
     * @param listener the listener to add
     */
    public void addListener(CommunicationsListener listener) {
        this.handlers.add(listener);
    }

    /**
     * Clear all current listeners
     */
    public void clearListeners() {
        this.handlers.clear();
    }

    /**
     * Receive a message from the server. Relay to any attached listeners
     *
     * @param websocket the socket
     * @param message   the message that was received
     */
    private void receive(WebSocket websocket, String message) {
        for (CommunicationsListener handler : handlers) {
            handler.receiveCommunication(message);
        }
    }

}
