
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public abstract class SocketClient {
  private static int port = 9009;
  private static String host = "localhost";
  private static DataInputStream reader;
  private static DataOutputStream writer;
  private static Socket socket;
  private static boolean reading = true;

  public static void connect() throws Exception {
    socket = new Socket(host, port);
    reader = new DataInputStream(socket.getInputStream());
    writer = new DataOutputStream(socket.getOutputStream());
    Thread th = new Thread(new IncomingReader()); // Reading incoming Message Thread
    th.start();
  }

  public static void send(String data) throws IOException {
    writer.writeInt(MessageType.STRING_MESSAGE);
    byte[] m = data.getBytes();
    writer.writeInt(m.length); // write length of the message
    writer.write(m);
    writer.flush();
  }

  public static void send(String key, String message) throws IOException {
    writer.writeInt(MessageType.KEY_VALUE_MESSAGE); // Set message type to Key Value
    byte[] m = key.getBytes();
    writer.writeInt(m.length);
    writer.write(m);
    m = message.getBytes();
    writer.writeInt(m.length); // write length of the message
    writer.write(m);
    writer.flush();
  }

  public static void send(String key, byte[] data) throws IOException {
    writer.writeInt(MessageType.BYTE_MESSAGE); // Set message type to Key Value
    byte[] m = key.getBytes();
    writer.writeInt(m.length);
    writer.write(m);
    writer.writeInt(data.length); // write length of the message
    writer.write(data);
    writer.flush();

  }

  public static void authneticateUser(String username, String password) throws IOException {
    writer.writeInt(MessageType.AUTH_MESSAGE); // Set Message type to Auth
    byte[] m = username.getBytes();
    writer.writeInt(m.length); // write length of the message
    writer.write(m);

    m = password.getBytes();
    writer.writeInt(m.length); // write length of the message
    writer.write(m);
    writer.flush();
  }

  public static void close() throws IOException {
    reading = false;
    socket.close();
  }

  private static class IncomingReader implements Runnable {

    @Override
    public void run() {
      try {
        int type;
        while (reading && (type = reader.readInt()) != 0) {
          int length = reader.readInt();
          if (type == MessageType.STRING_MESSAGE) {// Check message type
            byte[] m = new byte[length];
            reader.readFully(m, 0, m.length);
            ClientController.handleMessage(new SocketMessage("", new String(m)));
          } 
          else if (type == MessageType.KEY_VALUE_MESSAGE) {
            byte[] m = new byte[length];
            reader.readFully(m, 0, m.length); // Read Message Key
            String key = new String(m);
            length = reader.readInt(); // Read Message length
            m = new byte[length];
            reader.readFully(m, 0, m.length);
            ClientController.handleMessage(new SocketMessage(key, new String(m)));
          } 
          else if (type == MessageType.BYTE_MESSAGE) {
            byte[] m = new byte[length];
            reader.readFully(m, 0, m.length); // Read Message Key
            String key = new String(m);
            length = reader.readInt(); // Read Message length
            m = new byte[length];
            reader.readFully(m, 0, m.length);
            ClientController.handleMessage(new SocketMessage(key, m));
          }
        }
      } 
      catch (Exception e) {
        if (e instanceof EOFException) {
        	ClientController.onDisconnect();
        } 
        else {
        	ClientController.onException(e);
        	ClientController.onDisconnect();
        }
      }

    }

  }

}
