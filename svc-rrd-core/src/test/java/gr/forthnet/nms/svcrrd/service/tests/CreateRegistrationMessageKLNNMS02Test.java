package gr.forthnet.nms.svcrrd.service.tests;

import gr.forthnet.nms.svc.rrd.common.entities.Group;
import gr.forthnet.nms.svc.rrd.common.entities.NE;
import gr.forthnet.nms.svc.rrd.common.entities.NE.Type;
import gr.forthnet.nms.svc.rrd.common.entities.RRA;
import gr.forthnet.nms.svc.rrd.common.messages.RegistrationCommandMessage;
import gr.forthnet.nms.svc.rrd.common.util.CommandMessageFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javax.jms.*;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.client.HornetQTopic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CreateRegistrationMessageKLNNMS02Test {

	Logger logger = Logger.getLogger(getClass().getName());

	private static final String REGISTRATION_TOPIC_NAME = "svc_rrd_ctrl_bus";

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

			session = connection.createSession(false, DeliveryMode.NON_PERSISTENT);

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
	public void test() {

		try {
			Set<NE> nes;
			Set<Group> groups;
			Set<RRA> rras;

			Group group;
			RRA rra;

			NE ne;

			nes = new HashSet<NE>();
			groups = new HashSet<Group>();

			ne = new NE();
			ne.setPrefixId("100");

			// cpu5sec             cpu1min             cpu5min
			// ********************************************************************

			group = new Group();
			group.setName("cpu");

			rras = new HashSet<RRA>();

			rra = new RRA();
			rra.setName("cpu5sec");
			rra.setType(Type.ABSOLUTE.toString());
			rra.setDescr("CPU 5sec");

			rras.add(rra);

			rra = new RRA();
			rra.setName("cpu1min");
			rra.setType(Type.ABSOLUTE.toString());
			rra.setDescr("CPU 1min");

			rras.add(rra);

			rra = new RRA();
			rra.setName("cpu5min");
			rra.setType(Type.ABSOLUTE.toString());
			rra.setDescr("CPU 5min");

			rras.add(rra);

			group.setRras(rras);

			groups.add(group);


			// freemem             usedmem             memutil
			// ********************************************************************
			group = new Group();
			group.setName("mem");

			rras = new HashSet<RRA>();

			rra = new RRA();
			rra.setName("freemem");
			rra.setType(Type.ABSOLUTE.toString());
			rra.setDescr("Free Memory");

			rras.add(rra);

			rra = new RRA();
			rra.setName("usedmem");
			rra.setType(Type.ABSOLUTE.toString());
			rra.setDescr("Used Memory");

			rras.add(rra);

			rra = new RRA();
			rra.setName("memutil");
			rra.setType(Type.ABSOLUTE.toString());
			rra.setDescr("Utilization");

			rras.add(rra);

			group.setRras(rras);

			groups.add(group);

			// ********************************************************************

			ne.setGroups(groups);

			nes.add(ne);

			RegistrationCommandMessage message = CommandMessageFactory
					.createRegistrationCommandMessage(UUID.randomUUID()
							.toString(), "/queue/klnnms02/svc_rrd", nes);

			TextMessage msg;

			try {

				final MessageProducer producer = session
						.createProducer(new HornetQTopic(REGISTRATION_TOPIC_NAME));

				msg = session.createTextMessage();
				msg.setStringProperty("type", "register");

				String body = JsonUtil.getInstance().toJSON(message);

				msg.setText(body);

				//System.out.println(body);
				producer.send(msg);

				System.out.println("Message sent. Please see server console output");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
