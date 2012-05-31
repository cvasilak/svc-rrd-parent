package gr.forthnet.nms.svc.rrd.core.management.mbeans;

public interface RoutingMonitorMXBean {

	public void clear();

	public String dumpActiveInstances();
	
	public String dumpRoutingMap();
	
	public String dumpActivePrefixes();
	
	public String dumpQueuesForNE(String neId);
	
	public String dumpQueueForNEGroup(String neId, String groupName);
	
	public String dumpQueueForNERRA(String neId, String rraName);
	
	public String dumpRRASForNE(String neId);

	public void pingInstances();

	
}
	
