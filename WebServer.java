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
			serverSocket = new ServerSocket(listenPort);	//WebServer sets up a new ServerSocket at the port #
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
				//once the ServerSocket receives a connection, it is accepted and the Connection class is called
				new Connection(socket1);
				
				continue;
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

	Socket socket;
	InputStream is;
	BufferedReader br;
	OutputStream os;
	DataOutputStream dos;

	String line;
	String tokens[];

	public Connection (Socket socketIn) 
	{	//constructor for the Connection class

		socket = socketIn;
		//connection has been accepted, set up the input and output streams 
		try 
		{
			is = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));

			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			System.out.println("Your IS, BR, OS, and DOS have been set up.");

			//start reading lines from the socket
			while (true)
			{
				line = br.readLine();
				tokens = line.split(" ");

				//if the first token is NOT GET AND NOT POST
				if (!tokens[0].equals("GET") && !tokens[0].equals("POST"))
				{
					String errorMessage = "This simplistic server only understand GET or POST request\r\n";
					dos.writeBytes("HTTP/1.1 400 Bad Request\r\n");
					dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
					dos.writeBytes(errorMessage);
					socket.close();
					//continue;
					return;
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
					handleGET();

					return;	
				}
				//System.out.println("Server is going to writeBytes");
				else if (tokens[0].equals("POST"))
				{
					System.out.println("In POST");
					

					socket.close();


				}
				return;
			}		//end of while(true)
		}	//end of try
		catch (IOException e)
		{
			System.err.println("Unable to read/write: " + e.getMessage());
		}
	}	//end of constructor


	void handleGET () 
	{	//the handleGet function handles the HTTP GET requests in Connection class
		try 
		{
			if (tokens[1] )
			filename = WEB_ROOT + tokens[1];	//    todo.pl
			file = new File(filename);
			Process p;

			//first check if the file requested is the dynamic Perl script
			if (filename.endsWith(".pl"))		//request is GET, the perl script
			{
				//need to execute the Perl script instead of sending the content back
				//on UNIX server:
				//p = Runtime.getRuntime().exec("/usr/bin/perl /home/b/bryan831/a1/" + filename);

				//on local machine
				p = Runtime.getRuntime().exec("/usr/bin/perl " + filename);
				//write to process std input
				DataOutputStream po = new DataOutputStream(p.getOutputStream());
				//po.writeBytes("");

				//get output from the process
				InputStream pIS = p.getInputStream();
				BufferedReader pBR = new BufferedReader(new InputStreamReader(pIS));
				String pLine;


				//at this point, everything is OK
				dos.writeBytes("HTTP/1.1 200 OK\r\n");
				//send back content length
				dos.writeBytes("Content-length: " + file.length() + "\r\n");

				pLine = pBR.readLine();
				System.out.println("Read 1 line from the process");
				while (pLine != null)
				{
					System.out.println("Reading lines from process");
					dos.writeBytes(pLine);
					pLine = pBR.readLine();
				}
				dos.flush();
				//complete:
				socket.close();
				return;
			}


			if (!file.exists()) 
			{
				String errorMessage = "I cannot find " + tokens[1] + " on this server.\r\n";
				dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
				dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
				dos.writeBytes(errorMessage);
				socket.close();
				
				return;
			}
			if (!file.canRead()) 
			{
				String errorMessage = "You have no permission to access " + tokens[1] + " on this server.\r\n";
				dos.writeBytes("HTTP/1.1 403 Forbidden\r\n");
				dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
				dos.writeBytes(errorMessage);
				socket.close();
			
				return;
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
			if (filename.endsWith(".gif"))
			{
				dos.writeBytes("Content-type: image/gif\r\n");
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

			return;
		}	//end of try
		catch (IOException e)
		{
			System.err.println("Unable to read/write: " + e.getMessage());
		}
	}		//end of handleGet function
}	//end of Class Connection