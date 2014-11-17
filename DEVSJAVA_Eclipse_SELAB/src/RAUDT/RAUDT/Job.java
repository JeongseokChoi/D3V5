package RAUDT.RAUDT;
import GenCol.*;

public class Job extends entity
{   
	public double size;
	public char type;
	
	
	public Job(String name)
	{
		super(name);
		
		size = 0.0;
		type = ' ';
	}
	
	public Job(double _size)
	{
		super("size:" + _size);
		
		size = _size;
		type = ' ';
	}
	
	public Job(double _size, char _type)
	{  
		super("size:" + _size + " / type:" + _type);
		
		size = _size;
		type = _type;
	}
	
	public void set_type(char _type)
	{
		super.name += (" / type:" + _type);
		type = _type;
	}
}
