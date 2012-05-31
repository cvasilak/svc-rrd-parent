package gr.forthnet.nms.svc.rrd.core.beans;

import gr.forthnet.nms.svc.rrd.common.entities.RRALast;
import gr.forthnet.nms.svc.rrd.common.messages.FetchLastCommandMessageReply;
import gr.forthnet.nms.svc.rrd.common.util.JsonUtil;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class ServiceRRDAggregationStrategy implements AggregationStrategy {
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (oldExchange == null) {
			return newExchange;
		}

		FetchLastCommandMessageReply aggregateReply = null, receivedReply = null;
		
		String aggregateReplyBody = oldExchange.getIn().getBody(String.class);
		String receivedReplyBody = newExchange.getIn().getBody(String.class);

		aggregateReply  = JsonUtil.getInstance().fromJSON(aggregateReplyBody, FetchLastCommandMessageReply.class);
		receivedReply  = JsonUtil.getInstance().fromJSON(receivedReplyBody, FetchLastCommandMessageReply.class);

		for (RRALast rra: receivedReply.getRRAs()) {
			aggregateReply.getRRAs().add(rra);
		}

		oldExchange.getIn().setBody(JsonUtil.getInstance().toJSON(aggregateReply));

		return oldExchange;
	}
}
