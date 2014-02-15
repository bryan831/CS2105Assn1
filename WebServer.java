import java.util.*;
import java.net.*;
import java.io.*;

/*A0088135B
* A0120802Y
* CS2105
* Webserver.java
*/

class WebServer {

	private int listenPort;
	private ServerSocket serverSocket = null;

	//the constructor:
	public WebServer (int portIn) {
		listenPort = portIn;

		System.out.println("In the WebServer now.");
		System.out.println("Creating ServerSocket...");
		try {
			serverSocket = new ServerSocket(listenPort);
		}
		catch (IOException e)
		{
			System.err.println("Unable to listen on port " + listenPort + ": " + e.getMessage());
			return;
		}
	// Keep accepting connections from clients until the server is terminated.
		while (true) {
			try
			{
				Socket socket1;
				socket1 = serverSocket.accept();
				System.out.println("Connection accepted.");
				new Connection(socket1);
				
				//continue;
			}
			catch (IOException ioE)
			{
				System.err.println("Unable to accept connection on port:" + listenPort);
				continue;
			}	

			}	
		}
	//the main function, program begins here
	public static void main (String portNum[]) throws Exception {
		
		int port;
		try {									//try to set port to the port entered in command line
			if (portNum.length ==1)
				port = new Integer(portNum[0]);
			else {
				System.out.println("Please enter port number.");

				return;
			}

		}
		catch (Exception portNumException) {	//if not, sets port to the default of 8789
			port = new Integer(8789);
			System.out.println("Default port number of 8789 assigned.");
		}
		System.out.println("WebServer set up with port number: " + port);
		WebServer ws = new WebServer(port);
		
		
		return;
	}	//end of main fn

}	//end of WebServer class

class Connection 
{
	String filename;
	File file;
	static String WEB_ROOT = "";
	public Connection (Socket socketIn) 
	{	//constructor for the Connection class
		Socket socket;
		//connection has been accepted, set up the input and output streams 
		socket = socketIn;
		try 
		{
			InputStream is = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			System.out.println("Your IS, BR, OS, and DOS have been set up.");

			//start reading lines from the socket
			while (true)
			{
				String line = br.readLine();

				String tokens[] = line.split(" ");

				//if the first token is NOT GET AND NOT POST
				if (!tokens[0].equals("GET") && !tokens[0].equals("POST"))
				{
					String errorMessage = "This simplistic server only understand GET or POST request\r\n";
					dos.writeBytes("HTTP/1.1 400 Bad Request\r\n");
					dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
					dos.writeBytes(errorMessage);
					socket.close();
					continue;
				}
				if (line == null)
				{
					System.out.println("Server is closing connection due to: line == null");
					socket.close();
					return;
				}
				if (tokens[0].equals("GET") && (tokens.length>1))	//if first token is GET, and length>1, assume token[1] is filename 
				{
					System.out.println("GET " + tokens[1]);
					filename = WEB_ROOT + tokens[1];
					file = new File(filename);
					if (!file.exists()) 
					{
						String errorMessage = "I cannot find " + tokens[1] + " on this server.\r\n";
						dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
						dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
						dos.writeBytes(errorMessage);
						socket.close();
						continue;
					}
					if (!file.canRead()) 
					{
						String errorMessage = "You have no permission to access " + tokens[1] + " on this server.\r\n";
						dos.writeBytes("HTTP/1.1 403 Forbidden\r\n");
						dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
						dos.writeBytes(errorMessage);
						socket.close();
						continue;
					}
					//at this point, everything is OK
					dos.writeBytes("HTTP/1.1 200 OK\r\n");
					//send back content length
					dos.writeBytes("Content-length: " + file.length() + "\r\n");
					//send back content type:
					if (filename.endsWith(".html")) 
					{
						dos.writeBytes("Content-type: text/html\r\n");
					}
					if (filename.endsWith(".jpg")) 
					{
						dos.writeBytes("Content-type: image/jpeg\r\n");
					}	
					dos.writeBytes("\r\n");

					// the body of the file.
				
					// Read the content 1KB at a time.
					byte[] buffer = new byte[1024];
					FileInputStream fis = new FileInputStream(file);
					int size = fis.read(buffer);						//reads from FileInputStream fis and stores into buffer
					while (size > 0) 
					{
						dos.write(buffer, 0, size);						//writes to the DataOutputStream with args (byte[], starting offset, length)
						size = fis.read(buffer);
					}
					dos.flush();										//flushes the DataOutputStream, forces buffered bytes to be written out

					// Finally, close the socket and get ready for
					// another connection.

					socket.close();

				}
								

				//System.out.println("Server is going to writeBytes");
				else if (tokens[0].equals("POST"))
				{
					System.out.println("In POST");
					socket.close();


				}
				return;
			}
		}	//end of try
		catch (IOException e)
		{
			System.err.println("Unable to read/write: " + e.getMessage());
		}
	}	//end of constructor
}	//end of Class Connection