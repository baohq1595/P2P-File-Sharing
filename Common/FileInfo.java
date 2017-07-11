package Common;

import java.io.Serializable;

public class FileInfo implements Serializable
{ 
	private String name;
	private long length;
	private String hash;
	public String status;
	
	public FileInfo() 
	{
		
	}
	
	public FileInfo(String name, long length, String hash)
	{
		this.name = name;
		this.length = length;
		this.hash = hash;
		this.status = "";
	}
	
	//get methods
	public String getName()
	{
		return name;
	}
	
	public long getLength()
	{
		return length;
	}
	
	public String getHash()
	{
		return hash;
	}
	
	//set methods
	public void setName(String n)
	{
		name = n;
	}
	
	public void setLength(long l)
	{
		length = l;
	}

	public void setHash(String h)
	{
		hash = h;
	}
	
	public String getLengDisplay()
	{
		double result;
		result = Math.round(length*100.0)/100.0;
		if (result < 1024)
			return String.valueOf(result)+" B";
		
		result = Math.round((result/1024)*100.0)/100.0;
		if (result < 1024)
			return String.valueOf(result)+" KB";
		
		result = Math.round((result/1024)*100.0)/100.0;
		if (result < 1024)
			return String.valueOf(result)+" MB";
		
		result = Math.round((result/1024)*100.0)/100.0;
		if (result < 1024)
			return String.valueOf(result)+" GB";
		
		result = Math.round((result/1024)*100.0)/100.0;
		if (result < 1024)
			return String.valueOf(result)+" TB";
		
		return "###";
	}
}
