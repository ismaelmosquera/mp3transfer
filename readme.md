
## **Mp3 Transfer With Java**  
 
### *Server and Client*  
  
  
The JDK ( Java Development Kit ) comes with a really huge API ( Application Program Interface )  
with classes already tested and ready to use; these set of classes can help you to implement software to do almost any task that you can imagine.  
In this work, we demonstrate several of those classes in the java.net, java.io and javax.sound packages. Actually, what we did are two applications:  
>  
> - Mp3Server.  
> - Mp3Client.  
>  
to transfer mp3 data from the server to a client; the idea is so simple:  
A MP3Server has 	a repository with *.mp3 files. The server runs listening in an already known port to response requests from multiple clients.  
The clients comunicate with the server connecting to it in that port. A client makes requests for concrete mp3 files;  
when a client makes such a request, the server checks if the requested file is in its repository; if the server has the concrete mp3 file,  
loads the file into a stream, ask for relevant parameters ( number of channels, sample rate... ) and sends that information to the client;  
the client takes it and configures a local player according the information transfered by the server; the server starts transfering mp3 data to the client;  
the client reproduces the audio using the player configured according the parameters sent from the server;  
when the server finishes transfering the mp3 file the client is notified, then, the client can ask for another file or  
enter ‘quit’ which causes the client to shutdown.  
We hope that this work can be useful for somebody.  
  
  