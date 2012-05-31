package gr.forthnet.nms.svc.rrd.remote.ejb;

import gr.forthnet.nms.svc.rrd.common.messages.CommandMessage;
import gr.forthnet.nms.svc.rrd.common.util.JsonUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.*;
import java.util.UUID;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class ServiceRRDRequestor {

	private final static Logger logger = Logger.getLogger(ServiceRRDRequestor.class
			.getName());

	@Resource(mappedName = "java:/JmsXA")
	private ConnectionFactory factory;

	/* Queues */
	@Resource(mappedName = "java:/queue/svc_rrd")
	private Queue svcRRDQueue;

	@PostConstruct
	public void init() {
		logger.info("@PostConstruct()");
	}

	@PreDestroy
	public void cleanup() {
		logger.info("@PreDestroy()");
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public String sendRequest(CommandMessage message) {
		Connection connection = null;

		try {
			connection = factory.createConnection();
			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			MessageProducer producer = session.createProducer(svcRRDQueue);

			TextMessage reqMsg;

			// this will uniquelly identify the request
			String UIID = UUID.randomUUID().toString();

			reqMsg = session.createTextMessage();
			reqMsg.setStringProperty("ServiceRRD_msg_type", message.getName());
			reqMsg.setStringProperty("ServiceRRD_correlation_id", UIID);

			String body = JsonUtil.getInstance().toJSON(message);

			reqMsg.setText(body);

			logger.info("SEND:\n" + body);

			producer.send(reqMsg);

			return UIID;

		} catch (JMSException e) {
			logger.info(e.getMessage());

		}  finally {
			try {
				if (connection != null)
					connection.close();

			} catch (JMSException e) {
				logger.info(e.getMessage());
			}
		}

		return null;
	}
}
