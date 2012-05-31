package gr.forthnet.nms.svc.rrd.core.routes;

import gr.forthnet.nms.svc.rrd.common.messages.FetchGraphSimpleCommandMessage;
import gr.forthnet.nms.svc.rrd.common.messages.FetchLastCommandMessage;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.switchyard.component.camel.Route;

@Route(ServiceRRDRoute.class)
public class ServiceRRDRouteBuilder extends RouteBuilder {

	/**
	 * The Camel route is configured via this method.  The from:
	 * endpoint is required to be a SwitchYard service.
	 */
	public void configure() {
		from("switchyard://ServiceRRDRoute").routeId("ServiceRRDRoute")
		.wireTap("jms:svc_rrd_audit?connectionFactory=#JmsXA")
		.choice()
			.when(header("ServiceRRD_msg_type").isEqualTo("fetchGraphSimple"))
				.unmarshal().json(JsonLibrary.Jackson, FetchGraphSimpleCommandMessage.class)
				.to("bean:RecipientListBean?method=processSimpleGraphRequest")
				.recipientList(header("ServiceRRD_recipients"))
				.log("fetchGraphSimple request send to consumers.").endChoice()
			.when(header("ServiceRRD_msg_type").isEqualTo("fetchLast"))
				.unmarshal().json(JsonLibrary.Jackson, FetchLastCommandMessage.class)
				.to("bean:RecipientListBean?method=processFetchLastRequest")
				.recipientList(header("ServiceRRD_recipients"))
				.log("fetchLast request send to consumers.");

			//.when(header("ServiceRRD_msg_type").isEqualTo("Pong"))
			// initialize cleaning aggregate
	}
}
