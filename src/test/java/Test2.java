import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.junit.Test;

import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;

public class Test2 {

	@Test
	public void test1() throws InterruptedException, ExecutionException {
		NioEventLoop eventloop = (NioEventLoop) new NioEventLoopGroup(1).next();
		FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				Thread.sleep(3000);
				return "huming";
			}
		});
		eventloop.execute(task);
		System.out.println(task.get());
	}
}
