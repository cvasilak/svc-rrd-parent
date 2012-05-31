package gr.forthnet.nms.svc.rrd.remote.ws;

import gr.forthnet.nms.svc.rrd.common.messages.FetchGraphSimpleCommandMessage;
import gr.forthnet.nms.svc.rrd.common.messages.FetchLastCommandMessage;
import gr.forthnet.nms.svc.rrd.common.util.CommandMessageFactory;
import gr.forthnet.nms.svc.rrd.remote.common.ServiceRRDMessageListener;
import gr.forthnet.nms.svc.rrd.remote.ejb.RepliesForwarder;
import gr.forthnet.nms.svc.rrd.remote.ejb.ServiceRRDRequestor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
@Stateless
public class ServiceRRDWS {

	private final static Logger logger = Logger.getLogger(ServiceRRDWS.class
			.getName());

	@EJB
	ServiceRRDRequestor requestor;

	@EJB
	RepliesForwarder forwarder;

	@PostConstruct
	public void init() {
		logger.info("@PostConstruct()");
	}

	@PreDestroy
	public void destroy() {
		logger.info("@PreDestroy()");
	}

	@WebMethod
	public
	@WebResult
	String fetchLast(@WebParam String neId,
					 @WebParam String consolFunc,
					 @WebParam long start,
					 @WebParam long end,
					 @WebParam Set<String> groups,
					 @WebParam Set<String> rras) {

		FetchLastCommandMessage message = CommandMessageFactory.createRRDLastCommandMessage(neId, consolFunc, start, end, null, rras);
		String uuid = requestor.sendRequest(message);

		BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(1);
		ServiceRRDMessageListener listener = new WSSyncMessageListener(queue);

		forwarder.addListener(uuid, listener);

		try {
			Message reply = queue.poll(10000, TimeUnit.MILLISECONDS);

			if (reply != null)
				return ((TextMessage) reply).getText();

		} catch (InterruptedException e) {
			logger.info(e.getMessage());
		} catch (JMSException e) {
			logger.info(e.getMessage());
		}

		return null;
	}

	@WebMethod
	public
	@WebResult
	String fetchGraph(@WebParam String neId,
					  @WebParam String group,
					  @WebParam int timespan,
					  @WebParam String titleX,
					  @WebParam String titleY) {

		FetchGraphSimpleCommandMessage message = CommandMessageFactory.createRRDGraphSimpleCommandMessage(neId, group, timespan, titleX, titleY);

		String uuid = requestor.sendRequest(message);
		BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(1);
		ServiceRRDMessageListener listener = new WSSyncMessageListener(queue);

		forwarder.addListener(uuid, listener);

		try {
			Message reply = queue.poll(10000, TimeUnit.MILLISECONDS);

			if (reply != null) {
				BytesMessage graphStream = (BytesMessage) reply;
				byte graphBin[] = new byte[(int) graphStream.getBodyLength()];
				graphStream.readBytes(graphBin);

				return new sun.misc.BASE64Encoder().encode(graphBin);
			}

		} catch (InterruptedException e) {
			logger.info(e.getMessage());
		} catch (JMSException e) {
			logger.info(e.getMessage());
		}

		return null;
	}
}