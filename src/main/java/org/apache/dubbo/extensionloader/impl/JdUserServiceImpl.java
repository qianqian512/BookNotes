package org.apache.dubbo.extensionloader.impl;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.extensionloader.UserService;

public class JdUserServiceImpl implements UserService {

	@Override
	public String regist(String name) {
		return "regist jd user " + name;
	}

	@Override
	public String login(URL url, String name) {
		return "login jd user " + name;
	}

	@Override
	@Activate(value = {"jd"})
	public String logout(URL url, String name) {
		return "logout jd user " + name;
	}
}
