package org.apache.dubbo.extensionloader;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

@SPI("baidu")
public interface UserService {
	
	public String regist(String name);
	
	@Adaptive("u")
	public String login(URL url, String name);
	
	public String logout(URL url, String name);
}
