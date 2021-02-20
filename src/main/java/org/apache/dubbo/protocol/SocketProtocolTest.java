package org.apache.dubbo.protocol;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.user.UserService;
import org.apache.dubbo.user.UserServiceImpl;
import org.junit.Test;

public class SocketProtocolTest {
	

	@Test
	public void testExport() throws Exception {
		// export
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("proxy", "spay_proxy");
		ServiceConfig<UserService> serviceConfig = new ServiceConfig<UserService>();
		serviceConfig.setApplication(new ApplicationConfig("huming-test"));
		serviceConfig.setRegistry(new RegistryConfig("N/A"));
		serviceConfig.setInterface(UserService.class);
		serviceConfig.setRef(new UserServiceImpl());
		serviceConfig.setParameters(parameters);
		serviceConfig.setProtocol(new ProtocolConfig("socket", 20880));
		serviceConfig.export();
		System.out.println("export");
		System.in.read();
	}

	@Test
	public void testReference0() {
		ReferenceConfig<UserService> referneceConfig = new ReferenceConfig<UserService>();
		referneceConfig.setApplication(new ApplicationConfig("humking-f"));
		referneceConfig.setProtocol("socket");
		referneceConfig.setUrl("socket://localhost:20880?timeout=1000000");
		referneceConfig.setInterface(UserService.class);
		System.out.println(referneceConfig.get().sayHello("333"));
		System.out.println(referneceConfig.get().sayHello("444"));
		System.out.println(referneceConfig.get().sayHello("555"));
		System.out.println(referneceConfig.get().sayHello("666"));
	}
	
	@Test
	public void test() throws Exception {
		ExtensionLoader<Protocol> extensionLoader = ExtensionLoader.getExtensionLoader(Protocol.class);
		extensionLoader.getSupportedExtensions();
		Field field = ExtensionLoader.class.getDeclaredField("cachedWrapperClasses");
		field.setAccessible(true);
		field.set(extensionLoader, null);
		System.out.println(field.get(extensionLoader));
	}
}
