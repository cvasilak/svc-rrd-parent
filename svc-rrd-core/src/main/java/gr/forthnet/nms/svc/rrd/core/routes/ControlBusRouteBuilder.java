package gr.forthnet.nms.svc.rrd.core.routes;

import gr.forthnet.nms.svc.rrd.common.messages.RegistrationCommandMessage;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.switchyard.component.camel.Route;

@Route(ControlBusRoute.class)
public class ControlBusRouteBuilder extends RouteBuilder {
    
    /**
     * The Camel route is configured via this method.  The from:
     * endpoint is required to be a SwitchYard service.
     */
    public void configure() {
		from("switchyard://ControlBusRoute").routeId("ControlBusRoute")
		.choice()
			.when(header("ServiceRRD_msg_type").isEqualTo("register"))
				.unmarshal().json(JsonLibrary.Jackson, RegistrationCommandMessage.class)
				.to("bean:Routing?method=addRouteFromRegistrationCommandMsg")
				.log("registration completed")
			.otherwise()
				.to("log:foo?showHeaders=true");
    }
}
