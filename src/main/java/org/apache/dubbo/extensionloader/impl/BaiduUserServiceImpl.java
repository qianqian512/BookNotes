package org.apache.dubbo.extensionloader.impl;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.extensionloader.UserService;

public class BaiduUserServiceImpl implements UserService {

	@Override
	public String regist(String name) {
		return "regist baidu _user" + name;
	}

	@Override
	public String login(URL url, String name) {
		return "login baidu user " + name;
	}

	@Override
	@Activate(value = {"baidu"})
	public String logout(URL url, String name) {
		return "logout baidu user " + name;
	}
}
