package gr.forthnet.nms.svc.rrd.common.messages;

import java.util.HashMap;

public abstract class CommandMessageReply {
	
	private String name;
	public boolean success;
	
	private HashMap<String, String> params;
	
	public CommandMessageReply(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean status) {
		this.success = status;
	}

	public HashMap<String, String> getParams() {
		return params;
	}

	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}
}
