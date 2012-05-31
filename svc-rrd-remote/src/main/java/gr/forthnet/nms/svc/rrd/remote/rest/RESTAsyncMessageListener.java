package gr.forthnet.nms.svc.rrd.remote.rest;

import gr.forthnet.nms.svc.rrd.remote.common.ServiceRRDMessageListener;
import org.jboss.resteasy.spi.AsynchronousResponse;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

public class RESTAsyncMessageListener implements ServiceRRDMessageListener {

	private final static Logger logger = Logger.getLogger(RESTAsyncMessageListener.class
			.getName());

	private AsynchronousResponse response;

	public RESTAsyncMessageListener(AsynchronousResponse response) {
		this.response = response;
	}

	@Override
	public void handleMessage(Message message) {
		if (response == null)
			return;

		Response body = null;

		try {
			if (message instanceof TextMessage) {
				body = Response.ok(((TextMessage) message).getText()).type(MediaType.TEXT_PLAIN).build();
			} else if (message instanceof BytesMessage) {
				BytesMessage graphStream = (BytesMessage) message;
				byte graphBin[] = new byte[(int) graphStream.getBodyLength()];
				graphStream.readBytes(graphBin);

				body = Response.ok(graphBin).type("image/png").build();
			}

			if (body != null)
				response.setResponse(body);

		} catch (JMSException e) {
			logger.info(e.getMessage());
		}
	}
}
