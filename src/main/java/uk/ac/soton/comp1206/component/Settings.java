package uk.ac.soton.comp1206.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.event.SettingsListener;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.utility.Utility;

public class Settings extends BorderPane{
    SettingsListener sl;
    boolean isVisible;
    Pane parent;
    private Slider slider1;
    private Slider slider2;

    private static final Logger logger = LogManager.getLogger(Settings.class);
    private TextField g1;
    private TextField g2;
    private Node t7;

    private TextField g3;


    public Settings(int i, int j){
        hide();
        setMaxHeight(i);
        setPadding(new Insets(15));
        setMaxWidth(j);
        getStyleClass().add("settings");

        var inner = new VBox();

        var t1 = new Text("Music Volume");
        var t2 = new Text("Sound Effect Volume");
        var t3 = new Text("Exit");
        var t4 = new Text("Server IP");
        var t5 = new Text("Server Port");
        var t8 = new Text("Resolution");
        var t6 = new Text("Reset Settings");
        t7 = new Text("Settings will be applied after restart");
        g1 = new TextField();
        g2 = new TextField();

        g3 = new TextField();

        //setAlignment(Pos.BOTTOM_CENTER);
        

        slider1 = new Slider(0, 1, 0.5);
        slider2 = new Slider(0, 1, 0.5);
        t1.getStyleClass().add("menuItem");
        t2.getStyleClass().add("menuItem");
        t3.getStyleClass().add("menuItem");
        t4.getStyleClass().add("menuItem");
        t5.getStyleClass().add("menuItem");
        t6.getStyleClass().add("menuItem");
        t8.getStyleClass().add("menuItem");
        t7.getStyleClass().add("error");

        g1.textProperty().addListener((a,b,c) -> Utility.reveal(200, t7));

        g2.textProperty().addListener((a,b,c) -> Utility.reveal(200, t7));
        g3.textProperty().addListener((a,b,c) -> Utility.reveal(200, t7));

        Multimedia.getMusicVolumeProperty().bind(slider1.valueProperty());
        Multimedia.getFXVolumeProperty().bind(slider2.valueProperty());
        
        inner.getChildren().addAll(t1,slider1,t2,slider2,t4,g1,t5,g2,t8,g3,t6);
        inner.setAlignment(Pos.CENTER);
        setAlignment(t3, Pos.CENTER);
        setCenter(inner);

        var g = new VBox(3);
        g.setAlignment(Pos.CENTER);
        
        g.getChildren().addAll(t7,t3);
        setBottom(g);

        t6.setOnMouseClicked(e -> setSettings(GameWindow.ip, GameWindow.port, GameWindow.bgVol, GameWindow.fxVol, GameWindow.width, GameWindow.height));
        t3.setOnMouseClicked(e -> {

            if(sl!=null){
                //sl.onExit();
            }
            hide();
        });

    }

    public void setSettings(String ip, String port, String bgVol, String fxVol, String width, String height){
        slider1.setValue(Double.parseDouble(bgVol));
        slider2.setValue(Double.parseDouble(fxVol));

        g1.setText(ip);
        g2.setText(port);
        g3.setText(width+"x"+height);

        t7.setOpacity(0);
    }

    public void toggle(){
        if(isVisible){
            hide();
        }
        else{
            show();
        }
    }
    public void onKeyPress(KeyEvent e){
        System.out.println("dskfjhsd");
        if (e.getCode() == KeyCode.ESCAPE) {
            hide();
        }
    }
    public void hide(){
        System.out.println("fg");
        if((slider1!=null)&&(slider2!=null)){
            var first = slider1.getValue();
            var second = slider2.getValue();
            var th = g1.getText();
            var th1 = g2.getText();

            var c = g3.getText().split("x");
            logger.info("First:{}  Second:{}",first,second);
            Utility.writeSettings(th,th1,first,second,c[0],c[1]);
        }

        
        if(parent!= null){
            parent.setEffect(null);
        }
        
        if(sl!=null){
            sl.onHide();
        }

        setOpacity(0);
        isVisible=false;
        setDisable(true);
    }
    
    public void setParent(Pane p){
        this.parent =p;
    }
    
    public void show(){

        
        GaussianBlur blur = new GaussianBlur(55); // 55 is just to show edge effect more clearly.
        parent.setEffect(blur);


        if(sl!=null){
            sl.onShow();
        }
        
        setOpacity(1);
        isVisible=true;
        setDisable(false);
    }

    public void setListener(SettingsListener s){
        this.sl = s;
    }
    

    
}
