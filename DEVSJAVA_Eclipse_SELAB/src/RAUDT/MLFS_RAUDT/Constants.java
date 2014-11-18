package RAUDT.MLFS_RAUDT;
import java.util.Random;

public class Constants
{
	private static Random rand = new Random(31253);
	
	final public static int USER_NUM = 5;
	final public static int USER_INT_ARR_TIME = 10;
	final public static int VM_NUM = 6;
	final public static int JOB_COUNT = 400;
	final public static int[] USER_SEED =
			new int [] {920721, 930214, 920824, 960121, 940308};
	final public static int CLASSIFIER_SEED = 920828;
	final public static Info[] VM_INFO = new Info []
			{
			new Info(0, 9, 8, 8),
			new Info(1, 8, 9, 8),
			new Info(2, 8, 8, 9),
			new Info(3, 9, 8, 8),
			new Info(4, 8, 9, 8),
			new Info(5, 8, 8, 9)
			};
	/*
	final public static Info[] VM_INFO = new Info []
			{
		new Info(0, rand.nextInt(4) + 7, rand.nextInt(6) + 1, rand.nextInt(6) + 1),
		new Info(1, rand.nextInt(6) + 1, rand.nextInt(4) + 7, rand.nextInt(6) + 1),
		new Info(2, rand.nextInt(6) + 1, rand.nextInt(6) + 1, rand.nextInt(4) + 7),
		new Info(3, rand.nextInt(4) + 7, rand.nextInt(6) + 1, rand.nextInt(6) + 1),
		new Info(4, rand.nextInt(6) + 1, rand.nextInt(4) + 7, rand.nextInt(6) + 1),
		new Info(5, rand.nextInt(6) + 1, rand.nextInt(6) + 1, rand.nextInt(4) + 7),
	};
	*/
}
