package lab06;
import simView.*;
import java.awt.*;

public class Math extends ViewableDigraph
{
	
	public Math()
	{
		this("math", 0);
	}
	
	public Math(String name, double proc_t)
	{
		super(name);
		
		addInport("in"); 
		addOutport("out");
		
		ViewableAtomic r = new Router("router", proc_t);
		ViewableAtomic ad = new Add("add", proc_t);
		ViewableAtomic ac = new Accumulator("acc", proc_t);

		add(r);
		add(ad);
		add(ac);
		
		addCoupling(this, "in", r, "in");
		addCoupling(r, "out1", ad, "in");
		addCoupling(r, "out2", ac, "in");
		addCoupling(ad, "out", this, "out");
		addCoupling(ac, "out", this, "out");
	}

    
    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(463, 297);
        ((ViewableComponent)withName("acc")).setPreferredLocation(new Point(209, 183));
        ((ViewableComponent)withName("add")).setPreferredLocation(new Point(209, 63));
        ((ViewableComponent)withName("router")).setPreferredLocation(new Point(24, 123));
    }
}