package org.apache.dubbo.exchange;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.ExchangeClient;
import org.apache.dubbo.remoting.exchange.ExchangeHandler;
import org.apache.dubbo.remoting.exchange.ExchangeServer;
import org.apache.dubbo.remoting.exchange.Exchanger;

public class MyExchanger implements Exchanger {

	@Override
	public ExchangeServer bind(URL url, ExchangeHandler handler) throws RemotingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExchangeClient connect(URL url, ExchangeHandler handler) throws RemotingException {
		// TODO Auto-generated method stub
		return null;
	}

}
