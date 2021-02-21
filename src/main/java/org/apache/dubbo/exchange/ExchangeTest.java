package org.apache.dubbo.exchange;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.user.UserService;
import org.apache.dubbo.user.UserServiceImpl;
import org.junit.Test;

public class ExchangeTest {

	@Test
	public void testExport() throws Exception {
		// export
		ServiceConfig<UserService> serviceConfig = new ServiceConfig<UserService>();
		serviceConfig.setApplication(new ApplicationConfig("huming-test"));
		serviceConfig.setRegistry(new RegistryConfig("N/A"));
		serviceConfig.setInterface(UserService.class);
		serviceConfig.setRef(new UserServiceImpl());
		serviceConfig.setProtocol(new ProtocolConfig("pisces", 20880));
		serviceConfig.export();
		System.out.println("export");
		System.in.read();
	}
}
