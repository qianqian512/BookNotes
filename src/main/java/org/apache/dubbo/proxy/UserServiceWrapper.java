package org.apache.dubbo.proxy;

/**
 * ProxyFactory用javassist为UserService生成的Wrapper代码
 * @author hudaming
 */
public class UserServiceWrapper {

	public static String[] pns;
	public static java.util.Map pts;
	public static String[] mns;
	public static String[] dmns;
	public static Class[] mts0;

	public String[] getPropertyNames() {
		return pns;
	}

	public boolean hasProperty(String n) {
		return pts.containsKey(n);
	}

	public Class getPropertyType(String n) {
		return (Class) pts.get(n);
	}

	public String[] getMethodNames() {
		return mns;
	}

	public String[] getDeclaredMethodNames() {
		return dmns;
	}

	public void setPropertyValue(Object o, String n, Object v) {
		org.apache.dubbo.user.UserServiceImpl w;
		try {
			w = ((org.apache.dubbo.user.UserServiceImpl) o);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
		throw new org.apache.dubbo.common.bytecode.NoSuchPropertyException("Not found property \"" + n
				+ "\" field or setter method in class org.apache.dubbo.export.UserServiceImpl.");
	}

	public Object getPropertyValue(Object o, String n) {
		org.apache.dubbo.user.UserServiceImpl w;
		try {
			w = ((org.apache.dubbo.user.UserServiceImpl) o);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
		throw new org.apache.dubbo.common.bytecode.NoSuchPropertyException("Not found property \"" + n
				+ "\" field or setter method in class org.apache.dubbo.export.UserServiceImpl.");
	}

	public Object invokeMethod(Object o, String n, Class[] p, Object[] v)
			throws java.lang.reflect.InvocationTargetException {
		org.apache.dubbo.user.UserServiceImpl w;
		try {
			w = ((org.apache.dubbo.user.UserServiceImpl) o);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
		try {
			if ("sayHello".equals(n) && p.length == 1) {
				return w.sayHello((java.lang.String) v[0]);
			}
		} catch (Throwable e) {
			throw new java.lang.reflect.InvocationTargetException(e);
		}
		throw new org.apache.dubbo.common.bytecode.NoSuchMethodException("Not found method \"" + n + "\" in class org.apache.dubbo.export.UserServiceImpl.");
	}
}
