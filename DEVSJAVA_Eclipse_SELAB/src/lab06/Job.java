package lab06;
import GenCol.*;

public class Job extends entity
{   
	int jobFlag;
	int[] value;
	
	public Job(String name)
	{
		super(name);
		
		jobFlag = -1;
		value = null;
	}
	
	public Job(String name, int a, int b)
	{
		super(name);
		
		jobFlag = 1;
		
		value = new int [2];
		value[0] = a;
		value[1] = b;
	}
	
	public Job(String name,
			int a, int b, int c, int d, int e,
			int f, int g, int h, int i, int j)
	{
		super(name);
		
		jobFlag = 2;
		
		value = new int [10];
		value[0] = a;
		value[1] = b;
		value[2] = c;
		value[3] = d;
		value[4] = e;
		value[5] = f;
		value[6] = g;
		value[7] = h;
		value[8] = i;
		value[9] = j;
	}
}
