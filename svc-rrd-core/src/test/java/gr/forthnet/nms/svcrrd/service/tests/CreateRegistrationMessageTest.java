package gr.forthnet.nms.svcrrd.service.tests;

import gr.forthnet.nms.svc.rrd.common.entities.Group;
import gr.forthnet.nms.svc.rrd.common.entities.NE;
import gr.forthnet.nms.svc.rrd.common.entities.NE.Type;
import gr.forthnet.nms.svc.rrd.common.entities.RRA;
import gr.forthnet.nms.svc.rrd.common.messages.RegistrationCommandMessage;
import gr.forthnet.nms.svc.rrd.common.util.CommandMessageFactory;

import java.util.*;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.client.HornetQTopic;
import org.junit.Before;
import org.junit.Test;

public class CreateRegistrationMessageTest {
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

	@Test
	public void test() {

		try {
			Set<NE> nes;
			Set<Group> groups;
			Set<RRA> rras;

			Group group;
			RRA rra;

			// casper

			nes = new HashSet<NE>();

			NE ne = new NE();
			ne.setPrefixId("100");

			groups = new HashSet<Group>();

			group = new Group();
			group.setName("bw");

			rras = new HashSet<RRA>();

			rra = new RRA();
			rra.setName("input");
			rra.setType(Type.BANDWIDTH.toString());
			rra.setDescr("In");

			rras.add(rra);

			rra = new RRA();
			rra.setName("output");
			rra.setType(Type.BANDWIDTH.toString());
			rra.setDescr("Out");

			rras.add(rra);

			group.setRras(rras);

			groups.add(group);

			ne.setGroups(groups);

			nes.add(ne);

			// -------------------------------
			group = new Group();
			group.setName("fooboo");

			rras = new HashSet<RRA>();

			rra = new RRA();
			rra.setName("foo");
			rra.setType(Type.BANDWIDTH.toString());
			rra.setDescr("Foofootos");

			rras.add(rra);

			rra = new RRA();
			rra.setName("boo");
			rra.setType(Type.BANDWIDTH.toString());
			rra.setDescr("Boobootos");

			rras.add(rra);

			group.setRras(rras);

			groups.add(group);

			nes.add(ne);

			RegistrationCommandMessage message1 = CommandMessageFactory
					.createRegistrationCommandMessage(UUID.randomUUID()
							.toString(), "/queue/casper/svc_rrd", nes);


			// ********************************************************************
			// klnnms02
			nes = new HashSet<NE>();

			ne = new NE();
			ne.setPrefixId("100");

			group = new Group();
			group.setName("cpu");

			rras = new HashSet<RRA>();

			rra = new RRA();
			rra.setName("cpu5sec");
			rra.setType(Type.BANDWIDTH.toString());
			rra.setDescr("CPU 5sec");

			rras.add(rra);

			rra = new RRA();
			rra.setName("cpu1min");
			rra.setType(Type.BANDWIDTH.toString());
			rra.setDescr("CPU 1min");

			rras.add(rra);

			rra = new RRA();
			rra.setName("cpu5min");
			rra.setType(Type.BANDWIDTH.toString());
			rra.setDescr("CPU 5min");

			rras.add(rra);

			group.setRras(rras);

			groups = new HashSet<Group>();
			groups.add(group);

			ne.setGroups(groups);

			nes.add(ne);

			ne = new NE();
			ne.setPrefixId("197");

			group = new Group();
			group.setName("dslam");

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

			rras.add(rra);

			group.setRras(rras);

			groups = new HashSet<Group>();
			groups.add(group);

			ne.setGroups(groups);

			nes.add(ne);

			RegistrationCommandMessage message2 = CommandMessageFactory
					.createRegistrationCommandMessage(UUID.randomUUID()
							.toString(), "/queue/klnnms01/svc_rrd", nes);

			TextMessage msg;

			try {

				final MessageProducer producer = session
						.createProducer(new HornetQTopic(REGISTRATION_TOPIC_NAME));
				
				msg = session.createTextMessage();
				msg.setStringProperty("type", "register");
				msg.setText(JsonUtil.getInstance().toJSON(message1));
				
				producer.send(msg);
				
				msg = session.createTextMessage();
				msg.setStringProperty("type", "register");
				msg.setText(JsonUtil.getInstance().toJSON(message2));

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
