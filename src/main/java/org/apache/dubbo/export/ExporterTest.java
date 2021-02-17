package org.apache.dubbo.export;

import static org.apache.dubbo.common.constants.CommonConstants.ANYHOST_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.ANYHOST_VALUE;
import static org.apache.dubbo.common.constants.CommonConstants.IO_THREADS_KEY;
import static org.apache.dubbo.remoting.Constants.CODEC_KEY;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.URLBuilder;
import org.apache.dubbo.common.bytecode.Wrapper;
import org.apache.dubbo.common.context.FrameworkExt;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.Codec2;
import org.apache.dubbo.remoting.Constants;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.Transporters;
import org.apache.dubbo.remoting.exchange.ExchangeHandler;
import org.apache.dubbo.remoting.exchange.Exchanger;
import org.apache.dubbo.remoting.exchange.Request;
import org.apache.dubbo.remoting.exchange.support.header.HeaderExchangeHandler;
import org.apache.dubbo.remoting.transport.DecodeHandler;
import org.apache.dubbo.remoting.transport.netty4.NettyCodecAdapter;
import org.apache.dubbo.remoting.transport.netty4.NettyEventLoopFactory;
import org.apache.dubbo.remoting.transport.netty4.NettyServerHandler;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.model.ServiceRepository;
import org.apache.dubbo.rpc.protocol.dubbo.DubboCodec;
import org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol;
import org.apache.dubbo.rpc.proxy.AbstractProxyInvoker;
import org.apache.dubbo.user.UserService;
import org.apache.dubbo.user.UserServiceImpl;
import org.junit.Test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;

@SuppressWarnings("deprecation")
public class ExporterTest {
	
	@Test
	public void testReference0() {
		new Request();
		ReferenceConfig<UserService> referneceConfig = new ReferenceConfig<UserService>();
		referneceConfig.setApplication(new ApplicationConfig("humking-f"));
		referneceConfig.setProtocol("dubbo");
		referneceConfig.setUrl("dubbo://localhost:20880?timeout=1000000");
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
		exportUrl = exportUrl.addParameter("proxy", "spay_proxy");
		
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
		
		// 代码摘自JavassistRpcProxyFactory
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
		exportUrl = URLBuilder.from(exportUrl).addParameter(CODEC_KEY, DubboCodec.NAME).build(); // 这行代码就是从DubboProtocol摘出来的，规定了编解码要用DubboCodec
		Exchanger exchanger = ExtensionLoader.getExtensionLoader(Exchanger.class).getExtension(exchangerType);
		exchanger.bind(exportUrl, new SimpleExchangeHandlerAdapter(invoker));

		System.out.println("service exported!");
		
		System.in.read();
	}
	
	/**
	 * 将Exchanger展开，了解HeaderExchanger
	 */
	@Test
	public void testExport4() throws Exception {

		// 初始化dubbo依赖的环境配置
		initDubboEnv();
		
		URL exportUrl = new URL("dubbo", "localhost", 20880, "org.apache.dubbo.export.UserService");
		
		UserService userServiceRef = new UserServiceImpl();
		
		ServiceRepository resp = (ServiceRepository) ExtensionLoader.getExtensionLoader(FrameworkExt.class).getExtension("repository");
		resp.registerProvider(UserService.class.getName(), userServiceRef, resp.registerService(UserService.class), null, null);
		Invoker<UserService> invoker = new AbstractProxyInvoker<UserService>(userServiceRef, UserService.class, exportUrl) {
			@Override
			protected Object doInvoke(UserService proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable {
				final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : UserService.class);
				return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
			}
		};

		/**
		 * 在receive请求后，3个Handler的处理顺序为：DecodeHandler->HeaderExchangeHandler->SimpleExchangeHandlerAdapter
		 * 意图很明显，先解码->再处理网络层，例如telnet命令行，事件等->最后处理Dubbo的业务分发逻辑
		 */
		ChannelHandler exchangeHandler = new SimpleExchangeHandlerAdapter(invoker); // 3
		ChannelHandler channelHandler = new HeaderExchangeHandler((ExchangeHandler) exchangeHandler); // 2
		ChannelHandler decodeHandler = new DecodeHandler(channelHandler); // 1

		// 摘自DubboProtocol.createServer代码
		exportUrl = URLBuilder.from(exportUrl).addParameter(CODEC_KEY, DubboCodec.NAME).build();
		
		// 代码剖析到这里，已经开始发现Netty的影子了
		Transporters.bind(exportUrl, decodeHandler); 

		System.out.println("service exported!");
		
		System.in.read();
	}

	/**
	 * 将Transporter.bind剥开，用Netty发布服务
	 */
	@Test
	public void testExport5() throws Exception {

		// 初始化dubbo依赖的环境配置
		initDubboEnv();
		
		URL exportUrl = new URL("dubbo", "localhost", 20880, "org.apache.dubbo.export.UserService");
		
		UserService userServiceRef = new UserServiceImpl();
		
		ServiceRepository resp = (ServiceRepository) ExtensionLoader.getExtensionLoader(FrameworkExt.class).getExtension("repository");
		resp.registerProvider(UserService.class.getName(), userServiceRef, resp.registerService(UserService.class), null, null);
		Invoker<UserService> invoker = new AbstractProxyInvoker<UserService>(userServiceRef, UserService.class, exportUrl) {
			@Override
			protected Object doInvoke(UserService proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable {
				final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : UserService.class);
				return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
			}
		};

		ChannelHandler exchangeHandler = new SimpleExchangeHandlerAdapter(invoker); // 3
		ChannelHandler channelHandler = new HeaderExchangeHandler((ExchangeHandler) exchangeHandler); // 2
		ChannelHandler decodeHandler = new DecodeHandler(channelHandler); // 1

		// 摘自DubboProtocol.createServer代码
		exportUrl = URLBuilder.from(exportUrl).addParameter(CODEC_KEY, DubboCodec.NAME).build();
		URL _exportUrl = exportUrl;
		
		/************* 新展开部分 *************/
		ServerBootstrap bootstrap = new ServerBootstrap();
		EventLoopGroup bossGroup = NettyEventLoopFactory.eventLoopGroup(1, "NettyServerBoss");
		EventLoopGroup workerGroup = NettyEventLoopFactory.eventLoopGroup(exportUrl.getPositiveParameter(IO_THREADS_KEY, Constants.DEFAULT_IO_THREADS), "NettyServerWorker");
        
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NettyEventLoopFactory.serverSocketChannelClass());
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				// 这里的codec的实现类是DubboCodec.java
				NettyCodecAdapter adapter = new NettyCodecAdapter(ExtensionLoader.getExtensionLoader(Codec2.class).getExtension(_exportUrl.getParameter(CODEC_KEY)), _exportUrl, decodeHandler);
				// in_pipeline: DubboCodec.decode -> DecodeHandler -> HeaderExchangeHandler ->  SimpleExchangeHandlerAdapter
				// out_pipeline: 
				ch.pipeline().addLast("decoder", adapter.getDecoder()).addLast("encoder", adapter.getEncoder())
						.addLast("handler", new NettyServerHandler(_exportUrl, decodeHandler));
			}
		});
        // bind
        ChannelFuture channelFuture = bootstrap.bind(getBindAddress(exportUrl));
        channelFuture.syncUninterruptibly();

		System.out.println("service exported!");
		
		System.in.read();
	}

	private SocketAddress getBindAddress(URL url) {
		String bindIp = url.getParameter(Constants.BIND_IP_KEY, url.getHost());
        int bindPort = url.getParameter(Constants.BIND_PORT_KEY, url.getPort());
        if (url.getParameter(ANYHOST_KEY, false) || NetUtils.isInvalidLocalHost(bindIp)) {
            bindIp = ANYHOST_VALUE;
        }
        return new InetSocketAddress(bindIp, bindPort);
	}
}
