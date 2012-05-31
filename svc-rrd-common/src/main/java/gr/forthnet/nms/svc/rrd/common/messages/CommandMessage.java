package gr.forthnet.nms.svc.rrd.common.messages;

public abstract class CommandMessage {

	private String name;

	public CommandMessage() {
	}

	public CommandMessage(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
