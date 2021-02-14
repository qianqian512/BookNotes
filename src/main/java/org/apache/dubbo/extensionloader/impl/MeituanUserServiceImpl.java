package org.apache.dubbo.extensionloader.impl;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.extensionloader.UserNode;
import org.apache.dubbo.extensionloader.UserService;

@Activate(group = { "B" }, value = { "MEI" })
public class MeituanUserServiceImpl implements UserService {

	@Override
	public String regist(String name) {
		return "regist meituan user " + name;
	}

	@Override
	public String login(URL url, String name) {
		return "login meituan user " + name;
	}

	@Override
	public String logout(UserNode node) {
		return "logout meituan user " + node.name;
	}
}
