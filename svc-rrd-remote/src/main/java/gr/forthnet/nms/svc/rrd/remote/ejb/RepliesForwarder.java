package gr.forthnet.nms.svc.rrd.remote.ejb;

import gr.forthnet.nms.svc.rrd.remote.common.ServiceRRDMessageListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.jms.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
@LocalBean
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class RepliesForwarder {

	private final static Logger logger = Logger.getLogger(RepliesForwarder.class
			.getName());
	
	Map<String, ServiceRRDMessageListener> listeners;

	@PostConstruct
	public void init() {
		logger.info("@PostConstruct()");

		listeners = new HashMap<String, ServiceRRDMessageListener>();
	}

	@PreDestroy
	public void cleanup() {
		logger.info("@PreDestroy()");

	}

	@Lock(LockType.WRITE)
	public void addListener(String uuid, ServiceRRDMessageListener listener) {
		listeners.put(uuid, listener);
	}

	@Lock(LockType.WRITE)
	public void removeListener(String uuid) {
		listeners.remove(uuid);
	}

	@Lock(LockType.READ)
	public void sendReply(String uuid, Message message) {
		
		ServiceRRDMessageListener listener = listeners.get(uuid);
		
		if (listener == null)
			return;

		listener.handleMessage(message);

		removeListener(uuid);
	}
}
