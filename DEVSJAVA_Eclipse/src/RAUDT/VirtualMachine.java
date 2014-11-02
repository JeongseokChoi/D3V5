package RAUDT;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;

public class VirtualMachine extends ViewableAtomic
{
	final protected int id;
	final protected double CPU;
	final protected double RAM;
	final protected double NetResponse;
	
	protected Queue q;
	protected Job job;
	protected double processing_time;
	
	public VirtualMachine()
	{
		this("VM", 0, 1.0, 1.0, 1.0, 20);
	}

	public VirtualMachine(String name,
			int _id, double c, double r, double n,
			double Processing_time)
	{
		super(name);
    
		addInport("in");
		addOutport("out");
		addOutport("req_out");
		addOutport("info_out");
	
		id = _id;
		CPU = c;
		RAM = r;
		NetResponse = n;
		
		processing_time = Processing_time;
	}
	
	public void initialize()
	{
		q = new Queue();
		job = new Job("");
		
		holdIn("initiating", 0);
	}

	public void deltext(double e, message x)
	{
		Continue(e);
		if (phaseIs("passive"))
		{
			for (int i = 0; i < x.size(); i++)
			{
				if (messageOnPort(x, "in", i))
				{
					job = (Job)x.getValOnPort("in", i);
					switch (job.type)
					{
					case 'V':
						processing_time = (double)(int)(job.size / CPU);
						break;
					case 'I':
						processing_time = (double)(int)(job.size / RAM);
						break;
					case 'A':
						processing_time = (double)(int)(job.size / NetResponse);
						break;
					default:
						processing_time = 0.0;
						break;
					}
					
					holdIn("busy", processing_time);
				}
			}
		}
		else if (phaseIs("busy"))
		{
			for (int i = 0; i < x.size(); i++)
			{
				if (messageOnPort(x, "in", i))
				{
					Job temp = (Job)x.getValOnPort("in", i);
					q.add(temp);
				}
			}
		}
	}
	
	public void deltint()
	{
		if (phaseIs("busy"))
		{
			if (!q.isEmpty())
			{
				job = (Job) q.removeFirst();
				switch (job.type)
				{
				case 'V':
					processing_time = (double)(int)(job.size / CPU);
					break;
				case 'I':
					processing_time = (double)(int)(job.size / RAM);
					break;
				case 'A':
					processing_time = (double)(int)(job.size / NetResponse);
					break;
				default:
					processing_time = 0.0;
					break;
				}
				
				holdIn("busy", processing_time);
			}
			else
			{
				job = new Job("");
				
				holdIn("passive", INFINITY);
			}
		}
		else if (phaseIs("initiating"))
		{
			holdIn("passive", INFINITY);
		}
	}

	public message out()
	{
		message m = new message();
		
		if (phaseIs("initiating"))
		{
			m.add(makeContent("info_out", new Info("VM info", id, CPU, RAM, NetResponse)));
		}
		else if (phaseIs("busy"))
		{
			m.add(makeContent("out", job));
			m.add(makeContent("req_out", new Info("finished", id, 0, 0, 0)));
		}
		return m;
	}	
	
	public String getTooltipText()
	{
		return
        super.getTooltipText()
        + "\n" + "queue length: " + q.size()
        + "\n" + "queue itself: " + q.toString();
	}
}
