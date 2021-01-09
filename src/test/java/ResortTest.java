import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用一段代码来证明存在指令重排序
 * <pre>
 *   MacOS + JDK8始终没能浮现，但大概代码应该就是这个样子吧
 * </pre>
 */
public class ResortTest {

	volatile int a = 0;
	volatile boolean flag = false;

	public void run1() {
		flag = true;
		a = 2;
	}

	public void run2() {
		if (flag && a == 0) {
			throw new RuntimeException("发生了重排序");
		}
	}

	public static void main(String[] args) throws IOException {
		ResortTest test = new ResortTest();
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.execute(() -> {
			test.run1();
		});
		executorService.execute(() -> {
			test.run2();
		});
	}
}
