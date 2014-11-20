package RAUDT.MLFS_JCDT_Throughput;
import simView.*;
import genDevs.modeling.*;
import GenCol.*;
import java.util.Random;

public class User extends ViewableAtomic
{
	protected double int_arr_time;
	protected int job_count;
	protected int count;
	protected Job job;
	private Random rand;
	private int random_seed;
  
	public User() 
	{
		this("User", 30, 5, 0);
	}
  
	public User(String name, double Int_arr_time, int _job_count, int _seed)
	{
		super(name);
   
		addOutport("out");
    
		random_seed = _seed;
		job_count = _job_count;
		int_arr_time = Int_arr_time;
	}
  
	public void initialize()
	{
		rand = new Random(random_seed);
		job = create_job();
		count = 0;
		
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
			job = create_job();
			
			if (count < job_count)
				holdIn("active", int_arr_time);
			else
				holdIn("stop", INFINITY);
		}
	}

	public message out()
	{
		message m = new message();
		if (phaseIs("active"))
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
	
	private Job create_job()
	{
		double size = (double)(rand.nextInt(30) + 1) * 10;
		
		return new Job(size);
	}
}
