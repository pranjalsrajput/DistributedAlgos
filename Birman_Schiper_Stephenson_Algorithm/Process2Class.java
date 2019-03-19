import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Process2Class extends UnicastRemoteObject implements ProcessInterface,Runnable{

	protected Process2Class() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private Message message;
	/*protected Process2Class(Message message) throws RemoteException {
		this.message=message;
		// TODO Auto-generated constructor stub
	}*/

	private static ProcessInterface look_up;
	private static int myProcessId=1;
	private static int[] myVectorClock= {0,0,0};
	List<Message> messageBuffer=new ArrayList<>();  // Buffer for messages which can't be delivered yet
    
	public static void main(String[] args) 
		throws MalformedURLException, RemoteException, NotBoundException {
		try {
			//myVectorClock=ProcessDataConstants.broadcastMsg(new Message(myProcessId, "Hello Processess", myVectorClock), look_up);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void broadcastMsg()  {
        myVectorClock=getClock();
        int count =ProcessDataConstants.getCount();
        System.out.println("Count value you set="+count);
        myVectorClock=ProcessDataConstants.incrementVectorClock(myVectorClock,myProcessId);
		ProcessDataConstants.broadcastMsg(new Message(myProcessId, "BROADCAST BY "+myProcessId, myVectorClock), look_up);
		putClock(myVectorClock);
	}
	
	public void run() 
    { 
        try
        { 
            // Displaying the thread that is running 
            System.out.println ("Thread " + 
                                Thread.currentThread().getId() + 
                                " is running"); 
          //  receiveMsg(message);
  
        } 
        catch (Exception e) 
        { 
            // Throwing an exception 
            System.out.println ("Exception is caught"); 
        } 
    } 
	
	public int[] receiveMsg(Message message) throws RemoteException {
		int[] vectorClock=null;
		try {
			myVectorClock=getClock();
			vectorClock=ProcessDataConstants.receiveMsg(message, myProcessId, myVectorClock, messageBuffer);
			putClock(myVectorClock);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return vectorClock;
	}

	
	public int[] getClock() {

        int[] clock = {0,0,0};
        try {
            if ( !new File("d:\\myVectorClock1.txt").exists())
                return clock;
            else {
                BufferedReader br = new BufferedReader(new FileReader(new File("d:\\myVectorClock1.txt")));
                String s = br.readLine().trim();
                s=s.substring(1, s.length()-1);
                for(int k=0;k<s.split(",").length;k++) {
                	clock[k] =Integer.parseInt(s.split(",")[k].trim());;
                }
                br.close();
            }                
        } catch(Exception e) {
            e.printStackTrace();
        }
        return clock;
    }

    public void putClock(int[] clock) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("d:\\myVectorClock1.txt")));
            bw.write(Arrays.toString(clock));
            bw.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}