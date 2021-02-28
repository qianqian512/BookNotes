import org.apache.dubbo.common.bytecode.Proxy;
import org.apache.dubbo.user.UserService;
import org.junit.Test;

public class HuMingTest {

	@Test
	public void test1() {
		Proxy proxy = Proxy.getProxy(UserService.class);
		System.out.println(proxy.newInstance());
	}
}
