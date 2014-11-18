package RAUDT.RoundRobinRAUDT;
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
	
	protected Queue Queue_CPU, Queue_RAM, Queue_NetResponse;
	protected Job job;
	protected Info info;
	protected int current_V, current_I, current_A;
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
		
		Queue_CPU = new Queue();
		Queue_RAM = new Queue();
		Queue_NetResponse = new Queue();
		vm_type = new char [vm_count];
		vm_available = new boolean [vm_count];
		vm_queue_length = new int [vm_count];
		for (int i = 0; i < vm_count; i++)
		{
			vm_type[i] = ' ';
			vm_available[i] = true;
			vm_queue_length[i] = 0;
		}
		job = new Job("");
		current_V = 0;
		current_I = 1;
		current_A = 2;
		
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
			switch (job.type)
			{
			case 'V':
				m.add(makeContent("out" + current_V, job));
				current_V = (current_V == 0)? 3 : 0;
				break;
			case 'I':
				m.add(makeContent("out" + current_I, job));
				current_I = (current_I == 1)? 4 : 1;
				break;
			case 'A':
				m.add(makeContent("out" + current_A, job));
				current_A = (current_A == 2)? 5 : 2;
				break;
			default:
				break;
			}
		}
		return m;
	}

	public String getTooltipText()
	{
		return
		super.getTooltipText()
		+ "\n" + "job: " + job.getName()
		+ "\n" + "queue: " + q.toString()
		+ "\n" + "Queue_CPU: " + Queue_CPU.toString()
		+ "\n" + "Queue_RAM: " + Queue_RAM.toString()
		+ "\n" + "Queue_NetResponse: " + Queue_NetResponse.toString();
	}

}

