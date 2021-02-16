package org.apache.dubbo.proxy;

import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.rpc.ProxyFactory;
import org.junit.Test;

public class ProxyTest {

	@Test
	public void test1() {
		ProxyFactory extension = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getExtension("spay_proxy");
		System.out.println(extension);
	}
}
