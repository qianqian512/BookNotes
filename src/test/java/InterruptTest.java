import java.io.IOException;

public class InterruptTest {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Thread t1 = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
			}
			System.out.println("t1 exit");
		});
		t1.start();
		
		new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			t1.interrupt();
			System.out.println("t2 interrupt t1");
		}).start();
	}
}
