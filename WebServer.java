import java.util.*;
import java.net.*;
import java.io.*;

/*A0088135B
* A0120802Y
* CS2105
* Webserver.java
*/

class WebServer {

	private int listenPort = null;

	//the constructor:
	public Webserver (int portIn) {
		listenPort = portIn;

	}
	







	//the main function, program begins here
	public static void main (String portNum[]) throws Exception {
		
		int port;
		try {									//try to set port to the port entered in command line
			port = new Integer(portNum[0]);
		}
		catch (Exception portNumException) {	//if not, sets port to the default of 8789
			port = new Integer(8789);
		}
		Webserver ws = new WebServer(port);
	}

}
