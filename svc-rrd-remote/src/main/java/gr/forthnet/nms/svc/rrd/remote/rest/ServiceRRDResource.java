package gr.forthnet.nms.svc.rrd.remote.rest;

import gr.forthnet.nms.svc.rrd.common.messages.FetchGraphSimpleCommandMessage;
import gr.forthnet.nms.svc.rrd.remote.common.ServiceRRDMessageListener;
import gr.forthnet.nms.svc.rrd.common.messages.FetchLastCommandMessage;
import gr.forthnet.nms.svc.rrd.common.util.CommandMessageFactory;
import gr.forthnet.nms.svc.rrd.remote.ejb.RepliesForwarder;
import gr.forthnet.nms.svc.rrd.remote.ejb.ServiceRRDRequestor;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import java.util.Set;

@Path("/")
@Stateless
public class ServiceRRDResource {

	@EJB
	ServiceRRDRequestor requestor;

	@EJB
	RepliesForwarder forwarder;

	@GET
	@Path("{neId}/fetchLast")
	@Produces("text/plain")
	public void fetchLast(final @Suspend(10000) AsynchronousResponse response,
						  @PathParam("neId") String neId,
						  @QueryParam("consolFunc") @DefaultValue("AVERAGE") String consolFunc,
						  @QueryParam("start") @DefaultValue("0") long start,
						  @QueryParam("end") @DefaultValue("0") long end,
						  @QueryParam("groups") Set<String> groups,
						  @QueryParam("rras") Set<String> rras) {

		// http://localhost:8080/svc_rrd_remote/rest/1005255/fetchLast?rras=cpu5sec&rras=cpu1min&rras=memutil
		// http://localhost:8080/svc_rrd_remote/rest/1005255/fetchLast?rras=cpu5sec&rras=cpu1min&rras=memutil&rras=cpuusage&rras=bususage

		FetchLastCommandMessage message = CommandMessageFactory.createRRDLastCommandMessage(neId, consolFunc, start, end, null, rras);
		String uuid = requestor.sendRequest(message);

		ServiceRRDMessageListener listener = new RESTAsyncMessageListener(response);
		forwarder.addListener(uuid, listener);
	}

	@GET
	@Path("{neId}/fetchGraph")
	@Produces("image/png")
	public void fetchGraph(final @Suspend(10000) AsynchronousResponse response,
						   @PathParam("neId") String neId,
						   @QueryParam("group") String group,
						   @QueryParam("timespan") @DefaultValue("0") int timespan,
						   @QueryParam("titleX") @DefaultValue("X") String titleX,
						   @QueryParam("titleX") @DefaultValue("X") String titleY) {

		// http://localhost:8080/svc_rrd_remote/rest/21100799/fetchGraph?group=bw&timespan=0&titleX=Bandwidth&titleY=bps
		// http://localhost:8080/svc_rrd_remote/rest/1005255/fetchGraph?group=cpu&timespan=0&titleX=CPU%20Utilization&titleY=bps

		FetchGraphSimpleCommandMessage message = CommandMessageFactory.createRRDGraphSimpleCommandMessage(neId, group, timespan, titleX, titleY);

		String uuid = requestor.sendRequest(message);

		ServiceRRDMessageListener listener = new RESTAsyncMessageListener(response);
		forwarder.addListener(uuid, listener);
	}
}
