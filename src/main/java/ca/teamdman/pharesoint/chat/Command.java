package ca.teamdman.pharesoint.chat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.teamdman.pharesoint.Config;
import ca.teamdman.pharesoint.payloads.handlers.AddWaypointHandler;
import ca.teamdman.pharesoint.payloads.handlers.RequestWaypointsHandler;
import journeymap.client.waypoint.WaypointStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class Command extends CommandBase {
	@Override
	public String getCommandName() {
		return Config.prefix;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/"+Config.prefix+" <push|pull> <player> [waypoint] | <trust|untrust> <player> | <trustinfo>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length == 0)
			throw new WrongUsageException(getCommandUsage(sender));
		String player;
		switch (args[0]) {
			case "push":
				if (args.length < 2)
					throw new WrongUsageException(getCommandUsage(sender));
				player = getAndAssertOtherPlayer(sender, args[1]);
				int amount = AddWaypointHandler.publishPayload(sender, player, args.length < 3 ? "" : args[2]);
				sender.addChatMessage(new ChatComponentTranslation("commands.pharesoint.push.success", amount, player));
				return;
				
			case "pull":
				if (args.length < 2)
					throw new WrongUsageException(getCommandUsage(sender));
				player = getAndAssertOtherPlayer(sender, args[1]);
				RequestWaypointsHandler.publishPayload(sender, player, args.length < 3 ? "" : args[2]);
				sender.addChatMessage(new ChatComponentTranslation("commands.pharesoint.pull.pending", player));
				return;
				
			case "trust":
				if (args.length != 2)
					throw new WrongUsageException(getCommandUsage(sender));
				player = getAndAssertOtherPlayer(sender, args[1]);
				if (Config.trustsPlayer(player)) {
					throw new CommandException("commands.pharesoint.trust.duplicate", player);
				} else {
					Config.trustPlayer(player);
					sender.addChatMessage(new ChatComponentTranslation("commands.pharesoint.trust.success", player));
				}
				return;
				
			case "untrust":
				if (args.length != 2)
					throw new WrongUsageException(getCommandUsage(sender));
				if (!Config.untrustPlayer(args[1]))
					throw new CommandException("commands.pharesoint.untrust.notfound", args[1]);
				sender.addChatMessage(new ChatComponentTranslation("commands.pharesoint.untrust.success", args[1]));
				return;	
			
			case "trustinfo":
				if (Config.trustedPlayers.size() == 0) {
					sender.addChatMessage(new ChatComponentTranslation("commands.pharesoint.trustinfo.count_none"));
				} else {					
					if (Config.trustedPlayers.size()==1)
						sender.addChatMessage(new ChatComponentTranslation("commands.pharesoint.trustinfo.count_one"));
					else
						sender.addChatMessage(new ChatComponentTranslation("commands.pharesoint.trustinfo.count_many", Config.trustedPlayers.size()));
					for (String name : Config.trustedPlayers)
						sender.addChatMessage(new ChatComponentText("- "+name));
				}
				return;
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length <= 1)
			return getListOfStringsMatchingLastWord(args, "push","pull","trust","untrust","trustinfo");
		if (args.length == 2)
			switch(args[0]) {
				case "push":
				case "pull":
				case "trust":
					return getListOfStringsFromIterableMatchingLastWord(args, getOtherPlayerNameStream(sender)::iterator);
				case "untrust":
					return  getListOfStringsFromIterableMatchingLastWord(args, Config.trustedPlayers);
			}
		if (args.length == 3 && args[0].equals("push"))
			return getListOfStringsFromIterableMatchingLastWord(args, 
					WaypointStore.instance().getAll().stream()
					.map(w -> w.getName())
					.collect(Collectors.toList()));
		return null;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender instanceof EntityPlayer;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		if (args.length < 2)
			return false;
		switch(args[0]) {
		case "push":
		case "pull":
		case "trust":
		case "untrust":
			return index==1;
		}
		return false;
	}

	
	@SuppressWarnings("unchecked")
	public List<GuiPlayerInfo> getPlayers() {
		return Minecraft.getMinecraft().thePlayer.sendQueue.playerInfoList;
	}
	
	public Stream<String> getPlayerNameStream() {
		return getPlayers().stream().map(info -> info.name);
	}
	
	public Stream<String> getOtherPlayerNameStream(ICommandSender sender) {
		return getPlayerNameStream().filter(name -> !name.equals(sender.getCommandSenderName()));
	}
	

	public String getAndAssertPlayer(String name) {
		if (getPlayerNameStream().noneMatch(x -> x.equals(name)))
			throw new PlayerNotFoundException();
		return name;
	}

	public String getAndAssertOtherPlayer(ICommandSender sender, String name) {
		if (name.equals(sender.getCommandSenderName()))
			throw new CommandException("commands.pharesoint.noself");
		if (getPlayerNameStream().noneMatch(x -> x.equals(name)))
			throw new PlayerNotFoundException();
		return name;
	}
}