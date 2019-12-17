package ca.teamdman.pharesoint;

import ca.teamdman.pharesoint.payloads.PayloadHandlerController;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class EventHandler {

	@SubscribeEvent
	public void onMsg(ClientChatReceivedEvent e) {
		try {
			PayloadHandlerController.identifyAndHandlePayload(e);	
		} catch (Throwable ex) {
			System.out.println("Failed parsing PhareSoint payload!");
			if (ex != null)
				ex.printStackTrace();
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if (eventArgs.modID.contentEquals(PhareSoint.MODID)) {
			Config.syncConfig();
		}
	}
	
//	@SubscribeEvent
//	public void test(PlayerEvent.BreakSpeed e) {
//		System.out.println("WORKING!");
//		Waypoint point = new Waypoint(
//				"Whack!",
//				e.x, e.y, e.z,
//				new java.awt.Color(e.entity.worldObj.rand.nextInt(0xffffff)),
//				Waypoint.Type.Normal,
//				e.entity.worldObj.provider.dimensionId
//		);
//		
//		WaypointStore.instance().add(point);
//		WaypointStore.instance().save(point);
//	}
}
