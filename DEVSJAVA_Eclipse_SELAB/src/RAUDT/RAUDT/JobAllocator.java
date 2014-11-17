package RAUDT.RAUDT;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;


public class JobAllocator extends ViewableAtomic
{
	protected int vm_count;
	protected char[] vm_type;
	protected boolean[] vm_available;
	protected int[] vm_queue_length;
	
	protected Queue Queue_CPU, Queue_RAM, Queue_NetResponse;
	protected Job job;
	protected Info info;
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
					switch (job.type)
					{
					case 'V': Queue_CPU.add(job); break;
					case 'I': Queue_RAM.add(job); break;
					case 'A': Queue_NetResponse.add(job); break;
					default: System.out.println("Exception!"); break;
					}
					
					holdIn("busy", processing_time);
				}
				else if (messageOnPort(x, "done", i))
				{
					info = (Info)x.getValOnPort("done", i);
					vm_available[info.vm_id] = true;
					
					holdIn("busy", processing_time);
				}
				else if (messageOnPort(x, "vm_info", i))
				{
					info = (Info)x.getValOnPort("vm_info", i);
					int CPU = info.CPU;
					int RAM = info.RAM;
					int NetResponse = info.NetResponse;
					if (CPU >= RAM && CPU >= NetResponse)
						vm_type[info.vm_id] = 'V';
					if (RAM >= NetResponse && RAM >= CPU)
						vm_type[info.vm_id] = 'I';
					if (NetResponse >= CPU && NetResponse >= RAM)
						vm_type[info.vm_id] = 'A';
					
					holdIn("passive", INFINITY);
				}
			}
		}
	}

	public void deltint()
	{
		if (phaseIs("busy"))
		{
			holdIn("passive", INFINITY);
		}
	}

	public message out()
	{
		message m = new message();
		if (phaseIs("busy"))
		{
			for (int i = 0; i < vm_count; i++)
			{
				if (vm_available[i] == true)
				{
					if (vm_type[i] == 'V' && Queue_CPU.size() > 0)
					{
						m.add(makeContent("out" + i, (Job)Queue_CPU.removeFirst()));
						vm_available[i] = false;
					}
					else if (vm_type[i] == 'I' && Queue_RAM.size() > 0)
					{
						m.add(makeContent("out" + i, (Job)Queue_RAM.removeFirst()));
						vm_available[i] = false;
					}
					else if (vm_type[i] == 'A' && Queue_NetResponse.size() > 0)
					{
						m.add(makeContent("out" + i, (Job)Queue_NetResponse.removeFirst()));
						vm_available[i] = false;
					}
				}
			}
		}
		return m;
	}

	public String getTooltipText()
	{
		return
		super.getTooltipText()
		+ "\n" + "job: " + job.getName()
		+ "\n" + "Queue_CPU: " + Queue_CPU.toString()
		+ "\n" + "Queue_RAM: " + Queue_RAM.toString()
		+ "\n" + "Queue_NetResponse: " + Queue_NetResponse.toString();
	}

}

