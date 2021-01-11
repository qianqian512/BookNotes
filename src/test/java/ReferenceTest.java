import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class ReferenceTest {

	public static void main(String[] args) throws InterruptedException {
		Object obj = new Object();
		SoftReference<Object> ref = new SoftReference<Object>(obj);
		System.out.println(ref.get());
		obj = null;
		System.gc();
		System.out.println(ref.get());
	}
}
