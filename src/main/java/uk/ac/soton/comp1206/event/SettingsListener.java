package uk.ac.soton.comp1206.event;


/**
 * The Settings listener is used to handle events in the Settings pane
 */
public interface SettingsListener {
        /**
     * Handle a event that the settings window is hidden
     */
    public void onHide();
    /**
     * Handle a event that the settings window is hidden
    */
    public void onShow();
    /**
     * Handle a event that the settings window is hard closed(Scene exit requested)
    */
    public void onExit();
}
