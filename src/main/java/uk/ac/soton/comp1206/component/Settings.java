package uk.ac.soton.comp1206.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
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
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.utility.Utility;

public class Settings extends BorderPane{
    SettingsListener sl;
    boolean isVisible;
    Pane parent;
    private Slider slider1;
    private Slider slider2;
    private String ip;
    private String port;
    private static final Logger logger = LogManager.getLogger(Settings.class);

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



        //setAlignment(Pos.BOTTOM_CENTER);


        slider1 = new Slider(0, 1, 0.5);
        slider2 = new Slider(0, 1, 0.5);
        t1.getStyleClass().add("menuItem");
        t2.getStyleClass().add("menuItem");
        t3.getStyleClass().add("menuItem");

        Multimedia.getMusicVolumeProperty().bind(slider1.valueProperty());
        Multimedia.getFXVolumeProperty().bind(slider2.valueProperty());
        
        inner.getChildren().addAll(t1,slider1,t2,slider2);
        inner.setAlignment(Pos.CENTER);
        setAlignment(t3, Pos.CENTER);
        setCenter(inner);

        setBottom(t3);

        t3.setOnMouseClicked(e -> {

            if(sl!=null){
                sl.onExit();
            }
            hide();
        });

        var b = Utility.loadSettings();
        if(!b.isEmpty()){
            this.ip = b.get(0)[1];
            this.port = b.get(1)[1];
            slider1.setValue(Double.parseDouble(b.get(2)[1]));
            slider2.setValue(Double.parseDouble(b.get(3)[1]));
        }
        else{
            this.ip= "discord.ecs.soton.ac.uk";
            this.port = "9700";
            slider1.setValue(0.5);
            slider2.setValue(0.5);
            Utility.writeSettings(this.ip, this.port,0.5,0.5);
        }

    }

    public String getIp(){
        return ip;
    }
    public String getPort(){
        return port;
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
            logger.info("First:{}  Second:{}",first,second);
            Utility.writeSettings("discord.ecs.soton.ac.uk","9700",first,second);
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
