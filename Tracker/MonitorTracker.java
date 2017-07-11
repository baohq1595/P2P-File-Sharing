


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tracker;

import Common.FileInfo;
import Common.General;
import Common.HashTable;
import Common.PeerInfo;
import Common.Tag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Mrkeys
 */
public class MonitorTracker {
       	private static String pathFileList = "fileList.txt";
        private static ArrayList<HashTable> hashTableList;
        private static ArrayList<FileInfo> fileList;
        private static DisplaySeed displaySeed;
        private static General Ins = new General();
        private static File f;
        public static void main(String[] arg){    
        Socket theConnection ;        
        hashTableList = new ArrayList<HashTable>();
        fileList = new ArrayList<FileInfo>();
        displaySeed = new DisplaySeed();
        displaySeed.start();
	
            try{
                ServerSocket theServer = new ServerSocket(Tag.PORT_SERVER);
                ExecutorService threadThracker = Executors.newCachedThreadPool();
                while (true){
            		//System.out.println("Ready print peer list:");
                    System.out.println("Waiting someone connect to me "+Tag.PORT_SERVER);

                    //////////////////////////////////////////
                    for (HashTable hashtb : hashTableList) {
                        ArrayList<PeerInfo> peerL = hashtb.getPeerList();
                        for (int i = 0; i < peerL.size(); i++) {
                            PeerInfo peer1 = peerL.get(i);
                            for (int j = i+1; j < peerL.size(); j++) {
                                if(peer1.compare(peerL.get(j))) {
                                    peerL.remove(j);
                                }
                            }
                        }
                    }

                    //////////////////////////////////////////

                    theConnection = theServer.accept();
                    Tracker tracker = new Tracker(theConnection, hashTableList, fileList);
                    BufferClass bufferTracker = new BufferClass(theConnection, hashTableList, fileList);
                    threadThracker.execute(tracker);
                    System.out.println("Tracker had been connected");
                            // tracker.join();
                }                  
            }
            catch(IOException ex){
                System.err.println("Err "+ex);
            }
             f = new File(Tag.HashFile);
             f.delete();
             f= new File(Tag.FileList);
             f.delete();
        }
}
