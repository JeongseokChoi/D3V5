package RAUDT.RoundRobinOnly;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;


public class JobAllocator extends ViewableAtomic
{
	protected Queue q;
	
	protected int vm_count;
	protected char[] vm_type;
	protected boolean[] vm_available;
	protected int[] vm_queue_length;
	
	protected Job job;
	protected Info info;
	protected int current;
	protected double processing_time;

	public JobAllocator()
	{
		this("JobAllocator", 10, 1);
	}

	public JobAllocator(String name, double Processing_time, int _vm_count)
	{
		super(name);
    
		addInport("in");
		addInport("vm_info");
		addInport("done");
		for (int i = 0; i < _vm_count; i++)
			addOutport("out" + i);
		
		vm_count = _vm_count;
		processing_time = Processing_time;
	}
  
	public void initialize()
	{
		q = new Queue();
		
		vm_type = new char [vm_count];

		job = new Job("");
		current = 0;
		
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
					
					holdIn("busy", processing_time);
				}
			}
		}
		else
		{
			for (int i = 0; i < x.getLength(); i++)
			{
				if (messageOnPort(x, "in", i))
				{
					q.add(x.getValOnPort("in", i));
				}
			}
		}
	}

	public void deltint()
	{
		if (phaseIs("busy"))
		{
			if (q.size() > 0)
			{
				job = (Job)q.removeFirst();
				
				holdIn("busy", processing_time);
			}
			else
			{
				holdIn("passive", INFINITY);
			}
		}
	}

	public message out()
	{
		message m = new message();
		if (phaseIs("busy"))
		{
			m.add(makeContent("out" + current, job));
			current = (current + 1) % vm_count;
		}
		return m;
	}

	public String getTooltipText()
	{
		return
		super.getTooltipText()
		+ "\n" + "job: " + job.getName()
		+ "\n" + "queue: " + q.toString();
	}

}

