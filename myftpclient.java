import java.io.*;
import java.net.*;
import java.util.*;

//client class
public class myftpclient {

  private final static String FILE_SEP = System.getProperty("file.separator");
  private final static int BUFFER_SIZE = 1024;

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    // TODO Auto-generated method stub

    Scanner scan = new Scanner(System.in);
    System.out.print("Enter the port number: ");
    int port = Integer.parseInt(scan.nextLine());
    System.out.print("Enter the machine name: ");
    String machineName = scan.nextLine();
    Socket server = new Socket(machineName, port);
    DataOutputStream output = new DataOutputStream(server.getOutputStream());
    DataInputStream input = new DataInputStream(new BufferedInputStream(server.getInputStream()));
    ObjectInputStream objInput = new ObjectInputStream(server.getInputStream());
    // BufferedWriter output= new BufferedWriter(new
    // OutputStreamWriter(server.getOutputStream()));
    FileOutputStream fileOutStream;
    FileInputStream fileInStream;
    OutputStream outStream = server.getOutputStream();
    InputStream inStream = server.getInputStream();

    boolean quit = false;
    while (!quit) {
      System.out.print("myftp> ");
      String command = scan.nextLine();
      output.writeUTF(command);
      // System.out.println(command.substring(4));
      // System.out.println(command.startsWith("get"));
      if (command.equals("ls")) {
        // System.out.println("Reached here");
        String listOfFiles[] = (String[]) objInput.readObject();
        for (int i = 0; i < listOfFiles.length; i++)
          System.out.println(listOfFiles[i]);
      } else if (command.equals("quit")) {
        server.close();
        quit = true;
      } else if (command.startsWith("get")) {

        byte[] getByteArray = new byte[BUFFER_SIZE];
        System.out.println(command.substring(4));
        fileOutStream = new FileOutputStream(System.getProperty("user.dir") + FILE_SEP + command.substring(4));
        inStream.read(getByteArray, 0, getByteArray.length);
        fileOutStream.write(getByteArray, 0, getByteArray.length);
      } else if (command.startsWith("put")) {
        File sendFile = new File(command.substring(4));

        fileInStream = new FileInputStream(sendFile.getAbsolutePath());

        // System.out.println(sendFile.length());
        byte[] putByteArray = new byte[(int) sendFile.length()];

        fileInStream.read(putByteArray, 0, putByteArray.length);
        outStream.write(putByteArray, 0, putByteArray.length);
        // outStream.flush();
      } else {
        System.out.println(input.readUTF());
      }

    }

  }

}
