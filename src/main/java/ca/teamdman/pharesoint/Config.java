package ca.teamdman.pharesoint;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {
	public static Configuration config;
	public static String prefix = PhareSoint.MODID;
	public static Set<String> trustedPlayers = new HashSet<>();
	public static boolean useRegex = false;
	public static int spamAvoidanceDelay = 1000;
	public static void init(File cfg) {
		config = new Configuration(cfg);
		try {
			config.load();
			syncConfig();
		} catch (Exception e) {
			System.out.println("Failed to sync config:");
			e.printStackTrace();
		}
	}
	
	private static Property getTrustProperty() {
		return config.get("General", "Trusted Players", trustedPlayers.toArray(new String[trustedPlayers.size()]));
	}
	
	public static void syncConfig() {
		prefix = config.getString("Command Prefix", "General", PhareSoint.MODID, "The name of the command to be used in chat");
		useRegex = config.getBoolean("Use Regex", "General", useRegex, "If push/pull commands will use regex or not.");
		spamAvoidanceDelay = config.getInt("Spam Avoidance Delay", "General", spamAvoidanceDelay, 0, Integer.MAX_VALUE, "Delay in ms between payloads to avoid being kicked for spam");
		trustedPlayers = Sets.newHashSet(getTrustProperty().getStringList());
		config.save();
	}
	
	public static void trustPlayer(String player) {
		trustedPlayers.add(player);
		getTrustProperty().set(trustedPlayers.toArray(new String[trustedPlayers.size()]));
		config.save();
	}
	
	public static boolean untrustPlayer(String player) {
		boolean rtn = trustedPlayers.remove(player);
		config.save();
		return rtn;
	}
	
	public static boolean trustsPlayer(String player) {
		return trustedPlayers.contains(player);
	}
}
