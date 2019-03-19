import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class RoundCheckerImplementation extends UnicastRemoteObject implements RoundCheckerInterface {

	private static final long serialVersionUID = 1L;
	public boolean isElectionDone = false;
	public int currentRound = 0;
	public int previousRound = 0;
	private List<Integer> ordinaryProcessList = new ArrayList<>();

	@Override
	public int getPreviousRound() throws RemoteException {
		return previousRound;
	}

	@Override
	public void setPreviousRound(int previousRound) throws RemoteException {
		this.previousRound = previousRound;
	}

	RoundCheckerImplementation(boolean isElectionDone, int currentRound, int previousRound,
			List<Integer> ordinaryProcessList) throws RemoteException {
		this.isElectionDone = isElectionDone;
		this.currentRound = currentRound;
		this.previousRound = previousRound;
		this.ordinaryProcessList = ordinaryProcessList;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isElectionDone() throws RemoteException {
		return isElectionDone;
	}

	@Override
	public void setElectionDone(boolean isElectionDone) throws RemoteException {
		this.isElectionDone = isElectionDone;
	}

	@Override
	public int getCurrentRound() throws RemoteException {
		return currentRound;
	}

	@Override
	public void setCurrentRound(int currentRound) throws RemoteException {
		this.currentRound = currentRound;
	}

	@Override
	public void roundCheck() throws RemoteException {
		try {
			synchronized (this) {
				
				System.err.println("Round check started...");
				Random randomizer = new Random();

				//System.err.println("Ordinary list= " + this.ordinaryProcessList.size());

				// int next=5;
				if(this.ordinaryProcessList.size()!=0 && this.ordinaryProcessList!=null) {
					int next = this.ordinaryProcessList.get(randomizer.nextInt(this.ordinaryProcessList.size()));
					System.err.println("Next= " + next);
					ProcessInterface lookup1;

					//System.err.println("Round= " + this.currentRound);
					lookup1 = (ProcessInterface) Naming.lookup(ComponentClass.repositoryName + next);
					if (!lookup1.isOrdinaryProcess() && !lookup1.isCandidateProcess()) {
						System.err.println("Ordinary process= " + next + " becoming a candidate process");
						this.ordinaryProcessList.remove(this.ordinaryProcessList.indexOf(next));
						new Thread(new ComponentClass(next, true, new Message(-1, -1), new CountDownLatch(0), false)).start();
					}
					
					  else if(lookup1.isOrdinaryProcess()){
					  this.ordinaryProcessList.remove(this.ordinaryProcessList.indexOf(next)); }
					 

					System.err.println("Round check completed...");
				}
				
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
