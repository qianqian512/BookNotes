package org.apache.dubbo.extensionloader;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.junit.Test;

public class ExtensionLoaderTest {

	/**
	 * 测试最基本的加载扩展
	 */
	@Test
	public void testGetExtension() {
		System.out.println(ExtensionLoader.getExtensionLoader(UserService.class).getExtension("jd").regist("huming"));
	}
	
	/**
	 * 测试加载默认扩展配置：
	 *   UserService.java 的class上配置SPI("baidu")
	 * 使用时
	 *   ExtensionLoader.getDefaultExtension();
	 */
	@Test
	public void testGetDefaultExtension() {
		System.out.println(ExtensionLoader.getExtensionLoader(UserService.class).getDefaultExtension().regist("huming"));
	}

	/**
	 * 测试加载自适应扩展点：
	 *   配置时：login方法带上@Adaptive，且第一个参数为URL类型(如果class上带有@Adaptive注解，则作为默认Adaptive实现)
	 *   使用时：传入URL和业务方法参数，如果不带@Adaptive参数，则默认使用@SPI定义的实现
	 */
	@Test
	public void testGetAdaptiveExtension() {
		URL url = new URL("dubbo", "localhost", 8080);
		url = url.addParameter("r", "meituan");
		System.out.println(ExtensionLoader.getExtensionLoader(UserService.class).getAdaptiveExtension().login(url, "huming3"));
	}

	/**
	 * 测试加载自适应扩展点：与上面testGetAdaptiveExtension相比，logout方法第一个参数虽然不是URL类型，但具备getUrl方法，因此也可以自适应。
	 */
	@Test
	public void testGetAdaptiveExtension2() {
		UserNode userNode = new UserNode();
		userNode.name = "huming";
		userNode.url = new URL("dubbo", "localhost", 8080).addParameter("r", "alibaba");
		System.out.println(ExtensionLoader.getExtensionLoader(UserService.class).getAdaptiveExtension().logout(userNode));
	}
	
	@Test
	public void testWrapper() {
		URL url = new URL("dubbo", "localhost", 8080);
		url = url.addParameter("r", "meituan");
		UserService userService = ExtensionLoader.getExtensionLoader(UserService.class).getAdaptiveExtension();
		System.out.println(userService.login(url, "huming2"));
	}
	
	@Test
	public void testGetActivateExtensions() {
		
	}
}
