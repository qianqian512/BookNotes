import java.io.IOException;

public class ShutdownHookTest {

	public static void main(String[] args) {
		System.out.println("test");
		
		Runtime.getRuntime().addShutdownHook(new Thread(()-> {
			System.out.println("jvm exit");
			try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} ));
	}
}
