package org.apache.dubbo.extensionloader;

import java.util.List;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;

public class ExtensionLoaderTest {
	
	public static void main(String[] args) {
		/**
		 * 测试最基本的加载扩展
		 */
		System.out.println(ExtensionLoader.getExtensionLoader(UserService.class).getExtension("jd").regist("huming"));
		
		/**
		 * 测试加载默认扩展配置：
		 *   UserService.java 的class上配置SPI("baidu")
		 * 使用时
		 *   ExtensionLoader.getDefaultExtension();
		 */
		System.out.println(ExtensionLoader.getExtensionLoader(UserService.class).getDefaultExtension().regist("huming"));
		
		/**
		 * 测试加载自适应扩展点：
		 *   配置时：login方法带上@Adaptive，且第一个参数为URL类型
		 *   使用时：传入URL和业务方法参数，如果不带@Adaptive参数，则默认使用@SPI定义的实现
		 */
		URL url = new URL("dubbo", "localhost", 8080);
		url = url.addParameter("u", "meituan");
		System.out.println(ExtensionLoader.getExtensionLoader(UserService.class).getAdaptiveExtension().login(url, "huming"));
		
		/**
		 * 测试自动激活扩展点
		 */
		url = url.addParameter("u", "baidu,alibaba,tencent");
		List<UserService> userServiceList = ExtensionLoader.getExtensionLoader(UserService.class).getActivateExtension(url, "u");
		for (UserService userService : userServiceList) {
			System.out.println(userService.logout("huming"));
		}
//		ExtensionLoader.getExtensionLoader(UserService.class).getActivateExtension(url, values)
	}
}
