package gr.forthnet.nms.svc.rrd.core.management.mbeans;

import gr.forthnet.nms.svc.rrd.common.util.JsonUtil;
import gr.forthnet.nms.svc.rrd.core.management.KeepAliveTimer;
import gr.forthnet.nms.svc.rrd.core.singleton.Routing;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

@Singleton
@LocalBean
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@DependsOn({"Routing", "KeepAliveTimer"})
public class RoutingMonitor implements RoutingMonitorMXBean {

	private final static Logger logger = Logger.getLogger(RoutingMonitor.class
			.getName());

	private MBeanServer platformMBeanServer;
	private ObjectName objectName = null;
	
	@EJB
	Routing routing;

	@EJB
	KeepAliveTimer keepAliveTimer;

	@Resource
	private SessionContext ctx;

	@PostConstruct
	public void init() {
		logger.info("@PostConstruct()");

		// set up monitoring
		try {
			objectName = new ObjectName("gr.forthnet.nms.svcrrd:name=RoutingMonitor");
			platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
			platformMBeanServer.registerMBean(ctx.getBusinessObject(RoutingMonitor.class), objectName);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Problem during registration of Monitoring into JMX:" + e);
		}
	}

	@PreDestroy
	public void cleanup() {
		logger.info("@PreDestroy()");

		try {
			platformMBeanServer.unregisterMBean(this.objectName);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Problem during unregistration of Monitoring into JMX:" + e);
		}
	}
	@Override
	public void clear() {
		routing.clear();
	}

	@Override
	public String dumpActiveInstances() {
		return JsonUtil.getInstance().toJSON(routing.getActiveInstances());
	}
	
	@Override
	public String dumpRoutingMap() {
		return JsonUtil.getInstance().toJSON(routing.getRoutingMap());
	}

	@Override
	public String dumpActivePrefixes() {
		return JsonUtil.getInstance().toJSON(routing.getActivePrefixs());
	}
	
	@Override
	public String dumpQueuesForNE(String neId) {
		return JsonUtil.getInstance().toJSON(routing.getQueuesForNE(neId));
	}

	@Override
	public String dumpQueueForNEGroup(String neId, String groupName) {
		return JsonUtil.getInstance().toJSON(routing.getQueueForNEGroup(neId, groupName));
	}
	
	@Override
	public String dumpQueueForNERRA(String neId, String rraName) {
		return JsonUtil.getInstance().toJSON(routing.getQueueForNERRA(neId, rraName));
	}
	
	@Override
	public String dumpRRASForNE(String neId) {
		return JsonUtil.getInstance().toJSON(routing.getRRASforNE(neId));
	}

	@Override
	public void pingInstances() {
		keepAliveTimer.pingInstance();
	}
}
