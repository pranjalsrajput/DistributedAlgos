import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;

// Implementing the remote interface
public class ProcessImplementation implements ProcessInterface {
    private int[] listOfProcessIds; // List of the id's of all processes
    private int currentProcessId; // The id of this process
    int[] vectorClock; // Local vector clock of the whole system
    List<Message> messageBuffer; // Buffer for messages which can't be delivered yet

    // Implementing the interface method
//    public void printMsg() {
//        System.out.println("This is an example RMI program");
//    }
   /* public ProcessImplementation(int[] listOfProcessIds, int currentProcessId, int[] vectorClock) {
        this.currentProcessId = currentProcessId;
        this.vectorClock = new int[listOfProcessIds.length];
        this.messageBuffer = new ArrayList<Message>();
    }*/

    
    
    
    public static void main(String[] args){

        try {
        		LocateRegistry.createRegistry(ProcessDataConstants.port);
        	//ProcessInterface object=new Process2Class();
        	
        	/*for(int i=0; i<ProcessDataConstants.listOfProcessIds.length;i++) {*/
        		Naming.rebind(ProcessDataConstants.repositoryName+0, new ClientOperation());
        		Naming.rebind(ProcessDataConstants.repositoryName+1, new Process2Class());
        		Naming.rebind(ProcessDataConstants.repositoryName+2, new ServerOperation());
        		
        		
                System.err.println("All processess ready");
        	/*}*/
            /*Naming.rebind(ProcessDataConstants.repositoryName+myProcessId, new Process2Class());    
            Naming.rebind(ProcessDataConstants.repositoryName+1, new ServerOperation());
            System.err.println("Server(Process 1 and 2) ready");*/

        } catch (Exception e) {

            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();

        }

    }

	@Override
	public int[] receiveMsg(Message message) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
    
    
    /*
    
    
    // Broadcasts a message to all processes
    @Override
    public void broadcastMsg(Message message) {
        vectorClock[currentProcessId] = vectorClock[currentProcessId] + 1;

        // broadcast the message
        for(int i = 0; i < listOfProcessIds.length; i ++) {
            if(i != currentProcessId) {
                // send message
            }
        }
    }

    // On receive message request, check whether it satisfies the HB order
    // If True: deliver message + check whether the messages in the buffer can be delivered
    // Otherwise: put message in buffer
    @Override
    public void receiveMsg(Message message) throws RemoteException {
        if((vectorClock[message.getProcessId()] + 1) >= message.getVectorClock()[message.getProcessId()]) {
            deliverMsg(message);
            while(!messageBuffer.isEmpty()) {
                for(Message m : messageBuffer) {
                    if((vectorClock[m.getProcessId()] + 1) >= m.getVectorClock()[m.getProcessId()]) {
                        deliverMsg(m);
                    }
                }
            }
        } else {
            messageBuffer.add(message);
        }
    }

    // Deliver the message
    @Override
    public void deliverMsg(Message message) throws RemoteException {
        // Update the vectorClock first
        for(int i = 0; i < listOfProcessIds.length; i++) {
            if(vectorClock[i] < message.getVectorClock()[i]) {
                vectorClock[i] = message.getVectorClock()[i];
            }
        }

        if(messageBuffer.contains(message)) {
            messageBuffer.remove(message);
        }
    }*/
}