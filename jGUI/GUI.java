package jGUI;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import Common.*;
import Peer.DownManager;
import Peer.Downloader;
import Peer.Uploader;
import Peer.KeepConnection;
import Peer.UpManager;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.table.*;


public class GUI
{
	private static int mode;
	/*
	Mode : show position user in
	2: Up Top and Up Left
	1: Down Left
	0: Search Left and Right
	*/
	private static String folderDownload;
	private static ArrayList<UpManager> ksoatUp;
	private static PeerInfo peer;
	private static ArrayList<PeerInfo> peerList;
	private static ArrayList<FileInfo> listDone;
	private static ArrayList<FileInfo> fileListUp;
	private static ArrayList<FileInfo> fileListDown;
	private static ArrayList<String> statusDown;
	private final String defaultPath = "C:\\Users\\BAOBAO\\Desktop\\test\\";
	private int fileCountList = 0;
	private int peerCount = 0;
	private int fileCountUp = 0;
	private static General Ins;
	private static KeepConnection keepIns;
	private static UpManager upManager;
	private static DownManager downManager;
	private static ExecutorService threadUploader;
	private static ExecutorService threadDownloader;	
	private static File fileChosen;
	private static Random idRandom;
	private static int peerIDRandom;
	private static int no;
	private static String size;
	private static String fileDisplay;
	private static String status;
	private static String name;
	private static String IPSERVER; 
	private static String hash;
	private JFrame frmBstapp;
	private JTable tbsearch;
	private boolean bol;
	private boolean add;
	private static String messenger;
	private static String Title;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				messenger = "Enter IP Server";
				int messengerType = 1;
				Title = "IP Server Request";
				int num = 3;
				do
				{
					try
					{
						JButton open = new JButton(); 
						Frame frame = new Frame();
						IPSERVER = JOptionPane.showInputDialog(frame,(String)messenger,Title,messengerType);
						if (IPSERVER != null && IPSERVER.length() > 0)
						{ 
							GUI window = new GUI();
							window.frmBstapp.setVisible(true);
							//initialize some data
							Ins = new General();
							idRandom = new Random();
							threadUploader = Executors.newCachedThreadPool();
							threadDownloader = Executors.newCachedThreadPool();
							keepIns = new KeepConnection();
							//fileList = new ArrayList<FileInfo> ();
							fileListUp = new ArrayList<FileInfo> ();
							listDone = new ArrayList<FileInfo>();
							fileListDown = new ArrayList<FileInfo> ();
							peerList = new ArrayList<PeerInfo> ();
							statusDown = new ArrayList<String>();
							ksoatUp = new ArrayList<UpManager>();
							peer = new PeerInfo();
						}
						else
						{
							if (IPSERVER == null || num <=0) System.exit(0);
							messenger = "YOU MUST ENTER IP SERVER";
							messengerType =2;
							Title = "IP Server Request ("+String.valueOf(num)+ ")";
							num--;
						}
					
				}
					catch (Exception e)
					{
						e.printStackTrace();
						System.exit(1);
					}
				}
				while (messengerType != 1);
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frmBstapp = new JFrame();
		frmBstapp.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent arg0)
			{
				JDialog.setDefaultLookAndFeelDecorated(true);
				int response = JOptionPane.showConfirmDialog(null, "Do you want to exit?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.YES_OPTION)
				{
					frmBstapp.dispose();
					threadUploader.shutdown();
					threadDownloader.shutdown();
					System.exit(0);
				}
			}
		});
		frmBstapp.setTitle("BSTApp");
		frmBstapp.setResizable(false);
		frmBstapp.setBounds(100, 100, 876, 537);
		frmBstapp.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frmBstapp.getContentPane().setLayout(springLayout);
		
		JButton btnsearch = new JButton("SEARCH");
		btnsearch.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				mode = 0;
				peerIDRandom = idRandom.nextInt(999999 - 100000) + 100000;
				fileListDown.clear();
				messenger = "Enter your key word ";
				Title = "Search";
				String keySearch;
				int messengerType = 1;
				int modeConnect = 2;
				JButton open = new JButton(); 
				Frame frame = new Frame();
				keySearch = JOptionPane.showInputDialog(frame,(String)messenger,Title,messengerType);
				if (keySearch == null)
				{
					//Do nothing
				}
				else
				{
					if (keySearch.equals("")) modeConnect = 2;
					else modeConnect = 6;
					keepIns = new KeepConnection("", null, fileListDown, null, String.valueOf(peerIDRandom), modeConnect, IPSERVER, Tag.PORT_SERVER, "",0,keySearch);
					if (!keepIns.runKeepConnection())
					{
						//Frame frame = new Frame();
						JOptionPane.showMessageDialog(frame, "Connect to \nServer " +IPSERVER+"\nPort "+Tag.PORT_SERVER+"\n Failed", "Notice",2);
					}
					else
					{
						int posi = 0;
						for (int i =0;i<listDone.size();i++)
						{
							for (int j=0;j<fileListDown.size();j++)
							{
								if (listDone.get(i).getName().equals(fileListDown.get(j).getName()))
								{
									posi = j;
									break;
								}
							}
							fileListDown.get(posi).status = listDone.get(i).status;
						}
						//display file list
						DefaultTableModel model = (DefaultTableModel) tbsearch.getModel();
						model.setRowCount(0);
						no = 1;
						for (FileInfo tmpFile : fileListDown)
						{
							fileDisplay = tmpFile.getName();
							size = tmpFile.getLengDisplay();
							status = tmpFile.status;
							if (status.equals("Success")) status = "Downloaded";
							model.addRow(new Object[]{no, fileDisplay, size,status});
							no++;
						}
					}
				}
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, btnsearch, 533, SpringLayout.WEST, frmBstapp.getContentPane());
		btnsearch.setFont(new Font("Tahoma", Font.BOLD, 14));
		frmBstapp.getContentPane().add(btnsearch);
		
		JTree treeFolder = new JTree();
		springLayout.putConstraint(SpringLayout.NORTH, treeFolder, 95, SpringLayout.NORTH, frmBstapp.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, treeFolder, 20, SpringLayout.WEST, frmBstapp.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, treeFolder, -13, SpringLayout.SOUTH, frmBstapp.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, treeFolder, -671, SpringLayout.EAST, frmBstapp.getContentPane());
		treeFolder.setFont(new Font("Tahoma", Font.PLAIN, 16));
		treeFolder.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		treeFolder.setBackground(Color.WHITE);
		treeFolder.setModel(new DefaultTreeModel
		(
			new DefaultMutableTreeNode("Torrent")
			{
				{
					add(new DefaultMutableTreeNode("Download"));
					add(new DefaultMutableTreeNode("Upload"));
					add(new DefaultMutableTreeNode("Search"));
				}
			}
		));
		treeFolder.setCellRenderer(new DefaultTreeCellRenderer()
		{
			private Icon torrentIcon =  new ImageIcon(this.getClass().getResource("torrent.png"));
			private Icon downloadIcon = new ImageIcon(this.getClass().getResource("down.png"));
			private Icon uploadIcon =  new ImageIcon(this.getClass().getResource("up.png"));
			private Icon searchIcon =  new ImageIcon(this.getClass().getResource("search.png"));
			@Override
			public Component getTreeCellRendererComponent(JTree treeFolder, Object value, boolean selected, boolean expanded, boolean isLeaf, int row, boolean focused) {
				Component c = super.getTreeCellRendererComponent(treeFolder, value, selected, expanded, isLeaf, row, focused);
					if (isLeaf && value.toString().equals("Download"))
						setIcon(downloadIcon);
					else if (isLeaf && value.toString().equals("Upload"))
						setIcon(uploadIcon);
					else if (isLeaf && value.toString().equals("Search"))
						setIcon(searchIcon);
					else
						setIcon(torrentIcon);
				return c;
			}
		});
		treeFolder.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				treeFolder.getLastSelectedPathComponent();
				if (node == null)
					return;
				Object nodeInfo = node.getUserObject();
				if(nodeInfo.toString().equals("Download"))
				{
					tbsearch.setModel(new DefaultTableModel
					(
						new Object[][] 
						{
						},
						new String[] 
						{
							"No.", "File name", "Size", "Status"
						}
					));
					/////code here
					mode = 1;
					FileInfo fileDown;
					String No, file, size,status;
					for (int i = 0; i < listDone.size(); i++)
					{
						fileDown = listDone.get(i);
						No = String.valueOf(i + 1);
						file = fileDown.getName();
						size = fileDown.getLengDisplay();
						status = fileDown.status;
						DefaultTableModel model = (DefaultTableModel) tbsearch.getModel();
						model.addRow(new Object[]{No, file, size,status});
					}			
					
				}
				else if(nodeInfo.toString().equals("Upload"))
				{
					tbsearch.setModel(new DefaultTableModel
					(
							new Object[][]
							{
							},
							new String[]
							{
									"No.", "Name", "Size", "Status"
							}	
					));
					mode = 2;
					FileInfo fileUp;
					String No, file, size;
					for (int i = 0; i < fileListUp.size(); i++)
					{
						fileUp = fileListUp.get(i);
						No = String.valueOf(i + 1);
						file = fileUp.getName();
						size = fileUp.getLengDisplay();
						status  =fileUp.status;
						DefaultTableModel model = (DefaultTableModel) tbsearch.getModel();
						model.addRow(new Object[]{No, file, size,status});
					}						
				}
				else if(nodeInfo.toString().equals("Search")){
					tbsearch.setModel(new DefaultTableModel
					(
							new Object[][]
							{
							},
							new String[]
							{
								"No.", "File name", "Size", "Status"
							}
					));
					//code here
					mode = 0;
					FileInfo fileSearch;
					DefaultTableModel model = (DefaultTableModel) tbsearch.getModel();
					String No, file, size;
					model.setRowCount(0);
					//Dong bo voi file da down 
					int posi = 0;
					for (int i =0;i<listDone.size();i++)
					{
						for (int j=0;j<fileListDown.size();j++)
						{
							if (listDone.get(i).getName().equals(fileListDown.get(j).getName()))
							{
								posi = j;
								break;
							}
						}
						fileListDown.get(posi).status = listDone.get(i).status;
					}
					for (int i = 0; i < fileListDown.size(); i++)
					{
						fileSearch = fileListDown.get(i);
						No = String.valueOf(i + 1);
						file = fileSearch.getName();
						size = fileSearch.getLengDisplay();
						status = fileSearch.status;
						
						if (status.equals("Success")) 
							status = "Downloaded";
						model.addRow(new Object[]{No, file, size,status});
					}
				}
			}
		});
		frmBstapp.getContentPane().add(treeFolder);
		
		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.SOUTH, btnsearch, -29, SpringLayout.NORTH, scrollPane);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, -414, SpringLayout.SOUTH, frmBstapp.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 6, SpringLayout.EAST, treeFolder);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -13, SpringLayout.SOUTH, frmBstapp.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -21, SpringLayout.EAST, frmBstapp.getContentPane());
		frmBstapp.getContentPane().add(scrollPane);
		
		tbsearch = new JTable();
		tbsearch.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"No.", "File name", "Size", "Status"
			}
		));
		scrollPane.setViewportView(tbsearch);
		//DOWNLOAD BUTTON IN TOP
		JButton btndown = new JButton("DOWNLOAD");
		btndown.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				///////////////code here
				boolean flag;
				int num;
				mode = 1;
				DefaultTableModel model = (DefaultTableModel) tbsearch.getModel();
				if(tbsearch.getSelectedRow()==-1)
				{
					if(tbsearch.getRowCount()==0)
					{
						JOptionPane.showMessageDialog(frmBstapp, "There are no files to download.");
					}
					else
					{
						JOptionPane.showMessageDialog(frmBstapp, "You should choose a file.");
					}
				}
				else 
				{
					JButton open = new JButton();
					JFileChooser fc = new JFileChooser();				
					fc.setDialogTitle("Choose Folder");
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int  select;
					select =fc.showOpenDialog(open);
					
					if (select != JFileChooser.APPROVE_OPTION  || select == JFileChooser.CANCEL_OPTION )
					{
						//do nothing
					}
					else
					{
						folderDownload = fc.getSelectedFile().toString();
						System.out.println(folderDownload); 
						ArrayList<PeerInfo> peerL = new ArrayList<PeerInfo>();
						//get file chosen
						int choosen;
						choosen = tbsearch.getSelectedRow();
						FileInfo fileInfo = fileListDown.get(choosen);
						System.out.println("Choosen is "+choosen);
						//send to tracker to request peer list
						peerIDRandom = idRandom.nextInt(999999 - 100000) + 100000;
						flag = true;
						num = 0;
						for(int i =0;i<listDone.size();i++)
						{
							if (listDone.get(i).getName().equals(fileInfo.getName()))
							{
								flag = false;
								num = i;
								break;
							}
						}
						
						if (flag)
						{
							//num  = listDone.size();
							listDone.add(fileInfo);
							num = listDone.indexOf(fileInfo);
						}
						
						keepIns = new KeepConnection("", fileInfo.getHash(), null, peerL, String.valueOf(peerIDRandom), 3, IPSERVER, Tag.PORT_SERVER, "", 0, null);
						if (!keepIns.runKeepConnection())
						{
							Frame frame =new Frame();
							JOptionPane.showMessageDialog(frame, "Connect to \nServer " +IPSERVER+"\nPort "+Tag.PORT_SERVER+"\n Failed", "Notice",2);
							listDone.get(num).status = "Fail";
							model.setValueAt("Fail",choosen,3);
						}
						
						else
						{
							try
							{
								//Add file to listDone(list file has Download)
								listDone.get(num).status = "Downloading...";
								model.setValueAt("Downloading...",choosen,3);
								downManager = new DownManager(listDone.get(num),fileInfo,folderDownload,peerL, model, choosen);
							} 
							catch (IOException ex)
							{
								Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
							}

							threadDownloader.execute(downManager);
						}
					}
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btndown, 30, SpringLayout.NORTH, frmBstapp.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btndown, -29, SpringLayout.NORTH, scrollPane);
		springLayout.putConstraint(SpringLayout.NORTH, btnsearch, 0, SpringLayout.NORTH, btndown);
		springLayout.putConstraint(SpringLayout.WEST, btndown, 205, SpringLayout.WEST, frmBstapp.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btndown, -510, SpringLayout.EAST, frmBstapp.getContentPane());
		btndown.setForeground(Color.BLUE);
		btndown.setFont(new Font("Tahoma", Font.BOLD, 14));
		frmBstapp.getContentPane().add(btndown);
		
		JButton btnup = new JButton("UPLOAD");
		btnup.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				JButton open = new JButton();
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new java.io.File("C:\\Users\\Mrkeys\\Desktop"));
				fc.setDialogTitle("Choose file");
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(fc.showOpenDialog(open) == JFileChooser.APPROVE_OPTION)
				{
					if (fc.getSelectedFile() == null)
					{
						System.out.println("You Select Cancel");
					}
					
					else
					{
						//get file selected
						mode = 0;
						fileChosen = fc.getSelectedFile();
						
						//random a 6 letters peer ID
						peerIDRandom = idRandom.nextInt(999999 - 100000) + 100000;
						FileInfo fInfo = new FileInfo(fileChosen.getAbsolutePath(), fileChosen.length(), "");
						bol   = true;
						for (FileInfo fI: fileListUp)
						{
							if (fI.getName().equals(fInfo.getName()))
							{
								if (fI.status.equals("Upload Success"))
								{
									Frame frame = new Frame();
									JOptionPane.showMessageDialog(frame,"File has Upload Success Before","Notice",2);                                              
									bol = false;
									break;
								}
							}
						}
						
						if (bol)
						{
							//create thread to upload file
							try
							{
								upManager = new UpManager(IPSERVER,fileChosen.getAbsolutePath(), String.valueOf(peerIDRandom), fileChosen.getName(),peer);                                    

							}
							catch (Exception ex)
							{
								System.err.println("Error UPLoad "+ex);
							}
							keepIns = new KeepConnection(fileChosen.getAbsolutePath(), "", fileListUp, peerList, String.valueOf(peerIDRandom), 0,IPSERVER, Tag.PORT_SERVER, fileChosen.getName(),peer.getPort(),null);
							
							if (keepIns.runKeepConnection())
							{
								ksoatUp.add(upManager);
								threadUploader.execute(upManager); 
								fInfo.status = "Upload Success";
							}
							
							else
							{
								System.out.println("____ false");
								Frame frame = new Frame();
								JOptionPane.showMessageDialog(frame, "Connect to \nServer " +IPSERVER+"\nPort "+Tag.PORT_SERVER+"\n Failed", "Notice",2);
								fInfo.status = "Upload Fail";
							}
							
							add  = true;
							
							for (FileInfo fI: fileListUp)
							{
								if (fI.getName().equals(fInfo.getName()))
								{
									add = false;
								}
							}
							
							if (add) fileListUp.add(fInfo);
						}
						
						//display in file uploaded
						DefaultTableModel model = (DefaultTableModel) tbsearch.getModel();
						model.setRowCount(0);
						no = 1;
						for (FileInfo tmpFile : fileListUp)
						{
							fileDisplay = tmpFile.getName();
							size = tmpFile.getLengDisplay();
							status = tmpFile.status;
							model.addRow(new Object[]{no, fileDisplay, size,status});
							no++;
						}
					}
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnup, 30, SpringLayout.NORTH, frmBstapp.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnup, 6, SpringLayout.EAST, btndown);
		springLayout.putConstraint(SpringLayout.SOUTH, btnup, -29, SpringLayout.NORTH, scrollPane);
		springLayout.putConstraint(SpringLayout.EAST, btnup, -12, SpringLayout.WEST, btnsearch);
		btnup.setForeground(Color.BLUE);
		btnup.setFont(new Font("Tahoma", Font.BOLD, 14));
		frmBstapp.getContentPane().add(btnup);
		
		JButton btnremove = new JButton("REMOVE");
		btnremove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				int rowNo;
				JDialog.setDefaultLookAndFeelDecorated(true);
				int response = JOptionPane.showConfirmDialog(null, "Do you want to remove this file?", "Confirm",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.CLOSED_OPTION)
				{
				}
				
				else
				{
					if (response == JOptionPane.YES_OPTION)
					{
						DefaultTableModel model = (DefaultTableModel) tbsearch.getModel();
						if(tbsearch.getSelectedRow()==-1)
						{
							if(tbsearch.getRowCount()==0)
							{
								JOptionPane.showMessageDialog(frmBstapp, "There are no files to remove");
							}
							
							else
							{
								JOptionPane.showMessageDialog(frmBstapp, "You should choose a file");
							}
						}
						
						else
						{
							String direc;
							rowNo = tbsearch.getSelectedRow();                                                   
							direc = tbsearch.getValueAt(rowNo,1).toString();//                                                 
							if( mode == 2)
							{
								name = new File(direc).getName();
							}
							else name = direc;
							System.out.println("direc "+direc+" name "+name);
							System.out.println("____DELETE MODE"+mode);
							switch(mode)
							{
								case 0:
									deleteMode0(name);
									break;
								case 1:
									deleteMode1(name);
									break;
								case 2:
									deleteMode2(name, direc);
									break;
								default:
									break;
							}
							//remove in GUI
							 model.removeRow(rowNo);
						}
					}
				}
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, btnsearch, -6, SpringLayout.WEST, btnremove);
		btnremove.setFont(new Font("Tahoma", Font.BOLD, 14));
		springLayout.putConstraint(SpringLayout.NORTH, btnremove, 30, SpringLayout.NORTH, frmBstapp.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnremove, 694, SpringLayout.WEST, frmBstapp.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnremove, -29, SpringLayout.NORTH, scrollPane);
		springLayout.putConstraint(SpringLayout.EAST, btnremove, -21, SpringLayout.EAST, frmBstapp.getContentPane());
		frmBstapp.getContentPane().add(btnremove);
		
		JLabel lbName = new JLabel("BST");
		lbName.setForeground(Color.BLUE);
		springLayout.putConstraint(SpringLayout.NORTH, lbName, 20, SpringLayout.NORTH, frmBstapp.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lbName, 56, SpringLayout.WEST, frmBstapp.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lbName, -19, SpringLayout.NORTH, treeFolder);
		lbName.setFont(new Font("Tahoma", Font.BOLD, 46));
		frmBstapp.getContentPane().add(lbName);
		
	}
	
	public void deleteMode2(String name,String direc)
	{
		//System.out.println("name "+name+" hash "+hash);
		bol = true;
		for (int i =0;i< ksoatUp.size();i++)
		{
			if (name.equals(ksoatUp.get(i).getFileName()))
			{
				//send to server delete it from database (
				keepIns = new KeepConnection(direc, hash, null, null, ksoatUp.get(i).getID(), 5,IPSERVER, Tag.PORT_SERVER, name,0,null);
				if (keepIns.runKeepConnection())
				{
					ksoatUp.get(i).stopThread();
				}
				
				else
				{
					Frame frame = new Frame();
					JOptionPane.showMessageDialog(frame, "Connect to \nServer " +IPSERVER+"\nPort "+Tag.PORT_SERVER+"\n Failed", "Notice",2);
					bol = false;
				}
				break;
			}
		}
		if (bol)
		{
			for(FileInfo f:fileListUp)
			{
				if (f.getName().equals(direc))
				{
					 hash = f.getHash();
					 fileListUp.remove(f);
					 break;
				}
			}
		}
	}
	
	public void deleteMode1(String name)
	{
		for(FileInfo f:listDone)
		{
			if (f.getName().equals(name))
			{
				listDone.remove(f);
				break;
			}
		}
	}
	
	public void deleteMode0(String name)
	{
		for(FileInfo f:fileListDown){
			if (f.getName().equals(name))
			{
				fileListDown.remove(f);
				break;
			}
		}
	}
}
