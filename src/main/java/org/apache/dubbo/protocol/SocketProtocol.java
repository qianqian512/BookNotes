package org.apache.dubbo.protocol;

import static org.apache.dubbo.common.constants.CommonConstants.PATH_KEY;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.Constants;
import org.apache.dubbo.rpc.AppResponse;
import org.apache.dubbo.rpc.AsyncRpcResult;
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
 * Transport用Socket同步短连接实现，忽略Exchange层，JDK序列化，为了省事越过了Request和Response，直接面向Invocation和Result传输
 * @author hudaming
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
	public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
		// 这里的地址应该从url里取
		try {
			return new AbstractInvoker<T>(type, url) {
				@Override
				protected Result doInvoke(Invocation invocation) throws Throwable {
					Socket socket = new Socket(url.getParameter(Constants.BIND_IP_KEY, "localhost"), url.getParameter(Constants.BIND_PORT_KEY, getDefaultPort()));
					ObjectOutputStream oos = null;
					ObjectInputStream ois = null;
					try {
						oos = new ObjectOutputStream(socket.getOutputStream());
						RpcInvocation rpcInvocation = new RpcInvocation();
						rpcInvocation.setMethodName(invocation.getMethodName());
						rpcInvocation.setArguments(invocation.getArguments());
						rpcInvocation.setObjectAttachment(PATH_KEY, type.getName());
						rpcInvocation.setObjectAttachment("param_types", invocation.getParameterTypes());
						oos.writeObject(rpcInvocation);
						oos.flush();
						ois = new ObjectInputStream(socket.getInputStream());
						AppResponse appResp = (AppResponse) ois.readObject();
						return new AsyncRpcResult(CompletableFuture.completedFuture(appResp), invocation);
					} finally {
						if (ois != null) {
							ois.close();
						}
						if (oos != null) {
							oos.close();
						}
						socket.close();
					}
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
					        Invoker<?> invoker = exporter.getInvoker();
					        Result result = invoker.invoke(invocation);
							ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
							AppResponse appResponse = new AppResponse(result.getValue());
							appResponse.setException(result.getException());
							oos.writeObject(appResponse);
							
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
