import javafx.scene.layout.AnchorPane;

public class DescriptionPane extends AnchorPane {
	private GlobalPane parent;
	private BlankLayout blank;
	private ChatLog chatLog;
	private ChatInput chatInput;
	private ProfileLayout profile;
	private WindowSettingsLayout windowSettings;
	private UserSettingsLayout userSettings;
	
	public DescriptionPane(GlobalPane parent) {
		super();
		this.parent = parent;
		
		// Initialize layouts
		chatLog = new ChatLog();
		chatInput = new ChatInput();
		blank = new BlankLayout(10.0);
		profile = new ProfileLayout(10.0);
		windowSettings = new WindowSettingsLayout(10.0);
		userSettings = new UserSettingsLayout(10.0);
	}
	
	public void setLayout(int layout) {
		getChildren().clear();
		
		switch(layout) {
		case GlobalPane.BLANK:
			getBlank();
			break;
		case GlobalPane.CHAT_LOG:
			getChatLog();
			break;
		case GlobalPane.PROFILE:
			getProfile();
			break;
		case GlobalPane.WINDOW_SETTINGS:
			getWindowSettings();
			break;
		case GlobalPane.USER_SETTINGS:
			getUserSettings();
			break;
		}
	}
	
	public void setProfile(User user) {
		profile.setUser(user, UserCache.isCurrentUser(user));
	}
	
	public void setChatLog(Conversation conversation) {
		chatLog.setConversation(conversation);
		chatInput.setConversationIndex(conversation.getIndex());
	}
	
	private void getBlank() {
		getChildren().add(blank);
	}
	private void getChatLog() {
		getChildren().addAll(chatLog, chatInput);
	}
	
	private void getProfile() {
		getChildren().add(profile);
	}
	
	private void getWindowSettings() {
		getChildren().add(windowSettings);
	}
	
	private void getUserSettings() {
		getChildren().add(userSettings);
		userSettings.setMessage();
	}
	
}
