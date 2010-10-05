package eai;

import java.io.*;
import java.net.*;

public class SimpleServer
{

	public static void main(String[] args)
	{
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		PrintWriter socketOut = null;
		BufferedReader socketIn = null;

		try
		{
			serverSocket = new ServerSocket(4444);
			clientSocket = serverSocket.accept();

			socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
			socketIn = new BufferedReader(
				new InputStreamReader(
					clientSocket.getInputStream()
				)
			);

			String line;
			while ((line = socketIn.readLine()) != null)
			{
				socketOut.println(line);
			}

			socketIn.close();
			socketOut.close();
			clientSocket.close();
		}
		catch (Exception e)
		{
			System.err.println(e.toString());
		}
	}

}
