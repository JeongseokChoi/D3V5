package RAUDT;
import simView.*;
import genDevs.modeling.*;
import GenCol.*;
import java.util.Random;

public class User extends ViewableAtomic
{
	protected Job job;
	protected double int_arr_time;
	protected int count;
	protected int seed;
	protected final int MAX;
	
	private Random random;
  
	public User() 
	{
		this("User", 0, 30, 100);
	}
  
	public User(String name, int _seed, double Int_arr_time, int max)
	{
		super(name);
   
		addOutport("out");
		
		MAX = max;
		random = null;
		seed = _seed;
		
		int_arr_time = Int_arr_time;
	}
  
	public void initialize()
	{
		count = 1;
		random = new Random(seed);
		
		int jobSize = (random.nextInt(30) + 1) * 10;
		job = new Job("Size: " + Double.toString(jobSize), jobSize, 'I');
		
		holdIn("active", int_arr_time);
	}
  
	public void deltext(double e, message x)
	{
		Continue(e);
	}

	public void deltint()
	{
		if (phaseIs("active"))
		{
			count = count + 1;
			
			if (count > MAX)
			{
				holdIn("stop", INFINITY);
			}
			else
			{				
				int jobSize = (random.nextInt(30) + 1) * 10;
				job = new Job("Size: " + Double.toString(jobSize), jobSize, 'I');
			
				holdIn("active", int_arr_time);
			}
		}
	}

	public message out()
	{
		message m = new message();
		m.add(makeContent("out", job));
		return m;
	}
  
	public String getTooltipText()
	{
		return
        super.getTooltipText()
        + "\n" + " int_arr_time: " + int_arr_time
        + "\n" + " count: " + count;
	}

}
