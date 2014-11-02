package lab06;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;

public class Add extends ViewableAtomic
{
	protected Job job_in, job_out;
	protected double processing_time;

	public Add()
	{
		this("add", 20);
	}

	public Add(String name, double Processing_time)
	{
		super(name);
    
		addInport("in");
		addOutport("out");
		
		processing_time = Processing_time;
	}
  
	public void initialize()
	{
		job_in = new Job("");
		job_out = new Job("");
		
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
					job_in = (Job)x.getValOnPort("in", i);
					
					int sum = job_in.value[0] + job_in.value[1];
					job_out = new Job(Integer.toString(sum));
					
					holdIn("busy", processing_time);
				}
			}
		}
	}
  
	public void deltint()
	{
		if (phaseIs("busy"))
		{
			job_in = new Job("");
			
			holdIn("passive", INFINITY);
		}
	}

	public message out()
	{
		message m = new message();
		if (phaseIs("busy"))
		{
			m.add(makeContent("out", job_out));
		}
		return m;
	}

	public String getTooltipText()
	{
		return
		super.getTooltipText()
		+ "\n" + "job_in: " + job_in.getName();
	}
}

