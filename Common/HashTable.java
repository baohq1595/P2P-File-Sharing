package Common;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formatter;
import java.io.Serializable;

public class HashTable implements Serializable
{

	private String hashValue;
	public ArrayList<PeerInfo> peerList;
	
	public HashTable(String hash)
	{
		hashValue = hash;
		peerList = new ArrayList<PeerInfo> ();
	}

	public void addPeer(PeerInfo peer)
	{
		peerList.add(peer);
	}

	public void removePeer(String peerID)
	{

		int index = 0;
		for (index = 0; index < peerList.size(); index++)
		{
			if (peerList.get(index).getPeerID() == peerID)
			{
				//hashList.remove(index);
				peerList.remove(index);
				break;
			}

		}
	}

	public ArrayList<PeerInfo> getPeerList()
	{
		return peerList;
	}

	public String getHashValue()
	{
		return hashValue;
	}
	
	public void peerList2File()
	{
		
		Formatter writer = null;
		try
		{
			writer = new Formatter(hashValue + ".txt");
		}
		catch(FileNotFoundException ex) {
			ex.printStackTrace();
		}

		for(PeerInfo peer : peerList)
		{
			writer.format("%s %s %s\n", peer.getPeerID(), peer.getIpAddr(), peer.getPort());
		}
		writer.close();
	}
	
	public void printHash()
	{
		System.out.println(hashValue);
		for(PeerInfo peer : peerList)
		{
			System.out.printf("%s: %s\n%s: %s\n%s: %s\n", "peerID", peer.getPeerID(), "peer IpAddr", peer.getIpAddr(), "peer port", peer.getPort());
		}
	}
}
