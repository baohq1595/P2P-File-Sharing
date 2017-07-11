package Common;

public class Tag {
	public final static String HASH_OK = "HASH_RIGHT";		
	public final static String HASH_WRONG = "HASH_WRONG";
	public final static String ASK_PIECE_LIST = "LIST_PARTS";
	public final static String MSSG_TYPE_RE = "REQUESPART";
	public final static String MSSG_TYPE_TER = "TERMINATES";
	public final static String MSSG_ACTION_SEND = "ACTIONSEND";	
	public final static String PEER_ID_OK = "PEERID_OK";
	public final static String PEER_ID_WRONG = "PEERID_WRO";
	public final static String CONNECT_SUCC = "CONNSUCCES";
	public final static String FILE_NOT_FOUND = "FILENOTFOU";
	public final static String PEER_ID_RE = "PEERID_REQ";
	public final static String ACK_FP_LENG = "ACK_FP_LEN";
	public final static String PEERLIST_REQ = "PL_REQUIRE";
	public final static String FILELIST_RE = "FILELISTRE";
	public final static String PEERLIST_READY = "PEERLISTOK";
	public final static String KEEP_ALIVE = "KEEP_ALIVE";
	public final static String PORT_OK = "PORT_IS_OK";
	public final static String DELETE_PEER = "DELETEPEER";
	public final static String SEACRH_KEY = "SEARCH_KEY";
	public final static String NOTEXIST = "NOT_EXIST_";
	public final static String KEEPSUCCES = "KEEPSUCCES";
	public final static String UPDATE_LISTFILE = "UPLISTFILE";
	public final static String READY_UPDATE = "READYUPDAT";
	public final static String END_UPDATE = "END_UPDATE";
	public final static String RECIVE_PEERID = "RECIPEERID";
	public final static String READY_PEERID = "READPEERID";
	public final static String HASRECEIVE  = "HASRECEIVE";
	public final static int BUFFER_SIZE = 128*1024;
	public final static int MSSG_ONLY_SIZE = 14;
	public final static int MSSG_SIZE = 14;
	public final static int HASH_SIZE = 40;
	public final static int PEER_ID_SIZE = 6;
	public final static int PEER_INFO_SIZE = 6 + 4;
	public final static int PORT_SERVER =60000;
	public final static String HashFile = "hashtable";
	public final static String FileList = "filelist";

	
}