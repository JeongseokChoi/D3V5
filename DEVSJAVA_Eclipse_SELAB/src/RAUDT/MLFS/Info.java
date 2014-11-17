package RAUDT.MLFS;
import GenCol.*;

public class Info extends entity
{   
	protected int id;
	protected double CPU;
	protected double RAM;
	protected double NetResponse;
	
	public Info(String name)
	{  
		super(name);
		
		id = -1;
		CPU = 1.0;
		RAM = 1.0;
		NetResponse = 1.0;
	}
	
	public Info(String name, int _id, double c, double r, double n)
	{
		super(name);
		
		id = _id;
		CPU = c;
		RAM = r;
		NetResponse = n;
	}
}
