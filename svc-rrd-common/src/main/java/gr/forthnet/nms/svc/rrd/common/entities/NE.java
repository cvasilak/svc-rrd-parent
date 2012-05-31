package gr.forthnet.nms.svc.rrd.common.entities;

import java.util.Set;

public class NE {
	
	private String prefixId;
	private Set<Group> groups;

	public static enum Type {
		BANDWIDTH,
		PERCENT,
		ABSOLUTE
	}
	
	public NE() {
	}
	
	public String getPrefixId() {
		return prefixId;
	}

	public void setPrefixId(String prefixId) {
		this.prefixId = prefixId;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> list) {
		this.groups = list;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Group group: groups) {
			builder.append(group).append("\n");
		}
		
		return builder.toString();
		
	}
}
