package gr.forthnet.nms.svc.rrd.core.routes;

import gr.forthnet.nms.svc.rrd.core.beans.ServiceRRDAggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.switchyard.component.camel.Route;

@Route(ServiceRRDAggregationRoute.class)
public class ServiceRRDAggregationRouteBuilder extends RouteBuilder {

	/**
	 * The Camel route is configured via this method.  The from:
	 * endpoint is required to be a SwitchYard service.
	 */
	public void configure() {
		// add when()
		// filter only fetchLast messages(for now)
		//
		from("switchyard://ServiceRRDAggregationRoute").routeId("ServiceRRDAggregationRoute")
		.wireTap("jms:svc_rrd_audit?connectionFactory=#JmsXA")
		.aggregate(header("ServiceRRD_correlation_id"), new ServiceRRDAggregationStrategy())
			.completionSize(header("ServiceRRD_completion_condition"))
			.completionTimeout(5000)
			.discardOnCompletionTimeout() // TODO maybe its better to send at lease one reply
		.to("jms:svc_rrd_reply?connectionFactory=#JmsXA");
	}
}
