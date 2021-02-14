package org.apache.dubbo.extensionloader.impl;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.extensionloader.UserService;

public class TencertUserServiceImpl implements UserService {

	@Override
	public String regist(String name) {
		return "regist tencent user " + name;
	}

	@Override
	public String login(URL url, String name) {
		return "login tencent user " + name;
	}

	@Override
	@Activate(value = {"tencent"})
	public String logout(String name) {
		return "logout tencent user " + name;
	}
}
