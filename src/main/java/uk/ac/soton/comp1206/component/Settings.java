package uk.ac.soton.comp1206.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.event.SettingsListener;
import uk.ac.soton.comp1206.utility.Multimedia;

public class Settings extends BorderPane{
    SettingsListener sl;
    boolean isVisible;
    Pane parent;

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


        var slider1 = new Slider(0, 1, 0.5);
        var slider2 = new Slider(0, 1, 0.5);
        t1.getStyleClass().add("menuItem");
        t2.getStyleClass().add("menuItem");
        t3.getStyleClass().add("menuItem");

        slider1.valueProperty().addListener((obs, oldValue, newValue) -> {
            Multimedia.setMusicVolume(newValue.doubleValue());
        });

        slider2.valueProperty().addListener((obs, oldValue, newValue) -> {
            Multimedia.setEffectsVolume(newValue.doubleValue());
        });
        
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

        //Platform.runLater(() -> scene.setOnKeyPressed(e -> onKeyPress(e)));
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
