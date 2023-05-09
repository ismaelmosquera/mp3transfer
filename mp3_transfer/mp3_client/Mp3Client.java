/*
 * Copyright (c) 2023 Ismael Mosquera Rivera
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

/*
* Mp3Client.java
*
* A TCP implementation.
*
* Author: Ismael Mosquera Rivera
*/

import java.net.Socket;
import java.net.UnknownHostException;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

/**
* Mp3Client application.
* This app implements a client which allows to make requests to a Mp3Server.
* The concrete mp3 client connects to a Mp3Server listening in an already known port.
* Multiple clients can connect and make requests to a single Mp3Server.
*
* @see Mp3Server
* @author Ismael Mosquera Rivera
*/
public class Mp3Client
{
public static void main(String[] args)
{
	/*
	* The host name where the mp3 server is running must be passed as parameter to the application
	*/
	if(args.length != 1)
	{
		System.out.println("Usage: Execute run.sh or run.bat depending on your shell.");
		System.out.println("You must edit these two files to set the host name where the Server is running.");
		System.out.println("In our case, the host name where the server is running is 'CAMPANILLA'");
		System.out.println("bye.");
	System.exit(1);
	}
	/* set the host name where the mp3 server is running */
	HOST = args[0];
	System.out.println("Mp3Client version 1.0");
	System.out.println("Transport protocol: TCP");
	System.out.println("Author: Ismael Mosquera Rivera");
	System.out.println();
Socket s = null;
try
{
	s = new Socket(HOST, StreamTransferPort.getPort());
}
catch(UnknownHostException e)
{
System.err.println(e);
}
catch(IOException e)
{
System.err.println(e);
}

/* Start the mp3 client */
(new AudioStreamReceiver(s)).run();

try
{
if(s != null) s.close();
}
catch(IOException e){}

}

/* variable to store the host name where the mp3 server is running */
private static String HOST;
}

/**
* AudioStreamReceiver class.
*
* This class implements all the logic to receive and reproduce audio data from the mp3 server.
*/
class AudioStreamReceiver
{
/**
* Constructor.
*
* @param Socket s A socket to comunicate to the mp3 server.
*
* @see Socket
*/
public AudioStreamReceiver(Socket s)
{
	bufferSize = 0;
	numChannels = 0;
	sampleRate = 0.0f;
socket = s;
player = null;
}

/**
* Method to do the main task.
* We decided to name this method 'run' but this class does not implement the Runnable interface.
* It was just the name choosed for this method.
*/
public void run()
{
BufferedReader in = null;
PrintWriter out = null;
BufferedReader userInput = null;
try
{
userInput = new BufferedReader(new InputStreamReader(System.in));
in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
out = new PrintWriter(socket.getOutputStream(), true);
System.out.println("Connected to mp3 server in port " + StreamTransferPort.getPort());
String filename;
while(true)
{
	System.out.println("<enter \"quit\" to shutdown>");
	System.out.print("File name: ");
filename = userInput.readLine();
out.println(filename);
if(filename.equals("quit"))
{
System.out.println("Mp3Client: shutdown!");
break;
}
if(in.readLine().equals("found"))
{
	// get parameters from server
	getParameters();
	// now we have parameters to setup the player
initPlayer();
// play audio stream
play();
}
else
{
	System.out.println("Sorry, " + filename + " file not found.");
}
System.out.println();
}
}
catch(IOException e)
{
System.err.println(e);
}
finally
{
try
{
	if(in != null) in.close();
if(out != null) out.close();
if(userInput != null) userInput.close();
}
catch(IOException e){}
}

}

private void getParameters() throws IOException
{
DataInputStream input = new DataInputStream(socket.getInputStream());
bufferSize = input.readInt();
numChannels = input.readInt();
sampleRate = input.readFloat();
}

private void initPlayer()
{
try
	{
AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
sampleRate,
16,
numChannels,
numChannels*2,
sampleRate,
false);
	player = AudioSystem.getSourceDataLine(decodedFormat);
	player.open(decodedFormat, bufferSize);
}
catch(LineUnavailableException e)
{
System.out.println("AudioStreamReceiver::loadAudio(String): player unavailable.");
System.out.println(e);
}
}

private void play() throws IOException
{
	player.start();

DataInputStream input = new DataInputStream(socket.getInputStream());
int bytesRead = 0;
byte[] data = new byte[bufferSize];
synchronized(input)
{
	synchronized(player)
	{
		while(true)
		{
		bytesRead = input.read(data, 0, data.length);
// check end of stream
		if(data[bytesRead-1]==(byte)0x55 && data[bytesRead-2]==(byte)0x55 && data[bytesRead-3]==(byte)0x55 && data[bytesRead-4]==(byte)0x55)
		{
			// send bytes to player except eof frame
			player.write(data, 0, bytesRead-4);
			break;
		}
		player.write(data, 0, bytesRead);
	}
	player.flush();
	}
}
	if(player.isActive()) player.stop();
}


private int bufferSize;
private int numChannels;
private float sampleRate;
private Socket socket;
private SourceDataLine player;
}

// END
