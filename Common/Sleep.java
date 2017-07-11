package Common;

public class Sleep 
{
	private int timer;
	
	public  Sleep(int time)
	{
		timer = time;
	}
	
	public  void init()
	{
		try
		{
			Thread.sleep(timer);
		}
		catch(InterruptedException e)
		{
			System.out.println(e);
		}
	}
	
	public void init_nano()
	{
		try
		{
			Thread.sleep(0, timer);
		}
		catch(InterruptedException e)
		{
			System.out.println(e);
		}
	}
}
