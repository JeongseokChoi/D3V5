package RAUDT.MLFS_RoundRobin_Throughput;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;

public class VM extends ViewableAtomic
{
	protected Queue q;
	protected Info info;
	protected Job job;
	protected double tmp_sigma;
	protected double processing_time;
	
	public VM()
	{
		this("VM", 10, new Info("dummy"));
	}

	public VM(String name, double Processing_time, Info _info)
	{
		super(name);
		
		addInport("in");
		addOutport("done");
		addOutport("vm_info");
		addOutport("q_length");
		addOutport("out");
		
		info = _info;
		processing_time = Processing_time;
	}
	
	public void initialize()
	{
		q = new Queue();
		job = new Job("");
		tmp_sigma = INFINITY;
		
		holdIn("init", 0);
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
					case 'V': processing_time = (int)job.size / info.CPU; break;
					case 'I': processing_time = (int)job.size / info.RAM; break;
					case 'A': processing_time = (int)job.size / info.NetResponse; break;
					default: System.out.println("Exception!"); break;
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
					tmp_sigma = sigma;
					
					holdIn("sending Q len", 0);
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
				case 'V': processing_time = (int)job.size / info.CPU; break;
				case 'I': processing_time = (int)job.size / info.RAM; break;
				case 'A': processing_time = (int)job.size / info.NetResponse; break;
				default: System.out.println("Exception!"); break;
				}
				
				tmp_sigma = processing_time;
				holdIn("busy", processing_time);
			}
			else
			{
				job = new Job("");
				
				holdIn("passive", INFINITY);
			}
		}
		else if (phaseIs("sending Q len"))
		{
			phase = "busy";
			sigma = tmp_sigma;
			tmp_sigma = INFINITY;
		}
		else if (phaseIs("init"))
		{
			passivate();
		}
	}

	public message out()
	{
		message m = new message();
		
		if (phaseIs("init"))
		{
			m.add(makeContent("vm_info", info));
		}
		else if (phaseIs("busy"))
		{
			m.add(makeContent("out", job));
			m.add(makeContent("done", info));
			if (q.size() > 0)
				m.add(makeContent("q_length", new Info(info.vm_id, q.size() - 1)));
		}
		else if (phaseIs("sending Q len"))
		{
			m.add(makeContent("q_length", new Info(info.vm_id, q.size())));
		}
		return m;
	}

	public String getTooltipText()
	{
		return
        super.getTooltipText()
        + "\n" + "CPU: " + info.CPU
        + "\n" + "RAM: " + info.RAM
        + "\n" + "NetResponse: " + info.NetResponse
        + "\n" + "queue length: " + q.size()
        + "\n" + "queue itself: " + q.toString();
	}
}
