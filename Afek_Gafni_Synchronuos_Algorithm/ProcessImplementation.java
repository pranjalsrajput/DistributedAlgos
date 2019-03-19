import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class ProcessImplementation extends UnicastRemoteObject implements ProcessInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProcessInterface[] listOfAllProcesses; // List of all processes

	@Override
	public ProcessInterface[] getListOfAllProcesses() {
		return listOfAllProcesses;
	}

	@Override
	public void setListOfAllProcesses(ProcessInterface[] listOfAllProcesses) {
		this.listOfAllProcesses = listOfAllProcesses;
	}

	private static volatile Message resultMsg = new Message(-1, -1);
	private int originalProcessId;
	private int myProcessId; // The id of this process
	private int level;
	private int candidateLinkId = 0;
	private List<Integer> myLinkList = new ArrayList<>();
	private List<Integer> receivedAckgList = new ArrayList<>();
	private boolean isCandidateProcess = false;
	@Override
	public boolean isCandidateProcess() throws RemoteException {
		return isCandidateProcess;
	}
	@Override
	public void setCandidateProcess(boolean isCandidateProcess) throws RemoteException {
		this.isCandidateProcess = isCandidateProcess;
	}

	private boolean isOrdinaryProcess = false;
	private ArrayList<Message> messagesReceived;
	private List<ProcessInterface> randomProcessList = new ArrayList<>();
	private Map<Integer, Message> resultMap = new HashMap<>();
	private CountDownLatch doneSignal;

	public ProcessImplementation(CountDownLatch doneSignal, int originalProcessId, int myProcessId, int level,
			List<Integer> myLinkList, boolean isCandidateProcess, boolean isOrdinaryProcess,
			List<Integer> receivedAckgList, List<ProcessInterface> randomProcessList, Map<Integer, Message> resultMap)
			throws RemoteException {
		this.myLinkList = myLinkList;
		this.myProcessId = myProcessId; // Will change during election process
		this.originalProcessId = originalProcessId; // Won't change during election process
		this.level = level;
		this.isCandidateProcess = isCandidateProcess;
		this.setOrdinaryProcess(isOrdinaryProcess);
		this.receivedAckgList = receivedAckgList;
		this.messagesReceived = new ArrayList<>();
		this.randomProcessList = new ArrayList<>();
		this.resultMap = new HashMap<>();
		this.doneSignal = doneSignal;
		// super();
		// TODO Auto-generated constructor stub
	}

	public int getCandidateLinkId() {
		return candidateLinkId;
	}

	public void setCandidateLinkId(int candidateLinkId) {
		this.candidateLinkId = candidateLinkId;
	}

	@Override
	public void startCandidateProcess() throws RemoteException {
		try {
			boolean isElected = false;
			this.isCandidateProcess = true;
			this.setCandidateProcess(true);
			isElected = candidateProcess(this.myProcessId, this.myLinkList, this.level);
			if (isElected) {
				System.out.println("I'm elected, regards...process=" + this.originalProcessId);
			} else {
				System.out.println("I'm NOT elected, regards...process=" + this.originalProcessId);
				Thread.currentThread().interrupt();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() throws RemoteException {
		try {
			startCandidateProcess();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean candidateProcess(int candidateId, List<Integer> linkSet, int level) throws RemoteException {
		Random randomizer = new Random();
		List<Integer> connectedLinkList = new ArrayList<>();
		List<Integer> receivedAckList = new ArrayList<>();
		connectedLinkList = linkSet;
		int noOfNextProcess = 0;
		boolean ack = false;
		boolean isElected = false;
		Message candidateMsg = new Message(level, candidateId);

		try {/* synchronized (this) { */
			System.out.println("Candidate process=" + this.originalProcessId + " with level and id ("
					+ candidateMsg.getLevel() + "," + candidateMsg.getId() + ")\n");
			while (this.isCandidateProcess) {
				level += 1;
				this.level = level;
				candidateMsg.setLevel(level);
				if (level % 2 == 0) {
					if (connectedLinkList.isEmpty()) {
						System.out.println("ELECTED candidate= " + this.originalProcessId);
						isElected = true;
						this.level = level;
						RoundCheckerInterface look_up1 = (RoundCheckerInterface) Naming
								.lookup(ComponentClass.repositoryName + "roundChecker");
						look_up1.setElectionDone(true);
						break;
					} else {
						this.receivedAckgList = new ArrayList<>();
						this.randomProcessList = new ArrayList<>();
						noOfNextProcess = (int) Math.min(Math.pow(2.0, (level / 2)), connectedLinkList.size());
						List<Integer> randomSubsetProcessList = new ArrayList<>();
						this.doneSignal = new CountDownLatch(noOfNextProcess);

						for (int j = 0; j < noOfNextProcess; j++) {

							int randomElement = connectedLinkList.get(randomizer.nextInt(connectedLinkList.size()));
							randomSubsetProcessList.add(randomElement);
							// Send(Level,id) over all links in randomSubsetProcessList
							System.out.println("Candidate process=" + this.originalProcessId
									+ " sending message with level and id (" + candidateMsg.getLevel() + ","
									+ candidateMsg.getId() + ") to ordinary process=" + randomSubsetProcessList.get(j));
							ProcessInterface look_up = (ProcessInterface) Naming
									.lookup(ComponentClass.repositoryName + randomSubsetProcessList.get(j));
							// look_up.ordinaryProcess(candidateMsg); //send messages to ordinary process
							this.randomProcessList.add(j, look_up);

							connectedLinkList.remove(connectedLinkList.indexOf(randomElement));

							try {
								synchronized (this) {
									boolean isthreadExists = false;
									Thread getThread = new Thread(new Runnable() {
										public void run() {
											// code goes here.
										}
									});
									for (Thread t : Thread.getAllStackTraces().keySet()) {
										if (t.getName().equalsIgnoreCase("T" + randomSubsetProcessList.get(j))) {
											isthreadExists = true;
											getThread = t;
										}
									}
									if (isthreadExists) {
										getThread.run();
									} else {
										new Thread(new ComponentClass(randomSubsetProcessList.get(j), false, candidateMsg,
												this.doneSignal, false), "T" + randomSubsetProcessList.get(j)).start();
									}	
								}
								

							} catch (RuntimeException e) {
								Thread t = Thread.currentThread();
								t.getUncaughtExceptionHandler().uncaughtException(t, e);
							}

						}
						System.out.println("Waiting for latch countdown...");
						this.doneSignal.await();
						System.out.println("Countdown done");
					}
				} else {
					// Waiting for ACK

					System.out.println(
							"Candidate process=" + this.originalProcessId + " waiting for ACK with level and id ("
									+ candidateMsg.getLevel() + "," + candidateMsg.getId() + ")");
					for (int n = 0; n < this.randomProcessList.size(); n++) {
						ProcessInterface lookup = this.randomProcessList.get(n);
						Message ordinaryProcessMsg = lookup.receiveAck(this.originalProcessId);
						
						do{  
							//code to be executed  
							ordinaryProcessMsg = lookup.receiveAck(this.originalProcessId);
							}while(ordinaryProcessMsg==null);
						
						if (ordinaryProcessMsg.getLevel() == (this.level - 1)
								&& ordinaryProcessMsg.getId() == this.originalProcessId && ordinaryProcessMsg.isAck()
								&& this.isCandidateProcess == true) {
							this.receivedAckgList.add(ordinaryProcessMsg.getOriginalProcessId());
						} else {
							System.out.println("Ordinary process= " + ordinaryProcessMsg.getOriginalProcessId()
									+ " with (" + ordinaryProcessMsg.getLevel() + "," + ordinaryProcessMsg.getId()
									+ ") captured Candidate process= (" + this.level + "," + candidateMsg.getId()
									+ ")");
							isElected = false;
							this.level = ordinaryProcessMsg.getLevel();
							this.myProcessId = ordinaryProcessMsg.getId();
							break;
						}
					}

					System.out.println("Process= " + this.originalProcessId + " received Ack from "
							+ this.receivedAckgList.size() + "//" + noOfNextProcess + " processess");
					if (this.receivedAckgList.size() < noOfNextProcess) {
						// STOPs
						System.out.println("Election process ceased for candidate= " + this.originalProcessId
								+ " and its current id and level= (" + this.level + "," + this.myProcessId + ")");
						isElected = false;
						break;
					} else {
						System.out.println("Candidate process=" + this.originalProcessId + " received all ACK\n");
					}
					
					RoundCheckerInterface look_up1 = (RoundCheckerInterface) Naming
							.lookup(ComponentClass.repositoryName + "roundChecker");
					System.err.println("Running roundChecker...");
					// System.err.println("look_up1.getCurrentRound()..."+look_up1.getCurrentRound());
					// System.err.println("look_up1.getPreviousRound()..."+look_up1.getPreviousRound());
					look_up1.roundCheck();
					/*if (look_up1.getCurrentRound() < this.level) {
						look_up1.setCurrentRound(this.level);
						System.err.println("Round updated= " + this.level);
					}*/
				}
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.doneSignal.countDown(); // cause await to return asap
		}
		return isElected;
	}

	@Override
	public void ordinaryProcess(Message candidateMsg) throws RemoteException {
		Message ordinaryProcessMsg = new Message(this.level, this.myProcessId);
		this.isOrdinaryProcess = true;
		this.setOrdinaryProcess(true);
		boolean ack = false;
		this.messagesReceived.add(candidateMsg); // R
		try {
			synchronized (this) {
				System.out.println("waiting for processess.....");
				this.wait(1500);
				receiveMsg2(candidateMsg, ordinaryProcessMsg);
				this.messagesReceived = new ArrayList<>();

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void receiveMsg2(Message candidateMsg, Message ordinaryProcessMsg) throws RemoteException {
		boolean ack = false;
		try {

			for (Message msg : this.messagesReceived) {
				System.out.println("Received message list in ordinary process= " + this.originalProcessId + " is ("
						+ msg.getLevel() + "," + msg.getId() + "), ");
			}

			System.out.println(" list printed\n");
			System.out.println("Ordinary process=" + this.originalProcessId + " with level and id (" + this.level + ","
					+ this.myProcessId + ") and candidate process=" + candidateMsg.getId());

			// Lexicographic maximum in R
			int maxLevelInMessagesReceived = -1;
			int maxIdInMessagesReceived = -1;

			for (Message m : this.messagesReceived) {
				if (m.getLevel() > maxLevelInMessagesReceived) {
					maxLevelInMessagesReceived = m.getLevel();
					maxIdInMessagesReceived = m.getId();
					this.candidateLinkId = m.getId();
				} else if (m.getLevel() == maxLevelInMessagesReceived) {
					if (m.getId() > maxIdInMessagesReceived) {
						maxIdInMessagesReceived = m.getId();
					}
				}
			}
			if (this.candidateLinkId == candidateMsg.getId()) {
				if (maxLevelInMessagesReceived > ordinaryProcessMsg.getLevel()) {
					ordinaryProcessMsg.setLevel(maxLevelInMessagesReceived);
					ordinaryProcessMsg.setId(maxIdInMessagesReceived);
					ack = true;
					System.out.println("Candidate process = " + candidateMsg.getId()
							+ " is greater than ordinary process= " + this.originalProcessId);
					this.isCandidateProcess = false;
				} else if (maxLevelInMessagesReceived == ordinaryProcessMsg.getLevel()
						&& maxIdInMessagesReceived > ordinaryProcessMsg.getId()) {
					ordinaryProcessMsg.setId(maxIdInMessagesReceived);
					ack = true;
					System.out.println("Candidate process = " + candidateMsg.getId()
							+ " is greater than ordinary process= " + this.originalProcessId);
					this.isCandidateProcess = false;
				} else {
					ack = false;
					System.out.println("Candidate process = " + candidateMsg.getId()
							+ " is less than ordinary process= " + this.originalProcessId);
				}

				if (ack) {
					System.out.println("Process= " + this.originalProcessId + " Sending ACK to candidate process= "
							+ candidateMsg.getId());// As now ordinaryProcessId=CandidateProcess id
					this.level = ordinaryProcessMsg.getLevel() + 1;
					this.myProcessId = ordinaryProcessMsg.getId();

					System.out.println("Current value of ordinary process= " + this.originalProcessId
							+ " after sending the ack is (" + this.level + "," + this.myProcessId + ")\n");
				}

			}
			ordinaryProcessMsg.setOriginalProcessId(this.originalProcessId);
			ordinaryProcessMsg.setAck(ack);
			for (Message m : this.messagesReceived) {
				this.resultMap.put(m.getId(), ordinaryProcessMsg);
			}
			// Determine whether the candidate process is larger than the ordinary process
			// If true Then ack=true, Else ack=false

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Message receiveAck(int candidateId) throws RemoteException {
		return this.resultMap.get(candidateId);
	}

	@Override
	public boolean isOrdinaryProcess() throws RemoteException {
		return isOrdinaryProcess;
	}

	@Override
	public void setOrdinaryProcess(boolean isOrdinaryProcess) throws RemoteException {
		this.isOrdinaryProcess = isOrdinaryProcess;
	}
}
