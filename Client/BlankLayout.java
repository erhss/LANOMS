

import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class BlankLayout extends VBox {
	
	public BlankLayout(double padding){
		super(padding);
		// Set anchors for blank page
		AnchorPane.setTopAnchor(this, 10.0);
		AnchorPane.setBottomAnchor(this, 10.0);
		AnchorPane.setLeftAnchor(this, 10.0);
		AnchorPane.setRightAnchor(this, 10.0);
				
		//Set text
		setStyle("-fx-background-color: white; -fx-font-size: 18px;");
		setAlignment(Pos.CENTER);
		getChildren().add(new Text("Nothing to show here..."));
	}
}
