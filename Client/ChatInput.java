import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class ChatInput extends TextField {
	private int conversationIndex;
	public ChatInput() {
		super();
		// Set anchors for chat input
		AnchorPane.setBottomAnchor(this, 10.0);
		AnchorPane.setLeftAnchor(this, 10.0);
		AnchorPane.setRightAnchor(this, 10.0);
				
		// Set height
		this.setPrefHeight(40);
		
		// Set action
		this.setOnAction(e -> {
			if (Lock.isBusy()) {
				setEditable(false);
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
						sendMessage();
						setEditable(true);
					}
				});
				Platform.runLater(thread);
			}
			else sendMessage();
		});
		
		conversationIndex = -1;
	}
	
	private void sendMessage() {
		if (conversationIndex != -1 && !getText().isBlank()) {
			ClientController.makeMessage(UserCache.getCurrentUser(), conversationIndex, getText());
			ConversationCache.addMessage(conversationIndex, UserCache.getUser(UserCache.getCurrentUser()), getText());
			GlobalPane.updateLayout();
			clear();
		    
		    // Reset auto-update timer.
		    GlobalPane.delayConversationUpdate();
		}
	}
	
	public void setConversationIndex(int index) {
		conversationIndex = index;
	}
}
