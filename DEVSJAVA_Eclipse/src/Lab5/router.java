package Lab5;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;

public class router extends ViewableAtomic
{
	
	protected Queue q;
	protected packet packet;
	protected double processing_time;
	
	public router()
	{
		this("procQ", 20);
	}

	public router(String name, double Processing_time)
	{
		super(name);
    
		addInport("in");
		
		for(int i=1; i<=5; i++)
		{
			addOutport("out" + i);
		}
		
		addOutport("out");
		
		processing_time = Processing_time;
	}
	
	public void initialize()
	{
		q = new Queue();
		packet = new packet("", 0);
		
		holdIn("passive", INFINITY);
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
					packet = (packet) x.getValOnPort("in", i);

					q.add(packet);
					
					if(q.size() == 5)
					{
						holdIn("passive", processing_time);
					}
				}
			}
		}
	}
	
	public void deltint()
	{
		if (phaseIs("passive") && q.size() == 5)
		{
			holdIn("sending", processing_time);
		}
	}

	public message out()
	{
		message m = new message();
		
		if (phaseIs("sending"))
		{
			if(!q.isEmpty())
			{
				packet = (packet) q.removeFirst();

				int portNum = getArrivalPort(packet);

				m.add(makeContent("out" + portNum, packet));					
				
				holdIn("sending", processing_time);	
			}
			else
			{
				m.add(makeContent("out", new packet("done", 0)));
				holdIn("passive", INFINITY);	
			}
		}

		return m;
	}
	
	private int getArrivalPort(packet _packet)
	{
		int port = 0;

		for(int i=1; i<=5; i++)
		{
			if(_packet.arrival == i)
			{
				port = i;
			}
		}
		
		return port;
	}
	
	public String getTooltipText()
	{
		return
        super.getTooltipText()
        + "\n" + "queue length: " + q.size()
        + "\n" + "queue itself: " + q.toString();
	}

}



