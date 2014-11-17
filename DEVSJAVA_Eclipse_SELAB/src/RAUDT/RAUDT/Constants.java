package RAUDT.RAUDT;
import java.util.Random;

public class Constants
{
	private static Random rand = new Random(31253);
	
	final public static int USER_NUM = 3;
	final public static int USER_INT_ARR_TIME = 10;
	final public static int VM_NUM = 6;
	final public static int JOB_COUNT = 500;
	final public static int[] USER_SEED = new int [] {123123, 456456, 789789};
	final public static int CLASSIFIER_SEED = 53475;
	final public static Info[] VM_INFO = new Info []
			{
		new Info(0, rand.nextInt(4) + 7, rand.nextInt(6) + 1, rand.nextInt(6) + 1),
		new Info(1, rand.nextInt(6) + 1, rand.nextInt(4) + 7, rand.nextInt(6) + 1),
		new Info(2, rand.nextInt(6) + 1, rand.nextInt(6) + 1, rand.nextInt(4) + 7),
		new Info(3, rand.nextInt(4) + 7, rand.nextInt(6) + 1, rand.nextInt(6) + 1),
		new Info(4, rand.nextInt(6) + 1, rand.nextInt(4) + 7, rand.nextInt(6) + 1),
		new Info(5, rand.nextInt(6) + 1, rand.nextInt(6) + 1, rand.nextInt(4) + 7),
	};
}
