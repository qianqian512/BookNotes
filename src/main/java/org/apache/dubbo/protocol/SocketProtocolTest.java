package org.apache.dubbo.protocol;

import java.io.IOException;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.context.FrameworkExt;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.export.ExporterTest;
import org.apache.dubbo.remoting.exchange.Request;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.model.ServiceRepository;
import org.apache.dubbo.user.UserService;
import org.apache.dubbo.user.UserServiceImpl;
import org.junit.Test;

public class SocketProtocolTest {

	@Test
	public void testExport() throws IOException {

		// 初始化dubbo依赖的环境配置
		ExporterTest.initDubboEnv();
		
		URL exportUrl = new URL("socket", "localhost", 20880, "org.apache.dubbo.export.UserService");
		exportUrl = exportUrl.addParameter("proxy", "spay_proxy");
		
		UserService userServiceRef = new UserServiceImpl();
		
		ServiceRepository resp = (ServiceRepository) ExtensionLoader.getExtensionLoader(FrameworkExt.class).getExtension("repository");
		// 这里传入的Instance是为telnet命令调用，下面invoker传入的Instance是为Dubbo协议调用
		resp.registerProvider(UserService.class.getName(), userServiceRef, resp.registerService(UserService.class), null, null);
		
		// 这里拿到的是DubboInvoker实例
		Invoker<UserService> invoker = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension().getInvoker(userServiceRef, UserService.class, exportUrl);
		
		// 这里拿到的是DubboProtocol实例
		ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension().export(invoker);
		
		System.in.read();
	}

	@Test
	public void testReference0() {
		new Request();
		ReferenceConfig<UserService> referneceConfig = new ReferenceConfig<UserService>();
		referneceConfig.setApplication(new ApplicationConfig("humking-f"));
		referneceConfig.setProtocol("socket");
		referneceConfig.setUrl("socket://localhost:20880?timeout=1000000");
		referneceConfig.setInterface(UserService.class);
		System.out.println(referneceConfig.get().sayHello("333"));
	}
}
