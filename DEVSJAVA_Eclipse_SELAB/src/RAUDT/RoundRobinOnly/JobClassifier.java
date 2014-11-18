package RAUDT.RoundRobinOnly;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;
import java.util.Random;


public class JobClassifier extends ViewableAtomic
{
	protected Queue Queue_V, Queue_I, Queue_A;
	protected Job job;
	protected double processing_time;
	private Random rand;
	private int random_seed;

	public JobClassifier()
	{
		this("JobClassifier", 10, 0);
	}

	public JobClassifier(String name, double Processing_time, int _seed)
	{
		super(name);
		
		addInport("in");
		addInport("done");
		addOutport("out");
		
		random_seed = _seed;
		processing_time = Processing_time;
	}
	
	public void initialize()
	{
		job = new Job("");
		Queue_V = new Queue();
		Queue_I = new Queue();
		Queue_A = new Queue();
		rand = new Random(random_seed);
		
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
					job.set_type(create_type());
					switch (job.type)
					{
					case 'V': Queue_V.add(job); break;
					case 'I': Queue_I.add(job); break;
					case 'A': Queue_A.add(job); break;
					default: System.out.println("Exception!"); break;
					}
					
					holdIn("busy", processing_time);
				}
			}
		}
	}
	
	public void deltint()
	{
		if (phaseIs("busy"))
		{
			if (Queue_V.isEmpty() && Queue_I.isEmpty() && Queue_A.isEmpty())
				holdIn("passive", INFINITY);
			else
				holdIn("busy", processing_time);
		}
	}
	
	public message out()
	{
		message m = new message();
		if (phaseIs("busy"))
		{
			if (Queue_V.isEmpty() == false)
			{
				job = (Job)Queue_V.removeFirst();
				m.add(makeContent("out", job));
			}
			else if (Queue_I.isEmpty() == false)
			{
				job = (Job)Queue_I.removeFirst();
				m.add(makeContent("out", job));
			}
			else if (Queue_A.isEmpty() == false)
			{
				job = (Job)Queue_A.removeFirst();
				m.add(makeContent("out", job));
			}
		}
		return m;
	}

	public String getTooltipText()
	{
		return
		super.getTooltipText()
		+ "\n" + "job: " + job.getName()
		+ "\n" + "Queue_V: " + Queue_V.toString()
		+ "\n" + "Queue_I: " + Queue_I.toString()
		+ "\n" + "Queue_A: " + Queue_A.toString();
	}

	private char create_type()
	{
		char[] type_arr = {'V', 'I', 'A'};
		int random = rand.nextInt(3);
		
		return type_arr[random];
	}
}
