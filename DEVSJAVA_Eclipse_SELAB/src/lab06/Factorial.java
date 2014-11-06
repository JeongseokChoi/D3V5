package lab06;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;

public class Factorial extends ViewableAtomic
{
  
	protected Job job;
	protected double processing_time;

	public Factorial()
	{
		this("Factorial", 20);
	}

	public Factorial(String name, double Processing_time)
	{
		super(name);
    
		addInport("in");
		addOutport("out");
		
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
					int factorial = 1;
					for (int j = 1; j <= job.number; j++)
						factorial *= j;
					job = new Job(Integer.toString(factorial));
					
					holdIn("busy", processing_time);
				}
			}
		}
	}
  
	public void deltint()
	{
		if (phaseIs("busy"))
		{
			job = new Job("");
			
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

