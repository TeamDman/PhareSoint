package ca.teamdman.pharesoint.gui;

import java.util.ArrayList;
import java.util.List;

import ca.teamdman.pharesoint.Config;
import ca.teamdman.pharesoint.PhareSoint;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;

public class ConfigGui extends cpw.mods.fml.client.config.GuiConfig {
	public ConfigGui(GuiScreen parent) {
		super(parent, getConfigElements(parent), PhareSoint.MODID, false, false, PhareSoint.MODID);
	}

	@SuppressWarnings("rawtypes")
    private static List<IConfigElement> getConfigElements(GuiScreen parent) {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        // adds sections declared in ConfigHandler. toLowerCase() is used because the
        // configuration class automatically does this, so must we.
        list.add(new ConfigElement<ConfigCategory>(Config.config.getCategory("general")));

        return list;
    }
}
