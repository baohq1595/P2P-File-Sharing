package Peer;

import Common.Data;
import Common.Tag;
import Common.General;
import Common.Sleep;
import java.io.*;
import java.net.*;
//import java.util.*;
import java.nio.ByteBuffer;

public class Uploader extends Thread{

	private Socket socket;
	private DataOutputStream outStream;
	private DataInputStream inStream;
	private String filePath;
	private String peerID;
	private String mssgType;
	private String hashValue;
	private General ins;
	private RandomAccessFile raf;
	private ByteBuffer byteBuffer;
	private byte[] sendBuffer,sendBufferMrkeys,messBuff;
	private byte[] recBuffer;
	private Data noContent;
	private Data content;
	private int index;		//piece index
	private int endPiece;
	public Uploader(Socket socket, String filePath, String peerID) {
		
		this.socket = socket;
		this.peerID = peerID;
		this.filePath = filePath;
		sendBuffer = new byte[Tag.BUFFER_SIZE + Tag.MSSG_SIZE];
                sendBufferMrkeys = new byte[Tag.BUFFER_SIZE ];
		recBuffer = new byte[Tag.MSSG_SIZE];
		noContent = new Data("", 0);
		content = new Data("", 0);
                messBuff = new byte[Tag.MSSG_SIZE];
	}
	
	//initial stream
	public boolean initialize() {
		
		try {
			//get output stream - to client
			outStream = new DataOutputStream(socket.getOutputStream());
			outStream.flush();
			
			//get input stream - from client
			inStream = new DataInputStream(socket.getInputStream());
			
			//get random access file stream - for reading
			raf = new RandomAccessFile(filePath, "r");
                        this.endPiece = (int )raf.length() / Tag.BUFFER_SIZE;
			
			/////////////
			ins = new General();
			File file = new File(filePath);
			hashValue = ins.toSHAValue(file);
			System.out.println(file.getName());
			System.out.println(hashValue);
                        return true;
		} 
		catch(IOException ex) {
			System.err.println("Error getting stream.");
                        return false;
		}
                
	}
	
	//read bytes in file into buffer
	public void seekByteStream(int index) {
		
		try {
			raf.seek(index * Tag.BUFFER_SIZE);
                          // System.out.println("Current posi "+raf.getFilePointer());
			//if byte remain in file greater than buffer size 
			if (index != endPiece) {
				raf.read(sendBufferMrkeys, 0, Tag.BUFFER_SIZE);
			}
			//if byte remain in file less than buffer size
			else {                                
				long remain = (raf.length() - index*Tag.BUFFER_SIZE);
				raf.read(sendBufferMrkeys,0, (int)remain);
                                System.out.println("MAS "+remain);
                             
			}
		}
		catch(IOException ex) {
			System.err.println("Error seeking file ."+ex);
		}	
	}
	
	public void run() {
		Sleep sleeper = new Sleep(10);
		//initialize step
		if (!initialize()){
                    closeConnection();
                }else {
                    System.out.println("PerID up "+peerID);
                    //handshaking process
                    int byteRead = 0;
                    int currentRead = 0;
                    byte[] hashBuffer = new byte[Tag.HASH_SIZE]; //for receiving hash value from downloader
                    try {

                            //read input stream into byte[]
                            //EDITED BY MRKEYS

                            // inStream.read(hashBuffer, 0, Tag.HASH_SIZE);
    //                        for(int i = 0; i < Tag.HASH_SIZE; i++)
    //                            hashBuffer[i] = inStream.readByte();
                            inStream.read(hashBuffer,0,Tag.HASH_SIZE);
                            //do {
                            //	byteRead = inStream.read(hashBuffer, currentRead, Tag.HASH_SIZE - currentRead);
                            //	if(byteRead >= 0) currentRead += byteRead;
                            //} while(byteRead > -1); //read byte[] finish
                            //convert byte[] into string and compare with hash value of uploader
                            String hashRec = new String(hashBuffer);
                            System.out.printf("Hash value received: %s\n", hashRec);
                            //System.out.printf("Hash value is: %s\n", hashValue);
                            if (!hashRec.equals(hashValue)) {

                                    //wrong hash value
                                    noContent.setMssg(Tag.HASH_WRONG);
                                    noContent.setPieceIndex(0);
                                    System.out.println("hashValue WRONG");
                                    //convert ByteBuffer to byte[]to send to socket
                                    sendBuffer = ins.data2ByteWithoutData(noContent).array();
                                    outStream.write(sendBuffer);
                                    outStream.flush();
                                    closeConnection();
                            }
                            else {
                                    //hash value is right
                                    System.out.println("hashValue OK");
                                    noContent.setMssg(Tag.HASH_OK);
                                    noContent.setPieceIndex(0);
                                   // System.out.println(sendBuffer.length);
                                    //convert ByteBuffer to byte[]to send to socket
                                    sendBuffer = ins.data2ByteWithoutData(noContent).array();
                                    outStream.write(sendBuffer);
                                    outStream.flush();
                            }//end handshaking process

                            //listening from downloader for sending peerID or terminate connection
                            byte[] mssgBuffer = new byte[Tag.MSSG_ONLY_SIZE];
                            while (inStream.available() == 0){
                                sleeper.init_nano();
                            }
                            for(int i = 0; i < Tag.MSSG_ONLY_SIZE; i++)
                                mssgBuffer[i] = inStream.readByte();

                            //byteRead = inStream.read(mssgBuffer, 0, Tag.MSSG_ONLY_SIZE);                       
                            //EDITED BY MRKEYS
                            //sned ID
                            noContent  = ins.serializeByteArrayWithoutData(ByteBuffer.wrap(mssgBuffer));
                            String tempMssg = noContent.getMssg();
                             //System.out.println("MSG " +tempMssg);
                            if (tempMssg.equals(Tag.PEER_ID_RE)) {
                                    byte[] peerIDBuffer = new byte[Tag.PEER_ID_SIZE];
                                    peerIDBuffer = peerID.getBytes();
                                    System.out.println("Send Peer ID " + new String(peerIDBuffer));
                                    outStream.write(peerIDBuffer);
                                    outStream.flush();
                            }
                            else if (tempMssg.equals(Tag.MSSG_TYPE_TER)) {
                                    System.out.println("From downloader " + tempMssg);
                                    closeConnection();
                            }
                    }catch(IOException ex) {
                        System.err.println("Error in Handshaking." +ex);
                    }

                            //exchanging data
                            boolean running;
                            running = true;
                    while(running == true) {
                        try{
                                    //read request from downloader
                              while (inStream.available() == 0){
                                 sleeper.init_nano();
                              }
                                for(int i = 0; i < Tag.MSSG_SIZE; i++)
                                    recBuffer[i] = inStream.readByte();

                                    //byteRead = inStream.read(recBuffer, 0, Tag.MSSG_SIZE);
                                    //EDITED BY MRKEYS

                                    //convert byte[] to Data object
                                    noContent = ins.serializeByteArrayWithoutData(ByteBuffer.wrap(recBuffer));
                                    mssgType = noContent.getMssg();

                                    //test message type from downloader
                                    switch(mssgType) {
                                            //request for data
                                    case Tag.MSSG_TYPE_RE:
                                            index = noContent.getPieceIndex();
                                            //System.out.println("Index of File "+index);                                        
                                            seekByteStream(index);
                                            content.setMssg(Tag.MSSG_TYPE_RE);
                                            content.setPieceIndex(index);
                                            content.setByteArr(sendBufferMrkeys);
                                         //  System.out.println("Data sent "+sendBufferMrkeys );
                                            byteBuffer = ins.data2ByteWithData(content);
                                            outStream.write(byteBuffer.array());
                                            outStream.flush();
                                            break;
                                            //downloader terminate connection
                                    case Tag.MSSG_TYPE_TER:
                                            closeConnection();
                                            running = false;
                                            break;
                                    default:
                                            //do nothing
                                            running = false;
                                            break;
                                    }
                        }catch(IOException ex) {
                            System.err.println("Error in Upload." +ex);
                            running = false;
                        }	
                    }

            }
        }
	
	//close connection
	public void closeConnection() {
		try {
                        raf.close();                
			outStream.close();
			inStream.close();
                        socket.close();
                        System.out.println("Closed stream.");
			//System.exit(0);
		}
		catch(IOException ex) {
			System.err.println("Error closing.");
		}
	}
}
