package org.apache.dubbo.extensionloader.impl;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.extensionloader.UserNode;
import org.apache.dubbo.extensionloader.UserService;

import io.netty.util.internal.StringUtil;

public class UserServiceWrapper implements UserService {
	
	private UserService userService;
	
	public UserServiceWrapper(UserService userService) {
		this.userService = userService;
	}

	@Override
	public String regist(String name) {
		if (StringUtil.isNullOrEmpty(name)) {
			throw new IllegalArgumentException("name mustn't be null");
		}
		System.out.println("\t print by wrapper1");
		return userService.regist(name);
	}

	@Override
	public String login(URL url, String name) {
		if (StringUtil.isNullOrEmpty(name)) {
			throw new IllegalArgumentException("name mustn't be null");
		}
		System.out.println("\t print by wrapper1");
		return userService.login(url, name);
	}

	@Override
	public String logout(UserNode node) {
		if (StringUtil.isNullOrEmpty(node.name)) {
			throw new IllegalArgumentException("name mustn't be null");
		}
		return userService.logout(node);
	}

}
