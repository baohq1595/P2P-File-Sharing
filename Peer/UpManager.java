package Peer;

import Common.PeerInfo;
import Common.Sleep;
import Common.Tag;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.lang.String;

public class UpManager extends Thread
{
	private PeerInfo peer;
	private String filePath;
	private String fileName;
	private ServerSocket theServer;
	private Socket theConnection;
	private String peerID;   
	private String IPSERVER;
	KeepConnection keepIns;
	private boolean running;
	
	public UpManager()
	{
		Timer tasknew = new Timer();
		tasknew.scheduleAtFixedRate(new remind(), 2*60*1000, 2*60*1000);//2mins
	}

	public UpManager(String IPSERVER,String filePath, String peerID, String fileName,PeerInfo peer) throws IOException
	{
		this.filePath = filePath;
		this.peerID = peerID;
		this.peer = peer;
		this.fileName = fileName;
		this.IPSERVER = IPSERVER;
		Timer tasknew = new Timer();
		tasknew.scheduleAtFixedRate(new remind(),  30*1000, 30*1000);//2mins
		theServer = new ServerSocket(0);
		this.peer.setPort(theServer.getLocalPort());    
		this.running = true;
	}
	
	class remind extends TimerTask
	{
		public void run()
		{
			System.out.println("Running into timer task for uploader.");
			keepIns = new KeepConnection(filePath, "", null, null, peerID,4, IPSERVER, Tag.PORT_SERVER, fileName,0,null);
			if (!keepIns.runKeepConnection())
			{
				System.out.print("Phat hien");
				keepIns = new KeepConnection(filePath, "", null, null, peerID,0, IPSERVER, Tag.PORT_SERVER, fileName,peer.getPort(),null);
				keepIns.runKeepConnection();
			};
		}
	}

	public void run()
	{
		try
		{
			ExecutorService threadUploader = Executors.newCachedThreadPool();
			while(running)
			{
				System.out.println("Waiting peers download from me." + "My port " + theServer.getLocalPort());
				theConnection = theServer.accept();
				if (running ==false)
				{
					//Exit
					theConnection.close();
				}
				
				else
				{
					Uploader uploader = new Uploader(theConnection, filePath, peerID);
					threadUploader.execute(uploader);
					System.out.println("Upload has start");
				}
			}
		}
		catch(IOException ex)
		{
			System.out.println("Error " + ex);
		}
	}
	
	public void stopThread()
	{
		running = false;
		
		//Creat one connect to close;
		Socket sock;
		try
		{
			sock = new Socket(theServer.getInetAddress(),theServer.getLocalPort());
			sock.close();
		}
		catch(Exception e)
		{
			
		}
		System.out.println("Thread has fileName Stop " +this.fileName);
	}
	
	public String getID()
	{
		return this.peerID;
	}
	
	public String getFileName()
	{
		return this.fileName;
	}
	
}
