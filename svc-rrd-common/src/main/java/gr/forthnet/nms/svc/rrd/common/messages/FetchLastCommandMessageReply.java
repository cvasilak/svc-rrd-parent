package gr.forthnet.nms.svc.rrd.common.messages;

import gr.forthnet.nms.svc.rrd.common.entities.RRALast;

import java.util.Set;

public class FetchLastCommandMessageReply extends CommandMessage {
	private String neId;
	private Set<RRALast> rras;

	public FetchLastCommandMessageReply() {
		super("fetchRRA");
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public Set<RRALast> getRRAs() {
		return rras;
	}

	public void setRRAs(Set<RRALast> rras) {
		this.rras = rras;
	}
}
