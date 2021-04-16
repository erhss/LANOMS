import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ProfileLayout extends VBox {
	private Text profileText;
	private ImageView displayPicture;
	private Button openConversation;
	
	public ProfileLayout(double padding) {
		super(padding);
		
		// Set anchors for blank page
		AnchorPane.setTopAnchor(this, 10.0);
		AnchorPane.setBottomAnchor(this, 10.0);
		AnchorPane.setLeftAnchor(this, 10.0);
		AnchorPane.setRightAnchor(this, 10.0);
	}
	
	public void setUser(User user, boolean mainUser) {
		// Clear old profile
		getChildren().clear();
		
		// Set profile text
		profileText = new Text(String.format("\n%s\n"
				+ "%s\n"
				+ "\n%s\n"
				+ "%s\n"
				+ "%s\n",
				user.getName(),
				user.getStatus(),
				user.getDepartment(),
				user.getMessage(),
				user.getEmail()));
		
		profileText.setTextAlignment(TextAlignment.CENTER);
		
		// Set display picture
		displayPicture = new ImageView(user.getDisplayPicture());
		
		// Set properties
		setStyle("-fx-background-color: white");
		setAlignment(Pos.CENTER);
		
		if (!mainUser) {
			int index = ConversationCache.getSize();
			
			openConversation = new Button("Open new conversation");
			openConversation.setOnAction(e -> {
				if (Lock.isBusy()) {
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							while(Lock.isBusy()) {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							openConversation(user, index);
						}
					});
					Platform.runLater(thread);
				}
				else openConversation(user, index);
			});
			
			getChildren().addAll(displayPicture, profileText, openConversation);
		}
		else {
			getChildren().addAll(displayPicture, profileText);
		}
	}
	
	private void openConversation(User user, int index) {
		ClientController.makeConversation(UserCache.getCurrentUser(), user.getUsername());
		
		Conversation conversation = new Conversation(index, 2);
		conversation.addMessage(UserCache.getUser(UserCache.getCurrentUser()), "@server has joined the conversation.");
		conversation.addMessage(user, "@server has joined the conversation.");
		ConversationCache.addConversation(conversation);
		
		GlobalPane.openConversation(index);
	    
	    // Reset auto-update timer.
	    GlobalPane.delayConversationUpdate();
	}
}
