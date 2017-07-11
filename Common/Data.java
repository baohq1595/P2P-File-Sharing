package Common;

public class Data {

	private String mssgType;
	private int pieceIndex;
	private byte[] byteArr;

	public Data(String mssg, int id)
	{
		mssgType = mssg;
		pieceIndex = id;
	}

	public Data(String mssg, int id, byte[] byteArr)
	{
		mssgType = mssg;
		pieceIndex = id;
		this.byteArr = byteArr;
	}

//method set
	public void setMssg(String mssg)
	{
		mssgType = mssg;
	}

	public void setPieceIndex(int id)
	{
		pieceIndex = id;
	}

	public void setByteArr(byte[] send)
	{
		byteArr = send;
	}

//method get
	public String getMssg()
	{
		return mssgType;
	}

	public int getPieceIndex()
	{
		return pieceIndex;
	}

	public byte[] getByteArr()
	{
		return byteArr;
	}
}
