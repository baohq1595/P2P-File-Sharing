package Peer;

import Common.Data;
import Common.DownloadList;
import Common.Tag;
import Common.General;
import Common.PeerInfo;
import Common.Sleep;
import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;


public class Downloader implements Runnable
{
	private Socket socket;
	private DataOutputStream outStream;
	private DataInputStream inStream;
	private RandomAccessFile raf;
	private General ins;
	private ArrayList<Integer> pieceList;
	private byte[] recBuffer;
	private byte[] sendBuffer;
	private byte[] dataBuffer;
	private String serverName;
	private String peerID;
	private String fileName;
	private String filePath;
	private String hashValue;
	private int port;
	private int pieceNum;
	private Data recData;
	private int endPiece ;
	private long lengFile;
	ArrayList<DownloadList> List;
	private int posi;
	private ExecutorService a;
	private static int numPiceDone;
	
	public Downloader(PeerInfo peer, String file,String folderDownload, String hash, ArrayList<DownloadList> pieceList,long lengFile,int posi,ExecutorService a)
	{
		this.filePath = folderDownload;
		this.a = a;
		this.peerID = peer.getPeerID();
		this.serverName = peer.getIpAddr();
		this.port = peer.getPort();
		fileName = file;
		hashValue = hash;
		List = pieceList;
		this.posi = posi;
		this.pieceList = pieceList.get(posi).list;
		sendBuffer = new byte[Tag.MSSG_SIZE];
		recBuffer = new byte[Tag.BUFFER_SIZE + Tag.MSSG_SIZE];
		dataBuffer = new byte[Tag.BUFFER_SIZE];
		recData = new Data("", 0);
		ins = new General();
		endPiece =(int) lengFile / Tag.BUFFER_SIZE;
		this.lengFile = lengFile;
	}
	
	public boolean getConnection()
	{
		try
		{
			//create socket
			socket = new Socket(serverName, port);
			System.out.println("Create connection to: " + serverName + "---" + port);
			//get streams
			//input stream from uploader
			inStream = new DataInputStream(socket.getInputStream());
			
			//output stream to uploader
			outStream = new DataOutputStream(socket.getOutputStream());
			outStream.flush();
			
			//random access file
			raf = new RandomAccessFile(filePath+"\\"+fileName, "rw");
			System.out.println("Done get Connection" + port+" "+serverName);                        
			
			//if piece list is null, terminate connection
			if (pieceList.isEmpty())
			{
				System.out.println("pieceList is Empty");
				closeConnection();
				return false;
			}
		}
		catch(IOException ex)
		{
			System.err.println("Error creating socket." +ex);
			return false;
		}
		return true;
	}
	
	//write byte stream into file by randomaccessfile
	public synchronized void writeByteStream(int index)
	{
		try
		{
			raf.seek(index * Tag.BUFFER_SIZE);
			if (index != endPiece)
			raf.write(dataBuffer, 0, Tag.BUFFER_SIZE);
			else raf.write(dataBuffer, 0,(int)lengFile - index*Tag.BUFFER_SIZE);
		}
		catch(IOException ex)
		{
			System.err.println("Error writing file.");
		}
	}
	
	public void run()
	{
		Sleep sleeper = new Sleep(10);
		int byteRead = 0;
		int currentRead = 0;
		Data data = new Data("", 0);
		boolean running;
		
		//handshaking
		byte[] hashBuffer = new byte[Tag.HASH_SIZE];
		hashBuffer = hashValue.getBytes();
		try 
		{
			//send hash value to uploader
			outStream.write(hashBuffer, 0, Tag.HASH_SIZE);
			outStream.flush();
			
			//waiting for uploader confirm hash value
			byte[] mssgBuffer = new byte[Tag.MSSG_ONLY_SIZE];
			while inStream.available() == 0)
			{
				sleeper.init_nano();
			}
			
			for(int i = 0; i < Tag.MSSG_ONLY_SIZE; i++)
				mssgBuffer[i] = inStream.readByte();
			
			
			//EDITED BY MRKEYS
			recData = ins.serializeByteArrayWithoutData(ByteBuffer.wrap(mssgBuffer));
			String hashMssg = recData.getMssg();
			//hash value is right - send request peerID
			if(hashMssg.equals(Tag.HASH_OK))
			{
				System.out.println("HASH OK");
				recData.setMssg(Tag.PEER_ID_RE);
				recData.setPieceIndex(0);
				sendBuffer = ins.data2ByteWithoutData(recData).array();
				outStream.write(sendBuffer, 0, Tag.MSSG_ONLY_SIZE);
				outStream.flush();
				
				//convert ByteBuffer to byte[]to send to socket
 				
			}
			
			//wrong hash value - send terminate control terminate connection
			else if (hashMssg.equals(Tag.HASH_WRONG))
			{
				System.out.println("HASH WRONG");
				recData.setMssg(Tag.MSSG_TYPE_TER);
				recData.setPieceIndex(0);
				sendBuffer = ins.data2ByteWithoutData(recData).array();
				outStream.write(sendBuffer, 0, Tag.MSSG_ONLY_SIZE);
				outStream.flush();
				closeConnection();
			}
			
			//receive peerID
			byte[] peerIDBuffer = new byte[Tag.PEER_ID_SIZE];
			while (inStream.available() == 0)
			{
				sleeper.init_nano();
			}
			
			for(int i = 0; i < Tag.PEER_ID_SIZE; i++)
				peerIDBuffer[i] = inStream.readByte();
			
			//EDITED BY MRKEYS
			//convert byte[] to peerID value
			String peer_id = new String(peerIDBuffer);
			System.out.println(peer_id);
			
			//if wrong hash value - send terminate control terminate connection
			if (!peer_id.equals(peerID)) 
			{
				System.out.println(peerID);
				data = new Data(Tag.MSSG_TYPE_TER, pieceList.get(0));
				sendBuffer = ins.data2ByteWithoutData(data).array();
				outStream.write(sendBuffer, 0, sendBuffer.length);
				outStream.flush();
				closeConnection();
			}
			}catch(Exception e)
			{
				System.err.println("Error while hand "+e);
				closeConnection();
			}
			try
			{
				System.out.println("Download File");                            
				running = true;
				for (int piece : pieceList)
				{
					do
					{
						//data = new Data(Tag.MSSG_TYPE_RE, piece);
						data.setMssg(Tag.MSSG_TYPE_RE);
						
						//System.out.println("Piece "+piece);
						data.setPieceIndex(piece);
						
						sendBuffer = ins.data2ByteWithoutData(data).array();
						outStream.write(sendBuffer, 0, sendBuffer.length);
						outStream.flush();
						
						//read data sent from uploader
						for(int i = 0; i < Tag.BUFFER_SIZE+Tag.MSSG_SIZE; i++)
						{
							recBuffer[i] = inStream.readByte();
						}
				//EDITED BY MRKEYS
						recData = ins.serializeByteArrayWithData(ByteBuffer.wrap(recBuffer));
						dataBuffer = recData.getByteArr();
						pieceNum = recData.getPieceIndex();
						if (pieceNum == piece)
						{
							writeByteStream(piece);
						}
					}while (pieceNum != piece && running == true);
					
					if (running = false )
						break;
				}
				//send uploader terminate signal
				data.setMssg(Tag.MSSG_TYPE_TER);
				sendBuffer = ins.data2ByteWithoutData(data).array();
				outStream.write(sendBuffer, 0, sendBuffer.length);
				outStream.flush();
				
				System.out.println("Download completed.");
				closeConnection();
				List.get(posi).list.clear();
				a.shutdown();
			}
			catch(IOException ex)
			{
				System.err.println("Error writing or reading stream." +ex);
				running = false;
				closeConnection();
			}
	}
	
	//close connection
	public void closeConnection()
	{
		try
		{
			outStream.close();
			inStream.close();
			raf.close();
			socket.close();
			System.out.println("Closed stream.");
		}
		catch(IOException ex)
		{
			System.err.println("Error closing. "+ex);
		}
	}
}
