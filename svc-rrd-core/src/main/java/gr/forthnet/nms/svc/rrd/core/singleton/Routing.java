package gr.forthnet.nms.svc.rrd.core.singleton;

import gr.forthnet.nms.svc.rrd.common.entities.Group;
import gr.forthnet.nms.svc.rrd.common.entities.NE;
import gr.forthnet.nms.svc.rrd.common.entities.RRA;
import gr.forthnet.nms.svc.rrd.common.messages.RegistrationCommandMessage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.*;
import java.util.logging.Logger;

@Singleton
@LocalBean
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Named("Routing")
@ApplicationScoped
public class Routing {

	private final static Logger logger = Logger.getLogger(Routing.class
			.getName());

	// HashMap<prefix-id, HashMap<queue, NE>>();
	private Map<String, Map<String, NE>> routing;
	
	private Map<String, Set<String>> activeInstances;
	

	@PostConstruct
	public void init() {
		logger.info("@PostConstruct()");

		routing = new HashMap<String, Map<String, NE>>();
		
		activeInstances = new HashMap<String, Set<String>>();
	}

	@PreDestroy
	public void cleanup() {
		logger.info("@PreDestroy()");

	}
	
	@Lock(LockType.WRITE)
	public void addRouteFromRegistrationCommandMsg(
			RegistrationCommandMessage message) {
		String listenQueue = message.getListeningQueue();
		
		Set<String> instances = activeInstances.get(listenQueue);
			
		if (instances == null) {
			instances = new HashSet<String>();
		} 
		
		instances.add(message.getInstanceId());
		activeInstances.put(listenQueue, instances);

		Set<NE> nes = message.getNes();
		for (NE ne : nes) {
			String prefixId = ne.getPrefixId();

			/*
			 * 100 -> bw -> input, output 100 -> cpu -> cpu5min, cpu5sec 197 ->
			 * dslam -> foo1, foo2
			 */

			Map<String, NE> details;

			if (routing.containsKey(prefixId)) {
				details = routing.get(prefixId);

			} else {
				details = new HashMap<String, NE>();

			}

			details.put(listenQueue, ne);
			routing.put(prefixId, details);
		}
	}

	@Lock(LockType.WRITE)
	public void clear() {
		routing.clear();
		activeInstances.clear();
	}

	@Lock(LockType.READ)
	public Map<String, Set<String>> getActiveInstances() {
		return Collections.unmodifiableMap(activeInstances);
	}

	@Lock(LockType.READ)
	public Map<String, Map<String, NE>> getRoutingMap() {
		return Collections.unmodifiableMap(routing);
	}
	
	@Lock(LockType.READ)
	public Set<String> getActivePrefixs() {
		Set<String> prefixs = new HashSet<String>();

		prefixs.addAll(routing.keySet());

		return prefixs;
	}

	@Lock(LockType.READ)
	public Set<String> getQueuesForNE(String neID) {
		if (!isValidAndExists(neID))
			return null;
		
		return routing.get(neID.substring(0, 3)).keySet();
	}

	@Lock(LockType.READ)
	public String getQueueForNEGroup(String neId, String groupName) {
		if (!isValidAndExists(neId))
			return null;
		
		// NE prefix-id, Group[group-name, Set<RRAs>]
		Map<String, NE> nes = routing.get(neId.substring(0, 3));
		
		for (Map.Entry<String /*queue*/, NE> entry : nes.entrySet()) {
			String queue = entry.getKey();
			NE ne = entry.getValue();
			
			Set<Group> groups = ne.getGroups();
			
			for (Group group: groups) {
				if (group.getName().equals(groupName))
					return queue;
			}
		}

		return null;
	}
	
	@Lock(LockType.READ)
	public String getQueueForNERRA(String neId, String rraName) {
		if (!isValidAndExists(neId))
			return null;

		// NE prefix-id, Group[group-name, Set<RRAs>]
		Map<String, NE> nes = routing.get(neId.substring(0, 3));
		
		for (Map.Entry<String /*queue*/, NE> entry : nes.entrySet()) {
			String queue = entry.getKey();
			NE ne = entry.getValue();
			
			Set<Group> groups = ne.getGroups();
			
			for (Group group: groups) {
				Set<RRA> rras = group.getRras();
				
				for (RRA rra: rras) {
					if (rra.getName().equals(rraName))
						return queue;
				}
			}
		}

		return null;
	}

	@Lock(LockType.READ)
	public Set<Group> getRRASforNE(String neId) {
		if (!isValidAndExists(neId))
			return null;
		
		Map<String, NE> nes = routing.get(neId.substring(0, 3));
		Set<Group> groups = new HashSet<Group>();
			
		for (NE ne : nes.values()) {
			groups.addAll(ne.getGroups());
		}

		return groups;
	}

	@Lock(LockType.READ)
	public int getSizeOfActiveInstances() {
		int size = 0;
		
		for (Set<String> active: activeInstances.values()) {
			size += active.size();
		}

		return size;
	}
	
	public boolean isValidAndExists(String neId) {
		if (neId == null || (neId != null && neId.length() < 3)) {
			return false;
		}

		return routing.containsKey(neId.substring(0, 3));
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (Map.Entry<String, Map<String, NE>> r : routing.entrySet()) {
			builder.append("prefix-id:" + r.getKey()).append("\n");

			for (Map.Entry<String, NE> v : r.getValue().entrySet())
				builder.append("\t queue:").append(v.getKey()).append("\n")
						.append(v.getValue()).append("\n");

			builder.append("\n");
		}

		return builder.toString();

	}
}
