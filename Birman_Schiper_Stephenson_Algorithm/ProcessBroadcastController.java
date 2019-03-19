import java.io.File;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ProcessBroadcastController {

	static ClientOperation clientOperation;
	static Process2Class process2Class;
	static ServerOperation serverOperation;
	public static void main(String[] args) 
			throws MalformedURLException, RemoteException, NotBoundException {
			clientOperation=new ClientOperation();
			process2Class=new Process2Class();
			serverOperation=new ServerOperation();
			try {
				File file = new File("d:\\myVectorClock.txt");
				File file1 = new File("d:\\myVectorClock1.txt");
				File file2 = new File("d:\\myVectorClock2.txt");
	    		if(file.delete()){
	    			System.out.println(file.getName() + " is deleted!");
	    		}
	    		if(file1.delete()){
	    			System.out.println(file.getName() + " is deleted!");
	    		}
	    		if(file2.delete()){
	    			System.out.println(file.getName() + " is deleted!");
	    		}
	    		else{
	    			System.out.println("Delete operation is failed.");
	    		}
				for(int i=0;i<ProcessDataConstants.listOfbroadcastingProcesses.length;i++) {
					if(ProcessDataConstants.listOfbroadcastingProcesses[i]==0) {
						clientOperation.broadcastMsg();
					}
					if(ProcessDataConstants.listOfbroadcastingProcesses[i]==1) {
						process2Class.broadcastMsg();
					}
					if(ProcessDataConstants.listOfbroadcastingProcesses[i]==2) {
						serverOperation.broadcastMsg();
					}
					
				}
				
				System.out.println("Final count value is"+ProcessDataConstants.getCount());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
}
