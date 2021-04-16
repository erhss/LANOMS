import java.net.Socket;

public interface MessageHandler {
  public  void handleMessage(SocketMessage data);

  public void onDisconnect();

  public void onException(Exception e);

}
