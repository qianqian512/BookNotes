import java.util.Arrays;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		int[] nums = new int[] { 12, 28, 36, 52, 4, 20, 68, 84 };
		for (int num : nums) {
			System.out.println(num % 8);
		}
		
		System.out.println("-----------");
		
		for (int num : nums) {
			System.out.println(num % 16);
		}
	}
}
