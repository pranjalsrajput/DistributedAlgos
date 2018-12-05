import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessDataConstants {

	public static int noOfProcesses=3;
	public static int[] listOfProcessIds= {0,1,2};
	public static int[] listOfbroadcastingProcesses= {0,2,1};
	public static int port=8098;
	public static String repositoryName="//localhost:"+port+"/RemoteDataPointHandler";
	private static int count=1;
	
	 public static int[] incrementVectorClock(int[] vectorClock, int processId) {
		 	vectorClock[processId]=vectorClock[processId]+1;
	    	System.out.println("VC  incremented and Current Vector clock of process "+processId+"="+Arrays.toString(vectorClock));
	    	return vectorClock;
	 }
	 
	 
	 
	 
	 public static void broadcastMsg(Message message, ProcessInterface look_up) {
	        //vectorClock[currentProcessId] = vectorClock[currentProcessId] + 1;
		 //int[] vectorClock=null;
	        try {
	        // broadcast the message
	        	System.out.println(message.getMessage());
	        	/*vectorClock=message.getVectorClock();
	        	vectorClock=incrementVectorClock(vectorClock,message.getProcessId());*/
	        	System.out.println("Incremented VC of broadcasting process="+message.getProcessId()+"..."+Arrays.toString(message.getVectorClock()));
	        	int[] processIdArray=ProcessDataConstants.listOfProcessIds;
	        	 for(int i = 0; i <processIdArray.length ; i ++) {
	                 if(i != message.getProcessId()) {
	                     // send message
	                	 look_up = (ProcessInterface) Naming.lookup(ProcessDataConstants.repositoryName+i);
	                	 look_up.receiveMsg(message);
	                	 
	                	 /*Thread object = new Thread((Runnable) look_up); 
	                     object.start(); */
	                 }
	             }
	        }
	        catch (Exception e) {
	            System.err.println("Server exception: " + e.toString());
	            e.printStackTrace();
	        }
	    }
	 
	 
	 
	 public static int[] receiveMsg(Message message,int myProcessId,int[] myVectorClock, List<Message> messageBuffer) throws RemoteException {
	    	boolean deliveryCondition=false;
	    	int[] tempVectorClock=null;
	    	try {
	    	//myVectorClock=incrementVectorClock(myVectorClock, myProcessId);
	    	tempVectorClock=myVectorClock;
	    	tempVectorClock[message.getProcessId()]=tempVectorClock[message.getProcessId()]+1;
	    	for(int j=0;j<tempVectorClock.length;j++) {
	    		if((tempVectorClock[j]) >= message.getVectorClock()[j]) {
	    			deliveryCondition=true;
	    			System.out.println("Deliverycondition true in process..."+myProcessId);
	    			break;
	    		}
	    	}
	    	if(deliveryCondition) {
	    		myVectorClock=incrementVectorClock(tempVectorClock, myProcessId);
	    		for(int n=0;n<myVectorClock.length;n++) {
	    			myVectorClock[n]=Math.max(message.getVectorClock()[n],myVectorClock[n]);
	    		}
	    		System.out.println("Updated VC of process="+myProcessId+"..."+Arrays.toString(myVectorClock));
	        	
	    		deliverMsg(message,myProcessId,messageBuffer);
	            while(!messageBuffer.isEmpty()) {
	                for(Message msg : messageBuffer) {
	                	
	                	boolean deliveryConditionForBuffMsg=false;
	                	
	                	int[] tempVectorClock1=myVectorClock;
	        	    	tempVectorClock1[msg.getProcessId()]=tempVectorClock1[msg.getProcessId()]+1;
	                	
	                	for(int j=0;j<myVectorClock.length;j++) {
	                		if((tempVectorClock1[j]) >= msg.getVectorClock()[j]) {
	                			deliveryConditionForBuffMsg=true;
	                			break;
	                		}
	                	}
	                	if(deliveryConditionForBuffMsg) {
	                		for(int n=0;n<myVectorClock.length;n++) {
	        	    			myVectorClock[n]=Math.max(tempVectorClock1[n],myVectorClock[n]);
	        	    		}
	                		System.out.println("Incremented VC of process="+myProcessId+"..."+Arrays.toString(myVectorClock));
	                		deliverMsg(message,myProcessId,messageBuffer);
	                	}
	                }
	            }
	    	}
	    	else {
	            messageBuffer.add(message);
	        }
	    	}
	    	catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    	return myVectorClock;
	    }
	    
	    // Deliver the message
	    private static void deliverMsg(Message message,int myProcessId, List<Message> messageBuffer) throws RemoteException {
	        // Update the vectorClock first
	    	try {
	    	System.out.println("Delivery starts in process..."+myProcessId);
	    	System.out.println("Message received from process..."+message.getProcessId()+" is delivered in process="+myProcessId+"\n\n");
	    	//myVectorClock=incrementVectorClock(myVectorClock,myProcessId);

	        if(messageBuffer.contains(message)) {
	            messageBuffer.remove(message);
	            System.out.println("message removed from the buffer by process..."+myProcessId);
	        }
	       // return myVectorClock;
	    	}
	    	catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }




		public static int getCount() {
			return count;
		}




		public static void setCount(int count) {
			ProcessDataConstants.count = count;
		}
}
