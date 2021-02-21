package org.apache.dubbo.transport;

import static org.apache.dubbo.common.constants.CommonConstants.IO_THREADS_KEY;

import java.net.InetSocketAddress;
import java.util.Collection;

import org.apache.dubbo.common.Parameters;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.Constants;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.RemotingServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class PiscesRemotingServer implements RemotingServer {

	private URL url;
	private ChannelHandler channelHandler;
	
	private ServerBootstrap bootStrap;
	
	public PiscesRemotingServer(URL url, ChannelHandler handler) {
		this.url = url;
		this.channelHandler = handler;
		initServer();
	}
	
	private void initServer() {
		bootStrap = new ServerBootstrap();
		bootStrap.channel(NioServerSocketChannel.class);
		bootStrap.group(new NioEventLoopGroup(url.getPositiveParameter(IO_THREADS_KEY, Constants.DEFAULT_IO_THREADS)));
		bootStrap.childHandler(new ChannelInitializer<io.netty.channel.Channel>() {
			@Override
			protected void initChannel(io.netty.channel.Channel ch) throws Exception {
				ch.pipeline().addLast(new PiscesServerHandler(url, channelHandler));
			}
		});
	}

	@Override
	public URL getUrl() {
		return url;
	}

	@Override
	public ChannelHandler getChannelHandler() {
		return channelHandler;
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void send(Object message) throws RemotingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(Object message, boolean sent) throws RemotingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close(int timeout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startClose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset(URL url) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isBound() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Channel> getChannels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Channel getChannel(InetSocketAddress remoteAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset(Parameters parameters) {
		// TODO Auto-generated method stub
		
	}
}
