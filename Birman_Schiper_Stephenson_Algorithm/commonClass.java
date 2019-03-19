/*import java.io.File;
import java.rmi.RemoteException;

public class commonClass {
	 public static int[] myVectorClock0= {0,0,0};
	 public static int[] myVectorClock1= {0,0,0};
	 public static int[] myVectorClock2= {0,0,0};
	 
	 static ClientOperation clientOperation;
		static Process2Class process2Class;
		static ServerOperation serverOperation;
		
	public void broadcastMethod() throws RemoteException {
		clientOperation=new ClientOperation();
		process2Class=new Process2Class();
		serverOperation=new ServerOperation();
		try {
			
			for(int i=0;i<ProcessDataConstants.listOfbroadcastingProcesses.length;i++) {
				if(ProcessDataConstants.listOfbroadcastingProcesses[i]==0) {
					broadcastMsg(myVectorClock0);
				}
				if(ProcessDataConstants.listOfbroadcastingProcesses[i]==1) {
					process2Class.broadcastMsg();
				}
				if(ProcessDataConstants.listOfbroadcastingProcesses[i]==2) {
					serverOperation.broadcastMsg();
				}
				
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	public static void broadcastMsg(int[] clock)  {
       // myVectorClock=getClock();
		clock=ProcessDataConstants.incrementVectorClock(clock,myProcessId);
		ProcessDataConstants.broadcastMsg(new Message(myProcessId, "BROADCAST BY "+myProcessId, clock), look_up);
		putClock(myVectorClock);
	}	 
}
*/