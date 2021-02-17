package org.apache.dubbo.user;

public class UserServiceImpl implements UserService {

	@Override
	public String sayHello(String name) {
		return "hello " + name;
	}
}
