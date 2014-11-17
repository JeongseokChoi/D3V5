package RAUDT.ERROR;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;
import java.util.Random;

public class JobClassifier extends ViewableAtomic
{
	protected Queue Queue_V, Queue_I, Queue_A;
	protected Queue q;
	protected Job job;
	protected double processing_time;
	
	private Random random;
	final private char[] types;
	
	public JobClassifier()
	{
		this("JobClassifier", 20);
	}

	public JobClassifier(String name, double Processing_time)
	{
		super(name);
    
		addInport("in");
		addInport("done");
		addOutport("out");
		
		processing_time = Processing_time;
		
		random = null;
		types = new char [3];
		types[0] = 'V';
		types[1] = 'I';
		types[2] = 'A';
	}
	
	public void initialize()
	{
		Queue_V = new Queue();
		Queue_I = new Queue();
		Queue_A = new Queue();
		q = new Queue();
		job = new Job("");
		random = new Random(53475);
		
		holdIn("passive", INFINITY);
	}

	public void deltext(double e, message x)
	{
		Continue(e);
		
		if (phaseIs("passive"))
		{
			int count = 0;
			for (int i = 0; i < x.getLength(); i++)
			{
				if (messageOnPort(x, "in", i))
				{
					job = (Job)x.getValOnPort("in", i);
					count += 1;
					
					double size = job.size;
					char type = types[random.nextInt(3)];
					job = new Job("Size: " + size + " / " + type, size, type);
					switch (type)
					{
					case 'V': Queue_V.add(job); break;
					case 'I': Queue_I.add(job); break;
					case 'A': Queue_A.add(job); break;
					default: break;
					}
				}
			}
			if (count > 0)
				holdIn("classifying", processing_time * count);
		}
		else
		{
			for (int i = 0; i < x.getLength(); i++)
			{
				if (messageOnPort(x, "in", i))
				{
					job = (Job)x.getValOnPort("in", i);
					q.add(job);
				}
			}
		}
	}
	
	public void deltint()
	{
		if (phaseIs("classifying"))
		{
			if (Queue_V.size() > 0 || Queue_I.size() > 0 || Queue_A.size() > 0)
			{
				holdIn("sending", processing_time);
			}
			else
			{
				job = new Job("");
				
				holdIn("passive", INFINITY);
			}
		}
		else if (phaseIs("sending"))
		{
			if (Queue_V.isEmpty() && Queue_I.isEmpty() && Queue_A.isEmpty())
			{
				if (q.isEmpty() == true)
				{
					holdIn("passive", INFINITY);
				}
				else
				{
					int sz = q.size();
					
					while (q.isEmpty() == false)
					{
						job = (Job)q.removeFirst();
						
						double size = job.size;
						char type = types[random.nextInt(3)];
						job = new Job("Size: " + size + " / " + type, size, type);
						switch (type)
						{
						case 'V': Queue_V.add(job); break;
						case 'I': Queue_I.add(job); break;
						case 'A': Queue_A.add(job); break;
						default: break;
						}
					}
					
					holdIn("classifying", processing_time * sz);
				}
			}
		}
	}

	public message out()
	{
		message m = new message();
		
		if (phaseIs("classifying") || phaseIs("sending"))
		{
			if (Queue_V.isEmpty() == false)
				m.add(makeContent("out", (Job)Queue_V.removeFirst()));
			else if (Queue_I.isEmpty() == false)
				m.add(makeContent("out", (Job)Queue_I.removeFirst()));
			else if (Queue_A.isEmpty() == false)
				m.add(makeContent("out", (Job)Queue_A.removeFirst()));
		}
		return m;
	}
	
	public String getTooltipText()
	{
		return
        super.getTooltipText()
        + "\n" + "queue itself: " + q.toString()
        + "\n" + "Queue_V: " + Queue_V.toString()
        + "\n" + "Queue_I: " + Queue_I.toString()
        + "\n" + "Queue_A: " + Queue_A.toString();
	}
}



