package gr.forthnet.nms.svc.rrd.common.messages;

public class FetchGraphSimpleCommandMessage extends CommandMessage {

	private String neId;
	private int timespan;
	private String titleX;
	private String titleY;
	private String group;

	public FetchGraphSimpleCommandMessage() {
		super("fetchGraphSimple");
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public int getTimespan() {
		return timespan;
	}

	public void setTimespan(int timespan) {
		this.timespan = timespan;
	}

	public String getTitleX() {
		return titleX;
	}

	public void setTitleX(String titleX) {
		this.titleX = titleX;
	}

	public String getTitleY() {
		return titleY;
	}

	public void setTitleY(String titleY) {
		this.titleY = titleY;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}