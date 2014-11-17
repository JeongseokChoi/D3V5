package RAUDT.MLFS;
import GenCol.*;

public class Job extends entity
{   
	char type;
	double size;
	
	public Job(String name)
	{  
		this(name, 0.0, 'O');
	}
	
	public Job(String name, double s)
	{
		this(name, s, 'O');
	}
	
	public Job(String name, double s, char t)
	{
		super(name);
		type = t;
		size = s;
	}
}
