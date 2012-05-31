package gr.forthnet.nms.svc.rrd.remote.ejb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Logger;

@MessageDriven(name = "ServiceRRDReplyListener", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/svc_rrd_reply"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ServiceRRDReplyListener implements MessageListener {

	private final static Logger logger = Logger.getLogger(ServiceRRDReplyListener.class
			.getName());
	
	@EJB
	RepliesForwarder forwarder;
	
	@Override
	public void onMessage(Message message) {
		try {
			
			String uuid = message.getStringProperty("ServiceRRD_correlation_id");

			forwarder.sendReply(uuid, message);

		} catch (JMSException e) {
			logger.info(e.getMessage());
		}
	}
}
