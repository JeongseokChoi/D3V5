package RAUDT.RAUDT;
import GenCol.*;

public class Info extends entity
{   
	public int vm_id;
	public int CPU, RAM, NetResponse;
	public int queue_length;
	
	public Info(String name)
	{  
		super(name);
		
		CPU = 0;
		RAM = 0;
		NetResponse = 0;
		
		queue_length = 0;
	}
	
	public Info(int _vm_id, int _CPU, int _RAM, int _NetResponse)
	{
		super("VM#" + _vm_id);
		
		vm_id = _vm_id;
		CPU = _CPU;
		RAM = _RAM;
		NetResponse = _NetResponse;
		
		queue_length = 0;
	}
	
	public Info(int _queue_length)
	{
		super("queue_length: " + _queue_length);
		
		CPU = 0;
		RAM = 0;
		NetResponse = 0;
		
		queue_length = _queue_length;
	}
}
