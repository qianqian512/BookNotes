package org.apache.dubbo.proxy;

import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.user.UserService;
import org.apache.dubbo.user.UserServiceImpl;
import org.junit.Test;

public class SpayProxyTest {

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
		serviceConfig.setProtocol(new ProtocolConfig("dubbo", 20880));
		serviceConfig.export();
		System.out.println("export");
		System.in.read();
	}
}
