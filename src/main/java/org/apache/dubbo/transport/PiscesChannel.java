package org.apache.dubbo.transport;

import java.net.InetSocketAddress;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.ChannelHandler;

public class PiscesChannel extends org.apache.dubbo.remoting.transport.AbstractChannel {

	public PiscesChannel(URL url, ChannelHandler handler) {
		super(url, handler);
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAttribute(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getAttribute(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String key, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAttribute(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		// TODO Auto-generated method stub
		return null;
	}

}
