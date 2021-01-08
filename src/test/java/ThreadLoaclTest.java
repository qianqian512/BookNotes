
public class ThreadLoaclTest {

	public static void main(String[] args) {
		
		System.out.println("12345".substring(0, 4));
		ThreadLocal<Integer> tl1 = new ThreadLocal<Integer>();
		ThreadLocal<Integer> tl2 = new ThreadLocal<Integer>();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				ThreadLocal<Integer> tl3 = new ThreadLocal<Integer>();
				tl3.set(3);
				System.out.println(tl3.get());
			}
		}).start();

		tl1.set(1);
		tl2.set(2);
		
		System.out.println(tl1.get());
		System.out.println(tl2.get());
	}
}
