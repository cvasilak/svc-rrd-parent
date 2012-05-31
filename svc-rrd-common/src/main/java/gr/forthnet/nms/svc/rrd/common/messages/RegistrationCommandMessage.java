package gr.forthnet.nms.svc.rrd.common.messages;

import gr.forthnet.nms.svc.rrd.common.entities.NE;

import java.util.Set;

public class RegistrationCommandMessage extends CommandMessage {

	private String instanceId;
	private String listeningQueue;
	private Set<NE> nes;

	public RegistrationCommandMessage() {
		super("register");
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getListeningQueue() {
		return listeningQueue;
	}

	public void setListeningQueue(String listeningQueue) {
		this.listeningQueue = listeningQueue;
	}

	public Set<NE> getNes() {
		return nes;
	}

	public void setNes(Set<NE> nes) {
		this.nes = nes;
	}
}