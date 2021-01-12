import java.util.concurrent.ConcurrentHashMap;

public class CMapTest {
	
	public static void main(String[] args) {
		ConcurrentHashMap<Integer, Object> map = new ConcurrentHashMap<>();
		map.put(0, "a");
		map.put(16, "b");
		map.put(32, "c");
		map.put(48, "d");
		map.put(64, "e");
		map.put(80, "f");
		map.put(96, "g");
		map.put(112, "h");
		map.put(128, "i");
	}

}
