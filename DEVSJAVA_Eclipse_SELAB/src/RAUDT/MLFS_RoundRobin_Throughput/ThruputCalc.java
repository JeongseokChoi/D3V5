package RAUDT.MLFS_RoundRobin_Throughput;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;

public class ThruputCalc extends ViewableAtomic
{
	protected double clock;
	protected double time;
	protected int finished;
	protected int total_job;
	protected double processing_time;

	public ThruputCalc()
	{
		this("ThruputCalc", 100, 0);
	}

	public ThruputCalc(String name, double Processing_time, int _total_job)
	{
		super(name);
    
		addInport("in");
		
		total_job = _total_job;
		processing_time = Processing_time;
	}
  
	public void initialize()
	{
		clock = 0.0;
		finished = 0;
		time = 0.0;
		
		holdIn("on", processing_time);
	}

	public void deltext(double e, message x)
	{
		clock += e;
		Continue(e);
		if (phaseIs("on"))
		{
			for (int i = 0; i < x.getLength(); i++)
			{
				if (messageOnPort(x, "in", i))
				{
					finished += 1;
					if (finished >= total_job)
						holdIn("off", 0);
				}
			}
		}
	}
  
	public void deltint()
	{
		if (phaseIs("on"))
		{
			clock += sigma;
			
			if (finished >= total_job)
				holdIn("off", INFINITY);
			else
				holdIn("on", processing_time);
		}
		else if (phaseIs("off"))
		{
			holdIn("off", INFINITY);
		}
	}

	public message out()
	{
		message m = new message();
		if (phaseIs("on"))
		{
			time += processing_time;
			System.out.println("Thruput: " + ((double)finished / time));
			System.out.println("Finished jobs: " + finished);
			System.out.println("Time: " + time);
			System.out.println("");
		}
		else if (phaseIs("off"))
		{
			System.out.println("Thruput: " + ((double)finished / clock));
			System.out.println("Finished jobs: " + finished);
			System.out.println("Clock: " + clock);
			System.out.println("");
		}
		return m;
	}

	public String getTooltipText()
	{
		return
		super.getTooltipText()
		+ "\n" + "finished: " + finished
		+ "\n" + "time: " + time
		+ "\n" + "thruput: " + finished/time;
	}

}

