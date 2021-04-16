import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

import javafx.scene.image.Image;

public abstract class UserCache {
	private static String currentUser;
	private static ArrayList<User> userList = new ArrayList<User>();
	
	private static volatile int userCount;
	private static volatile String userInfo;
	private static volatile byte[] userDisplayPicture;
	
	private static class UserCacheThread implements Runnable {
		private final long SLEEP_TIME = 100, TIMEOUT_TIME = 5000;
		private UserCacheThread() {
			try {
				Lock.SEMAPHORE.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			final int USERNAME = 0, NAME = 1, PROFILE = 2, STATUS = 3, DEPARTMENT = 4, EMAIL = 5;
			userCount = -1;
			ClientController.getUserCount();

			long startTime = System.currentTimeMillis();
			while (userCount == -1) {
				if (System.currentTimeMillis() - startTime > TIMEOUT_TIME) 
					throw new RuntimeException("UserCacheThread timedout.");
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			User[] toAdd = new User[userCount];
			for (int i = 0; i < userCount; i++) {
				
				userInfo = null;
				ClientController.getUserInfo(i);
				
				startTime = System.currentTimeMillis();
				while (userInfo == null) {
					if (System.currentTimeMillis() - startTime > TIMEOUT_TIME) 
						throw new RuntimeException("UserCacheThread timedout.");
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				String[] splitUserInfo = userInfo.split("\n");
				userInfo = null;
				
				// Request ClientController to update userDisplayPicture.
				/*
				 userDisplayPicture = null;
				 ClientController.getUserDisplayPicture(i);
				
				// Start our timeout timer.
				startTime = System.currentTimeMillis();
				
				// Wait for userDisplayPicture to be updated by ClientController.
				while (userDisplayPicture == null) {
					if (System.currentTimeMillis() - startTime > TIMEOUT_TIME) 
						throw new RuntimeException("UserCacheThread timedout.");
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				// Create image from binary array.
				Image picture = new Image(new ByteArrayInputStream(userDisplayPicture));*/
				
				// Stub
				Image picture = new Image("user_placeholder.png");
				
				toAdd[i] = new User(
						splitUserInfo[USERNAME],
						splitUserInfo[NAME],
						splitUserInfo[PROFILE],
						splitUserInfo[STATUS],
						splitUserInfo[DEPARTMENT],
						splitUserInfo[EMAIL],
						picture);
			}

			UserCache.clearUsers();
			for(User user: toAdd) {
				System.out.println("Adding " + user.getName());
				userList.add(user);
			}
			
			Lock.SEMAPHORE.release();
		}
		
	}
	
	public static void updateUserList() {
		if (Lock.getQueueLength() < 2) {
			Thread thread = new Thread (new UserCacheThread());
			thread.start();
		}
	}
	
	public static void setUserCount(int userCount) {
		System.out.println("Hi, there are " + userCount + " users.");
		UserCache.userCount = userCount;
	}

	public static void setUserInfo(String userInfo) {
		System.out.println(userInfo);
		UserCache.userInfo = userInfo;
	}

	public static void setUserDisplayPicture(byte[] userDisplayPicture) {
		UserCache.userDisplayPicture = userDisplayPicture;
	}

	public static void setCurrentUser(String username) {
		currentUser = username;
	}
	
	public static String getCurrentUser() {
		return currentUser;
	}
	
	public static boolean isCurrentUser(User user) {
		return currentUser.equals(user.getUsername());
	}
	
	public static boolean isLoggedIn() {
		return currentUser.isBlank();
	}
	
	public static void addUser(String username, String name, String message, String status, String department, String email, Image displayPicture) {
		userList.add(new User(username, name, message, status, department, email, displayPicture));
	}
	
	public static void clearUsers() {
		userList.clear();
	}
	
	public static User getUser(String username) {
		User toReturn = null;
		for(User user: userList)
			if (user.getUsername().equals(username)) toReturn = user;
		
		return toReturn;
	}
	
	public static User[] getUserList() {
		Collections.sort(userList);
		User[] toReturn = new User[userList.size()];
		for (int i = 0; i < toReturn.length; i++) toReturn[i] = userList.get(i);
		
		return toReturn;
	}
	
	public static boolean isEmpty() {
		return userList.size() == 0;
	}
}
