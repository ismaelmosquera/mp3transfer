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
* Mp3Server.java
*
* A TCP implementation.
*
* Author: Ismael Mosquera Rivera
*/

import java.net.Socket;
import java.net.ServerSocket;

import java.io.File;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
* Mp3Server Application.
*
* This app implements a mp3 file server using the TCP protocol.
* The server can manage multiple requests listening in an already known port.
* The server has a mp3 file repository; you must put your mp3 files there.
*
* @see ServerSocket, Socket
* @ author Ismael Mosquera Rivera
*/
public class Mp3Server
{
public static void main(String[] args)
{
	System.out.println("Mp3Server version 1.0");
	System.out.println("Transport protocol: TCP");
	System.out.println("Author: Ismael Mosquera Rivera");
	System.out.println();
	System.out.println("Mp3Server listening in  port " + StreamTransferPort.getPort());
	System.out.println("Mp3Server: press ctrl+c to quit.");
boolean listening = true;
ServerSocket server = null;
try
{
server = new ServerSocket(StreamTransferPort.getPort());
}
catch(IOException e)
{
System.err.println(e);
System.exit(1);
}

try
{
	while(listening)
{
(new Thread(new AudioStreamSender(server.accept()))).start();
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
	System.out.println("AudioStreamServer: shutdown and clean resources...");
if(server != null) server.close();
}
catch(IOException e){}
}
}

}

/**
* Class AudioStreamSender.
* This class sends responses to a client request.
* That is, a client requests for a file and, if it exists in the repository, sends the concrete file to the concrete client.
*
* @ author Ismael Mosquera Rivera
*/
class AudioStreamSender implements Runnable
{

/**
* Constructor.
*
* @param Socket s A socket to comunicate with a client.
*
*/
public AudioStreamSender(Socket socket)
{
	bufferSize = 0;
		audioInput = null;
client = socket;
}

/**
* Method to do the task.
* Since this class implements the Runnable interface, this is its threaded method.
*
*/
public void run()
{
BufferedReader in = null;
PrintWriter out = null;
try
{
in = new BufferedReader(new InputStreamReader(client.getInputStream()));
out = new PrintWriter(client.getOutputStream(), true);
String filename;
while(true)
{
filename = in.readLine();
if(filename.equals("quit"))
{
	System.out.println();
	System.out.println("AudioStreamSender: shutdown!");
	break;
}
if(hasFile(filename))
{
	out.println("found");
loadAudio(FILE_REPOSITORY+filename);
System.out.println("AudioStreamSender: dispatch " + filename + "...");
// send audio stream
sendAudioStream(audioInput, new DataOutputStream(client.getOutputStream()));
}
else
{
	out.println("!found");
}
}
}
catch(FileNotFoundException e)
{
	System.err.println(e);
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
if(audioInput != null) audioInput.close();
if(client != null) client.close();
}
catch(IOException e){}
}
}

private boolean hasFile(String filename)
{
	int i = filename.lastIndexOf(".")+1;
		String ext = filename.substring(i).toLowerCase();
	if(!ext.equals("mp3")) return false;
	boolean found = false;
	String[] files = null;

	synchronized(this)
	{
		files = (new File(FILE_REPOSITORY)).list();
		}

		for(String s : files)
	{
		if(s.equals(filename))
		{
			found = true;
			break;
		}
	}
return found;
}

private void loadAudio(String audioFile)
{
	try
	{
	AudioInputStream rawInput	= AudioSystem.getAudioInputStream(new File(audioFile));
	AudioFormat baseFormat = rawInput.getFormat();
	bufferSize = (int)baseFormat.getSampleRate();
	if(bufferSize == 22050) bufferSize = 22100;
DataOutputStream output = new DataOutputStream(client.getOutputStream());
output.writeInt(bufferSize);
output.writeInt(baseFormat.getChannels());
output.writeFloat(baseFormat.getSampleRate());
	AudioFormat decodedFormat = new AudioFormat(
		AudioFormat.Encoding.PCM_SIGNED,
		baseFormat.getSampleRate(),
		16,
		baseFormat.getChannels(),
		baseFormat.getChannels()*2,
		baseFormat.getSampleRate(),
		false);
	audioInput = AudioSystem.getAudioInputStream(decodedFormat, rawInput);
}
catch(IOException ioe)
	{
		System.out.println("AudioStreamSender::loadAudio(String audioFile): no such file.");
		System.out.println(ioe);
	}
	catch(UnsupportedAudioFileException afe)
	{
		System.out.println("AudioStreamSender::loadAudio(String audioFile): unsupported audio file.");
		System.out.println(afe);
	}
}

private void sendAudioStream(AudioInputStream input, DataOutputStream output) throws IOException
{
// end of stream frame
	byte[] eof = new byte[4];
	eof[0] = (byte)0x55;
	eof[1] = (byte)0x55;
	eof[2] = (byte)0x55;
	eof[3] = (byte)0x55;

	int bytesRead = 0;
	byte[] data = new byte[bufferSize];
	synchronized(input)
	{
		synchronized(output)
		{
	while(true)
	{
		bytesRead = input.read(data, 0, data.length);
		if(bytesRead == -1)
		{
			// send end of stream
			output.write(eof, 0, eof.length);
			break;
	}
		output.write(data, 0, bytesRead);
	}
	output.flush();
}
}
}

private int bufferSize;
private Socket client;
private AudioInputStream audioInput;
private final String FILE_REPOSITORY = "../file/";

}

// END
