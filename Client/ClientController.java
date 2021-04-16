import java.io.IOException;

public abstract class ClientController {

  public static void getUserCount() {
    try {
      SocketClient.send("USER_COUNT", "");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String getUserInfo(int i) {
    try {
      SocketClient.send("USER_INFO", i +"");
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static byte[] getUserDisplayPicture(int i) {
    return null;
  }

  public static void getConversationCount(String user) {
    try {
      SocketClient.send("CONV_COUNT", user +"");
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void getMessageCount(String user, int j) {
    try {
      SocketClient.send("MSG_COUNT", user +"\n" + j);
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
    
    // return 0;
  }

  public static void getMessage(String user, int i, int j) {
	  try {
	      SocketClient.send("GET_MSG", user + "\n" + i +"\n" + j);
	    } 
	    catch (IOException e) {
	      e.printStackTrace();
	    }
  }

  private static void setUserCount(int count){
    UserCache.setUserCount(count);
  }

  private static void setUserInfo(String data){
    UserCache.setUserInfo(data);
  }
  
	/*// Authentication stub
	public static int authenticateUser(String username, String password) {
		// TODO Auto-generated method stub
		return !username.isBlank() && !password.isBlank() ? 3 : 1;
	}*/
	
	public static void login(String userName, String password) {
	      try {
	          SocketClient.send("USER_AUTH", userName + "\n"+ password);
	        } 
	        catch (IOException e) {
	          e.printStackTrace();
	        }
	  }

  public static void makeMessage(String username, int i, String message){
    try {
      SocketClient.send("MAKE_MESSAGE", username + "\n" + i + "\n" + message);
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void makeConversation(String senderUsername, String receiverUsername) {
	  try {
	      SocketClient.send("MAKE_CONVO", senderUsername + "\n" + receiverUsername);
	    } 
	    catch (IOException e) {
	      e.printStackTrace();
	    }
  }
  
  public static void makeUserInfo(String username, String profile, String status) {
	  try {
	      SocketClient.send("MAKE_USER_INFO", username + "\n" + profile + "\n" + status);
	    } 
	    catch (IOException e) {
	      e.printStackTrace();
	    }
  }

  public static void handleMessage(SocketMessage data) {
    if (data.getKey() != null) {
      switch (data.getKey()) {
        case "USER_COUNT":
          setUserCount(Utility.convertToInt(data.getData()));
          break;
        case "USER_INFO":
           setUserInfo(data.getData());
          break;
        case "DISPLAY_PICTURE":
        	UserCache.setUserDisplayPicture(data.getByteData());
        	break;
        case "USER_AUTH":
            LoginPane.setLoginCode(Utility.convertToInt(data.getData()));
            break;
        case "CONV_COUNT":
        	ConversationCache.setConversationCount(Utility.convertToInt(data.getData()));
            break;
        case "MSG_COUNT":
        	ConversationCache.setMessageCount(Utility.convertToInt(data.getData()));
        	break;
        case "GET_MSG":
        	ConversationCache.setMessage(data.getData());
        	break;
        case "MAKE_MESSAGE":
        	break;
        default:
          System.out.println("Key: " + data.getKey() + " Value: " + data.getData());
          return;

      }
    }
  }

  public static void onDisconnect() {
    System.out.println("Server Disconnected");

  }

  public static void onException(Exception e) {
    e.printStackTrace();

  }

}
