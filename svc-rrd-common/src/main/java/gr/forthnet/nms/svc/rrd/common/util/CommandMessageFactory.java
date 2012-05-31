package gr.forthnet.nms.svc.rrd.common.util;

import gr.forthnet.nms.svc.rrd.common.entities.NE;
import gr.forthnet.nms.svc.rrd.common.messages.FetchGraphSimpleCommandMessage;
import gr.forthnet.nms.svc.rrd.common.messages.FetchLastCommandMessage;
import gr.forthnet.nms.svc.rrd.common.messages.RegistrationCommandMessage;
import gr.forthnet.nms.svc.rrd.common.messages.RegistrationCommandMessageReply;

import java.util.Set;

public class CommandMessageFactory {

	public static RegistrationCommandMessage createRegistrationCommandMessage(
			String instanceId, String listeningQueue, Set<NE> nes) {
		RegistrationCommandMessage message = new RegistrationCommandMessage();

		message.setInstanceId(instanceId);
		message.setListeningQueue(listeningQueue);
		message.setNes(nes);

		return message;
	}
	
	public static RegistrationCommandMessageReply createRegistrationCommandMessageReply(
			String instanceId, boolean status) {
		RegistrationCommandMessageReply message = new RegistrationCommandMessageReply();

		message.setInstanceId(instanceId);
		message.setStatus(true);

		return message;
	}
	
	public static FetchLastCommandMessage createRRDLastCommandMessage(String neId, String consolFunc, long start, long end, Set<String> groups, Set<String> rras) {
		FetchLastCommandMessage message = new FetchLastCommandMessage();
		
		message.setNeId(neId);
		message.setConsolFunc(consolFunc);
		message.setStart(start);
		message.setEnd(end);
		message.setGroups(groups);
		message.setRras(rras);

		return message;
	}

	public static FetchGraphSimpleCommandMessage createRRDGraphSimpleCommandMessage(String neId, String group, int timespan, String titleX, String titleY) {
		FetchGraphSimpleCommandMessage message = new FetchGraphSimpleCommandMessage();

		message.setNeId(neId);
		message.setGroup(group);
		message.setTimespan(timespan);
		message.setTitleX(titleX);
		message.setTitleY(titleY);

		return message;
	}
}
