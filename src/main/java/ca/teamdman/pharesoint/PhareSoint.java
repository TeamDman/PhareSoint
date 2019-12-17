package ca.teamdman.pharesoint;

import java.io.File;

import ca.teamdman.pharesoint.chat.Command;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = PhareSoint.MODID, version = PhareSoint.VERSION, dependencies="required-after:journeymap", guiFactory = "ca.teamdman.pharesoint.gui.GuiFactory")
public class PhareSoint
{
    public static final String MODID = "pharesoint";
    public static final String VERSION = "1.0.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (!event.getSide().isClient())
        	return;
        ClientCommandHandler.instance.registerCommand(new Command());
    	MinecraftForge.EVENT_BUS.register(new ca.teamdman.pharesoint.EventHandler());
    }
    
    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        if (!event.getSide().isClient())
        	return;
        
    	Config.init(new File(event.getModConfigurationDirectory(), MODID + ".cfg"));
    }
}
