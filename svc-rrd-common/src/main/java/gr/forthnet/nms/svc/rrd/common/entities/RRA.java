package gr.forthnet.nms.svc.rrd.common.entities;

public class RRA {
	
	private String name;
	private String type;
	private String descr;

	public RRA() {
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("name:").append(name).append(", type:").append(type).append(", descr:").append(descr);
		
		return builder.toString();
	}
}
