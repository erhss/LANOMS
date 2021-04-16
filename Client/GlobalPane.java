import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class GlobalPane extends AnchorPane {
	public static final int INBOX = 0, SEARCH = 1, SETTINGS = 2;
	public static final int BLANK = 0, CHAT_LOG = 1, PROFILE = 2, WINDOW_SETTINGS = 3, USER_SETTINGS = 4;
	private static final double CONVERSATION_UPDATE_DURATION = 5000, USER_UPDATE_DURATION = 60000;
	
	private final double NAV_WIDTH = 50.0;
	private final double SEL_WIDTH = 200.0f;
	
	
	private NavigationPane navigationPane;
	private SelectionPane selectionPane;
	private DescriptionPane descriptionPane;
	
	private Timeline conversationTimeline;
	private Timeline userTimeline;
	
	private static GlobalPane instance;
	
	public GlobalPane() {
		if (instance == null)
			instance = this;
	}
	
	public void initialize() {
		// Setup panes
		navigationPane = new NavigationPane(this);
		selectionPane = new SelectionPane(this);
		descriptionPane = new DescriptionPane(this);
		
		// Setup styles
		setStyle("-fx-background-color: dodgerblue");
		navigationPane.setStyle("-fx-background-color: dodgerblue");
		selectionPane.setStyle("-fx-background-color: whitesmoke; -fx-background-radius: 10 0 0 0;");
		descriptionPane.setStyle("-fx-background-color: white");
		
		// Setup sizes
		navigationPane.setPrefWidth(NAV_WIDTH);
		selectionPane.setPrefWidth(SEL_WIDTH);
		
		// Set children
		getChildren().addAll(navigationPane, selectionPane, descriptionPane);
		
		// Setup anchors
		AnchorPane.setLeftAnchor(navigationPane, 0.0);
		AnchorPane.setTopAnchor(navigationPane, 20.0);
		AnchorPane.setBottomAnchor(navigationPane, 0.0);
				
		AnchorPane.setLeftAnchor(selectionPane, NAV_WIDTH);
		AnchorPane.setTopAnchor(selectionPane, 20.0);
		AnchorPane.setBottomAnchor(selectionPane, 0.0);
				
		AnchorPane.setLeftAnchor(descriptionPane, NAV_WIDTH + SEL_WIDTH);
		AnchorPane.setTopAnchor(descriptionPane, 20.0);
		AnchorPane.setBottomAnchor(descriptionPane, 0.0);
		AnchorPane.setRightAnchor(descriptionPane, 0.0);
		
		// Test user cache
		//testUserCache();
		
		// Test conversation cache
		//testConversationCache();
		
		// Setup first screen
		setSelection(INBOX);
		
		conversationTimeline = new Timeline();
		conversationTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(CONVERSATION_UPDATE_DURATION), e -> ConversationCache.updateConversations()));
		conversationTimeline.setCycleCount(Animation.INDEFINITE);
		conversationTimeline.play();
		
		userTimeline = new Timeline();
		userTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(USER_UPDATE_DURATION), e -> UserCache.updateUserList()));
		userTimeline.setCycleCount(Animation.INDEFINITE);
		userTimeline.play();
	}
	
	public void setSelection(int selection) {
		selectionPane.setLayout(selection);
	}
	
	public void setDescription(int description) {
		descriptionPane.setLayout(description);
	}
	
	public void setDescription(int description, User profile) {
		descriptionPane.setLayout(description);
		descriptionPane.setProfile(profile);
	}
	
	public void setDescription(int description, Conversation conversation) {
		descriptionPane.setLayout(description);
		descriptionPane.setChatLog(conversation);
	}
	
	public static void openConversation(int conversationIndex) {
		if (instance != null){
			instance.navigationPane.selectInbox();
			instance.selectionPane.openConversation(conversationIndex);
		}
	}
	
	public static void updateLayout() {
		if (instance != null)
			instance.selectionPane.updateLayout();
	}
	
	public static void delayConversationUpdate() {
		if (instance != null)
			instance.conversationTimeline.jumpTo(Duration.ZERO);
	}
	
	public static void delayUserUpdate() {
		if (instance != null)
			instance.userTimeline.jumpTo(Duration.ZERO);
	}
	
	/*// Old Code
	public void getUserList() {
		final int USERNAME = 0, NAME = 1, PROFILE = 2, STATUS = 3, DEPARTMENT = 4, EMAIL = 5;
		UserCache.clearUsers();
		
		int userCount = ClientController.getUserCount();
		for (int i = 0; i < userCount; i++) {
			String[] userInfo = ClientController.getUserInfo(i).split("\n");
			
			Image userDisplayPicture = new Image(
					new ByteArrayInputStream(ClientController.getUserDisplayPicture(i))
					);
			
			UserCache.addUser(
					userInfo[USERNAME],
					userInfo[NAME],
					userInfo[PROFILE],
					userInfo[STATUS],
					userInfo[DEPARTMENT],
					userInfo[EMAIL],
					userDisplayPicture);
		}
	}
	
	// Old code
	public void getConversationList() {
		final int NAME = 0, MESSAGE = 1;
		ConversationCache.clearConversations();
		
		Conversation[] conversations = new Conversation[ClientController.getConversationCount()];
		for (int i = 0; i < conversations.length; i++) {
			conversations[i] = new Conversation(i);
			
			int messageCount = ClientController.getMessageCount(i);
			for (int j = 0; j < messageCount; j++) {
				String[] message = ClientController.getMessage(i, j).split("\n");
				conversations[i].addMessage(UserCache.getUser(message[NAME]), message[MESSAGE]);
			}
			
			ConversationCache.addConversation(conversations[i]);
		}
	}
	
	// Old stub
	public void testUserCache() {
		UserCache.addUser("acheddadi", "Ali Cheddadi", "Hey man, what's cooking?", "Available", "Computer Science", "acheddadi@algomau.ca", new Image("user_placeholder.png"));
		UserCache.addUser("nkodakandla", "Nikhil Kodakandla", "Not much, just hangin'.", "Busy", "Computer Science", "nkodakandla@algomau.ca", new Image("user_placeholder.png"));
		UserCache.addUser("spaudel", "Shreejan Paudel", "Awesome sauce.", "Out to lunch", "Computer Science", "spaudel@algomau.ca", new Image("user_placeholder.png"));
	}
	
	// Old stub
	public void testConversationCache() {
		Conversation convo01 = new Conversation(0);
		convo01.addMessage(UserCache.getUser("Ali Cheddadi"), "WASAAAAP?!");
		convo01.addMessage(UserCache.getUser("Nikhil Kodakandla"), "Not much bruv");
		convo01.addMessage(UserCache.getUser("Ali Cheddadi"), "word");
		convo01.addMessage(UserCache.getUser("Shreejan Paudel"), ":D");
		convo01.addMessage(UserCache.getUser("Ali Cheddadi"), "Cool beans");
		convo01.addMessage(UserCache.getUser("Nikhil Kodakandla"), "The coolest");
		convo01.addMessage(UserCache.getUser("Ali Cheddadi"), "word");
		convo01.addMessage(UserCache.getUser("Shreejan Paudel"), ":D...");
		
		ConversationCache.addConversation(convo01);
		
		Conversation convo02 = new Conversation(1);
		convo02.addMessage(UserCache.getUser("Shreejan Paudel"), "new phone, who dis");
		convo02.addMessage(UserCache.getUser("Ali Cheddadi"), "how could you :(");
		convo02.addMessage(UserCache.getUser("Shreejan Paudel"), "you have five seconds to comply before I annihilate you");
		
		ConversationCache.addConversation(convo02);
	} */
}
