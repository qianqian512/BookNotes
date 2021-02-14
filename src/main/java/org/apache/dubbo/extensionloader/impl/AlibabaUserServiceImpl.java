package org.apache.dubbo.extensionloader.impl;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.extensionloader.UserNode;
import org.apache.dubbo.extensionloader.UserService;

@Activate(group = { "A", "B" })
public class AlibabaUserServiceImpl implements UserService {

	@Override
	public String regist(String name) {
		return "regist alibaba user " + name;
	}

	@Override
	public String login(URL url, String name) {
		return "login alibaba user " + name;
	}

	@Override
	public String logout(UserNode node) {
		return "logout alibaba user " + node.name;
	}
}
