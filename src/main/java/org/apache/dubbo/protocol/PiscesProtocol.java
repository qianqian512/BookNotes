package org.apache.dubbo.protocol;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.ExchangeChannel;
import org.apache.dubbo.remoting.exchange.ExchangeHandler;
import org.apache.dubbo.remoting.exchange.ExchangeServer;
import org.apache.dubbo.remoting.exchange.Exchangers;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.protocol.AbstractExporter;

public class PiscesProtocol implements Protocol {

	private final Map<String, Exporter<?>> ExporterMap = new ConcurrentHashMap<String, Exporter<?>>(); 
	private final Map<String, List<Invoker<?>>> InvokerMap = new ConcurrentHashMap<>();
	private final Object initServerLock = new Object();
	private ExchangeServer ExchangeServer;
	
	private static final ExchangeHandler exchangeHandler = new ExchangeHandler() {
		
		@Override
		public String telnet(Channel channel, String message) throws RemotingException {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void sent(Channel channel, Object message) throws RemotingException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void received(Channel channel, Object message) throws RemotingException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void disconnected(Channel channel) throws RemotingException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void connected(Channel channel) throws RemotingException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void caught(Channel channel, Throwable exception) throws RemotingException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public CompletableFuture<Object> reply(ExchangeChannel channel, Object request) throws RemotingException {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	@Override
	public int getDefaultPort() {
		return 20881;
	}

	@Override
	public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {

		// 判断是否初始化了Server
		try {
			if (ExchangeServer == null) {
				synchronized (initServerLock) {
					if (ExchangeServer == null) {
						ExchangeServer = Exchangers.bind(invoker.getUrl(), exchangeHandler);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 确保上面已经初始化了Server，这里只要将Export记录即可
		Exporter<T> exporter = new AbstractExporter<T>(invoker) {};
		ExporterMap.put(invoker.getInterface().getName(), exporter);
		
		return exporter;
	}

	@Override
	public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroy() {
		// TODO closed servers
		
		// TODO closed clients
	}

}
