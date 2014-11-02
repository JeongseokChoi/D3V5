package lab06;
import simView.*;
import genDevs.modeling.*;
import GenCol.*;

public class User extends ViewableAtomic
{
	protected Job job;
	protected double int_arr_time;
  
	public User() 
	{
		this("User", 30);
	}
  
	public User(String name, double Int_arr_time)
	{
		super(name);
   
		addOutport("out");
		addInport("in");
    
		int_arr_time = Int_arr_time;
	}
  
	public void initialize()
	{
		job = new Job("2 + 8", 2, 8);
		
		holdIn("active", int_arr_time);
	}
  
	public void deltext(double e, message x)
	{
		Continue(e);
		if (phaseIs("sent_1"))
		{
			for (int i = 0; i < x.getLength(); i++)
			{
				if (messageOnPort(x, "in", i))
				{
					job = new Job("goto acc",
							3, 6, 4, 4, 7, 1, 8, 5, 2, 1);
					
					holdIn("active", int_arr_time);
				}
			}
		}
		if (phaseIs("sent_2"))
		{
			for (int i = 0; i < x.getLength(); i++)
			{
				holdIn("finished", INFINITY);
			}
		}
	}

	public void deltint()
	{
		if (phaseIs("active"))
		{
			if (job.jobFlag == 1)
				holdIn("sent_1", INFINITY);
			if (job.jobFlag == 2)
				holdIn("sent_2", INFINITY);
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
        + "\n" + " int_arr_time: " + int_arr_time;
	}

}
