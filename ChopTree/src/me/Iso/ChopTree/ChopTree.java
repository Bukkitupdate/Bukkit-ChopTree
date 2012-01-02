package me.Iso.ChopTree;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import com.gmail.nossr50.mcMMO;

public class ChopTree extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");
	public static PermissionHandler permissionHandler;
	public static mcMMO mcMMO;
	private final ChopTreeBlockListener blockListener = new ChopTreeBlockListener(this);
	private final ChopTreePlayerListener playerListener = new ChopTreePlayerListener(this);
	private final ChopTreeFiles files = new ChopTreeFiles(this);
	
	public final List <String> options = new LinkedList <String> ();
	public final List <String> chunks = new LinkedList <String> ();
	public final List <Integer> mats = new LinkedList <Integer> ();
	public final List <Player> chunkmsg = new LinkedList <Player> ();
	public final HashMap<String, Boolean> players = new HashMap<String, Boolean> ();
	public final HashMap<Player, Block[]> trees = new HashMap<Player, Block[]> ();
	
    public void onEnable() {
		
    	PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Low, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Normal, this);
		
		files.initFile();
		files.initPlayers();
		files.initChunks();
		setupPermissions();
		
		log.info("ChopTree 1.2 enabled!");
	}
    
	public void onDisable() {
		
		if (files.writeFile()) {
			log.info("[ChopTree] Saved changes.");
		} else {
			log.warning("[ChopTree] Could not save changes.");
		}
		
		files.writePlayers();
		files.writeChunks();
		
		log.info("ChopTree disabled!");
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("ChopTree") || commandLabel.equalsIgnoreCase("ct")) {
			if (Array.getLength(args) == 0) {
				files.listOption(-1, sender);
			} else {
				if (args[0].toLowerCase().contains("reload")
						|| args[0].equalsIgnoreCase("r")) {
					options.clear();
					mats.clear();
					files.initFile();
					files.listOption(-1, sender);
					sender.sendMessage(ChatColor.DARK_AQUA + "[ChopTree] Reloaded settings from properties file.");
				} else if (files.getOptionIndex(args[0]) != -1) {
					
					if (Array.getLength(args) > 1) {
						files.toggleOption(args[0], args[1]);
					}
					
					files.listOption(files.getOptionIndex(args[0]), sender);
					
				} else {
					sender.sendMessage(ChatColor.RED + "[ChopTree] That option doesn't exist.");
				}
			}
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("static-access")
	private void setupPermissions() {
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
	      if (this.permissionHandler == null) {
	    	  if (permissionsPlugin != null) {
	        	  this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	        	  log.info("[ChopTree] Using Permissions.");
	        	  options.add("Permissions");
	          } else {
	        	  log.info("[ChopTree] Permissions not detected, defaulting to ops.");
	          }
	      }
	}
	
	@SuppressWarnings("static-access")
	public boolean activemcMMO() {
		if (options.contains("SupportMcmmoIfAvailable")) { 
			Plugin ChopTree = this.getServer().getPluginManager().getPlugin("mcMMO");
			if (this.mcMMO == null) {
				if (ChopTree != null) {
					return true;
				}
			}
		}
		return false;
	}
	
}
