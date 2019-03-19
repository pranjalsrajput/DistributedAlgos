import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComponentClass implements Runnable{
	
	public static int[] listOfProcessIds= {1,2,3,4};
	public static int[] listOfCandidateProcesses= {1};
	public static int port=8098;
	public static String repositoryName="rmi://145.94.210.95:"+port+"/RemoteDataPointHandler";
	static ProcessInterface look_up;
	static RoundCheckerInterface look_up1;
	int j=0;
	boolean isCandidateProcess=false;
	Message candidateMsg=new Message(-1, -1);
	private final CountDownLatch doneSignal;
	private static boolean processStart=false;
	private boolean processStop=false;
	private boolean isRoundCheckerProcess=false;
	
	ComponentClass(int j, boolean isCandidateProcess,Message candidateMsg,CountDownLatch doneSignal,boolean isRoundCheckerProcess) {
		this.j = j;
		this.isCandidateProcess=isCandidateProcess;
		this.isRoundCheckerProcess=isRoundCheckerProcess;
		this.candidateMsg=candidateMsg;
		this.doneSignal = doneSignal;
	}
	public static void main(String[] args) 
			throws MalformedURLException, RemoteException, NotBoundException {
		
			try {
				System.out.println("Candidate processes are..."+Arrays.toString(listOfCandidateProcesses));
				//ExecutorService executor = Executors.newFixedThreadPool(listOfCandidateProcesses.length);
				for(int i=0;i<listOfCandidateProcesses.length;i++) {
					
					//if(i!=listOfCandidateProcesses.length) {
						processStart=true;
						new Thread(new ComponentClass(listOfCandidateProcesses[i],true,new Message(-1, -1),new CountDownLatch(0),false)).start();
					//}
					/*else if(i==listOfCandidateProcesses.length) {
						
						new Thread(new ComponentClass(0,false,new Message(-1, -1),new CountDownLatch(0),true)).start();
						}*/
					
				}
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
					// calling release() after a successful acquire()
					//System.out.println( "releasing thread...");
					//Thread.currentThread().interrupt();
				}
		}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			if(this.isCandidateProcess) {
				look_up = (ProcessInterface) Naming.lookup(repositoryName+this.j);
				look_up.startCandidateProcess();
			}
			/*else if(!this.isCandidateProcess && this.isRoundCheckerProcess) {
				look_up1 = (RoundCheckerInterface) Naming.lookup(repositoryName + "roundChecker");
				//look_up1.roundCheck();
			}*/
			else {
				look_up = (ProcessInterface) Naming.lookup(repositoryName+this.j);
				look_up.ordinaryProcess(this.candidateMsg);
				this.doneSignal.countDown();
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
		}
		catch (Exception e) {
			e.printStackTrace();

		}
	}
}
