package Common;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.security.*;

public class General
{

	public General()
	{
	
	}

	//function split file into pieces with specified length
	public void SplitFile(File inputFile, int partSize) {
		
		String fileName = inputFile.getName();
		//File inputFile = new File(fileName);
		FileInputStream inputStream;
		String newFileName;
		FileOutputStream filePart;
		int fileSize = (int) inputFile.length();
		int nChunks = 0, read = 0, readLength = partSize;
		byte[] byteChunkPart;
		try {
			inputStream = new FileInputStream(inputFile);
			while (fileSize > 0)
			{
				if (fileSize <= 5)
				{
					readLength = fileSize;
				}
				byteChunkPart = new byte[readLength];
				read = inputStream.read(byteChunkPart, 0, readLength);
				fileSize -= read;
				assert (read == byteChunkPart.length);
				nChunks++;
				newFileName = fileName + ".part" + Integer.toString(nChunks - 1);
				filePart = new FileOutputStream(new File(newFileName));
				filePart.write(byteChunkPart);
				filePart.flush();
				filePart.close();
				byteChunkPart = null;
				filePart = null;
			}
			inputStream.close();
		} catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}

	//function calculate SHA1 value
	public String toSHAValue(File file)
	{
		//File file = new File (fileName);
		
		Formatter formatter = new Formatter();
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			final byte[] buffer = new byte[1024];
			for (int read = 0; (read = is.read(buffer)) != -1;)
			{
				messageDigest.update(buffer, 0, read);
			}
			
			for (final byte b : messageDigest.digest())
			{
				formatter.format("%02x", b);
			}
			is.close();
			
		}
		catch (NoSuchAlgorithmException ex)
		{
			System.err.println(ex.toString());
		}
		catch (IOException ex)
		{
			System.err.println(ex.toString());
		}
		return formatter.toString();
	}

	//convert Data to byte stream sent from uploader (with data)
	public ByteBuffer data2ByteWithData(Data data)
	{
		ByteBuffer buffer = ByteBuffer.allocate(Tag.BUFFER_SIZE + 14);
		buffer.put(data.getMssg().getBytes());
		buffer.putInt(data.getPieceIndex());
		buffer.put(data.getByteArr());

		return buffer;
	}
	//convert Data to byte stream sent from client (no data)
	public ByteBuffer data2ByteWithoutData(Data data)
	{

		ByteBuffer buffer = ByteBuffer.allocate(14);	//message type cost 10byte
		buffer.put(data.getMssg().getBytes());
		buffer.putInt(data.getPieceIndex());
		
		return buffer;
}

	//convert byte stream to Data object with data
	public Data serializeByteArrayWithData(ByteBuffer buffer)
	{

		Data data = new Data("", 0);
		byte[] byteS = new byte[10];	//byte array hold message
		for (int i = 0; i < 10; i++)
		{
			byteS[i] = buffer.get(i);
		}

		String mssg = new String(byteS);	//message
		buffer.position(10);
		int value = buffer.getInt();		//piece index
		
		byte[] dataBuff = new byte[Tag.BUFFER_SIZE];
		buffer.get(dataBuff, 0, Tag.BUFFER_SIZE);
		
		//wrap into Data object
		data.setMssg(mssg);
		data.setPieceIndex(value);
		data.setByteArr(dataBuff);
		
		return data;
	}

	//convert byte stream to Data object without data
	public Data serializeByteArrayWithoutData(ByteBuffer buffer)
	{
		Data data = new Data("", 0);
		byte[] byteS = new byte[10];        //byte array hold message
		for (int i = 0; i < 10; i++)
		{
			byteS[i] = buffer.get(i);
		}

		String mssg = new String(byteS);    //message
		buffer.position(10);
		int value = buffer.getInt();		//piece index
		
		//wrap into Data object
		data.setMssg(mssg);
		data.setPieceIndex(value);
		
		return data;

	}
	
	public void readFromPeerInfofile(String pathPeerList,ArrayList<PeerInfo> peerList) {
		PeerInfo peerInfo = new PeerInfo();
		Scanner reader = null;
		File fi = new File(pathPeerList);
		try
		{
			reader = new Scanner(fi);
		}
		catch(IOException e)
		{
			System.err.println("Err "+e);
		}

		while(reader.hasNext())
		{
			try 
			{
				peerInfo = new PeerInfo(reader.next(), reader.next(), reader.nextInt(), 0);
    			peerList.add(peerInfo);
    		}
    		catch(NoSuchElementException ex)
			{
    			System.err.println(ex);
			}
		}
		reader.close();
	}

	public void readFromFileInfofile(String pathFileList,ArrayList<FileInfo> fileList)
	{

		//ArrayList<FileInfo> fileList = null;
		FileInfo fileInfo = new FileInfo();
		File fi = new File(pathFileList);
		Scanner reader = null;
		try
		{
			reader = new Scanner(fi);
		}
		catch(FileNotFoundException e)
		{
			;
		}

		while(reader.hasNext())
		{
			try
			{
				fileInfo = new FileInfo(reader.next(), reader.nextLong(), reader.next());
				
				if (fileList == null)
				{
					fileList = new ArrayList<FileInfo>();
				}
				
				fileList.add(fileInfo);
		}	
			catch(NoSuchElementException ex)
			{
				System.err.println(ex);
			}	
		}
		reader.close();
	}
	public synchronized void writeHashTbale2File(ArrayList<HashTable> hashTableList)
	{
		try
		{
			File f = new File(Tag.HashFile);
			f.delete();
			FileOutputStream fout = new FileOutputStream(Tag.HashFile);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(hashTableList);
			oos.close();
		}
		catch(Exception  ex)
		{
			ex.printStackTrace();
		}
	}
	
	public synchronized void writeFilelist2File(ArrayList<FileInfo> fileList)
	{
		try
		{
			File f = new File(Tag.FileList);
			f.delete();
			FileOutputStream fout = new FileOutputStream(Tag.FileList);
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(fileList);
			oos.close();
		}
		catch(Exception  ex)
		{
			ex.printStackTrace();
		}
	}
}
