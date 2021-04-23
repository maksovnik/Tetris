package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending
 * on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid
 * model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);
    private boolean hover = false;

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = { Color.TRANSPARENT, Color.DEEPPINK, Color.RED, Color.ORANGE, Color.YELLOW,
            Color.YELLOWGREEN, Color.LIME, Color.GREEN, Color.DARKGREEN, Color.DARKTURQUOISE, Color.DEEPSKYBLUE,
            Color.AQUA, Color.AQUAMARINE, Color.BLUE, Color.MEDIUMPURPLE, Color.PURPLE };

    private final double width;
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render
     * as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    private boolean showCenter;

    private static Color hoverColor =  Color.color(1.0, 1.0, 1.0, 0.5);

    /**
     * Create a new single Game Block
     * 
     * @param gameBoard the board this block belongs to
     * @param x         the column the block exists in
     * @param y         the row the block exists in
     * @param width     the width of the canvas to render
     * @param height    the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        // A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        // Do an initial paint
        paint();

        // When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    public static void setHoverColor(Color m){
        hoverColor =  m;
    }

    
    public void setShowCenter(boolean m) {
        showCenter = m;
        paint();
    }

    /**
     * When the value of this block is updated,
     * 
     * @param observable what was updated
     * @param oldValue   the old value
     * @param newValue   the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    public void paintHover() {
        final GraphicsContext gc = this.getGraphicsContext2D();
        gc.setFill(hoverColor);
        gc.fillRect(0.0, 0.0, this.width, this.height);
    }

    public void setHoverX(boolean m) {
        if(hover!=m){
            hover = m;
            paint();
        }
    }

    public void fadeOut() {

        paintEmpty();
        new AnimationTimer() {
            double opacity = 1;

            @Override
            public void handle(long now) {
                fadeFrame();
            }

            public void fadeFrame() {
                paintEmpty();
                this.opacity -= 0.025;
                if (this.opacity <= 0.0) {
                    this.stop();
                    return;
                }
                var gc = getGraphicsContext2D();
                gc.setFill(Color.color(0.0, 1.0, 0.0, this.opacity));
                gc.fillRect(0.0, 0.0, GameBlock.this.width, GameBlock.this.height);
            }

        }.start();
        ;
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        // If the block is empty, paint as empty
        if (value.get() == 0) {
            paintEmpty();
        } else {
            // If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }

        if (showCenter) {
            paintCircle();
        }

        if (hover) {
            paintHover();
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        // Clear
        gc.clearRect(0, 0, width, height);

        // Fill
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, width, height);

        // Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, width, height);

    }

    /**
     * Paint this canvas with the given colour
     * 
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        // Clear
        gc.clearRect(0, 0, width, height);

        // Colour fill
        gc.setFill(colour);
        gc.fillRect(0, 0, width, height);

        // Triangle
        gc.setFill(Color.color(1.0, 1.0, 1.0, 0.12));
        var xCords = new double[] { 0.0, width, 0.0 };
        var yCords = new double[] { 0.0, height, height };
        gc.fillPolygon(xCords, yCords, 3);

        // Left Bevel
        gc.setFill(Color.color(1.0, 1.0, 1.0, 0.3));
        gc.fillRect(0.0, 0.0, 4, height);

        // Right Bevel
        gc.setFill(Color.color(0.0, 0.0, 0.0, 0.5));
        gc.fillRect(width - 4, 0.0, width, height);

        // Bottom Bevel
        gc.setFill(Color.color(0.0, 0.0, 0.0, 0.5));
        gc.fillRect(0.0, height - 4, width, height);

        // Border
        gc.setFill(Color.color(0.0, 0.0, 0.0, 0.5));
        gc.strokeRect(0, 0, width, height);
    }

    public void paintCircle() {
        var gc = getGraphicsContext2D();
        var radius = 8;
        gc.setFill(Color.color(1.0, 1.0, 1.0, 0.5));
        gc.fillOval((width / 2) - radius, (height / 2) - radius, radius * 2, radius * 2);
    };

    /**
     * Get the column of this block
     * 
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * 
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * 
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual
     * block to a corresponding block in the Grid.
     * 
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

}
