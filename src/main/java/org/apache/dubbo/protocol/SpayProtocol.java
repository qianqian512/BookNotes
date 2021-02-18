package org.apache.dubbo.protocol;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.RpcException;

/**
 * TODO Transport用Socket实现，忽略Exchange层，支持序列化，header还是遵循Dubbo报文格式
 * @author hudaming
 *
 */
public class SpayProtocol implements Protocol {

	@Override
	public int getDefaultPort() {
		return 20880;
	}

	@Override
	public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
