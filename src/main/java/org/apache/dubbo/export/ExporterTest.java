package org.apache.dubbo.export;

import java.io.IOException;
import java.util.Set;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.bytecode.Wrapper;
import org.apache.dubbo.common.context.FrameworkExt;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.remoting.Constants;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.Exchanger;
import org.apache.dubbo.remoting.exchange.Exchangers;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.ServiceRepository;
import org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol;
import org.apache.dubbo.rpc.proxy.AbstractProxyInvoker;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class ExporterTest {
	
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
	 * 使用ServiceConfig发布Dubbo服务
	 * @throws IOException
	 */
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

	/**
	 * 拆解ServiceConfig，通过Exporter发布服务(不含注册中心)
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
		
		// 这里拿到的是DubboInvoker实例
		Invoker<UserService> invoker = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension().getInvoker(userServiceRef, UserService.class, exportUrl);
		
		// 这里拿到的是DubboProtocol实例
		ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension().export(invoker);
		
		System.out.println("service exported!");
		
		System.in.read();
	}
	
	/**
	 * 将Invoker展开
	 * @throws IOException
	 */
	@Test
	public void testExport2() throws IOException {

		// 初始化dubbo依赖的环境配置
		initDubboEnv();
		
		URL exportUrl = new URL("dubbo", "localhost", 20880, "org.apache.dubbo.export.UserService");
		
		UserService userServiceRef = new UserServiceImpl();
		
		ServiceRepository resp = (ServiceRepository) ExtensionLoader.getExtensionLoader(FrameworkExt.class).getExtension("repository");
		// 这里传入的Instance是为telnet命令调用，下面invoker传入的Instance是为Dubbo协议调用
		resp.registerProvider(UserService.class.getName(), userServiceRef, resp.registerService(UserService.class), null, null);
		
		// 这里拿到的是DubboInvoker实例
		Invoker<UserService> invoker = new AbstractProxyInvoker<UserService>(userServiceRef, UserService.class, exportUrl) {
            @Override
            protected Object doInvoke(UserService proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable {
            	final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : UserService.class);
                return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
            }
        };
		
        // export内部主要其实用到的是openServer
        DubboProtocol protocol = new DubboProtocol();
        protocol.export(invoker);
		
		System.out.println("service exported!");
		
		System.in.read();
	}

	
	/**
	 * 将DubboProtocol展开，删除了一些细节部分，仅仅保留主题方便阅读学习
	 * @throws IOException
	 * @throws RemotingException 
	 */
	@Test
	public void testExport3() throws IOException, RemotingException {
		
		// 初始化dubbo依赖的环境配置
		initDubboEnv();
		
		URL exportUrl = new URL("dubbo", "localhost", 20880, "org.apache.dubbo.export.UserService");
		
		UserService userServiceRef = new UserServiceImpl();
		
		ServiceRepository resp = (ServiceRepository) ExtensionLoader.getExtensionLoader(FrameworkExt.class).getExtension("repository");
		// 这里传入的Instance是为telnet命令调用，下面invoker传入的Instance是为Dubbo协议调用
		resp.registerProvider(UserService.class.getName(), userServiceRef, resp.registerService(UserService.class), null, null);
		
		// 这里拿到的是DubboInvoker实例
		Invoker<UserService> invoker = new AbstractProxyInvoker<UserService>(userServiceRef, UserService.class, exportUrl) {
			@Override
			protected Object doInvoke(UserService proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable {
				final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : UserService.class);
				return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
			}
		};
		
		// 其实Dubbo内置只有一个Exchange实现，因此这里的exchangerType永远都等于header，即HeaderExchanger
		String exchangerType = exportUrl.getParameter(Constants.EXCHANGER_KEY, Constants.DEFAULT_EXCHANGER);
		Exchanger exchanger = ExtensionLoader.getExtensionLoader(Exchanger.class).getExtension(exchangerType);
		exchanger.bind(exportUrl, new SimpleExchangeHandlerAdapter(invoker));

		System.out.println("service exported!");
		
		System.in.read();
	}
}
