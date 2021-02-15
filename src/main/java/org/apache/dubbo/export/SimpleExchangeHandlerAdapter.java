package org.apache.dubbo.export;

import static org.apache.dubbo.common.constants.CommonConstants.GROUP_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.INTERFACE_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.PATH_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.STUB_EVENT_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.VERSION_KEY;
import static org.apache.dubbo.rpc.protocol.dubbo.Constants.ON_CONNECT_KEY;
import static org.apache.dubbo.rpc.protocol.dubbo.Constants.ON_DISCONNECT_KEY;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.ExchangeChannel;
import org.apache.dubbo.remoting.exchange.support.ExchangeHandlerAdapter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcInvocation;

public class SimpleExchangeHandlerAdapter extends ExchangeHandlerAdapter {
	
	private Invoker<?> invoker;
	
	public SimpleExchangeHandlerAdapter(Invoker<?> invoker) {
		this.invoker = invoker;
	}

    @Override
    public CompletableFuture<Object> reply(ExchangeChannel channel, Object message) throws RemotingException {

        if (!(message instanceof Invocation)) {
            throw new RemotingException(channel, "Unsupported request: " + (message == null ? null : (message.getClass().getName() + ": " + message)) + ", channel: consumer: " + channel.getRemoteAddress() + " --> provider: " + channel.getLocalAddress());
        }
        Invocation inv = (Invocation) message;
        RpcContext.getContext().setRemoteAddress(channel.getRemoteAddress());
        Result result = invoker.invoke(inv);
        return result.thenApply(Function.identity());
    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        if (message instanceof Invocation) {
            reply((ExchangeChannel) channel, message);
        } else {
            super.received(channel, message);
        }
    }

    @Override
    public void connected(Channel channel) throws RemotingException {
        invoke(channel, ON_CONNECT_KEY);
    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {
        invoke(channel, ON_DISCONNECT_KEY);
    }

    private void invoke(Channel channel, String methodKey) throws RemotingException {
        Invocation invocation = createInvocation(channel, channel.getUrl(), methodKey);
        if (invocation != null) {
        	received(channel, invocation);
        }
    }

    private Invocation createInvocation(Channel channel, URL url, String methodKey) {
        String method = url.getParameter(methodKey);
        if (method == null || method.length() == 0) {
            return null;
        }

        RpcInvocation invocation = new RpcInvocation(method, url.getParameter(INTERFACE_KEY), new Class<?>[0], new Object[0]);
        invocation.setAttachment(PATH_KEY, url.getPath());
        invocation.setAttachment(GROUP_KEY, url.getParameter(GROUP_KEY));
        invocation.setAttachment(INTERFACE_KEY, url.getParameter(INTERFACE_KEY));
        invocation.setAttachment(VERSION_KEY, url.getParameter(VERSION_KEY));
        if (url.getParameter(STUB_EVENT_KEY, false)) {
            invocation.setAttachment(STUB_EVENT_KEY, Boolean.TRUE.toString());
        }

        return invocation;
    }
}