import java.util.concurrent.Semaphore;

public abstract class Lock {
	public final static Semaphore SEMAPHORE = new Semaphore(1);
	
	public static boolean isBusy() {
		return SEMAPHORE.availablePermits() == 0;
	}
	
	public static int getQueueLength() {
		return SEMAPHORE.getQueueLength();
	}
}
