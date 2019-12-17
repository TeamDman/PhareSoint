package ca.teamdman.pharesoint.payloads.handlers;

import ca.teamdman.pharesoint.Config;
import ca.teamdman.pharesoint.payloads.IPayloadHandler;
import ca.teamdman.pharesoint.payloads.Payload;
import ca.teamdman.pharesoint.payloads.PayloadHandlerController;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentTranslation;

public class RequestWaypointsHandler implements IPayloadHandler {
	private static final String NAME = "REQUEST";
	@Override
	public void handle(Payload payload) {
		if (!Config.trustsPlayer(payload.author)) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("payload.received.untrusted", payload.author));
			return;
		}
		AddWaypointHandler.publishPayload(null, payload.author, payload.content);
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("payload.sending.notify", payload.author));
	}

	@Override
	public String getName() {
		return NAME;
	}


	public static void publishPayload(ICommandSender sender, String player, String pattern) {
		Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C01PacketChatMessage(String.format(
				"/msg %s %s %s %s",
				player,
				PayloadHandlerController.PAYLOAD_IDENTIFIER,
				NAME,
				pattern)));
	}
}
