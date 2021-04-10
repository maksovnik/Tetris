package uk.ac.soton.comp1206.component;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.event.SettingsListener;
import uk.ac.soton.comp1206.ui.GameWindow;

public class Settings extends VBox{
    SettingsListener sl;
    boolean isVisible;
    Pane parent;

    public Settings(int i, int j){
        hide();
        setMaxHeight(i);
        setPadding(new Insets(15));
        setMaxWidth(j);
        getStyleClass().add("settings");
        setAlignment(Pos.BOTTOM_CENTER);
        var t1 = new Text("Exit");
        t1.getStyleClass().add("menuItem");

        getChildren().add(t1);

        t1.setOnMouseClicked(e -> {
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
