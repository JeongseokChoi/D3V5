package lab06;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;

public class Router extends ViewableAtomic
{
	protected Job job;
	protected double processing_time;

	public Router()
	{
		this("router", 20);
	}

	public Router(String name, double Processing_time)
	{
		super(name);
    
		addInport("in");
		addOutport("out1");
		addOutport("out2");
		
		processing_time = Processing_time;
	}
  
	public void initialize()
	{
		job = new Job("");
		
		holdIn("passive", INFINITY);
	}

	public void deltext(double e, message x)
	{
		Continue(e);
		if (phaseIs("passive"))
		{
			for (int i = 0; i < x.getLength(); i++)
			{
				if (messageOnPort(x, "in", i))
				{
					job = (Job)x.getValOnPort("in", i);
					
					holdIn("sending", processing_time);
				}
			}
		}
	}
  
	public void deltint()
	{
		if (phaseIs("sending"))
		{
			job = new Job("");
			
			holdIn("passive", INFINITY);
		}
	}

	public message out()
	{
		message m = new message();
		if (phaseIs("sending"))
		{
			if (job.jobFlag == 1)
				m.add(makeContent("out1", job));
			else if (job.jobFlag == 2)
				m.add(makeContent("out2", job));
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

