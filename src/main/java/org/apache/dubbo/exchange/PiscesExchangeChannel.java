package org.apache.dubbo.exchange;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.ExchangeChannel;
import org.apache.dubbo.remoting.exchange.ExchangeHandler;

public class PiscesExchangeChannel implements ExchangeChannel {
	
	private Channel channel;
	
	public PiscesExchangeChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public URL getUrl() {
		return channel.getUrl();
	}

	@Override
	public ChannelHandler getChannelHandler() {
		return channel.getChannelHandler();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return channel.getLocalAddress();
	}

	@Override
	public void send(Object message) throws RemotingException {
		channel.send(message);
	}

	@Override
	public void send(Object message, boolean sent) throws RemotingException {
		channel.send(message, sent);
	}

	@Override
	public void close() {
		channel.close();
	}

	@Override
	public void close(int timeout) {
		channel.close(timeout);
	}

	@Override
	public void startClose() {
		channel.startClose();
	}

	@Override
	public boolean isClosed() {
		return channel.isClosed();
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return channel.getRemoteAddress();
	}

	@Override
	public boolean isConnected() {
		return channel.isConnected();
	}

	@Override
	public boolean hasAttribute(String key) {
		return channel.hasAttribute(key);
	}

	@Override
	public Object getAttribute(String key) {
		return channel.getAttribute(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		channel.setAttribute(key, value);
	}

	@Override
	public void removeAttribute(String key) {
		channel.removeAttribute(key);
	}

	@Override
	public CompletableFuture<Object> request(Object request) throws RemotingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Object> request(Object request, int timeout) throws RemotingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Object> request(Object request, ExecutorService executor) throws RemotingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Object> request(Object request, int timeout, ExecutorService executor) throws RemotingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExchangeHandler getExchangeHandler() {
		return (ExchangeHandler) channel.getChannelHandler();
	}

}
