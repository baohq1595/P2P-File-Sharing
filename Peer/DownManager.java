package Peer;

import Common.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.lang.String;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class DownManager extends Thread
{
	private String hash;
	private String fileName;
	private ArrayList<PeerInfo> peerList;
	private long fileLength;
	private DownloadList  _list;
	private ArrayList<DownloadList> l;      
	private int timeWait;
	private long pieceNum ;
	private DefaultTableModel model;
	private FileInfo _fileDown;
	private int row ;
	private String status;    
	private ArrayList<Thread> downloader;
	private Downloader downloader_x ;
	private String folderDownload;
	
	public DownManager(FileInfo _fileDownIn,FileInfo fileIn,String folderDownload,ArrayList<PeerInfo> peerList, DefaultTableModel model,int row) throws IOException
	{
		this._fileDown = _fileDownIn;
		this.model = model;                
		this.hash = fileIn.getHash();
		this.row = 0;
		this.fileName = fileIn.getName();
		this.peerList = peerList;
		this.fileLength = fileIn.getLength();
		this.status = "";
		_list = new DownloadList();
		l = new ArrayList<DownloadList>();                
		pieceNum = fileLength / Tag.BUFFER_SIZE;
		this.row = row;   
		downloader = new ArrayList<Thread>(); 
		this.folderDownload = folderDownload;
	}
	
	public void run()
	{
		long num;
		num = 0;
		int dem;
		dem =0;
		_list.list.clear();
		l.clear();
		//Thread t =
		ExecutorService threadDownloader = Executors.newCachedThreadPool();
		//ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
		for (int i = 0;i<=(pieceNum);i++)
		{
			_list.list.add(i);
			num++;
			if (num == ((pieceNum)/(peerList.size())+1) || num == pieceNum)
			{
				l.add(_list);
				_list = new DownloadList();
				num = 0;
			} 
		}   
		if (num != 0)
			l.add(_list);
		dem = -1;
		try
		{
			for(PeerInfo peer : peerList)
			{
				dem++;
				System.out.println(peer.getPeerID() + "-----" + peer.getIpAddr() + "-----" + peer.getPort());
				//Do something here
				downloader_x = new Downloader(peer,fileName,folderDownload, hash, l, fileLength,dem,threadDownloader);                       
				//threadDownloader.execute(downloader_x);  
				if (downloader_x.getConnection() == true)
				{
					Thread t = new Thread(downloader_x);
					downloader.add(t);
					t.start();
					dem--;
				}
			}
			System.out.println("_________________________________");
			//threadDownloader.execute(downloader);
		}
		catch(Exception e)
		{
			System.err.println("Err while down 1"+e);
		}
		
		System.out.println(threadDownloader.isTerminated());
		try
		{
			for(int i =0;i<downloader.size();i++)
			{
				downloader.get(i).join();
			}
		}
		catch(Exception ex)
		{
			System.err.println("Error wait thread "+ex );
		}
		
		System.out.println("Thread doi xong");
		boolean flag ;

		flag = true;  
		for (int i =0;i<l.size();i++)
		{
			if (!l.get(i).list.isEmpty())
			{
				flag = false;
				break;
			}
		}
		try
		{
			if (flag)
			{
				status = "Success";
				this._fileDown.status = status;
				model.setValueAt(status,row,3);
			}
			else
			{
				if (Xulingoaile(dem))
				{
					status = "Success"; 
					this._fileDown.status = status;
					model.setValueAt(status,row,3);                                
				}
				else
				{
					status = "Fail";
					this._fileDown.status = status;
					model.setValueAt(status,row,3);  
					//File fi = new File(
				}
			}
		}
		catch(InterruptedException ex)
		{
			System.err.println("Eror while wait thread down "+ex);
		}
        }

    private boolean Xulingoaile(int dem) throws InterruptedException
	{
		System.out.println("p2p.DownManager.Xulingoaile()");
		ArrayList<PeerInfo> peerEx = new ArrayList<PeerInfo>();
		ArrayList<DownloadList> lEx = new ArrayList<DownloadList>();
		ExecutorService threadDownloader = Executors.newCachedThreadPool();
		boolean taskEnded ;
		int x;
		do
		{
			peerEx.clear();                           
			//Get piece of File download fail
			lEx.clear();
			for (int i =0;i<l.size();i++)
			{
				if (!l.get(i).list.isEmpty())
				{
					lEx.add(l.get(i));
				}
				else
				{
					peerEx.add(peerList.get(i));
				}
			}
			if (lEx.isEmpty())
			{
				return true;
			}
			
			if (peerEx.isEmpty())
				return false;
			
			peerList.clear();
			peerList.addAll(peerEx);
			l.clear();
			l.addAll(lEx);
			x = 0;
			// timeWait = ((int)pieceNum/peerEx.size())/5;
			downloader.clear();
			
			for (int i =0;i<lEx.size();i++)
			{
				downloader_x = new Downloader(peerEx.get(x),fileName, folderDownload,hash, lEx, fileLength,i,threadDownloader);
				if (downloader_x.getConnection() == true)
				{
					Thread t  =new Thread(downloader_x);
					downloader.add(t);
					t.start();
				};
			}
			
			try
			{
				for (int i =0;i<downloader.size();i++)
				{
					downloader.get(i).join();
				}
			}
			catch(Exception ex)
			{
				System.err.println("Error wait thread "+ex );
			}
		}while (true);

	}
}
