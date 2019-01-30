import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;;

/**
 * This is the FTP server for the project. The main method works as the driver
 * and calls the start method which boots up the server and allows it to serve
 * requests from the client.
 */
public class myftpserver {

  private final static String FILE_SEP = System.getProperty("file.separator");
  private final static int BUFFER_SIZE = 1024;

  private ServerSocket ss;
  private Socket client;

  // might remove this later in favor of command line arguments
  private static final Scanner scan = new Scanner(System.in);

  public static void main(String args[]) throws IOException {

    // scan = new Scanner(System.in);
    System.out.print("Enter the port number:");
    int port = Integer.parseInt(scan.nextLine());

    myftpserver ftpServer = new myftpserver();
    ftpServer.start(port);

    ftpServer.closeResources();

  } // end main

  /**
   * This method is responsible for all the actions performed by the server.
   * 
   * @param port
   * @throws IOException
   */
  public void start(int port) throws IOException {
    ss = new ServerSocket(port);

    File currentDir;

    // The infinite loop for the server so that it
    // doesn't close even when the client quits
    while (true) {
      client = ss.accept();
      System.out.println("Connection estblished!");
      DataInputStream input = new DataInputStream(new BufferedInputStream(client.getInputStream()));
      FileInputStream fileInStream;
      FileOutputStream fileOutStream;
      OutputStream outStream = client.getOutputStream();
      InputStream inStream = client.getInputStream();
      DataOutputStream output = new DataOutputStream(client.getOutputStream());
      ObjectOutputStream objOutput = new ObjectOutputStream(client.getOutputStream());
      String command[] = null;
      System.out.println("Waiting for a command...");
      do {

        String response = input.readUTF();
        command = response.split("\\s+");
        switch (command[0]) {

        case "get":
          if (command.length == 2) {
            File sendFile = new File(command[1]);

            fileInStream = new FileInputStream(sendFile.getAbsolutePath());
            byte[] getByteArray = new byte[(int) sendFile.length()];

            fileInStream.read(getByteArray, 0, getByteArray.length);
            outStream.write(getByteArray, 0, getByteArray.length);
          }

          // If no file is specified as the argument
          else {
            output.writeUTF("You must specify a path after a get command.");
          }

          break;

        case "put":
          if (command.length == 2) {
            byte[] putByteArray = new byte[BUFFER_SIZE];
            fileOutStream = new FileOutputStream(System.getProperty("user.dir") + FILE_SEP + command[1]);
            inStream.read(putByteArray, 0, putByteArray.length);
            fileOutStream.write(putByteArray, 0, putByteArray.length);
          }
          
          else {
            output.writeUTF("You must specify a path after a get command.");
          }
          break;

        case "delete":
          File del = new File(System.getProperty("user.dir") + FILE_SEP + command[1]);
          System.out.println(System.getProperty("user.dir") + FILE_SEP + command[1]);
          if (del.exists()) {
            del.delete();
            output.writeUTF(command[1] + " successfully deleted.");
          } else {
            output.writeUTF("File does not exist!");
          }
          break;

        case "ls":
          File curDir = new File(System.getProperty("user.dir"));
          objOutput.writeObject(curDir.list());
          break;

        // For windows system
        case "cd..":
          currentDir = new File(System.getProperty("user.dir"));
          System.setProperty("user.dir", currentDir.getAbsoluteFile().getParent());
          output.writeUTF("Directory changed to " + System.getProperty("user.dir"));
          break;

        case "cd":

          if (command.length <= 1) {
            output.writeUTF("You must specify a path after a cd command.");
            break;
          }

          if (command[1].equals("..")) {

            currentDir = new File(System.getProperty("user.dir"));
            File parentDir = new File(System.getProperty("user.dir", currentDir.getAbsoluteFile().getParent()));
            // System.out.println("Parent dir: ->>>>>" + parentDir);
            if (parentDir.exists()) {
              System.setProperty("user.dir", currentDir.getAbsoluteFile().getParent());
              output.writeUTF("Directory changed to " + System.getProperty("user.dir"));
            } else {
              output.writeUTF("No parent directory exists!");
            }

          } // end if checking ".."

          // when the second param is anything other than ".."
          else {
            File changeDir = new File(System.getProperty("user.dir") + FILE_SEP + command[1]);

            if (changeDir.exists()) {
              System.setProperty("user.dir", changeDir.getAbsoluteFile().getPath());
              output.writeUTF("Directory changed to " + System.getProperty("user.dir"));
            } else {
              output.writeUTF("No such directory exists!");
            }

          }

          break;

        case "mkdir":
          if (command.length == 2) {
            File newFile = new File(System.getProperty("user.dir") + FILE_SEP + command[1]);
            newFile.mkdir();
            output.writeUTF("New directory named " + command[1] + " created.");
          }

          else {
            output.writeUTF("You must specify a path after a get command.");
          }
          break;

        case "pwd":
          output.writeUTF("Remote working directory: " + System.getProperty("user.dir"));
          break;

        case "quit":
          client.close();
          break;

        default:
          System.out.println("Invalid command!");
          output.writeUTF("Invalid Command!");
          break;
        }

      } while (!command[0].equals("quit"));

    } // end infinite loop

  }

  public void closeResources() throws IOException {
    ss.close();
    scan.close();
  }

}
