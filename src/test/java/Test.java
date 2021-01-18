import java.io.IOException;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import sun.misc.Unsafe;

public class Test {
	private static final Unsafe UnSafe;
	static {
		try {
			final PrivilegedExceptionAction<Unsafe> action = new PrivilegedExceptionAction<Unsafe>() {
				public Unsafe run() throws Exception {
					Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
					theUnsafe.setAccessible(true);
					return (Unsafe) theUnsafe.get(null);
				}
			};
			UnSafe = AccessController.doPrivileged(action);
		} catch (Exception e) {
			throw new RuntimeException("Unable to load unsafe", e);
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		
		final User<Name> user = new User<>();
		user.arr = new Name[] {new Name("1"), new Name("2"), new Name("3"), new Name("4")};
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (user.arr[1].value.equals("2")) {
					// never break
				}
				System.out.println("i see flag");				
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (true) {
					user.arr[1].value = "1234";
				}
			}
		}).start();
		
		System.in.read();
	}
}

class Name {
	
	public Name(String name) {
		this.value = name;
	}
	
	public String value = "";
}