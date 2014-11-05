package RAUDT;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;

public class JobAllocatorRoundRobin extends ViewableAtomic
{
	protected int currVM;
	protected int vmNum;
	protected boolean[] vmAvailable;
	protected Info[] vmInfo;
	protected char[] vmType;
	protected Queue Queue_CPU, Queue_RAM, Queue_NetResponse;
	protected Job job;
	protected Info info;
	protected double processing_time;

	public JobAllocatorRoundRobin()
	{
		this("JobAllocator", 1, 20);
	}

	public JobAllocatorRoundRobin(String name, int _vmNum, double Processing_time)
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
		currVM = 0;
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
			m.add(makeContent("out" + currVM, job));
			currVM = (currVM + 1) % vmNum;
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

