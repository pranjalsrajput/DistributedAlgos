import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

public class Proces implements Interface, Serializable {
    private Interface[] listOfAllProcesses;
    private CopyOnWriteArrayList<Message> receivedMessages;
    private int round;
    private boolean decided;
    private int value;
    private boolean faulty;
    private Semaphore semaphore;
    private int id;

    public Proces(int id, boolean faulty) {
        this(id, faulty, new Random().nextInt(2));
    }

    public Proces(int id, boolean faulty, int value) {
        this.id = id;
        this.receivedMessages = new CopyOnWriteArrayList<>();
        this.round = 1;
        this.decided = false;
        this.value = value;
        this.faulty = faulty;
        this.semaphore = new Semaphore(1);
    }

    public int getRound() throws RemoteException {
        return round;
    }

    public boolean isDecided() throws RemoteException {
        return decided;
    }

    public int getValue() throws RemoteException {
        return value;
    }

    public int getId() throws RemoteException {
        return id;
    }

    public Semaphore getSemaphore() throws RemoteException {
        return semaphore;
    }
    
    public int countZeroNotificationMessages() throws InterruptedException {
        int count = 0;

        this.semaphore.acquire();
        try {
            for(Message m : receivedMessages) {
                if(m.getValue() == 0 && m.getRound() == round && m instanceof NotificationMessage) {
                    count++;
                }
            }
        } finally {
            this.semaphore.release();
        }

        return count;
    }
    
    public int countOneNotificationMessages() throws InterruptedException {
        int count = 0;

        this.semaphore.acquire();
        try {
            for(Message m : receivedMessages) {
                if(m.getValue() == 1 && m.getRound() == round && m instanceof NotificationMessage) {
                    count++;
                }
            }
        } finally {
            this.semaphore.release();
        }

        return count;
    }

    public int countZeroProposalMessages() throws InterruptedException {
        int count = 0;

        this.semaphore.acquire();
        try {
            for(Message m : receivedMessages) {
                if(m.getValue() == 0 && m.getRound() == round && m instanceof ProposalMessage) {
                    count++;
                }
            }
        } finally {
            this.semaphore.release();
        }

        return count;
    }

    public int countOneProposalMessages() throws InterruptedException {
        int count = 0;

        this.semaphore.acquire();
        try {
            for(Message m : receivedMessages) {
                if(m.getValue() == 1 && m.getRound() == round && m instanceof ProposalMessage) {
                    count++;
                }
            }
        } finally {
            this.semaphore.release();
        }


        return count;
    }
    
    public int countNotificationMessages() throws InterruptedException {
        int count = 0;

        this.semaphore.acquire();
        try {
            for(Message m : receivedMessages) {
                if(m.getRound() == round && m instanceof NotificationMessage) {
                    count++;
                }
            }

        } finally {
            this.semaphore.release();
        }

        return count;
    }

    public int countProposalMessages() throws InterruptedException {
        int count = 0;

        this.semaphore.acquire();
        try {
            for (Message m : receivedMessages) {
                if (m.getRound() == round && m instanceof ProposalMessage) {
                    count++;
                }
            }
        } finally {
            this.semaphore.release();
        }

        return count;
    }

    public void broadcast(Message message) throws RemoteException {
        for(Interface p : listOfAllProcesses) {
//                System.out.println("I'm Process " + id + " and I'm trying to acquire the lock of Process " +
//                        p.getId());
                p.getSemaphore();
                //System.out.println("I'm Process " + id + " and I have acquired the lock of Process " + p.getId());
                try {
                    p.receive(message);
                } finally {
                    p.getSemaphore().release();
//                    System.out.println("I'm Process " + id + " and I released the semaphore of Process " +
//                            p.getId());
                }
        }
    }

    public void receive(Message message) throws RemoteException {
        this.receivedMessages.add(message);
    }

    public void setListOfAllProcesses(Interface[] processesArray) throws RemoteException {
        this.listOfAllProcesses = processesArray;
    }

    public void run() throws RemoteException, InterruptedException {
        System.out.println("1BEGIN " + getString());
        int f = listOfAllProcesses.length/5;

        // If faulty: pick 0 or 1 to either:
        // 0: do not broadcast
        // 1: broadcast wrong value
        // -1: not faulty
        int whatToDo = -1;
        if(faulty) {
            whatToDo = new Random().nextInt(2);
        }

        // do forever
        while(true) {
            System.out.println("I'm Process " + id + " and I am in round " + this.round + " Value: " + this.value + " Faulty: " + this.faulty + ", " + whatToDo);
            Thread.sleep(200);
            // broadcast(N; r, v)
            if(!faulty) {
                broadcast(new NotificationMessage(round, value));
            } else if(faulty && whatToDo == 1) {
                if(value == 1) {
                    broadcast(new NotificationMessage(round, 0));
                } else if(value == 0) {
                    broadcast(new NotificationMessage(round, 1));
                }
            }

            // await n − f messages of the form (N;r,*)
            while(countNotificationMessages() < listOfAllProcesses.length - f) {
                Thread.sleep(1000);
                //System.out.println("I'm Process " + id + " and I am waiting for this");
//                System.out.println("I'm Process " + id + " and I have " + this.receivedMessages.size() +
//                        " messages");
            }

            // if (> (n + f)/2 messages (N;r,w) received with w=0 or 1)
            if(countZeroNotificationMessages() > (listOfAllProcesses.length + f) / 2 && !faulty) {
                broadcast(new ProposalMessage(round, 0));
            } else if(countOneNotificationMessages() > (listOfAllProcesses.length + f) / 2 && !faulty) {
                broadcast(new ProposalMessage(round, 1));
            } else if(countZeroNotificationMessages() > (listOfAllProcesses.length + f) / 2 && faulty && whatToDo == 1) {
                broadcast(new ProposalMessage(round, 1));
            } else if(countOneNotificationMessages() > (listOfAllProcesses.length + f) / 2 && faulty && whatToDo == 1) {
                broadcast(new ProposalMessage(round, 0));
            } else if(!faulty) {
                broadcast(new ProposalMessage(round, -1));
            } else if(faulty && whatToDo == 1) {
                broadcast(new ProposalMessage(round, new Random().nextInt(2)));
            }
            //System.out.println("I'm Process " + id + " and I have this2");
            
            if(decided) {
                //break;
                System.out.println("1END " + getString());
                Thread.sleep(3000);
                break;
            } else {
                // else await n − f messages of the form (P,r,*)
                while(countProposalMessages() < listOfAllProcesses.length - f) {
                    Thread.sleep(1000);
                }

                // if (> f messages (P;r,w) received with w=0 or 1)
                int w = Integer.MIN_VALUE;
                if(countZeroProposalMessages() > f) {
                    value = 0;
                    System.out.println("Process " + id + " adopts value " + value);
                    System.out.println("----- Process " + id + " has " + countZeroProposalMessages() + " 0 P messages -----");
                    // if (> 3f messages (P;r,w))
                    if (countZeroProposalMessages() > 3*f) {
                        decided = true;
                    }
                } else if (countOneProposalMessages() > f) {
                    value = 1;
                    System.out.println("Process " + id + " adopts value " + value);
                    System.out.println("----- Process " + id + " has " + countOneProposalMessages() + " 1 P messages -----");
                    // if (> 3f messages (P;r,w))
                    if (countOneProposalMessages() > 3*f) {
                        decided = true;
                    }
                } else {
                    value = new Random().nextInt(2);
                    System.out.println("Process " + id + " adopts random value " + value);
                }
            }

            round++;
        }
    }

    public String getString() throws RemoteException {
        try {
            return "Process " + this.getId() +
                    ", decided: " + this.isDecided() +
                    ", round: " + this.getRound() +
                    ", value: " + this.getValue();
        } catch (RemoteException e) {
            return e.getMessage();
        }
    }
}
