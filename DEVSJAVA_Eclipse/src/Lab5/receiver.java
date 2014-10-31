package Lab5;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;

public class receiver extends ViewableAtomic
{
  
	protected entity job;
	protected double processing_time;

	public receiver()
	{
		this("proc", 20);
	}

	public receiver(String name, double Processing_time)
	{
		super(name);
    
		addInport("in");
		
		processing_time = Processing_time;
	}
  
	public void initialize()
	{
		job = new entity("");
		holdIn("passive", INFINITY);
	}

	public void deltext(double e, message x)
	{
		System.out.println("deltext");
		Continue(e);
		if (phaseIs("passive"))
		{
			for (int i = 0; i < x.getLength(); i++)
			{
				if (messageOnPort(x, "in", i))
				{
					job = x.getValOnPort("in", i);
					
					holdIn("busy", processing_time);
				}
			}
		}
	}
  
	public void deltint()
	{
		if (phaseIs("busy"))
		{
			job = new entity("");
			
			holdIn("passive", INFINITY);
		}
	}

	public message out()
	{
		message m = new message();
		if (phaseIs("busy"))
		{
			m.add(makeContent("out", job));
		}
		return m;
	}

	public String getTooltipText()
	{
		return
		super.getTooltipText()
		+ "\n" + "job: " + job.getName();
	}
}

