package ca.teamdman.pharesoint.payloads;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ca.teamdman.pharesoint.payloads.handlers.AddWaypointHandler;
import ca.teamdman.pharesoint.payloads.handlers.RequestWaypointsHandler;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class PayloadHandlerController {
	private static List<IPayloadHandler> handlers = new ArrayList<IPayloadHandler>();
	public static final String PAYLOAD_IDENTIFIER = "☺";//PhareSoint.MODID.toUpperCase() + "_PAYLOAD";
	static {
		registerHandler(new AddWaypointHandler());
		registerHandler(new RequestWaypointsHandler());
	}
	
	public static void registerHandler(IPayloadHandler c) {
		handlers.add(c);
	}
	
	public static Optional<IPayloadHandler> getCommand(Payload payload) {
		return handlers.stream()
				.filter(c -> c.getName().equals(payload.route))
				.findFirst();
	}
	
	public static void handlePayload(Payload payload) {
		getCommand(payload).ifPresent(c -> c.handle(payload));
	}
	
	
	public static void identifyAndHandlePayload(ClientChatReceivedEvent e) {
		if (!(e.message instanceof ChatComponentTranslation))
			return;
		ChatComponentTranslation msg = (ChatComponentTranslation) e.message;
		if (!msg.getKey().contentEquals("commands.message.display.incoming"))
			return;
		if (msg.getFormatArgs().length != 2)
			return;
		if (!(msg.getFormatArgs()[0] instanceof ChatComponentText) || !(msg.getFormatArgs()[1] instanceof ChatComponentText))
			return;
		
		ChatComponentText author = (ChatComponentText) msg.getFormatArgs()[0];
		ChatComponentText contents = (ChatComponentText) msg.getFormatArgs()[1];
		@SuppressWarnings("unchecked")
		List<ChatComponentText> siblings = (List<ChatComponentText>) contents.getSiblings().stream()
				.filter(x -> x instanceof ChatComponentText)
				.collect(Collectors.toList());
		
		if (siblings.size() < 3)
			return;
		if (!siblings.get(0).getUnformattedText().equals(PAYLOAD_IDENTIFIER))
			return;
		
		String route = ((ChatComponentText) siblings.get(2)).getUnformattedText();
		StringBuilder builder = new StringBuilder();
		for (int i=4; i<siblings.size(); i++) {
			Object v = siblings.get(i);
			if (v instanceof ChatComponentText) {
				builder.append(((ChatComponentText) v).getUnformattedText());
			}
		}
		
		handlePayload(new Payload(author.getUnformattedText(), route, builder.toString()));
	}
}
