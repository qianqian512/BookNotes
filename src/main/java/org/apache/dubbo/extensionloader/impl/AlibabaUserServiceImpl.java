package org.apache.dubbo.extensionloader.impl;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
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
	@Activate(value = { "ALI" })
	public String logout(String name) {
		return "logout alibaba user " + name;
	}
}
