package Tracker;

import Common.FileInfo;
import Common.General;
import Common.HashTable;
import Common.PeerInfo;
import Common.Sleep;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Timer;
import java.util.TimerTask;

public class DisplaySeed extends Thread
{
	private static Timer tasknew ;
	private static SeedOnline  _moniterSeed;
	private static ArrayList<HashTable> _hashTableList = null;
	private static ArrayList<FileInfo> _fileList = null;
	private static General Ins;
	
	public DisplaySeed()
	{
		Ins = new General();
		_moniterSeed = new SeedOnline();
		tasknew = new Timer();
		tasknew.scheduleAtFixedRate(new remind(), 20*1000, 20*1000);
	}
	
	public void run()
	{
		Sleep sleeper = new Sleep(1000);
		_moniterSeed = new SeedOnline();
		_moniterSeed.Interfaces();
		while (true)
		{
			sleeper.init();
		}
	}
	
	class remind extends TimerTask
	{
		public void run()
		{
			ArrayList<PeerInfo> tmpPeer;
			
			//Update hashTable from file
			try
			{
				FileInputStream fin = new FileInputStream("hashtable");
				ObjectInputStream ois = new ObjectInputStream(fin);
				_hashTableList = (ArrayList<HashTable>) ois.readObject();
				fin = new FileInputStream("filelist");
				ois = new ObjectInputStream(fin);
				_fileList = (ArrayList<FileInfo>) ois.readObject();
				ois.close();
			}
			catch(Exception ex)
			{
				
			}
			
			HashTable tmpHash;
			int num;
			num = 0;
			if(_hashTableList == null)
			{
				
			}
			else
			{
				for(int index = 0; index < _hashTableList.size(); index++)
				{
					tmpHash = _hashTableList.get(index);
					tmpPeer = tmpHash.getPeerList();
					for (int i = 0; i < tmpPeer.size(); i++)
					{
						tmpPeer.get(i).decAlive();
					}
				}
				num = 0;
				int run;
				run = 0;
				
				while (run == 0)
				{
					run =1;
					for (HashTable hTable : _hashTableList )
					{
						run = 1;
						num = 0;   
						while(num == 0)
						{
							num =1;
							for(PeerInfo pInfo: hTable.getPeerList())
							{
								if (pInfo == null)
								{
									num++;
									break;
								}
								
								if (pInfo.getAlive() == 0)
								{
									hTable.getPeerList().remove(pInfo);
									num = 0;
									break;
								}
							}
						}
						
						if (hTable.getPeerList().isEmpty())
						{
							for (FileInfo fInfo:_fileList)
							{
								if (fInfo.getHash().equals(hTable.getHashValue()))
								{
									_fileList.remove(fInfo);
									break;
								}
							}
							_hashTableList.remove(hTable);  
							run = 0;
							break;
						}
						
						if (_hashTableList.size() == 0)
						{
							_hashTableList = null;
							_fileList = null;      
							run++;
							break;
						}
					}    
				}
				Ins.writeHashTbale2File(_hashTableList);
				Ins.writeFilelist2File(_fileList);
			}
		}
	}
}
