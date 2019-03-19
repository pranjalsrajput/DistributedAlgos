import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

// Creating Remote interface for our application
public interface ProcessInterface extends Remote {
	
   void setListOfAllProcesses(ProcessInterface[] processesArray) throws RemoteException;
   ProcessInterface[] getListOfAllProcesses() throws RemoteException;
  // public Message receiveMsg(Message candidateMsg) throws RemoteException;
   public void startCandidateProcess()  throws RemoteException;
   public void run() throws RemoteException;
   public void ordinaryProcess(Message candidateMsg) throws RemoteException;
   public boolean candidateProcess(int candidateId, List<Integer> linkSet, int level) throws RemoteException;
   //public void sendMessage(ProcessInterface look_up, Message candidateMsg, int randomProcessId) throws RemoteException;
   public Message receiveAck(int candidateId) throws RemoteException;
   boolean isOrdinaryProcess() throws RemoteException;
   void setOrdinaryProcess(boolean isOrdinaryProcess) throws RemoteException;
   boolean isCandidateProcess() throws RemoteException;
   void setCandidateProcess(boolean isCandidateProcess) throws RemoteException;
}