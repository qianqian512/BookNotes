package org.apache.dubbo.transport;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.Client;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.RemotingServer;
import org.apache.dubbo.remoting.Transporter;

public class PiscesTransporter implements Transporter {

	@Override
	public RemotingServer bind(URL url, ChannelHandler handler) throws RemotingException {
		return new PiscesRemotingServer(url, handler);
	}

	@Override
	public Client connect(URL url, ChannelHandler handler) throws RemotingException {
		// TODO Auto-generated method stub
		return null;
	}

}
