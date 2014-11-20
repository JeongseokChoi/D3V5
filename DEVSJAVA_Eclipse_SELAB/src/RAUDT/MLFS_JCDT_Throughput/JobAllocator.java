package RAUDT.MLFS_JCDT_Throughput;
import java.beans.DesignMode;

import genDevs.modeling.*;
import GenCol.*;
import simView.*;


public class JobAllocator extends ViewableAtomic
{
	protected int vm_count;
	protected char[] vm_type;
	protected int[] vm_sent;
	protected int[] vm_queue_length;

	protected Queue q;
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
		addInport("q_length");
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
		vm_sent = new int [vm_count];
		vm_queue_length = new int [vm_count];
		for (int i = 0; i < vm_count; i++)
		{
			vm_type[i] = ' ';
			vm_sent[i] = 0;
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
					/*
					switch (job.type)
					{
					case 'V': Queue_CPU.add(job); break;
					case 'I': Queue_RAM.add(job); break;
					case 'A': Queue_NetResponse.add(job); break;
					default: System.out.println("Exception!"); break;
					}
					*/
					
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
				else if (messageOnPort(x, "q_length", i))
				{
					info = (Info)x.getValOnPort("q_length", i);
					vm_queue_length[info.vm_id] = info.queue_length;
					
					// DEBUG
					/*
					for (int j = 0; j < vm_count; j++)
						System.out.println("VM #" + j + " queue length: " + vm_queue_length[j]);
					System.out.println("");
					*/
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
					/*
					switch (job.type)
					{
					case 'V': Queue_CPU.add(job); break;
					case 'I': Queue_RAM.add(job); break;
					case 'A': Queue_NetResponse.add(job); break;
					default: System.out.println("Exception!"); break;
					}
					*/
				}
				else if (messageOnPort(x, "q_length", i))
				{
					info = (Info)x.getValOnPort("q_length", i);
					vm_queue_length[info.vm_id] = info.queue_length;
					
					// DEBUG
					/*
					for (int j = 0; j < vm_count; j++)
						System.out.println("VM #" + j + " queue length: " + vm_queue_length[j]);
					System.out.println("");
					*/
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
			int max_q_idx = 0, min_q_idx = 0;
			for (int i = 0; i < vm_count; i++)
			{
				if (vm_queue_length[i] > vm_queue_length[max_q_idx])
					max_q_idx = i;
				if (vm_queue_length[i] < vm_queue_length[min_q_idx])
					min_q_idx = i;
			}
			if ((vm_queue_length[max_q_idx] - vm_queue_length[min_q_idx]) > 2)
			{
				m.add(makeContent("out" + min_q_idx, job));
			}
			else
			{
				int destination;
				switch (job.type)
				{
				case 'V':
					if (vm_queue_length[0] < vm_queue_length[3])
						destination = 0;
					else if (vm_queue_length[0] > vm_queue_length[3])
						destination = 3;
					else
						destination = (vm_sent[0] < vm_sent[3])? 0 : 3;
					m.add(makeContent("out" + destination, job));
					vm_sent[destination] += 1;
					break;
				case 'I':
					if (vm_queue_length[1] < vm_queue_length[4])
						destination = 1;
					else if (vm_queue_length[1] > vm_queue_length[4])
						destination = 4;
					else
						destination = (vm_sent[1] < vm_sent[4])? 1 : 4;
					m.add(makeContent("out" + destination, job));
					vm_sent[destination] += 1;
					break;
				case 'A':
					if (vm_queue_length[2] < vm_queue_length[5])
						destination = 2;
					else if (vm_queue_length[2] > vm_queue_length[5])
						destination = 5;
					else
						destination = (vm_sent[2] < vm_sent[5])? 2 : 5;
					m.add(makeContent("out" + destination, job));
					vm_sent[destination] += 1;
					break;
				default:
					break;
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
		+ "\n" + "queue: " + q.toString()
		+ "\n" + "Queue_CPU: " + Queue_CPU.toString()
		+ "\n" + "Queue_RAM: " + Queue_RAM.toString()
		+ "\n" + "Queue_NetResponse: " + Queue_NetResponse.toString();
	}
}

