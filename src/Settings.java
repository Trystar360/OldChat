import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
 
public class Settings extends Application {

	public Preferences prefs = Preferences.userNodeForPackage(ClientGUI.class);
 	boolean notify = Boolean.parseBoolean(prefs.get("notify", "true"));
 	boolean notifySound = Boolean.parseBoolean(prefs.get("notifySound", "true"));
 	private String notifyText = "ON";

    @Override
    public void start(Stage settingsStage) {
        settingsStage.setTitle("Chat Preferances");
        settingsStage.setResizable(false);
        
        settingsStage.show();
        settingsStage.getIcons().add(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		Scene scene = new Scene(grid,300,110);
		settingsStage.setScene(scene);
        settingsStage.show();
        
        
		Label serverLabel = new Label("Notifitions:");
		grid.add(serverLabel, 0, 0);
		
		if(notify)
			notifyText = "ON";
		if(!notify)
			notifyText = "OFF";
		
		// notifications ON/OFF
        ComboBox<String> notifications = new ComboBox<String>();
        notifications.getItems().addAll(
            "ON",
            "OFF"
        );
		if(Boolean.parseBoolean(prefs.get("notifySound", "true"))){
			notifications.setValue("ON");
		} else {
			notifications.setValue("OFF");
		}
        
		// notification sound ON/OFF
        ComboBox<String> notificationSound = new ComboBox<String>();
        notificationSound.getItems().addAll(
            "Sound ON",
            "Sound OFF"
        );
		if(Boolean.parseBoolean(prefs.get("notifySound", "true"))){
			notificationSound.setValue("Sound ON");
		} else {
			notificationSound.setValue("Sound OFF");
		}

        grid.add(notifications, 0, 1);
        grid.add(notificationSound, 1, 1);
        
        notifications.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
            public void handle(ActionEvent e) {
        		if(notifications.getValue()=="ON") {
        			prefs.put("notify", "true");
        		} else {
        			prefs.put("notify", "false");
        		}
        		System.out.println("Notifications: " + prefs.get("notify", "true"));
	        }
		});
        
        notificationSound.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
            public void handle(ActionEvent e) {
        		if(notificationSound.getValue()=="Sound ON") {
        			prefs.put("notifySound", "true");
        		} else {
        			prefs.put("notifySound", "false");
        		}
        		System.out.println("Notification Sound: " + prefs.get("notifySound", "true"));
	        }
		});
    }
	public static void main(String[] args) {
		launch(args);
	}
}