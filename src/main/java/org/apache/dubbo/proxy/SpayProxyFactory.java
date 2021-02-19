package org.apache.dubbo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.AppResponse;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;

public class SpayProxyFactory implements ProxyFactory {

	@Override
	public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
		return new Invoker<T>() {
			@Override
			public URL getUrl() {
				return url;
			}
			@Override
			public boolean isAvailable() {
				return true;
			}
			@Override
			public void destroy() {
			}
			@Override
			public Class<T> getInterface() {
				return type;
			}
			@Override
			public Result invoke(Invocation invocation) throws RpcException {
				System.out.println("SpayProxyFactory.invoke " + invocation.getMethodName());
				try {
					Object result = type.getMethod(invocation.getMethodName(), invocation.getParameterTypes()).invoke(proxy, invocation.getArguments());
					return new AppResponse(result);
				} catch (Exception e) {
					AppResponse resp = new AppResponse();
					resp.setException(e);
					return resp;
				}
			}
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getProxy(Invoker<T> invoker) throws RpcException {
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { invoker.getInterface() }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		        return invoker.invoke(new RpcInvocation(method, invoker.getInterface().getName(), args)).recreate();
			}
		});
	}

	@Override
	public <T> T getProxy(Invoker<T> invoker, boolean generic) throws RpcException {
		throw new UnsupportedOperationException("Unsupported export generic service. url: " + invoker.getUrl());
	}
}
