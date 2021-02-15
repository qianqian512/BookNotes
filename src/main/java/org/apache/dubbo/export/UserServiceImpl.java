package org.apache.dubbo.export;

public class UserServiceImpl implements UserService {

	@Override
	public String sayHello(String name) {
		return "hello " + name;
	}
}
