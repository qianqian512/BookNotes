import java.util.Map;

/**
 * 由com.alibaba.dubbo.common.bytecode.Wrapper生成
 * @author dubbo
 */
public class UserInvokerWrapper$1 extends com.alibaba.dubbo.common.bytecode.Wrapper {

	public static String[] pns;

	public static Map pts;

	public static String[] mns = new String[] { "sayHello", "sayHello2" };

	public static String[] dmns = new String[] { "sayHello", "sayHello2" };

	public static Class[] mts1 = new Class[] { String.class }; // sayHello参数类型列表

	public static Class[] mts2 = new Class[] { User.class }; // sayHello2参数类型列表

	public String[] getPropertyNames() {
		return pns;
	}

	public boolean hasProperty(String n) {
		return pts.containsKey($1);
	}

	public Class getPropertyType(String n) {
		return (Class) pts.get($1);
	}

	public String[] getMethodNames() {
		return mns;
	}

	public String[] getDeclaredMethodNames() {
		return dmns;
	}

	public void setPropertyValue(Object o, String n, Object v) {
		org.hum.scaffold.dubbo.DemoService w;
		try {
			w = ((org.hum.scaffold.dubbo.DemoService) $1);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
		throw new com.alibaba.dubbo.common.bytecode.NoSuchPropertyException("Not found property \"" + $2
				+ "\" filed or setter method in class org.hum.scaffold.dubbo.DemoService.");
	}

	public Object getPropertyValue(Object o, String n) {
		org.hum.scaffold.dubbo.DemoService w;
		try {
			w = ((org.hum.scaffold.dubbo.DemoService) $1);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
		throw new com.alibaba.dubbo.common.bytecode.NoSuchPropertyException("Not found property \"" + $2
				+ "\" filed or setter method in class org.hum.scaffold.dubbo.DemoService.");
	}

	public Object invokeMethod(Object o, String n, Class[] p, Object[] v) throws java.lang.reflect.InvocationTargetException {
		org.hum.scaffold.dubbo.DemoService w;
		try {
			w = ((org.hum.scaffold.dubbo.DemoService) $1);
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
		try {
			if ("sayHello".equals($2) && $3.length == 1) {
				return ($w) w.sayHello((java.lang.String) $4[0]);
			}
			if ("sayHello2".equals($2) && $3.length == 1) {
				return ($w) w.sayHello2((org.hum.scaffold.dubbo.User) $4[0]);
			}
		} catch (Throwable e) {
			throw new java.lang.reflect.InvocationTargetException(e);
		}
		throw new com.alibaba.dubbo.common.bytecode.NoSuchMethodException(
				"Not found method \"" + $2 + "\" in class org.hum.scaffold.dubbo.DemoService.");
	}
}
