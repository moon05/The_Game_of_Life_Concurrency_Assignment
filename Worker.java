import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class Worker extends Thread {
    private final LifeBoard lb;
    private final Coordinator c;
    private final UI u;
    private int s;
    private int e;
    private CyclicBarrier barrier;
    private long nt;

    // The run() method of a Java Thread is never invoked directly by
    // user code.  Rather, it is called by the Java runtime when user
    // code calls start().
    //
    // The run() method of a worker thread *must* begin by calling
    // c.register() and end by calling c.unregister().  These allow the
    // user interface (via the Coordinator) to pause and terminate
    // workers.  Note how the worker is set up to catch KilledException.
    // In the process of unwinding back to here we'll cleanly and
    // automatically release any monitor locks.  If you create new kinds
    // of workers (as part of a parallel player), make sure they call
    // c.register() and c.unregister() properly.
    //
    public void run() {
        try {
            c.register();
            try {	
            	while(true){
//            		System.out.println("Thread name is: " + Thread.currentThread().getName());
            		if (nt == 1) {
            			lb.doPartGeneration(s,e);
            			lb.mergeGeneration(s, e);
            		}
            		else {
	                    lb.doPartGeneration(s,e);
	                    barrier.await();
	                    lb.mergeGeneration(s, e);
            		}
            	}
            } catch(Coordinator.KilledException | InterruptedException | BrokenBarrierException e) {}
        } finally {
            c.unregister();
        }
    }

    // Constructor
    //
    public Worker(LifeBoard LB, Coordinator C, UI U, int start, int end, long nt) {
        lb = LB;
        c = C;
        u = U;
        s = start;
        e = end;
        this.nt = nt;
    }
    public Worker(LifeBoard LB, Coordinator C, UI U, int start, int end, CyclicBarrier barrier, long nt) {
        lb = LB;
        c = C;
        u = U;
        s = start;
        e = end;
        this.barrier = barrier;
        this.nt = nt;
    }

}