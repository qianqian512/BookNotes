package org.apache.dubbo.extensionloader.impl;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.extensionloader.UserNode;
import org.apache.dubbo.extensionloader.UserService;

@Activate(group = { "A" }, value = { "BAI" })
public class BaiduUserServiceImpl implements UserService {

	@Override
	public String regist(String name) {
		return "regist baidu user " + name;
	}

	@Override
	public String login(URL url, String name) {
		return "login baidu user " + name;
	}

	@Override
	public String logout(UserNode node) {
		return "logout baidu user " + node.name;
	}
}
