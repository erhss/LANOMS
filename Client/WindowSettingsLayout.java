import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class WindowSettingsLayout extends VBox {
	
	public WindowSettingsLayout(double padding) {
		super(padding);
		// Set anchors for blank page
		AnchorPane.setTopAnchor(this, 10.0);
		AnchorPane.setBottomAnchor(this, 10.0);
		AnchorPane.setLeftAnchor(this, 10.0);
		AnchorPane.setRightAnchor(this, 10.0);
		
		// Set menu items
		CheckBox cb_sound = new CheckBox("Enable sound notifications");
		CheckBox cb_popUp = new CheckBox("Enable pop-up notifications");
		
		ComboBox<String> cb_autoDel = new ComboBox<String>();
		cb_autoDel.getItems().addAll("5 days", "10 days", "30 days");
		cb_autoDel.getSelectionModel().selectLast();
		HBox hb_autoDel = new HBox(5.0);
		hb_autoDel.setAlignment(Pos.CENTER_LEFT);
		hb_autoDel.getChildren().addAll(new Label("Automatically delete messages after: "), cb_autoDel);
		
		// Set children
		setStyle("-fx-background-color: white");
		setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
		getChildren().addAll(cb_sound, cb_popUp, hb_autoDel);
	}
}
