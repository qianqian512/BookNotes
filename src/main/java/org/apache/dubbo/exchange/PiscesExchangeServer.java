package org.apache.dubbo.exchange;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.dubbo.common.Parameters;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.RemotingServer;
import org.apache.dubbo.remoting.exchange.ExchangeChannel;
import org.apache.dubbo.remoting.exchange.ExchangeServer;

public class PiscesExchangeServer implements ExchangeServer {

    private final RemotingServer server;
    
    public PiscesExchangeServer(RemotingServer server) {
    	this.server = server;
    }

	@Override
	public boolean isBound() {
		return this.server.isBound();
	}

	@Override
	public Collection<Channel> getChannels() {
		return server.getChannels();
	}

	@Override
	public void reset(Parameters parameters) {
		server.reset(parameters);
	}

	@Override
	public URL getUrl() {
		return server.getUrl();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return server.getLocalAddress();
	}

	@Override
	public void send(Object message) throws RemotingException {
		this.server.send(message);
	}

	@Override
	public void send(Object message, boolean sent) throws RemotingException {
		this.server.send(message, sent);
	}

	@Override
	public void close() {
		this.server.close();
	}

	@Override
	public void close(int timeout) {
		this.server.close(timeout);
	}

	@Override
	public void startClose() {
		this.server.startClose();
	}

	@Override
	public boolean isClosed() {
		return server.isClosed();
	}

	@Override
	public void reset(URL url) {
		server.reset(url);
	}

	@Override
	public Collection<ExchangeChannel> getExchangeChannels() {
		List<ExchangeChannel> channelList = new ArrayList<>();
		Collection<Channel> channels = server.getChannels();
		if (channels == null || channels.isEmpty()) {
			return Collections.emptyList();
		}
		channels.forEach(channel -> {
			channelList.add(new PiscesExchangeChannel(channel));
		});
		
		return channelList;
	}

	@Override
	public ExchangeChannel getExchangeChannel(InetSocketAddress remoteAddress) {
		return new PiscesExchangeChannel(server.getChannel(remoteAddress));
	}

	@Override
	public Channel getChannel(InetSocketAddress remoteAddress) {
		return server.getChannel(remoteAddress);
	}

	@Override
	public ChannelHandler getChannelHandler() {
		return server.getChannelHandler();
	}
}
