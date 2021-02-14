package org.apache.dubbo.extensionloader;

import org.apache.dubbo.common.Node;
import org.apache.dubbo.common.URL;

public class UserNode implements Node {
	
	public URL url;
	public String name;

	@Override
	public URL getUrl() {
		return url;
	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
