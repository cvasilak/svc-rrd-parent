package gr.forthnet.nms.svc.rrd.core.beans;

import gr.forthnet.nms.svc.rrd.common.messages.FetchGraphSimpleCommandMessage;
import gr.forthnet.nms.svc.rrd.common.messages.FetchLastCommandMessage;
import gr.forthnet.nms.svc.rrd.common.util.JsonUtil;
import gr.forthnet.nms.svc.rrd.core.singleton.Routing;
import gr.forthnet.nms.svc.rrd.core.util.StringUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Named("RecipientListBean")
@ApplicationScoped
public class RecipientListBean {
	private final static Logger logger = Logger.getLogger(RecipientListBean.class
			.getName());

	@Inject
	Routing routing;
	
	public String processSimpleGraphRequest(FetchGraphSimpleCommandMessage message, Exchange exchange) {
		String neId = message.getNeId();

		if (!routing.isValidAndExists(neId))
			return null; // this will throw exception

		String group = message.getGroup();
		
		String queue = routing.getQueueForNEGroup(neId, group);

		Message out = exchange.getOut();

		// correlation_id uniquely identifies the client request
		// and is set by the client
		out.setHeader("ServiceRRD_correlation_id", exchange.getIn().getHeader("ServiceRRD_correlation_id"));
		out.setHeader("ServiceRRD_recipients", queue);

		out.setBody(JsonUtil.getInstance().toJSON(message));

		return queue;
	}

	public void processFetchLastRequest(FetchLastCommandMessage message, Exchange exchange) {
		String neId = message.getNeId();

		if (!routing.isValidAndExists(neId))
			return;

		Set<String> groups = message.getGroups();
		Set<String> rras = message.getRras();

		// Set so that we won't have multiple
		// deliveries on the same queue.
		Set<String> recipients = new HashSet<String>();

		if (groups == null && rras == null) {
			return;
		}

		if (groups != null) {
			for(String group: groups) {
				String queue = routing.getQueueForNEGroup(neId, group);
				if (queue != null)
					recipients.add(queue);
			}
		} else {
			for(String rra: rras) {
				String queue = routing.getQueueForNERRA(neId, rra);
				if (queue != null)
					recipients.add(queue);
			}
		}

		Message out = exchange.getOut();

		// correlation_id uniquely identifies the client request
		// and is set by the client
		out.setHeader("ServiceRRD_correlation_id", exchange.getIn().getHeader("ServiceRRD_correlation_id"));
		out.setHeader("ServiceRRD_recipients", StringUtils.tokenizeList(recipients));
		out.setHeader("ServiceRRD_completion_condition", recipients.size());

		out.setBody(JsonUtil.getInstance().toJSON(message));
	}
}
