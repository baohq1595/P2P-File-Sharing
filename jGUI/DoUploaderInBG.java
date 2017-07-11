package jGUI;

import javax.swing.SwingWorker;
import Peer.UpManager;

public class DoUploaderInBG extends SwingWorker< Long, Object > 
{
	private UpManager upManager;
	
	public DoUploaderInBG(UpManager upManager)
	{
		this.upManager = upManager;
	}
	
	public Long doInBackground()
	{
		upManager.run();
		return (long)1;
	}
}
