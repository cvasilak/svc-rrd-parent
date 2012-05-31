package gr.forthnet.nms.svc.rrd.common.messages;

import java.util.Set;

public class FetchLastCommandMessage extends CommandMessage {

	private String neId;
	private String consolFunc;
	private long start;
	private long end;
	private Set<String> groups;
	private Set<String> rras;

	public FetchLastCommandMessage() {
		super("fetchLast");
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public String getConsolFunc() {
		return consolFunc;
	}

	public void setConsolFunc(String consolFunc) {
		this.consolFunc = consolFunc;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public Set<String> getGroups() {
		return groups;
	}

	public void setGroups(Set<String> groups) {
		this.groups = groups;
	}

	public Set<String> getRras() {
		return rras;
	}

	public void setRras(Set<String> rras) {
		this.rras = rras;
	}
}