package gr.forthnet.nms.svc.rrd.remote.common;

import javax.jms.Message;

public interface ServiceRRDMessageListener {
	public void handleMessage(Message msg);
}
