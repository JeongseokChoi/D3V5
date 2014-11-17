package RAUDT.MLFS;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;

public class JobAllocator extends ViewableAtomic
{
	protected int vmNum;
	protected boolean[] vmAvailable;
	protected Info[] vmInfo;
	protected char[] vmType;
	protected Queue Queue_CPU, Queue_RAM, Queue_NetResponse;
	protected Job job;
	protected Info info;
	protected double processing_time;

	public JobAllocator()
	{
		this("JobAllocator", 1, 20);
	}

	public JobAllocator(String name, int _vmNum, double Processing_time)
	{
		super(name);
    
		addInport("job_in");
		addInport("req_in");
		addInport("info_in");
		for (int i = 0; i < _vmNum; i++)
			addOutport("out" + i);
		
		processing_time = Processing_time;
		
		vmNum = _vmNum;
	}
  
	public void initialize()
	{
		job = new Job("");
		info = new Info("");
		Queue_CPU = new Queue();
		Queue_RAM = new Queue();
		Queue_NetResponse = new Queue();
		
		vmAvailable = new boolean [vmNum];
		vmInfo = new Info [vmNum];
		vmType = new char [vmNum];
		for (int i = 0; i < vmNum; i++)
		{
			vmAvailable[i] = true;
			vmInfo[i] = null;
			vmType[i] = 'O';
		}
		
		holdIn("passive", INFINITY);
	}

	public void deltext(double e, message x)
	{
		Continue(e);
		if (phaseIs("passive"))
		{
			for (int i = 0; i < x.getLength(); i++)
			{
				if (messageOnPort(x, "job_in", i))
				{
					job = (Job)x.getValOnPort("job_in", i);
					
					switch(job.type)
					{
					case 'V': Queue_CPU.add(job); break;
					case 'I': Queue_RAM.add(job); break;
					case 'A': Queue_NetResponse.add(job); break;
					default: break;
					}
					
					holdIn("busy", processing_time);
				}
				else if (messageOnPort(x, "info_in", i))
				{
					info = (Info)x.getValOnPort("info_in", i);
					vmInfo[info.id] = info;
					if (info.CPU >= 7)
						vmType[info.id] = 'V';
					else if (info.RAM >= 7)
						vmType[info.id] = 'I';
					else if (info.NetResponse >= 7)
						vmType[info.id] = 'A';
					else
						System.out.println("! Exception !");
					
					holdIn("passive", INFINITY);
				}
				else if (messageOnPort(x, "req_in", i))
				{
					info = (Info)x.getValOnPort("req_in", i);
					vmAvailable[info.id] = true;
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
			if (Queue_CPU.isEmpty() == false)
			{
				for (int i = 0; i < vmNum; i++)
				{
					if (vmType[i] == 'V' && vmAvailable[i] == true)
					{
						m.add(makeContent("out" + i, (Job)Queue_CPU.removeFirst()));
						vmAvailable[i] = false;
						break;
					}
				}
			}
			if (Queue_RAM.isEmpty() == false)
			{
				for (int i = 0; i < vmNum; i++)
				{
					if (vmType[i] == 'I' && vmAvailable[i] == true)
					{
						m.add(makeContent("out" + i, (Job)Queue_RAM.removeFirst()));
						vmAvailable[i] = false;
						break;
					}
				}
			}
			if (Queue_NetResponse.isEmpty() == false)
			{
				for (int i = 0; i < vmNum; i++)
				{
					if (vmType[i] == 'A' && vmAvailable[i] == true)
					{
						m.add(makeContent("out" + i, (Job)Queue_NetResponse.removeFirst()));
						vmAvailable[i] = false;
						break;
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
		+ "\n" + "Queue_CPU: " + Queue_CPU.toString()
		+ "\n" + "Queue_RAM: " + Queue_RAM.toString()
		+ "\n" + "Queue_NetResponse: " + Queue_NetResponse.toString();
	}

}

