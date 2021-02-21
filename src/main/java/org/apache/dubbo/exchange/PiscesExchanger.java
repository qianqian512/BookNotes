package org.apache.dubbo.exchange;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.Transporters;
import org.apache.dubbo.remoting.exchange.ExchangeClient;
import org.apache.dubbo.remoting.exchange.ExchangeHandler;
import org.apache.dubbo.remoting.exchange.ExchangeServer;
import org.apache.dubbo.remoting.exchange.Exchanger;

public class PiscesExchanger implements Exchanger {

	@Override
	public ExchangeServer bind(URL url, ExchangeHandler handler) throws RemotingException {
		return new PiscesExchangeServer(Transporters.bind(url, handler));
	}

	@Override
	public ExchangeClient connect(URL url, ExchangeHandler handler) throws RemotingException {
		return new PiscesExchangeClient(Transporters.connect(url, handler));
	}

}
