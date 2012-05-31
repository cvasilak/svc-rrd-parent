package gr.forthnet.nms.svc.rrd.common.messages;

public class RegistrationCommandMessageReply extends CommandMessage {

	private String instanceId;
	private boolean status;

	public RegistrationCommandMessageReply() {
		super("register");
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}