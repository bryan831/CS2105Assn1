class Connection {

	Socket socket;
	String filename;
	File file;
	static String WEB_ROOT = "";

	public Connection (Socket socketIn) {
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
			String line = br.readLine();
			String tokens[] = line.split(" ");
			while (true)
			{
				//String line = br.readLine();

				//String tokens[] = line.split(" ");

				if (!tokens[0].equals("GET") || !tokens[0].equals("POST"))
				{
					String errorMessage = "This simplistic server only understand GET or POST request\r\n";
					dos.writeBytes("HTTP/1.1 400 Bad Request\r\n");
					dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
					dos.writeBytes(errorMessage);
					socket.close();
				//	continue;
				}
				if (line == null)
				{
					System.out.println("Server is closing connection due to: line == null");
					socket.close();
					return;
				}
				if (tokens[0].equals("GET") && (tokens.length>1)) 
				{
					System.out.println("GET " + tokens[1]);
					//filename = WEB_ROOT + tokens[1];
					filename = tokens[1];
					file = new File(filename);
					System.out.println("M here posz");
					if (!file.exists()) 
					{
						System.out.println("did i get here");
						String errorMessage = "I cannot find " + tokens[1] + " on this server.\r\n";
						dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
						dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
						dos.writeBytes(errorMessage);
						System.out.println("did i get hereline 134");
						socket.close();
						continue;
					}
					if (file.exists())
						System.out.println("got here x");
					if (file.canRead())
						System.out.println("got here 2");
					if (!file.canRead()) 
					{
						String errorMessage = "You have no permission to access " + tokens[1] + " on this server.\r\n";
						dos.writeBytes("HTTP/1.1 403 Forbidden\r\n");
						dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
						dos.writeBytes(errorMessage);
						socket.close();
						continue;
					}

					System.out.println("got here y");
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
					System.out.println("here at pos1");
					int size = fis.read(buffer);						//reads from FileInputStream fis and stores into buffer
					while (size > 0) 
					{
						System.out.println("here at pos2");
						dos.write(buffer, 0, size);						//writes to the DataOutputStream with args (byte[], starting offset, length)
						size = fis.read(buffer);
					}
					dos.flush();										//flushes the DataOutputStream, forces buffered bytes to be written out

					// Finally, close the socket and get ready for
					// another connection.

					System.out.println("got here pos3");
					socket.close();

				}
								

				//System.out.println("Server is going to writeBytes");
				else if (tokens[0].equals("POST"))
				{
					System.out.println("In POST");
					socket.close();


				}
				System.out.println("192");
				return;
			}
		}
		catch (IOException e)
		{
			System.out.println("198");
			System.err.println("Unable to read/write: " + e.getMessage());
		}
	//end of constructor
	}
//end of class Connection
}
