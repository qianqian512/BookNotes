package org.apache.dubbo.protocol;

import static org.apache.dubbo.common.constants.CommonConstants.PATH_KEY;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.Constants;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.protocol.AbstractExporter;
import org.apache.dubbo.rpc.protocol.AbstractInvoker;

/**
 * TODO Transport用Socket实现，忽略Exchange层，支持序列化，header还是遵循Dubbo报文格式
 * @author hudaming
 *
 */
public class SocketProtocol implements Protocol {

	private volatile boolean isOpen = false;
	private final CountDownLatch startLatch = new CountDownLatch(1);
	
	@Override
	public int getDefaultPort() {
		return 20880;
	}
	
	private final Map<String, Exporter<?>> ExporterMap = new ConcurrentHashMap<String, Exporter<?>>(); 

	@Override
	public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		
		if (!isOpen) {
			new Thread(new SocketServerStarter(invoker.getUrl().getParameter(Constants.BIND_PORT_KEY, getDefaultPort()))).start();
			try {
				startLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isOpen = true;
		}
		
		Exporter<T> exporter = new AbstractExporter<T>(invoker) {};
		ExporterMap.put(invoker.getInterface().getName(), exporter);
		
		return exporter;
	}

	@Override
	@SuppressWarnings({ "resource" })
	public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
		// 这里的地址应该从url里取
		Socket socket;
		try {
			socket = new Socket("localhost", 20880);
			return new AbstractInvoker<T>(type, url) {
				@Override
				protected Result doInvoke(Invocation invocation) throws Throwable {
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					RpcInvocation rpcInvocation = new RpcInvocation();
					rpcInvocation.setMethodName(invocation.getMethodName());
					rpcInvocation.setArguments(invocation.getArguments());
					rpcInvocation.setObjectAttachment(PATH_KEY, type.getName());
					rpcInvocation.setObjectAttachment("param_types", invocation.getParameterTypes());
					oos.writeObject(rpcInvocation);
					oos.flush();
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					return (Result) ois.readObject();
				}
			};
		} catch (Exception e) {
			throw new RuntimeException("refer error, error=" + e.getMessage());
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	class SocketServerStarter implements Runnable {
		
		private int port;
		
		public SocketServerStarter(int port) {
			this.port = port;
		}

		@Override
		public void run() {
			try {
				ServerSocket server = new ServerSocket(port);
				ExecutorService ExecutorService = Executors.newFixedThreadPool(16);
				startLatch.countDown();
				while (true) {
					Socket socket = server.accept();
					ExecutorService.execute(() -> {
						try {
							ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
							RpcInvocation invocation = (RpcInvocation) ois.readObject();
							String path = (String) invocation.getObjectAttachments().get(PATH_KEY);
							Exporter<?> exporter = ExporterMap.get(path);
							invocation.setParameterTypes((Class<?>[]) invocation.getObjectAttachment("param_types"));

					        if (exporter == null) {
					            throw new RuntimeException("Not found exported service: " + path + " in " + ExporterMap.keySet());
					        }
					        
					        // 抛开了Request和Response，直接面向invocation和result
					        Result result = exporter.getInvoker().invoke(invocation);
							ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
							oos.writeObject(result);
							
							// 连接关闭
							oos.close();
							ois.close();
							socket.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
