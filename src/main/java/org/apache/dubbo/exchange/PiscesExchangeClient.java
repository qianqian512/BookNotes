package org.apache.dubbo.exchange;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.dubbo.common.Parameters;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.Client;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.ExchangeChannel;
import org.apache.dubbo.remoting.exchange.ExchangeClient;
import org.apache.dubbo.remoting.exchange.ExchangeHandler;

public class PiscesExchangeClient implements ExchangeClient {
	
	private Client client;
	private ExchangeChannel channel;
	
	public PiscesExchangeClient(Client client) {
		this.client = client;
		this.channel = new PiscesExchangeChannel(client);
	}

	@Override
	public void reconnect() throws RemotingException {
		client.reconnect();
	}

	@Override
	public void reset(Parameters parameters) {
		client.reset(parameters);
	}

	@Override
	public URL getUrl() {
		return client.getUrl();
	}

	@Override
	public ChannelHandler getChannelHandler() {
		return client.getChannelHandler();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return client.getLocalAddress();
	}

	@Override
	public void send(Object message) throws RemotingException {
		client.send(message);
	}

	@Override
	public void send(Object message, boolean sent) throws RemotingException {
		client.send(message, sent);
	}

	@Override
	public void close() {
		client.close();
	}

	@Override
	public void close(int timeout) {
		client.close(timeout);
	}

	@Override
	public void startClose() {
		client.startClose();
	}

	@Override
	public boolean isClosed() {
		return client.isClosed();
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return client.getRemoteAddress();
	}

	@Override
	public boolean isConnected() {
		return client.isConnected();
	}

	@Override
	public boolean hasAttribute(String key) {
		return client.hasAttribute(key);
	}

	@Override
	public Object getAttribute(String key) {
		return client.getAttribute(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		client.setAttribute(key, value);
	}

	@Override
	public void removeAttribute(String key) {
		client.removeAttribute(key);
	}

	@Override
	public void reset(URL url) {
		client.reset(url);
	}

	@Override
	public CompletableFuture<Object> request(Object request) throws RemotingException {
		return channel.request(request);
	}

	@Override
	public CompletableFuture<Object> request(Object request, int timeout) throws RemotingException {
		return channel.request(request, timeout);
	}

	@Override
	public CompletableFuture<Object> request(Object request, ExecutorService executor) throws RemotingException {
		return channel.request(request, executor);
	}

	@Override
	public CompletableFuture<Object> request(Object request, int timeout, ExecutorService executor) throws RemotingException {
		return channel.request(request, timeout, executor);
	}

	@Override
	public ExchangeHandler getExchangeHandler() {
		return channel.getExchangeHandler();
	}

}
