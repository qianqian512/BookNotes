package org.apache.dubbo.export;

import java.io.IOException;
import java.util.Set;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.context.FrameworkExt;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.ServiceRepository;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class ExporterTest {
	
	@Test
	public void testExport0() throws IOException {
		ServiceConfig<UserService> serviceConfig = new ServiceConfig<UserService>();
		serviceConfig.setApplication(new ApplicationConfig("huming-test"));
		serviceConfig.setRegistry(new RegistryConfig("N/A"));
		serviceConfig.setInterface(UserService.class);
		serviceConfig.setRef(new UserServiceImpl());
		serviceConfig.setProtocol(new ProtocolConfig("dubbo", 20880));
		serviceConfig.export();
		System.out.println("export");
		System.in.read();
	}
	
	@Test
	public void testReference0() {
		ReferenceConfig<UserService> referneceConfig = new ReferenceConfig<UserService>();
		referneceConfig.setApplication(new ApplicationConfig("humking-f"));
		referneceConfig.setProtocol("dubbo");
		referneceConfig.setUrl("dubbo://localhost:20880");
		referneceConfig.setInterface(UserService.class);
		System.out.println(referneceConfig.get().sayHello("333"));
	}
	
	public void initDubboEnv() {
		// init config
        ConfigManager configManager = ApplicationModel.getConfigManager();
        configManager.setApplication(new ApplicationConfig("huming-test"));

        // init dubbo bootstrap
		DubboBootstrap bootstrap = DubboBootstrap.getInstance();
		bootstrap.initialize();

		// init service repository
        Set<FrameworkExt> exts = ExtensionLoader.getExtensionLoader(FrameworkExt.class).getSupportedExtensionInstances();
        for (FrameworkExt ext : exts) {
            ext.initialize();
        }
	}

	/**
	 * 通过Exporter发布服务
	 * @throws IOException 
	 */
	@Test
	public void testExport1() throws IOException {
		
		// 初始化dubbo依赖的环境配置
		initDubboEnv();
		
		URL exportUrl = new URL("dubbo", "localhost", 20880, "org.apache.dubbo.export.UserService");
		
		UserService userServiceRef = new UserServiceImpl();
		
		ServiceRepository resp = (ServiceRepository) ExtensionLoader.getExtensionLoader(FrameworkExt.class).getExtension("repository");
		// 这里传入的Instance是为telnet命令调用，下面invoker传入的Instance是为Dubbo协议调用
		resp.registerProvider(UserService.class.getName(), userServiceRef, resp.registerService(UserService.class), null, null);
		
		Invoker<UserService> invoker = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension().getInvoker(userServiceRef, UserService.class, exportUrl);
		
		ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension().export(invoker);
		
		System.out.println("service exported!");
		
		System.in.read();
	}
}
