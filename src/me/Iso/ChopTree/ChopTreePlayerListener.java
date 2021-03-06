package me.Iso.ChopTree;

import java.lang.reflect.Array;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChopTreePlayerListener implements Listener {
	
	public static ChopTree plugin;
	
	public ChopTreePlayerListener (ChopTree instance) {
		plugin = instance;
	}
	
	private final ChopTreeFiles files = new ChopTreeFiles(plugin);
	
	@EventHandler
	public void onPlayerCommandPreprocess (PlayerCommandPreprocessEvent event) {
		String cmd = event.getMessage().toLowerCase();
		cmd = cmd.replace("/", "");
		String[] split = cmd.split(" ");
		
		//Command
		if (plugin.options.contains("cmd" + split[0].toLowerCase())) {		
			
			split[0] = "/ChopTree";
			for (int counter = 0; counter < Array.getLength(split); counter++) {
				cmd = cmd.concat(split[counter] + " ");
			}
			event.setMessage(cmd.trim());
			
			//Chunk protect
			if (Array.getLength(split) > 1) {
				if (split[1].toLowerCase().contains("fullprotect")
						|| split[1].toLowerCase().contains("full")
						|| split[1].equalsIgnoreCase("f")) {
					event.setCancelled(true);
					event.setMessage("/_");
					if (denyPermission("full", event.getPlayer())) return;
					files.chunkFullProtect(event.getPlayer(), event.getPlayer().getLocation().getBlock().getChunk());
					
				} else if (split[1].toLowerCase().contains("protect")
						|| split[1].equalsIgnoreCase("p")) {
					event.setCancelled(true);
					event.setMessage("/_");
					if (denyPermission("protect", event.getPlayer())) return;
					files.chunkProtect(event.getPlayer(), event.getPlayer().getLocation().getBlock().getChunk());
					
				} else if (split[1].toLowerCase().contains("chunk")
						|| split[1].equalsIgnoreCase("c")) {
					event.setCancelled(true);
					event.setMessage("/_");
					if (denyPermission("chunk", event.getPlayer())) return;
					files.chunkMessage(event.getPlayer(), event.getPlayer().getLocation().getBlock().getChunk());
				} else if (split[1].toLowerCase().contains("reload")
						|| split[1].equalsIgnoreCase("r")) {
					if (denyPermission("reload", event.getPlayer())) {
						event.setCancelled(true);
						event.setMessage("/_");
						return;
					}
				} else {
					if (Array.getLength(split) > 2) {
						if (denyPermission("cmd", event.getPlayer())) {
							event.setCancelled(true);
							event.setMessage("/_");
							return;
						}
					} else {
						if (denyPermission("check", event.getPlayer())) {
							event.setCancelled(true);
							event.setMessage("/_");
							return;
						}
					}
				}
			} else {
				if (denyPermission("check", event.getPlayer())) {
					event.setCancelled(true);
					event.setMessage("/_");
					return;
				}
			}
		} 
		
		//Toggle
		else if (plugin.options.contains("tgl" + cmd)) {
			event.setCancelled(true);
			event.setMessage("/_");
			
			if (denyPermission("toggle", event.getPlayer())) return;
			
			Player player = event.getPlayer();
			String playername = player.getName();
			if (plugin.players.containsKey(playername)) {
				
				if (plugin.players.get(playername) == true) {
					plugin.players.remove(playername);
					plugin.players.put(playername, false);
				} else {
					plugin.players.remove(playername);
					plugin.players.put(playername, true);
				}
			
			} else {
				
				//Since player is toggling put in the reverse value
				if (plugin.options.contains("ActiveByDefault")) {
					plugin.players.put(playername, false);
				} else {
					plugin.players.put(playername, true);
				}
			}
			
			if (plugin.players.get(playername) == true) {
				player.sendMessage(ChatColor.GREEN + "[ChopTree enabled!]");
			} else {
				player.sendMessage(ChatColor.RED + "[ChopTree disabled!]");
			}
			
		}
		
	}
	
	public boolean denyPermission (String string, Player player) {
		
		boolean deny = false;
		
		if (string.equals("check")) {
			if (!player.hasPermission("choptree.command.check")) {
				deny = true;
			}
		}
		
		if (string.equals("cmd")) {
			if (!player.hasPermission("choptree.command.change")) {
				deny = true;
			}
		}
		
		if (string.equals("toggle")) {
			if (!player.hasPermission("choptree.command.toggle")) {
				deny = true;
			}
		}
		
		if (string.equals("full")) {
			if (!player.hasPermission("choptree.command.fullprotect")) {
				deny = true;
			}
		}
		
		if (string.equals("protect")) {
			if (!player.hasPermission("choptree.command.protect")) {
				deny = true;
			}
		}
		
		if (string.equals("chunk")) {
			if (!player.hasPermission("choptree.command.chunk")) {
				deny = true;
			}
		}
		
		if (string.equals("reload")) {
			if (!player.hasPermission("choptree.command.reload")) {
				deny = true;
			}
		}
		
		if (deny) player.sendMessage(ChatColor.RED + "[ChopTree] You have no permission to perform this command.");
		
		return deny;
	}
	
}
