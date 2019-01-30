import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * This is the FTP client for the project. On executing this file, you are given
 * a "myftp>" prompt where you can type the available commands.
 */
public class myftpclient {

  private final static String FILE_SEP = System.getProperty("file.separator");
  private final static int BUFFER_SIZE = 1024;
  private static final Scanner scan = new Scanner(System.in);

  public static void main(String[] args) throws IOException, ClassNotFoundException {

    System.out.print("Enter the port number: ");
    int port = Integer.parseInt(scan.nextLine());
    System.out.print("Enter the machine name: ");
    String machineName = scan.nextLine();
    Socket server = new Socket(machineName, port);
    DataOutputStream output = new DataOutputStream(server.getOutputStream());
    DataInputStream input = new DataInputStream(new BufferedInputStream(server.getInputStream()));
    ObjectInputStream objInput = new ObjectInputStream(server.getInputStream());

    FileOutputStream fileOutStream;
    FileInputStream fileInStream;
    OutputStream outStream = server.getOutputStream();
    InputStream inStream = server.getInputStream();

    boolean quit = false;
    while (!quit) {
      
      System.out.print("myftp> ");
      String command = scan.nextLine();
      int cmdLength = command.split(" ").length;
      output.writeUTF(command);

      if (command.equals("ls")) {
        String listOfFiles[] = (String[]) objInput.readObject();
        for (int i = 0; i < listOfFiles.length; i++) {
          System.out.printf("%s \t", listOfFiles[i]);
        }
        System.out.println();
               
      }
      
      else if (command.equals("quit")) {
        server.close();
        quit = true;
      }

      else if (cmdLength == 2 && command.startsWith("get")) {
        byte[] getByteArray = new byte[BUFFER_SIZE];
        String getFile = command.substring(4);
        System.out.println(getFile);
        fileOutStream = new FileOutputStream(System.getProperty("user.dir") + FILE_SEP + getFile);
        inStream.read(getByteArray, 0, getByteArray.length);
        fileOutStream.write(getByteArray, 0, getByteArray.length);
      }

      else if (cmdLength == 2 && command.startsWith("put")) {
        File sendFile = new File(command.substring(4));
        fileInStream = new FileInputStream(sendFile.getAbsolutePath());
        // System.out.println(sendFile.length());
        byte[] putByteArray = new byte[(int) sendFile.length()];
        fileInStream.read(putByteArray, 0, putByteArray.length);
        outStream.write(putByteArray, 0, putByteArray.length);
        // outStream.flush();
      }
      
      else {
        System.out.println(input.readUTF());
      }

    } // end while

  }

}
