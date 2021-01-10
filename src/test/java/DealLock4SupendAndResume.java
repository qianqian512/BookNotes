import java.io.IOException;

/**
 * suspend和resume不是不能用，只不过是非线程安全的而已
 * @author hudaming
 */
public class DealLock4SupendAndResume {

	public static void main(String[] args) throws IOException {

		Object lock = new Object();

		Thread t1 = new Thread(() -> {
			synchronized (lock) {
				System.out.println("wait message...");
				Thread.currentThread().suspend();
				System.out.println("received message");
			}
		});

		t1.start();

		new Thread(() -> {
			synchronized (lock) {
				System.out.println("enter");
				t1.resume();
				System.out.println("sended message");
			}
		}).start();

		System.in.read();
	}
}
