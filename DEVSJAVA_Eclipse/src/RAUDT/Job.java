package RAUDT;
import GenCol.*;

public class Job extends entity
{   
	final public static int VIDEO = 0;
	final public static int IMAGE = 1;
	final public static int AUDIO = 2;
	final public static int OTHER = 3;
	
	public int type;
	public int size;
	
	public Job(String name)
	{
		super(name);  
	}
}
