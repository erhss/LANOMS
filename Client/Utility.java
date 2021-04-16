import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Utility {
  public static int convertToInt(String data) {
    try {
      return Integer.parseInt(data);
    } 
    catch (Exception e) {
      return 0;
    }
  }

  public static byte[] fromMessage(Object obj) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ObjectOutputStream stream = new ObjectOutputStream(outputStream);
    stream.writeObject(obj);
    stream.close();
    return outputStream.toByteArray();
  }
}
