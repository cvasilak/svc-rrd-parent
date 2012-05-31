package gr.forthnet.nms.svc.rrd.core.management;

import gr.forthnet.nms.svc.rrd.core.singleton.Routing;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.jms.*;
import java.util.logging.Logger;

@Singleton
@LocalBean
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@DependsOn("Routing")
public class KeepAliveTimer {

	private final static Logger logger = Logger.getLogger(KeepAliveTimer.class
			.getName());

	@Resource
	TimerService timerService;

	@Resource(mappedName = "java:/JmsXA")
	private ConnectionFactory factory;

	@Resource(mappedName = "java:/topic/svc_rrd_ctrl_bus")
	private Topic topic;

	private Connection connection;

	private Session session;

	@EJB
	Routing routing;

	@PostConstruct
	public void init() {
		logger.info("@PostConstruct()");

		try {
			connection = factory.createConnection();
			connection.start();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		} catch (JMSException e) {
			logger.info(e.getMessage());
		}
	}

	@PreDestroy
	public void cleanup() {
		logger.info("@PreDestroy()");

		try {
			if (connection != null)
				connection.close();
		} catch (JMSException e) {
			logger.info(e.getMessage());
		}
	}

	//connection-ttl is 5min
	@Schedule(minute = "*/1", hour = "*", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void pingInstance() {
		if (routing == null || connection == null) {
			return;
		}

		if (routing.getSizeOfActiveInstances() == 0) {
			logger.info("no instance to ping, aborting!");
			return;
		}

		logger.info(">>PING");
		try {
			MessageProducer producer = session.createProducer(topic);

			TextMessage pingMsg = session.createTextMessage();
			pingMsg.setStringProperty("ServiceRRD_msg_type", "PING");
			// will be used by the instances aggregator
			pingMsg.setIntProperty("ServiceRRD_completion_condition", routing.getSizeOfActiveInstances());

			pingMsg.setText("{ \"name\" : \"ping\"}");   // TODO encapsulate to a ingMessage class
			producer.send(pingMsg);

		} catch (Exception e) {
			logger.info("error sending ping on instances");
		}
	}
}
