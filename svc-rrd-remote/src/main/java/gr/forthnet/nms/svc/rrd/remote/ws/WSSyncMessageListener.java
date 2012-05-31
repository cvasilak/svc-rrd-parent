package gr.forthnet.nms.svc.rrd.remote.ws;

import gr.forthnet.nms.svc.rrd.remote.common.ServiceRRDMessageListener;

import javax.jms.Message;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class WSSyncMessageListener implements ServiceRRDMessageListener {

	private final static Logger logger = Logger.getLogger(WSSyncMessageListener.class
			.getName());

	private BlockingQueue<Message> queue;

	public WSSyncMessageListener(BlockingQueue<Message> queue) {
		this.queue = queue;
	}

	@Override
	public void handleMessage(Message message) {
		try {
			queue.put(message); //inform ws thread to unblock and serve
		} catch (InterruptedException e) {
			logger.info(e.getMessage());
		}
	}
}
