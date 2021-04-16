import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class SelectionPane extends AnchorPane {
	private GlobalPane parent;
	private VBox iconPane;
	private RadioButton[] icons;
	private ToggleGroup group;
	
	private int currentLayout;
	private int currentIcon;
	
	public SelectionPane(GlobalPane parent) {
		super();
		this.parent = parent;
		
		iconPane = new VBox(3.0);
		iconPane.setAlignment(Pos.TOP_CENTER);
		group = new ToggleGroup();
		
		getChildren().add(iconPane);
		AnchorPane.setTopAnchor(iconPane, 10.0);
		AnchorPane.setLeftAnchor(iconPane, 10.0);
		AnchorPane.setRightAnchor(iconPane, 10.0);
	}
	
	public void openConversation(int conversationIndex) {
		currentLayout = GlobalPane.INBOX;
		currentIcon = conversationIndex;
		updateLayout();
	}
	
	public void updateLayout() {
		if (currentLayout == GlobalPane.SETTINGS)
			return;
		
		iconPane.getChildren().clear();
		group.getToggles().clear();
		
		switch(currentLayout) {
		case GlobalPane.INBOX:
			getInbox();
			break;
		case GlobalPane.SEARCH:
			getUsers();
			break;
		}
		
		// Fail-safe
		if (currentIcon >= icons.length)
			currentIcon = 0;
		
		iconPane.getChildren().addAll(icons);
		group.getToggles().addAll(icons);
		
		
		if (icons.length > 0) {
			// Select first button
			icons[currentIcon].fire();
			group.selectToggle(icons[currentIcon]);
			
			// Set button style
			for (RadioButton icon: icons) {
				icon.getStyleClass().remove("radio-button");
				icon.getStyleClass().add("toggle-button");
				icon.getStylesheets().add("selection.css");
			}
		}
		else parent.setDescription(GlobalPane.BLANK);
	}
	
	public void setLayout(int layout) {
		iconPane.getChildren().clear();
		group.getToggles().clear();
		switch(layout) {
		case GlobalPane.INBOX:
			getInbox();
			break;
		case GlobalPane.SEARCH:
			getUsers();
			break;
		case GlobalPane.SETTINGS:
			getSettings();
			break;
		}
		
		iconPane.getChildren().addAll(icons);
		group.getToggles().addAll(icons);
		
		if (icons.length > 0) {
			// Select first button
			icons[0].fire();
			group.selectToggle(icons[0]);
			
			// Set button style
			for (RadioButton icon: icons) {
				icon.getStyleClass().remove("radio-button");
				icon.getStyleClass().add("toggle-button");
				icon.getStylesheets().add("selection.css");
			}
		}
		else parent.setDescription(GlobalPane.BLANK);
		
		currentLayout = layout;
		currentIcon = 0;
	}
	
	private void getInbox() {
		Conversation[] conversationList = ConversationCache.getConversations();
		icons = new RadioButton[conversationList.length];
		
		for (int i = 0; i < icons.length; i++) {
			// Get rid of main user from list of participants
			User[] temp = conversationList[i].getParticipants();
			
			User[] users = new User[temp.length - 1];
			for (int j = 0, k = 0; j < temp.length; j++) {
				if (!UserCache.isCurrentUser((temp[j]))) {
					users[k] = temp[j];
					k++;
				}
			}
			
			// Create name tags
			String name = new String(" ");
			for (int j = 0; j < users.length; j++) {
				//name += users[j].getName().substring(0, users[j].getName().indexOf(" "));
				name += users[j].getName();
				if (j < users.length - 1) name += ", ";
			}
			
			// Get display picture
			ImageView displayPicture = new ImageView(
					conversationList[i].getParticipants()[0].getDisplayPicture()
					);
			displayPicture.setPreserveRatio(true);
			displayPicture.setFitHeight(32.0);
			
			// Create button
			icons[i] = new RadioButton(name);
			icons[i].setGraphic(displayPicture);
			icons[i].pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), conversationList[i].getUnread());
			
			int index = i;
			icons[i].setOnAction(e -> {
				parent.setDescription(GlobalPane.CHAT_LOG, conversationList[index]);
				icons[index].pseudoClassStateChanged(PseudoClass.getPseudoClass("unread"), false);
				currentIcon = index;
			});
		}
	}
	
	private void getUsers() {
		User[] userList = UserCache.getUserList();
		icons = new RadioButton[userList.length];
		for (int i = 0; i < icons.length; i++) {
			ImageView displayPicture = new ImageView(userList[i].getDisplayPicture());
			displayPicture.setPreserveRatio(true);
			displayPicture.setFitHeight(32.0);
			icons[i] = new RadioButton(userList[i].getName());
			icons[i].setGraphic(displayPicture);
			
			int index = i;
			icons[i].setOnAction(e -> {
				parent.setDescription(GlobalPane.PROFILE, userList[index]);
				currentIcon = index;
			});
		}
	}
	
	private void getSettings() {
		icons = new RadioButton[2];
		icons[0] = new RadioButton("Window Settings");
		icons[1] = new RadioButton("User Settings");
		
		icons[0].setOnAction(e -> {
			parent.setDescription(GlobalPane.WINDOW_SETTINGS);
			currentIcon = 0;
		});
		icons[1].setOnAction(e -> {
			parent.setDescription(GlobalPane.USER_SETTINGS);
			currentIcon = 1;
		});
	}
	
	private void draw() {
		for(RadioButton icon: icons) {
			icon.setPrefWidth(getWidth());
			icon.setPrefHeight(40);
			icon.setAlignment(Pos.CENTER_LEFT);
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
