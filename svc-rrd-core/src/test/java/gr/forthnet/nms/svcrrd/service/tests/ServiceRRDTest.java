package gr.forthnet.nms.svcrrd.service.tests;

import gr.forthnet.nms.svc.rrd.common.messages.FetchGraphSimpleCommandMessage;
import gr.forthnet.nms.svc.rrd.common.messages.FetchLastCommandMessage;
import gr.forthnet.nms.svc.rrd.common.util.CommandMessageFactory;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.client.HornetQQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.*;
import java.io.FileOutputStream;
import java.util.*;
import java.util.logging.Logger;

public class ServiceRRDTest {
	Logger logger = Logger.getLogger(getClass().getName());

	private static final String SERVICE_QUEUE = "svc_rrd";
	private static final String SERVICE_REPLY_QUEUE = "svc_rrd_reply";

	private HornetQConnectionFactory hornetQConnectionFactory;
	private Connection connection;
	private Session session;
	
	@Before
	public void setUp() {
		// Set up connection to the broker
		try {
			Map<String, Object> connectionParams = new HashMap<String, Object>();

			connectionParams.put(org.hornetq.integration.transports.netty.TransportConstants.HOST_PROP_NAME, "localhost");
			connectionParams.put(org.hornetq.integration.transports.netty.TransportConstants.PORT_PROP_NAME, 5445);

			TransportConfiguration transport = new TransportConfiguration(NettyConnectorFactory.class.getName(), connectionParams);

			hornetQConnectionFactory = new HornetQConnectionFactory(false, transport);

			connection = hornetQConnectionFactory.createConnection();
			connection.start();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() {
		try {
			if (session != null) {
				session.close();
			}

			if (connection != null) {
				connection.close();
			}

			if (hornetQConnectionFactory != null) {
				hornetQConnectionFactory.close();
			}

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFetchLast() {
		try {

			String neId = "1005255";    // KLNNMS02(cpuusage=YES cpu1min=YES memutil=YES) KLNNMS05(cpuusage=YES bususage=YES)
			//String neId = "1006119";    // KLNNMS02(cpuusage=YES cpu1min=YES memutil=YES  KLNNMS05(cpuusage=NO bususage=NO)

			Set<String> rras = new HashSet<String>();
			// klnnms02
			rras.add("cpu5sec");
			rras.add("cpu1min");
			rras.add("memutil");

			// klnnms05
			rras.add("cpuusage");
			rras.add("bususage");

			FetchLastCommandMessage message = CommandMessageFactory.createRRDLastCommandMessage(neId, "AVERAGE", 0, 0, null, rras);

			MessageProducer producer=null;
			MessageConsumer consumer = null;

			// time to send the JMS request
			try {
				TextMessage reqMsg, replyMsg;

				producer = session
						.createProducer(new HornetQQueue(SERVICE_QUEUE));

				// this will uniquelly identify the request
				String UIID = UUID.randomUUID().toString();

				reqMsg = session.createTextMessage();
				reqMsg.setStringProperty("ServiceRRD_msg_type", "fetchLast");
				reqMsg.setStringProperty("ServiceRRD_correlation_id", UIID);

				String body = JsonUtil.getInstance().toJSON(message);

				reqMsg.setText(body);

				logger.info("SEND:\n" + body);
			
				producer.send(reqMsg);

				consumer = session
						.createConsumer(new HornetQQueue(SERVICE_REPLY_QUEUE), "ServiceRRD_correlation_id = '" + UIID + "'");

				replyMsg = (TextMessage)consumer.receive(30000);

				if (replyMsg == null) {
					logger.info("ServiceRRD timeout on receive()");
				} else {
					logger.info("REPLY:\n" + replyMsg.getText());
				}
				

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (producer != null)
						producer.close();
					if (consumer != null)
						consumer.close();
				} catch (JMSException e) {}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//@Test
	public void testFetchGraphSimple() {
		try {

			String neId = "21100799";
			String group = "bw";    // interesting for the BW

			String titleX = "Bandwidth";
			String titleY = "bps";
			int timespan = 0;   // Daily

			/*
			String neId = "1005255";
			String group = "cpu";    // interesting for the CPU

			String titleX = "CPU Utilization";
			String titleY = "Utilization";
			int timespan = 0;   // Daily
             */
			FetchGraphSimpleCommandMessage message = CommandMessageFactory.createRRDGraphSimpleCommandMessage(neId, group, timespan, titleX, titleY);

			MessageProducer producer = null;
			MessageConsumer consumer = null;

			try {
				// time to send the JMS request
				TextMessage reqMsg;
				Message replyMsg;

				producer = session
						.createProducer(new HornetQQueue(SERVICE_QUEUE));

				// this will uniquelly identify the request
				String UIID = UUID.randomUUID().toString();

				reqMsg = session.createTextMessage();
				reqMsg.setStringProperty("ServiceRRD_msg_type", "fetchGraphSimple");
				reqMsg.setStringProperty("ServiceRRD_correlation_id", UIID);

				String body = JsonUtil.getInstance().toJSON(message);

				reqMsg.setText(body);

				logger.info("SEND:\n" + body);

				producer.send(reqMsg);

				consumer = session
						.createConsumer(new HornetQQueue(SERVICE_REPLY_QUEUE), "ServiceRRD_correlation_id = '" + UIID + "'");

				replyMsg = consumer.receive(30000);

				if (replyMsg == null) {
					logger.info("ServiceRRD timeout on receive()");
				} else {
					if (replyMsg instanceof BytesMessage) {

						BytesMessage graphStream = (BytesMessage) replyMsg;

						byte[] graph = new byte[(int) graphStream.getBodyLength()];
						graphStream.readBytes(graph);

						FileOutputStream image = new FileOutputStream(
								"/Users/cvasilak/Temp/svc-rrd-images/" + neId + "_" + group + "_" + timespan+ ".png");

						image.write(graph);
						image.close();

						logger.info("image retrieved and saved!");

					} else if (replyMsg instanceof TextMessage) {  // the server responded with an error
						logger.info(((TextMessage)replyMsg).getText());
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (producer != null)
						producer.close();
					if (consumer != null)
						consumer.close();
				} catch (JMSException e) {}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
