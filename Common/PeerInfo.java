package Common;

import java.io.Serializable;

public class PeerInfo implements Serializable
{
	
	private String peerID;
	private String ipAddr;
	private int port;
	private int alive;

	public PeerInfo()
	{
		;
	}
	
	public PeerInfo(String id, String ip, int port, int alive)
	{
		peerID = id;
		ipAddr = ip;
		this.alive = alive;
		this.port = port;
	}

	//set method
	public void setAlive(int alive)
	{
		this.alive = alive;
	}
	
	public void setPeerID(String id)
	{
		peerID = id;
	}

	public void setIpAddr(String ip)
	{
		ipAddr = ip;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	//get methods
	public int getAlive()
	{
		return alive;
	}
	
	public String getPeerID()
	{
		return peerID;
	}

	public String getIpAddr()
	{
		return ipAddr;
	}

	public int getPort()
	{
		return port;
	}
	
	public void decAlive()
	{
		alive = alive - 1;
	}
	
	public boolean compare(PeerInfo peer)
	{
		if (peerID == peer.getPeerID() && ipAddr == peer.getIpAddr() && port == peer.getPort() && alive == peer.getAlive())
		{
			return true;
		}
		else
			return false;
	}
}
