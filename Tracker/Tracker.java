package Tracker;

import Common.FileInfo;
import Common.HashTable;
import Common.PeerInfo;
import Common.Sleep;
import Common.Tag;
import Common.General;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;


public class Tracker extends Thread{
	boolean running = true;
	private int port;
	private static ArrayList<HashTable> hashTableList = null;
	private static ArrayList<FileInfo> fileList=  null;
	private Socket socket;
	private DataInputStream inStream;
	private DataOutputStream outStream;
	private static String pathFileList = "";
        private static PeerInfo newPeer ;
        private String peerIP ;
        private String peerID;
        private int peerAlive;
        private static Timer tasknew;
        private static General Ins;  
        private static String keySearch;
	//private General ins;
	//private String peerListFile = "peerlist.txt";
	
	//Tracker(Socket theConnection, ArrayList<HashTable> hashTableList, ArrayList<FileInfo> fileList) {
	public Tracker(Socket socket, ArrayList<HashTable> hashTB, ArrayList<FileInfo> fileList) {
		this.socket = socket;
		hashTableList = hashTB;
		this.fileList = fileList;
		//ins = new General();
                this.port = this.socket.getPort();
                this.peerIP = socket.getInetAddress().getHostAddress();   
                Ins = new General();
                
        }
//         		tasknew = new Timer();
//           		tasknew.scheduleAtFixedRate(new remind(), 5*1000, 5*1000);//5*60s
//	}
//	
//	class remind extends TimerTask {
//		   public void run() {
//			   ArrayList<Integer> erasePeer = new ArrayList<Integer> ();
//			   ArrayList<Integer> eraseHB = new ArrayList<Integer> ();
//			   System.out.println("Running into timer task for tracker." +port);
//			   HashTable tmpHash;
//			   for (int index = 0; index < hashTableList.size(); index++) {
//				   tmpHash = hashTableList.get(index);
//				   ArrayList<PeerInfo> tmpPeer = tmpHash.getPeerList();
//				   PeerInfo peer;
//				   for (int i = 0; i < tmpPeer.size(); i++) {
//					   peer = tmpPeer.get(i);
//					   if (peer.getAlive() == 0) {
//						   erasePeer.add(i);
//					   }
//					   else {
//						   peer.decAlive();
//					   }
//				   }
//				   for (int i = 0; i < erasePeer.size(); i++) {
//					   tmpPeer.remove(i);
//				   }
//				   if (tmpHash.getPeerList().isEmpty()) {
//					   eraseHB.add(index);
//				   }
//			   }
//			   for (int i = 0; i < eraseHB.size(); i++) {
//				   hashTableList.remove(i);
//				   fileList.remove(i);
//			   }
//                           if (hashTableList.size() == 0) hashTableList = null;
//                           writeHashTbale2File(hashTableList);
//		   }
//	   }
	
	//add new hash table into hash table list
	private synchronized void addHashTable(String hash, PeerInfo peer) {
		boolean flagH = false;
		boolean flagP = false;
		
		//if hash value is already present in list, add new peer
//                if (hashTableList == null){
//                    hashTableList = new ArrayList<HashTable>();
//                    HashTable hashTB = new HashTable(hash);
//                    hashTB.addPeer(peer);
//                    hashTableList.add(hashTB);
//                }
// 				  else {
                    for (int i = 0; i < hashTableList.size(); i++) {
                            if (hashTableList.get(i).getHashValue().equals(hash)) {
                            	ArrayList<PeerInfo> tmpList = hashTableList.get(i).getPeerList();
                            	for (PeerInfo tmpPeer : tmpList) {
                            		if (tmpPeer.compare(peer)) {
                            			tmpPeer.setAlive(3);
                            			flagP = true;
                            			flagH = true;
                            			break;
                            		}
                            	}
                            	if (flagP == false) {
                            		hashTableList.get(i).addPeer(peer);
                            		flagH = true;
                            	}
                            }
                    }

                    //if hash value does not present in list, create new, then add peer
                    if (flagH == false) {
                            HashTable hashTB = new HashTable(hash);
                            hashTB.addPeer(peer);
                            hashTableList.add(hashTB);
                            System.out.println("A peer has been added in" + hash);
                    }
                //}
		//System.out.println("A peer has been added in" + hash);
	}
	
	//get input and output streams
	private void getConnection() {
		
		try {
			outStream = new DataOutputStream(socket.getOutputStream());
			outStream.flush();
			
			inStream = new DataInputStream(socket.getInputStream());
		}
		catch(IOException ex) {
			System.err.println("Error getting streams.");
		}
	}
	
	//find peerList attaches with hash value
	private HashTable findHash(String hash) {
		
		HashTable hashTable = null;
		for(HashTable hashTB : hashTableList) {
			if (hashTB.getHashValue().equals(hash)) {
				hashTable = hashTB;
				break;
			}
		}
		return hashTable;
	}
	
	public void run() {
		getConnection();
		//communicate with peer - receive PeerInfo - send peerList 
                File f;
		byte[] mssgBuff = new byte[10];
		String mssg;
                Sleep sleeper = new Sleep(100);
                running = true;
                while (running == true ){
                    //TODO HERE CPU HIGHT
                    sleeper.init();
                    try {
                            inStream.read(mssgBuff, 0, mssgBuff.length);
                            mssg = new String(mssgBuff);
                            switch(mssg){
                            //request download file
                            case Tag.PEERLIST_REQ:
                                    mssgBuff = Tag.PEERLIST_READY.getBytes();
                                    outStream.write(mssgBuff, 0, mssgBuff.length);
                                    outStream.flush();
                                    //update HashTbale from file
                                    try{
                                        // System.out.println("isRunDisplaySeed 1111");
                                        FileInputStream fin = new FileInputStream("hashtable");
                                        ObjectInputStream ois = new ObjectInputStream(fin);
                                        hashTableList.clear();
                                        this.hashTableList = (ArrayList<HashTable>) ois.readObject();
                                        // System.out.println("Hash Table "+_hTable.get(1).getHashValue());
                                        ois.close();	
                                        fin.close();
                                    }catch(Exception ex){
                                        // ex.printStackTrace();
                                      } 
                                    sendPeerList();                                    
                                    running = false;
                                    break;

                            //request search file available
                            case Tag.FILELIST_RE:
                                    //update FileList from file
                                    try{
                                        // System.out.println("isRunDisplaySeed 1111");
                                         FileInputStream fin = new FileInputStream("filelist");
                                         ObjectInputStream ois = new ObjectInputStream(fin);
                                         fileList.clear();
                                         fileList = (ArrayList<FileInfo>) ois.readObject();
                                        // System.out.println("Hash Table "+_hTable.get(1).getHashValue());
                                         ois.close();		   
                                         fin.close();
                                   }catch(Exception ex){
                                               // ex.printStackTrace();
                                        } 
                                    if (sendFileList() == 1) running = false;
                                    f= new File ("fileList.txt");
                                    f.delete();
                                    break;
                            case Tag.MSSG_TYPE_TER:
                                    running = false;
                                    closeConnection();
                                    break;
                            case Tag.UPDATE_LISTFILE:
                                    mssgBuff = Tag.READY_UPDATE.getBytes();
                                    outStream.write(mssgBuff, 0, 10);
                                    outStream.flush();
                                    try{
                                        // System.out.println("isRunDisplaySeed 1111");
                                         FileInputStream fin = new FileInputStream("filelist");
                                         ObjectInputStream ois = new ObjectInputStream(fin);
                                         fileList = (ArrayList<FileInfo>) ois.readObject();
                                         fin = new FileInputStream("hashtable");
                                         ois = new ObjectInputStream(fin);                                         
                                         hashTableList = (ArrayList<HashTable>) ois.readObject();
                                        // System.out.println("Hash Table "+_hTable.get(1).getHashValue());
                                         ois.close();		   
                                   }catch(Exception ex){
                                               // ex.printStackTrace();
                                    } 
                                    updateListFile();
                                    //update file HashTable
                                    Ins.writeHashTbale2File(Tracker.hashTableList);
                                    Ins.writeFilelist2File(Tracker.fileList);
                                    break;
                                    
                            case Tag.RECIVE_PEERID:
                                    mssgBuff = Tag.READY_PEERID.getBytes();
                                    outStream.write(mssgBuff, 0,10);
                                    outStream.flush();
                                    byte[] peID = new byte[Tag.PEER_ID_SIZE];
                                    inStream.read(peID);
                                    this.peerID = new String(peID);
                                    break;
                            case Tag.KEEP_ALIVE:
                                keepAlive();
                                break;
                            case Tag.DELETE_PEER:
                                try{
                                        // System.out.println("isRunDisplaySeed 1111");
                                         FileInputStream fin = new FileInputStream("filelist");
                                         ObjectInputStream ois = new ObjectInputStream(fin);
                                         fileList = (ArrayList<FileInfo>) ois.readObject();
                                         fin = new FileInputStream("hashtable");
                                         ois = new ObjectInputStream(fin);                                         
                                         hashTableList = (ArrayList<HashTable>) ois.readObject();
                                        // System.out.println("Hash Table "+_hTable.get(1).getHashValue());
                                         ois.close();		   
                                   }catch(Exception ex){
                                               // ex.printStackTrace();
                                    } 
                                   deletePeer();
                                    //update file HashTable
                                    Ins.writeHashTbale2File(Tracker.hashTableList);
                                    Ins.writeFilelist2File(Tracker.fileList);
                                    break;
                            case Tag.SEACRH_KEY:
                                try{
                                    FileInputStream fin = new FileInputStream("filelist");
                                    ObjectInputStream ois = new ObjectInputStream(fin);
                                    fileList = (ArrayList<FileInfo>) ois.readObject();
                                }
                                catch(Exception e){
                                    //
                                }
                                searchFile();
                                break;
                            case Tag.MSSG_TYPE_RE:
                                running = false;
                                closeConnection();
                                break;
                            }
                    }
                    catch(IOException ex) {
                        //EDIT BY MRKEYS
                        running = false;
                        closeConnection();
                        System.err.println(ex);
                    }
                }
			
	}
	
    private synchronized void updateListFile(){
            byte[] hashBuff = new byte[Tag.HASH_SIZE];
            byte[] mssgBuff = new byte[10];
            byte lengBuff[] = new byte[4];
            byte[] lengFile = new byte[8];
            boolean running;
            running = true;
            FileInfo fileInfo = new FileInfo();
            int leng;
            String mssg;
            String hash;            
            try{
            this.port = inStream.readInt();
            System.out.println("Port nhan dc "+port);
            newPeer = new PeerInfo(this.peerID, this.peerIP,this.port, 3);  
            outStream.write(Tag.PORT_OK.getBytes(), 0,10);
            outStream.flush();
            }
            catch(Exception e){
                
            }
            while ( running == true){
                try{
                    //read Port
                    
                    inStream.read(hashBuff, 0, Tag.HASH_SIZE);
                    for (int i = 0;i<10;i++)
                    mssgBuff[i] = hashBuff[i];
                    mssg = new String (mssgBuff);
                    switch(mssg){
                        case Tag.END_UPDATE:
                            running = false;
                            outStream.write(Tag.END_UPDATE.getBytes(), 0,10);
                            outStream.flush();
                            break;
                        default:
                            hash = new String(hashBuff);
                            addHashTable(hash,newPeer);
                            fileInfo.setHash(hash);
                            outStream.write(Tag.HASH_OK.getBytes(), 0,10);
                            outStream.flush();
                           // break;
                    //receive lengofName file                    
                            inStream.read(lengBuff);
                            leng = ByteBuffer.wrap(lengBuff).getInt();
                            outStream.write(Tag.HASRECEIVE.getBytes(), 0,10);
                            outStream.flush();
                            //receive name file
                            System.out.println("leng "+leng);
                            byte[] nameFile = new byte[leng];                   
                            inStream.read(nameFile);
                            fileInfo.setName(new String(nameFile));
                            System.out.println("Rec namfile " +new String(nameFile));
                            outStream.write(Tag.HASRECEIVE.getBytes(), 0,10);
                            outStream.flush();
                            //receive LengOfFile
                            inStream.read(lengFile);
                            long len = ByteBuffer.wrap(lengFile).getLong();
                            fileInfo.setLength(len);

                            outStream.write(Tag.HASRECEIVE.getBytes(), 0,10);
                            outStream.flush();
                            //running = false;//exit UpdateFileList
                            //Update ListFile
                            if (fileList == null){
                                fileList = new ArrayList<FileInfo>();
                                fileList.add(fileInfo);
                            }
                            else {                        
                            	boolean flag = false;
                                for (FileInfo infox : fileList){
                                    if (infox.getHash().equals(fileInfo.getHash())) {
                                    	flag = true;
                                        //fileList.add(fileInfo);
                                    	break;
                                    }
                                }
                                if (!flag) fileList.add(fileInfo);
                            }
                            for (FileInfo info : fileList) {
                            	System.out.println(info.getName() + "  " + info.getLength());
                            }
                    }
                }
                catch(IOException ex){
                    System.err.println("Err "+ex);
                }
        }         
       // Ins.writeHashTbale2File(hashTableList, "hashtable");
        //Ins.writeFilelist2File(fileList, "filelist");
    }
	
    //if peer require file list to choose
	private int sendFileList() {	
            pathFileList = "fileList.txt";
            byte[] lengBuff = new byte[4];
            lengBuff = ByteBuffer.wrap(lengBuff).putInt(0).array();
            if (fileList == null) {
                try{
                    System.out.println("Null listFile ");
                    System.out.println("Send leng file List 0");
                    outStream.write(lengBuff, 0, lengBuff.length);
                    outStream.flush();
                    byte[] mssgBuffer = new byte[10];	//read 10 bytes message
                    inStream.read(mssgBuffer, 0, mssgBuffer.length);    
                    return 1;
                }
                catch(Exception ex){
                    System.err.println(ex);
                }      
            }
		//write file list to file
 		Formatter writer = null;                
		try {
			writer = new Formatter(pathFileList); 
		}
		catch(FileNotFoundException ex) {
			System.err.println("Error wrting file.");
		}
		for (FileInfo fileInfo : fileList) {
			writer.format("%s %d %s\n", fileInfo.getName(), fileInfo.getLength(), fileInfo.getHash());
                       // System.out.println(fileInfo.getName()+" "+ fileInfo.getLength()+"  "+ fileInfo.getHash());
		}
		writer.close();
		File file = new File(pathFileList);                
		int leng = (int)file.length();
		lengBuff = ByteBuffer.wrap(lengBuff).putInt(leng).array();
		try {
			//send to length file to peer
                        System.out.println("Send leng file List "+leng);
			outStream.write(lengBuff, 0, lengBuff.length);
			outStream.flush();
			
			//read ACK from peer
			byte[] mssgBuffer = new byte[10];	//read 10 bytes message
			inStream.read(mssgBuffer, 0, mssgBuffer.length);
			
			//convert to string value
			String ackFleng = new String(mssgBuffer);
			if (ackFleng.equals(Tag.ACK_FP_LENG)) {
				//send file list to peer
				byte[] fileBuff = new byte[(int) leng]; //create buffer with file length length
				FileInputStream fis = new FileInputStream(file);
				
				//read file bytes into buffer
				fis.read(fileBuff);
				fis.close();
				//send file buffer to peer
                                System.out.println("Send File ");
				outStream.write(fileBuff, 0, fileBuff.length);
				outStream.flush();
                                inStream.read(mssgBuffer); //read MSSG_TYPE_TER
                                return 1;
                                
			}else return 1;
		}
		catch(IOException ex) {
			System.err.println(ex);
		}
                return 1;
	}
	
	//if peer require peer list for downloading
	private void sendPeerList() {
		try {
			
			byte[] hashNinfo = new byte[Tag.HASH_SIZE]; //50byte
			int byteRead = 0;
			//get hash and  peerInfo data
			byteRead = inStream.read(hashNinfo, 0, hashNinfo.length);			
			System.out.println("Get hash and peer info:" + "\n" + "number of bytes read: " + byteRead);			
			//convert to hash value and peerInfo (peerID and port)
			byte[] hashBuff = new byte[Tag.HASH_SIZE];
			ByteBuffer buffer = ByteBuffer.wrap(hashNinfo);
                        //Ship 40 byte to hashBuff
			buffer.get(hashBuff, 0, Tag.HASH_SIZE);		
			String hashRec = new String(hashBuff);			
			System.out.println("hash value received: " + hashRec);			
			//create new peerInfo to add into torrent			
			//find hash value in database (HashTable List)
                        HashTable tmpHashTB = findHash(hashRec);			
			byte[] mssgBuffer = new byte[Tag.MSSG_SIZE - 4];
			//if hash value doesn't match in database, terminate connection
                        byte[] lengBuff = new byte[4];
			if (tmpHashTB == null) {				
				lengBuff = ByteBuffer.wrap(lengBuff).putInt(0).array();
				outStream.write(lengBuff, 0, lengBuff.length);
				outStream.flush();
				closeConnection();
			}
			
			//if hash value match, send peer list to peer
			else {
				String peerFile = hashRec +".txt";
				tmpHashTB.peerList2File();			//write peerInfo to hash.txt
				File file = new File(peerFile);
				
				//send file length to peer					//length file is long type
                                //send lengh hash.txt file to peer
				lengBuff = ByteBuffer.wrap(lengBuff).putInt((int)file.length()).array();
				outStream.write(lengBuff, 0, lengBuff.length);
				outStream.flush();
				
				//wait peer to ack file length
				inStream.read(mssgBuffer, 0, mssgBuffer.length);
				String mssg = new String(mssgBuffer);
				System.out.println("Meesage receive: " + mssg);	
				//send file to peer				
				byte[] fileBuff = new byte[(int) file.length()];
				FileInputStream fis = new FileInputStream(file);				
				int numBytes = fis.read(fileBuff);
				System.out.println("Number of bytes read in " + hashRec + ".txt: "
						+ numBytes);	
				
				//write to output stream to send
				outStream.write(fileBuff, 0, fileBuff.length);
				outStream.flush();
				fis.close();				
				//add new peer into torrent
				//addHashTable(hashRec, newPeer);
                                file.delete();
			}
		}
		catch(IOException ex) {
			System.out.println("Error reading/writing data.");
		}
	}
	
	private void closeConnection() {
//		System.out.println("Ready print peer list:");
//		for (HashTable hashtb : hashTableList) {
//			hashtb.printHash();
//		}
		try {
			socket.close();
			outStream.close();
			inStream.close();
			//System.exit(0);
			//System.exit(1);
		}
		catch(IOException ex) {
			System.err.println("Error closing streams.");
		}
	}

    private synchronized void keepAlive() {
        byte[] mssgBuff = new byte[10];
        String ID;
       // System.out.println("before "+hashTableList.size());
        try{    
            mssgBuff = Tag.KEEP_ALIVE.getBytes();
            outStream.write(mssgBuff, 0,10);
            byte[] UID = new byte[Tag.PEER_ID_SIZE];
            inStream.read(UID); 
            ID = new String(UID);
            System.out.println("--> " +ID);
            FileInputStream fin = new FileInputStream("hashtable");
            ObjectInputStream ois = new ObjectInputStream(fin);       
            hashTableList = (ArrayList<HashTable>) ois.readObject();        
            outStream.flush();
            //System.out.println("UID recvice" +new String(UID));
            boolean exist;
            exist = false;
            for (HashTable hTable: hashTableList){
                if (hTable == null) {
                    break;
                }
                for (PeerInfo pInfo : hTable.getPeerList()){
                    if (pInfo == null) break;
                    if (pInfo.getPeerID().equals(ID)){
                      pInfo.setAlive(3);
                      exist = true;
                    }
                }
            }
            if(exist){
                outStream.write(Tag.KEEPSUCCES.getBytes());
                outStream.flush();
            }
            else {
                outStream.write(Tag.NOTEXIST.getBytes());
                outStream.flush();
            }
        }catch(Exception ex){
             ex.printStackTrace();
        } 
        //System.out.println("after "+hashTableList.size());
        Ins.writeHashTbale2File(hashTableList);

    }

    private void deletePeer() {
       //reciver UID of Peer will delete
       try{
           System.out.println("Into Delete mode");
            byte[] bUID = new byte[Tag.PEER_ID_SIZE];
            byte[] bHash = new byte[Tag.HASH_SIZE];
            inStream.read(bUID);
            String UID = new String(bUID,0,Tag.PEER_ID_SIZE);
            inStream.read(bHash);
            String hash = new String(bHash,0,Tag.HASH_SIZE);
            for(HashTable hTable : hashTableList){
                if (hash.equals(hTable.getHashValue())){
                    for (PeerInfo pInfo : hTable.getPeerList()){
                        if (pInfo.getPeerID().equals(UID)){
                            for (FileInfo fInfo : fileList){
                                if (fInfo.getHash().equals(hash)){
                                    fileList.remove(fInfo);
                                    break;
                                }
                            }
                            hashTableList.remove(hTable);
                            break;
                        }
                    }
                    break;
                }
            }
            System.out.println("Delete Complete");            
       }
       catch(Exception e){
           System.err.println("err " +e);
           
           
       }
    }

    private void searchFile() {
        keySearch = "";
         pathFileList = "fileList.txt";
        try{
            keySearch = inStream.readUTF();
            byte[] lengBuff = new byte[4];
            lengBuff = ByteBuffer.wrap(lengBuff).putInt(0).array();
            if (fileList == null) {
                try{
                    System.out.println("Null listFile ");
                    System.out.println("Send leng file List 0");
                    outStream.write(lengBuff, 0, lengBuff.length);
                    outStream.flush();
                }
                catch(Exception ex){
                    System.err.println(ex);
                }      
            }
            else{
		//write file list to file
 		Formatter writer = null;                
		try {
			writer = new Formatter(pathFileList); 
		}
		catch(FileNotFoundException ex) {
			System.err.println("Error wrting file.");
		}
		for (FileInfo fileInfo : fileList) {
                    if (fileInfo.getName().toLowerCase().contains(keySearch.toLowerCase())){
			writer.format("%s %d %s\n", fileInfo.getName(), fileInfo.getLength(), fileInfo.getHash());
                    }
                     
		}
		writer.close();
                //Send file
                File file = new File(pathFileList);                
		int leng = (int)file.length();
		lengBuff = ByteBuffer.wrap(lengBuff).putInt(leng).array();		
		//send to length file to peer                
                System.out.println("Send leng file List "+leng);
                outStream.write(lengBuff, 0, lengBuff.length);
                outStream.flush();
                if (leng >0){
                    //send file list to peer
                    byte[] fileBuff = new byte[(int) leng]; //create buffer with file length length
                    FileInputStream fis = new FileInputStream(file);		
                    fis.read(fileBuff);
                    fis.close();				
                    System.out.println("Send File ");
                    outStream.write(fileBuff, 0, fileBuff.length);
                    outStream.flush();
                    file.delete();
                }
            }
        }
        catch(Exception ex){
            System.err.println("Err while search file "+ex);
        }
    }
}
