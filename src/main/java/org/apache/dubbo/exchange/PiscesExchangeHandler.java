package org.apache.dubbo.exchange;

import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.ExchangeHandler;
import org.apache.dubbo.remoting.transport.ChannelHandlerDelegate;

public class PiscesExchangeHandler implements ChannelHandlerDelegate {
	
	private ExchangeHandler exchangeHandler;
	
	public PiscesExchangeHandler(ExchangeHandler exchangeHandler) {
		this.exchangeHandler = exchangeHandler;
	}

	@Override
	public void connected(Channel channel) throws RemotingException {
		exchangeHandler.connected(channel);
	}

	@Override
	public void disconnected(Channel channel) throws RemotingException {
		exchangeHandler.disconnected(channel);
	}

	@Override
	public void sent(Channel channel, Object message) throws RemotingException {
		exchangeHandler.sent(channel, message);
	}

	@Override
	public void received(Channel channel, Object message) throws RemotingException {
		exchangeHandler.received(channel, message);
	}

	@Override
	public void caught(Channel channel, Throwable exception) throws RemotingException {
		exchangeHandler.caught(channel, exception);
	}

	@Override
	public ChannelHandler getHandler() {
		return exchangeHandler;
	}

}
