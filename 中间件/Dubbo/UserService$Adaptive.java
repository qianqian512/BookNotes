package org.apache.dubbo.extensionloader;

import org.apache.dubbo.common.extension.ExtensionLoader;

public class UserService$Adaptive implements org.apache.dubbo.extensionloader.UserService {
	public java.lang.String regist(java.lang.String arg0) {
		throw new UnsupportedOperationException(
				"The method public abstract java.lang.String org.apache.dubbo.extensionloader.UserService.regist(java.lang.String) of interface org.apache.dubbo.extensionloader.UserService is not adaptive method!");
	}

	public java.lang.String login(org.apache.dubbo.common.URL arg0, java.lang.String arg1) {
		if (arg0 == null)
			throw new IllegalArgumentException("url == null");
		org.apache.dubbo.common.URL url = arg0;
		String extName = url.getParameter("u", url.getParameter("r", "baidu"));
		if (extName == null)
			throw new IllegalStateException(
					"Failed to get extension (org.apache.dubbo.extensionloader.UserService) name from url ("
							+ url.toString() + ") use keys([u, r])");
		org.apache.dubbo.extensionloader.UserService extension = (org.apache.dubbo.extensionloader.UserService) ExtensionLoader
				.getExtensionLoader(org.apache.dubbo.extensionloader.UserService.class).getExtension(extName);
		return extension.login(arg0, arg1);
	}

	public java.lang.String logout(org.apache.dubbo.extensionloader.UserNode arg0) {
		if (arg0 == null)
			throw new IllegalArgumentException("org.apache.dubbo.extensionloader.UserNode argument == null");
		if (arg0.getUrl() == null)
			throw new IllegalArgumentException("org.apache.dubbo.extensionloader.UserNode argument getUrl() == null");
		org.apache.dubbo.common.URL url = arg0.getUrl();
		String extName = url.getParameter("u", url.getParameter("r", "baidu"));
		if (extName == null)
			throw new IllegalStateException(
					"Failed to get extension (org.apache.dubbo.extensionloader.UserService) name from url ("
							+ url.toString() + ") use keys([u, r])");
		org.apache.dubbo.extensionloader.UserService extension = (org.apache.dubbo.extensionloader.UserService) ExtensionLoader
				.getExtensionLoader(org.apache.dubbo.extensionloader.UserService.class).getExtension(extName);
		return extension.logout(arg0);
	}
}