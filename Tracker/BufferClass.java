package Tracker;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import Common.*;

public class BufferClass extends Thread
{
	private Socket socket;
	private static ArrayList<HashTable> hashTableList;
	private static ArrayList<FileInfo> fileList;

	public BufferClass (Socket theConnection,ArrayList<HashTable> hashTableList,ArrayList<FileInfo> fileList)
	{
		this.socket = theConnection;
		this.hashTableList = hashTableList;
		this.fileList = fileList;
	}
	
	public void run()
	{
		Tracker tracker = new Tracker(socket,hashTableList,fileList);
		tracker.run();
	}
}
