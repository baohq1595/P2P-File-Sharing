package Peer;

import java.io.*;
import java.net.*;
import Common.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class KeepConnection
{
	private String fileName;
	private String fileRequest;
	private Socket theSocket = null;
	private DataInputStream inStream = null;
	private DataOutputStream outStream = null;
	private ArrayList<FileInfo> fileList = null;
	private ArrayList<PeerInfo> peerList = null;
	private String hashValue;
	private String hashValueRequest;
	private String UID;
	private int mode;
	private General ins;
	private String ip;
	private int port;
	private File fis;
	private File fisRe;
	private String NAME;
	private int portUp;
	private String keySearch;
	
	public KeepConnection()
	{
		;
	}
	
	public KeepConnection(String fileName, String hashValueRequest, ArrayList<FileInfo> fileList, ArrayList<PeerInfo> peerList, String UID, int mode, String ip, int port, String NAME,int portUP,String keySearch)
	{
		
		this.NAME = NAME;
		this.fileName = fileName;
		this.hashValueRequest = hashValueRequest;
		this.fileList = fileList;
		this.peerList = peerList;
		this.UID = UID;
		this.mode = mode;
		this.ip = ip;
		this.port = port;
		this.portUp = portUP;
		this.keySearch = keySearch;
		ins = new General();
	}
	/*
	0:send UID : peerID
	1: send hash (update peerInfo to database)
	2:send request sendPeerList (list peer has file)
	3:send request sendFileList(GUI request)
	4:Keep alive upload
	5:Delete Peer from database
	*/
		
	public boolean initialize()
	{
		
		try
		{
			theSocket = new Socket(ip, port);
			
			inStream = new DataInputStream(theSocket.getInputStream());
			outStream = new DataOutputStream(theSocket.getOutputStream());
			outStream.flush();
			
			System.out.println("Connected to tracker.");
    		
			if (!fileName.equals(""))
			{
				fis = new File (fileName);
				hashValue = ins.toSHAValue(fis);
			}
			
		}
		catch (IOException ex)
		{
			System.out.println("Error. creat connect"+ex);
				return false;
		}
		return true;
	}
	
	public boolean runKeepConnection()
	{
		boolean flag;
		if (!initialize())
		{
			//Creat Stream fail
			return false;
		}
		else
		{
			File f;
			byte[] mssgBuff = new byte[10];
			FileOutputStream fii;
			String mssg;
			boolean running;
			flag = true;
			int leng ;
			running = true;
			byte[] lengBuff = new byte[4];
			while (running == true)
			{
				try
				{
					switch(mode)
					{
						case 0:
							System.out.println("Send mode 0 "+ Tag.RECIVE_PEERID);
							mssgBuff = Tag.RECIVE_PEERID.getBytes();
							outStream.write(mssgBuff, 0, mssgBuff.length);
							outStream.flush();
							inStream.read(mssgBuff);                        
							mssgBuff = UID.getBytes();                        
							outStream.write(mssgBuff, 0, Tag.PEER_ID_SIZE);
							outStream.flush();
							System.out.println("Send mode 0 "+ UID);
							mode = 1; 
							break;
						case 1:
							System.out.println("Send mode 1 " + Tag.UPDATE_LISTFILE);
							mssgBuff = Tag.UPDATE_LISTFILE.getBytes();
							outStream.write(mssgBuff, 0, mssgBuff.length);
							outStream.flush();
							inStream.read(mssgBuff);
							System.out.println("msg "+new String(mssgBuff));
							System.out.println("Port gui di "+portUp);
							outStream.writeInt(this.portUp);
							outStream.flush();
							inStream.read(mssgBuff); 
							byte[] hashBuff = new byte[Tag.HASH_SIZE];
							System.out.println("Send SHash mode 1 "+ hashValue);
							outStream.write(hashValue.getBytes(), 0, Tag.HASH_SIZE);  //send hash
							outStream.flush();
							inStream.read(mssgBuff);
							
							//send lengofName file
							lengBuff = ByteBuffer.wrap(lengBuff).putInt(NAME.length()).array();
							System.out.println("Send lengName mode 1 "+ NAME.length());
							outStream.write(lengBuff);
							outStream.flush();
							
							//read ACK
							inStream.read(mssgBuff);
							
							//send Name of File
							System.out.println("Send Name mode 1 "+ NAME);
							outStream.write(NAME.getBytes());
							outStream.flush();
							
							//read ack
							inStream.read(mssgBuff);
							
							//send lengFile
							byte[] lengFile = new byte[8];
							long lengg = fis.length();
							lengFile = ByteBuffer.wrap(lengFile).putLong(lengg).array();
							System.out.println("Send lengfile mode 1 "+ fis.length());
							outStream.write(lengFile);
							outStream.flush();
							
							//Receive ACK
							inStream.read(mssgBuff);
							System.out.println("Send mode 1 "+ Tag.END_UPDATE);
							outStream.write(Tag.END_UPDATE.getBytes(), 0, 10);
							inStream.read(mssgBuff);
							mode = 10;
							break;
						case 2:
							System.out.println("Send mode 2 "+ Tag.FILELIST_RE);
							mssgBuff = Tag.FILELIST_RE.getBytes();
							outStream.write(mssgBuff, 0, mssgBuff.length);
							outStream.flush();
							inStream.read(lengBuff);
							leng = ByteBuffer.wrap(lengBuff).getInt();
							System.out.println("Send mode 2 "+ Tag.ACK_FP_LENG);
							outStream.write(Tag.ACK_FP_LENG.getBytes());
							outStream.flush();
							if (leng == 0)
							{
								System.out.println("Null fileList");
								this.fileList = null;
								mode = 10; //mode Exit
								break;
							}
							
							System.out.println("leng "+leng);
							byte[] fileBuff = new byte[(int) leng];
							inStream.read(fileBuff);
							
							//cover fileBuff
							System.out.println("Send mode 2 Xu li file ");
							FileOutputStream fi = new FileOutputStream ("listFilep.txt");
							fi.write(fileBuff);
							fi.close();
							String pathFileList = "listFilep.txt";
							ins.readFromFileInfofile(pathFileList,fileList);
							outStream.write(Tag.MSSG_TYPE_TER.getBytes());
							outStream.flush();
							f = new File (pathFileList);
							f.delete();
							mode = 10;
							break;
						case 3:
							System.out.println("Send mode 3 "+ Tag.PEERLIST_REQ);
							mssgBuff = Tag.PEERLIST_REQ.getBytes();
							outStream.write(mssgBuff, 0, mssgBuff.length);
							outStream.flush();

							inStream.read(mssgBuff);
							//Send HashRequest
							System.out.println("Send mode 3 hash request"+ hashValueRequest);
							outStream.write(hashValueRequest.getBytes());//, 0, mssgBuff.length);
							outStream.flush();
							inStream.read(mssgBuff, 0, mssgBuff.length);
							mssg = new String(mssgBuff);
							if (mssg.equals(Tag.MSSG_TYPE_TER))
								break;
							
							else
							{
								for (int i =0;i<4;i++)
									lengBuff[i] = mssgBuff[i];
							}
							
							mssgBuff = Tag.PEERLIST_REQ.getBytes();
							System.out.println("Send mode 3 "+ Tag.PEERLIST_REQ);
							outStream.write(mssgBuff, 0, mssgBuff.length);
							outStream.flush();
							byte[] fileBuff3 = new byte[ByteBuffer.wrap(lengBuff).getInt()];
							inStream.read(fileBuff3);
							
							//xu li file
							System.out.println("Xu li file mode 3 ");
							fii = new FileOutputStream ("listPeerp.txt");
							fii.write(fileBuff3);
							fii.close();
							ins.readFromPeerInfofile("listPeerp.txt",peerList);
							f = new File("listPeerp.txt");
							f.delete();
							mode = 10;
							break;
						case 4:
							try
							{
								mssgBuff = Tag.KEEP_ALIVE.getBytes();
								outStream.write(mssgBuff, 0, mssgBuff.length);
								outStream.flush();
								inStream.read(mssgBuff);
								
								//Send HashRequest
								outStream.write(this.UID.getBytes());
								outStream.flush();
								outStream.write(Tag.MSSG_TYPE_TER.getBytes());
								outStream.flush();       
								inStream.read(mssgBuff);
								mssg = new String (mssgBuff);
								if(msg.equals(Tag.NOTEXIST))
								{
									flag = false;
								};
							}
							catch(Exception ex)
							{
								//
							}
							mode = 10;
							break;
						case 5:
							outStream.write(Tag.DELETE_PEER.getBytes(),0,10);
							outStream.flush();
							outStream.write(this.UID.getBytes());
							outStream.flush();
							outStream.write(this.hashValue.getBytes());
							outStream.flush();
							outStream.write(Tag.MSSG_TYPE_TER.getBytes());
							outStream.flush();                        
							mode = 10;
							break;
						case 6:
							//Seacrh file
							outStream.write(Tag.SEACRH_KEY.getBytes());
							outStream.flush();
							outStream.writeUTF(keySearch);
							outStream.flush();
							inStream.read(lengBuff,0,lengBuff.length);
							leng = ByteBuffer.wrap(lengBuff).getInt();
							if (leng == 0 )
							{
								//close connection
								outStream.write(Tag.MSSG_TYPE_TER.getBytes());
								outStream.flush();
								mode  = 10;
							}
							else
							{
								byte[] fileBuff4 = new byte[leng];
								inStream.read(fileBuff4,0,leng);
								fii = new FileOutputStream ("listPeerp.txt");
								fii.write(fileBuff4);
								fii.close();
								ins.readFromFileInfofile("listPeerp.txt",fileList);
								f = new File("listPeerp.txt");
								f.delete();
								outStream.write(Tag.MSSG_TYPE_TER.getBytes());
								outStream.flush();
								mode = 10;
								
							}
							break;
						case 10: 
							running = false;  
							closeConnection();
							break;
					}

				}
				catch(Exception ex)
				{
					System.err.println("ERR while KeepConnection "+ex);
					closeConnection();
					return false;
				}

			}
		}
		return flag;
	}
	void closeConnection()
	{
		try
		{
			theSocket.close();
			outStream.close();
			inStream.close();
		}
		catch( Exception e)
		{
			System.err.println("Err while close Stream "+e);
			System.exit(1);
		}
	}
}
