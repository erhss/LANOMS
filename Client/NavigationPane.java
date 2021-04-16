import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class NavigationPane extends AnchorPane {
	private GlobalPane parent;
	private VBox iconPane;
	private RadioButton[] icons;
	private ToggleGroup group;
	
	public NavigationPane(GlobalPane parent) {
		super();
		this.parent = parent;
		
		// Setup icon list
		iconPane = new VBox();
		//iconPane.setAlignment(Pos.TOP_CENTER);
		group = new ToggleGroup();
		
		// Initialize icons
		icons = new RadioButton[3];
		icons[GlobalPane.INBOX] = new RadioButton();
		icons[GlobalPane.SEARCH] = new RadioButton();
		icons[GlobalPane.SETTINGS] = new RadioButton();
		
		// Initialize icon graphics
		ImageView iv_inbox = new ImageView("mail-icon.png");
		iv_inbox.setPreserveRatio(true);
		iv_inbox.setFitHeight(32.0);
		icons[GlobalPane.INBOX].setGraphic(iv_inbox);
		
		ImageView iv_search = new ImageView("search-icon.png");
		iv_search.setPreserveRatio(true);
		iv_search.setFitHeight(32.0);
		icons[GlobalPane.SEARCH].setGraphic(iv_search);
		
		ImageView iv_settings = new ImageView("settings-icon.png");
		iv_settings.setPreserveRatio(true);
		iv_settings.setFitHeight(32.0);
		icons[GlobalPane.SETTINGS].setGraphic(iv_settings);
		
		// Set toggle group
		group.getToggles().addAll(icons);
		
		// Set button style
		for (RadioButton icon: icons) {
			icon.getStyleClass().remove("radio-button");
			icon.getStyleClass().add("toggle-button");
			icon.getStylesheets().add("navigation.css");
		}
		
		// Select first option
		group.selectToggle(icons[0]);
		
		// Event handling
		icons[GlobalPane.INBOX].setOnAction(e -> this.parent.setSelection(GlobalPane.INBOX));
		icons[GlobalPane.SEARCH].setOnAction(e -> this.parent.setSelection(GlobalPane.SEARCH));
		icons[GlobalPane.SETTINGS].setOnAction(e -> this.parent.setSelection(GlobalPane.SETTINGS));
		
		// Setup children
		iconPane.getChildren().addAll(icons[GlobalPane.INBOX], icons[GlobalPane.SEARCH]);
		getChildren().addAll(iconPane, icons[GlobalPane.SETTINGS]);
		
		// Setup anchors
		AnchorPane.setTopAnchor(iconPane, 20.0);
		AnchorPane.setLeftAnchor(iconPane, 0.0);
		AnchorPane.setRightAnchor(iconPane, 0.0);
		
		AnchorPane.setBottomAnchor(icons[GlobalPane.SETTINGS], 10.0);
		AnchorPane.setLeftAnchor(icons[GlobalPane.SETTINGS], 0.0);
		AnchorPane.setRightAnchor(icons[GlobalPane.SETTINGS], 0.0);
	}
	
	public void selectInbox() {
		group.selectToggle(icons[0]);
	}
	
	private void draw() {
		for(RadioButton icon: icons) {
			icon.setPrefWidth(getWidth());
			icon.setPrefHeight(50);
			icon.setAlignment(Pos.CENTER);
		}
	}
	
	@Override
	public void setWidth(double width) {
		super.setWidth(width);
		draw();
	}
	
	@Override
	public void setHeight(double height) {
		super.setHeight(height);
		draw();
	}
}
