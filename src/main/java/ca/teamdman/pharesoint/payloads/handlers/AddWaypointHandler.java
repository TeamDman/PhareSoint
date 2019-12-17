package ca.teamdman.pharesoint.payloads.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;

import ca.teamdman.pharesoint.Config;
import ca.teamdman.pharesoint.payloads.IPayloadHandler;
import ca.teamdman.pharesoint.payloads.Payload;
import ca.teamdman.pharesoint.payloads.PayloadHandlerController;
import journeymap.client.model.Waypoint;
import journeymap.client.model.Waypoint.Origin;
import journeymap.client.model.Waypoint.Type;
import journeymap.client.waypoint.WaypointStore;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentTranslation;

public class AddWaypointHandler implements IPayloadHandler {
	private static final String NAME = "ADD";
	@Override
	public void handle(Payload payload) {
		if (!Config.trustsPlayer(payload.author)) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("payload.received.untrusted", payload.author));
			return;
		}
		
		System.out.printf("Received payload %s\n", payload.content);
		try {
			final Pattern p = Pattern.compile("\"(?<name>\\w*=*)\" ?(?<x>\\d+),(?<y>\\d+),(?<z>\\d+) (?<r>\\d+),(?<g>\\d+),(?<b>\\d+) (?<dims>\\d+\\s*)");
			Matcher m = p.matcher(payload.content);
			if (!m.matches())
				throw new IllegalArgumentException("Failed parsing waypoint, didn't match pattern!");
			String name = new String(Base64.decodeBase64(m.group("name")));
			int x = Integer.parseInt(m.group("x"));
			int y = Integer.parseInt(m.group("y"));
			int z = Integer.parseInt(m.group("z"));
			int r = Integer.parseInt(m.group("r"));
			int g = Integer.parseInt(m.group("g"));
			int b = Integer.parseInt(m.group("b"));
			ArrayList<Integer> dimensions = new ArrayList<>();
			for (String d : m.group("dims").split("\\s*"))
				dimensions.add(Integer.parseInt(d));
			Waypoint w = new Waypoint(name, x, y, z, true, r, g, b, Type.Normal, Origin.JourneyMap, dimensions.get(0), dimensions);
			WaypointStore.instance().add(w);
			WaypointStore.instance().save(w);	
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("payload.received.success", payload.author));
		} catch (Throwable t) {
			System.out.println("Failed to parse waypoint!");
			t.printStackTrace();
		}
		
	}

	@Override
	public String getName() {
		return NAME;
	}


	public static int publishPayload(ICommandSender sender, String player, String pattern) {
		Pattern p;
		try {
			p = Config.useRegex ? Pattern.compile(pattern) : Pattern.compile(pattern, Pattern.LITERAL);
		} catch (Exception e) {
			p = Pattern.compile(pattern, Pattern.LITERAL);
		}
		final Pattern pat = p;
		final List<Waypoint> waypoints = WaypointStore.instance().getAll().stream()
				.filter(w -> pat.matcher(w.getName()).find())
				.collect(Collectors.toList());
		new Thread(()->{
			for (Waypoint w : waypoints) {
				sendWaypoint(w, player);
				try {					
					Thread.sleep(Config.spamAvoidanceDelay);
				} catch (Throwable t) {
					// yikes.
				}
			}
		}).start();
		return waypoints.size();
	}
	
	public static void sendWaypoint(Waypoint waypoint, String player) {
		System.out.println(waypoint.getName());
		Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C01PacketChatMessage(String.format(
				"/msg %s %s %s %s",
				player,
				PayloadHandlerController.PAYLOAD_IDENTIFIER,
				NAME,
				compressWaypoint(waypoint)
				)));
	}
	
	public static String compressWaypoint(Waypoint waypoint) {
		StringBuilder rtn = new StringBuilder();
		rtn.append("\"");
		rtn.append(Base64.encodeBase64String(waypoint.getName().getBytes()));
		rtn.append("\" ");
		rtn.append(waypoint.getX());
		rtn.append(",");
		rtn.append(waypoint.getY());
		rtn.append(",");
		rtn.append(waypoint.getZ());
		rtn.append(" ");
		rtn.append(waypoint.getR());
		rtn.append(",");
		rtn.append(waypoint.getG());
		rtn.append(",");
		rtn.append(waypoint.getB());
		waypoint.getDimensions().forEach(dim -> {
			rtn.append(" ");
			rtn.append(dim);
		});
		return rtn.toString();
	}
	
	public static Waypoint decompressWaypoint(String str) {
		
		return null;
	}
}
