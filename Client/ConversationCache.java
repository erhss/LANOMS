import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javafx.application.Platform;

public abstract class ConversationCache {
	private static ArrayList<Conversation> conversations = new ArrayList<Conversation>();
	
	private static volatile int conversationCount;
	private static volatile int messageCount;
	private static volatile String message;
	
	private static volatile boolean initialized;
	
	private static class ConversationCacheThread implements Runnable {
		private final long SLEEP_TIME = 100, TIMEOUT_TIME = 5000;
		
		private ConversationCacheThread() {
			try {
				Lock.SEMAPHORE.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			final int NAME = 0, MESSAGE = 1;
			conversationCount = -1;
			ClientController.getConversationCount(UserCache.getCurrentUser());
			
			long startTime = System.currentTimeMillis();
			
			while(conversationCount == -1) {
				if (System.currentTimeMillis() - startTime > TIMEOUT_TIME) 
					throw new RuntimeException("ConversationCacheThread timedout.");
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			Conversation[] conversationBuffer = new Conversation[conversationCount];
			for (int i = 0; i < conversationBuffer.length; i++) {
				conversationBuffer[i] = new Conversation(i, initialized ?
						conversations.get(i).getInitialSize() : 0); // Replace 0 with the value stored on disk.
				
				messageCount = -1;
				ClientController.getMessageCount(UserCache.getCurrentUser(), i);
				
				startTime = System.currentTimeMillis();
				while(messageCount == -1) {
					if (System.currentTimeMillis() - startTime > TIMEOUT_TIME) 
						throw new RuntimeException("ConversationCacheThread timedout.");
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				for (int j = 0; j < messageCount; j++) {
					
					message = null;
					ClientController.getMessage(UserCache.getCurrentUser(), i, j);
					
					startTime = System.currentTimeMillis();
					while(message == null) {
						if (System.currentTimeMillis() - startTime > TIMEOUT_TIME) 
							throw new RuntimeException("ConversationCacheThread timedout.");
						try {
							Thread.sleep(SLEEP_TIME);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					String[] splitMessage = message.split("\n");
					System.out.println("User being passed: " + splitMessage[NAME]);
					conversationBuffer[i].addMessage(UserCache.getUser(splitMessage[NAME]), splitMessage[MESSAGE]);
				}
				
			}
			
			ConversationCache.clearConversations();
			for (Conversation toAdd: conversationBuffer)
				ConversationCache.addConversation(toAdd);
			
			if (!initialized) {
				initialized = true;
			}
			else {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						GlobalPane.updateLayout();
					}
				});
				Platform.runLater(thread);
			}
				
			
			Lock.SEMAPHORE.release();
		}
		
	}
	
	public static void setConversationCount(int count) {
		conversationCount = count;
	}
	
	public static void setMessageCount(int count) {
		messageCount = count;
	}
	
	public static void setMessage(String message) {
		ConversationCache.message = message;
	}
	
	public static boolean isInitialized() {
		return initialized;
	}
	
	public static void updateConversations() {
		if (Lock.getQueueLength() < 2) {
			Thread thread = new Thread(new ConversationCacheThread());
			thread.start();
		}
	}
	
	public static void addConversation(Conversation conversation) {
		conversations.add(conversation);
	}
	
	public static void addMessage(int conversationIndex, User user, String message) {
		if (conversationIndex > conversations.size() || conversationIndex < 0)
			throw new RuntimeException("Invalid conversation index");
		
		conversations.get(conversationIndex).addMessage(user, message);
	}
	
	public static void clearConversations() {
		conversations.clear();
	}
	
	public static Conversation[] getConversations() {
		Conversation[] toReturn = new Conversation[conversations.size()];
		for (int i = 0; i < toReturn.length; i++) toReturn[i] = conversations.get(i);
		
		return toReturn;
	}
	
	public static int getSize() {
		return conversations.size();
	}
	
	public static boolean isEmpty() {
		return conversations.size() == 0;
	}
}
