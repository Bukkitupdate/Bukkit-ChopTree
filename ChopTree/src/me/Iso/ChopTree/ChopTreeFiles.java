package me.Iso.ChopTree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChopTreeFiles {
	
	private final File dir = new File ("plugins/ChopTree");
	private final File file = new File (dir, "ChopTree.properties");
	private final File playerfile = new File (dir, "ChopTree.players");
	private final File chunkfile = new File (dir, "ChopTree.chunks");
	private final int maxopt = 8;
	private final List <Block> blink = new LinkedList <Block> ();
	private static final Logger log = Logger.getLogger("Minecraft");
	public static ChopTree plugin;
	
	public ChopTreeFiles(ChopTree instance) {
		plugin = instance;
	}
	
	public void initFile () {
		if (createFile()) {
			if (readFile()) {
				return;
			}
		}
		//Use defaults
		log.warning("[ChopTree] Using default settings.");
		putDefault();
	}
	
	public boolean createFile () {
		//Create directory
		if (!dir.exists()) {
			log.info("[ChopTree] Attempting to create directory...");
			try {
				dir.mkdir();
			} catch (Exception e) {
				log.warning("[ChopTree] Could not create directory. Create plugins/ChopTree/ manually.");
				return false;
			}
			log.info("[ChopTree] Success.");
		}
		//Create file
		if (!file.exists()) {
			log.info("[ChopTree] Attempting to create properties file...");
			try {
				FileWriter stream = new FileWriter(file);
				BufferedWriter writer = new BufferedWriter(stream);
				
				writer.write("# -ChopTree Properties-");
				writer.newLine();
				writer.write("# You can add or change the command and toggle command aliases here.");
				writer.newLine();
				writer.write("# Because of technical limitations the console commands will always be \"ChopTree\" and " +
						"\"ct\", but the in-game comments will be taken from here.");
				writer.newLine();
				writer.write(" ");
				writer.newLine();
				writer.write("Command = ChopTree, ct");
				writer.newLine();
				writer.write("Toggle = ToggleChop, tc");
				writer.newLine();
				writer.write(" ");
				writer.newLine();
				writer.write("# Here you can change the options (can also be done in-game).");
				writer.newLine();
				writer.write(" ");
				
				for (int counter = 0; counter <= maxopt; counter++) {
					if (getOptionName(counter) != null) {
						writer.newLine();
						writer.write(getOptionName(counter) + " = " + getDefault(counter));
					}
				}
				writer.newLine();
				writer.write(" ");
				writer.newLine();
				writer.write("# -Material List-");
				writer.newLine();
				writer.write("# If the \"UseAnything\" option is false, the plugin will take the items you can use to chop "
						+ "trees with from here.");
				writer.newLine();
				writer.write("# You can either put in the material's name or its ID.");
				writer.newLine();
				writer.write(" ");
				writer.newLine();
				writer.write("mat = WOOD_AXE");
				writer.newLine();
				writer.write("mat = STONE_AXE");
				writer.newLine();
				writer.write("mat = IRON_AXE");
				writer.newLine();
				writer.write("mat = GOLD_AXE");
				writer.newLine();
				writer.write("mat = DIAMOND_AXE");
				writer.newLine();
				writer.close();
				stream.close();
			} catch (Exception e) {
				log.warning("[ChopTree] Error writing file.");
				return false;
			}
			log.info("[ChopTree] Success.");
		}
		//File was created or existed already
		return true;
	}
	
	public boolean readFile() {
		
		String index = null;
		try {
			FileInputStream stream = new FileInputStream(file);
			BufferedReader input = new BufferedReader(new InputStreamReader(stream));
			
			do {
				index = input.readLine();
				
				if (index != null) {
					//See if it's a comment or an empty line
					if (!index.startsWith("#") && index.contains("=")) {
		
						String[] split = index.split("=");
	
						//Check if it is a command
						if (index.toLowerCase().startsWith("command")) {
							split = split[1].trim().split(",");
							for (int counter = 0; counter < Array.getLength(split); counter++) {
								putCommand(split[counter].trim(), "cmd");
							}
						} else
						
						//Check if it is a command
						if (index.toLowerCase().startsWith("toggle")) {
							split = split[1].trim().split(",");
							for (int counter = 0; counter < Array.getLength(split); counter++) {
								putCommand(split[counter].trim(), "tgl");
							}
						} else
						
						//Check if it's "true", else no need to see if it's valid
						if (split[1].trim().equalsIgnoreCase("true")) {
							//Check if it's a valid option
							if (getOptionIndex(split[0].trim()) != -1) {
								putOption(getOptionName(getOptionIndex(split[0].trim())));
							}
						} else
						
						//Check if it's a material
						if (split[0].trim().equalsIgnoreCase("mat")) {
							putMaterial(split[1].trim());
						}
					}
				}
			} while (index != null);
			
			input.close();
			stream.close();
		} catch (Exception e) {
			log.warning("[ChopTree] Error reading file.");
			return false;
		}
		
		return true;
	}
	
	public boolean writeFile() {
		log.info("[ChopTree] Saving changes...");
		
		if (createFile()) {
			//Get options
			String[] stringoptions = new String[maxopt + 1];
			for (int counter = 0; counter <= maxopt; counter++) {
				if (plugin.options.contains(getOptionName(counter))) {
					stringoptions[counter] = (getOptionName(counter) + " = true");
					plugin.options.remove(getOptionName(counter));
				} else {
					stringoptions[counter] = (getOptionName(counter) + " = false");
				}
			}
			
			List <String> props = new LinkedList <String> ();
			try {
				FileInputStream stream = new FileInputStream(file);
				BufferedReader input = new BufferedReader(new InputStreamReader(stream));
				
				String index = null;
				do {
					
					index = input.readLine();
					
					if (index == null) break;
					
					if (index.contains("=")) {
						String[] split = index.split("=");
						split[0] = split[0].trim();
						split[1] = split[1].trim();
						if (split[1].equalsIgnoreCase("true") || split[1].equalsIgnoreCase("false")) {
							for (int counter = 0; counter < Array.getLength(stringoptions); counter++) {
								if (stringoptions[counter].equalsIgnoreCase(split[0] + " = true")) {
									index = split[0] + " = true";
									stringoptions[counter] = "0";
								} else if (stringoptions[counter].equalsIgnoreCase(split[0] + " = false")) {
									index = split[0] + " = false";
									stringoptions[counter] = "0";
								}
							}
						}
					}
					
					props.add(index);
					
				} while (index != null);
				
				for (int counter = 0; counter < Array.getLength(stringoptions); counter++) {
					if (!stringoptions[counter].equals("0")) {
						props.add(stringoptions[counter]);
					}
				}
				
				input.close();
				stream.close();
			} catch (Exception e) {
				log.warning("[ChopTree] Error reading file.");
				return false;
			}
			
			//Write new file
			
			File backup = new File (dir, "backup.properties");
			file.renameTo(backup);
			
			try {
				FileWriter stream = new FileWriter(file);
				BufferedWriter writer = new BufferedWriter(stream);
				
				for (int counter = 0; counter < props.size(); counter++) {
					writer.write(props.get(counter));
					writer.newLine();
				}
				
				writer.close();
				stream.close();
				if (backup.exists()) backup.delete();
			} catch (Exception e) {
				log.warning("[ChopTree] Error writing file, restoring backup.");
				if (file.exists()) file.delete();
				if (backup.exists()) backup.renameTo(file);
				return false;
			}
			return true;
		}
		
		return false;
	}
	
	public void initPlayers () {
		if (playerfile.exists()) {
			try {
				FileInputStream stream = new FileInputStream(playerfile);
				BufferedReader input = new BufferedReader(new InputStreamReader(stream));
				
				String index = null;
				
				do {
					index = input.readLine();
					if (index == null) break;
					if (index.contains("=")) {
						String[] split = index.split("=");
						if (!plugin.players.containsKey(split[0])) {
							if (split[1].equalsIgnoreCase("true")) {
								plugin.players.put(split[0], true);
							} else {
								plugin.players.put(split[0], true);
							}
						}
					}
				} while (index != null);
							
				input.close();
				stream.close();
			} catch (Exception e) {
				log.warning("[ChopTree] Error reading players file. Toggles will be default.");
			}
			
		}
	}
	
	public void writePlayers () {
		if (plugin.players.isEmpty()) return;
		
		String hashstring = plugin.players.toString();
		hashstring = hashstring.replace("{", "");
		hashstring = hashstring.replace("}", "");
		
		String[] split = hashstring.split(", ");
		File backup = new File (dir, "backup.players");
		try {
			
			if (playerfile.exists()) playerfile.renameTo(backup);
			
			FileWriter stream = new FileWriter(playerfile);
			BufferedWriter writer = new BufferedWriter(stream);
			
			for (int counter = 0; counter < Array.getLength(split); counter++) {
				writer.write(split[counter]);
				writer.newLine();
			}
			
			writer.close();
			stream.close();
			
			if (backup.exists()) backup.delete();
			
		} catch (Exception e) {
			log.warning("[ChopTree] Error writing players file.");
			if (playerfile.exists()) playerfile.delete();
			if (backup.exists()) backup.renameTo(playerfile);
		}
	}
	
	public String getOptionName (int index) {
		if (index == 0) {
			return ("ActiveByDefault");
		}
		
		if (index == 1) {
			return ("UseAnything");
		}
		
		if (index == 2) {
			return ("MoreDamageToTools");
		}
		
		if (index == 3) {
			return ("InterruptIfToolBreaks");
		}
		
		if (index == 4) {
			return ("SupportMcmmoIfAvailable");
		}
		
		if (index == 5) {
			return ("TreeFellerNeeded");
		}
		
		if (index == 6) {
			return ("LogsMoveDown");
		}
		
		if (index == 7) {
			return ("OnlyTrees");
		}
		
		if (index == 8) {
			return ("EnableOverride");
		}
		
		return null;
	}

	public String getOptionReadable (int index, boolean state) {
		if (index == 0) {
			if (state) {
				return ("Plugin is toggled active by default.");
			} else {
				return ("Plugin is toggled inactive by default.");
			}
		}
		
		if (index == 1) {
			if (state) {
				return ("Players can use anything.");
			} else {
				return ("Players can only use specified materials.");
			}
		}
		
		if (index == 2) {
			if (state) {
				return ("Tools will be damaged for every destroyed log.");
			} else {
				return ("Tools will only be damaged once.");
			}
		}
		
		if (index == 3) {
			if (state) {
				return ("Chopping will be interrupted if the tool breaks.");
			} else {
				return ("Chopping will not be interrupted if the tool breaks.");
			}
		}
		
		if (index == 4) {
			if (state) {
				return ("mcMMO support is enabled.");
			} else {
				return ("mcMMO support is disabled.");
			}
		}
		
		if (index == 5) {
			if (state) {
				return ("mcMMO Tree Feller is needed to chop.");
			} else {
				return ("mcMMO Tree Feller is not needed to chop.");
			}
		}
		
		if (index == 6) {
			if (state) {
				return ("Chopped trees will move down.");
			} else {
				return ("Chopped trees will pop like cacti.");
			}
		}
		
		if (index == 7) {
			if (state) {
				return ("Only trees are choppable.");
			} else {
				return ("All logs are choppable.");
			}
		}
		
		if (index == 8) {
			if (state) {
				return ("Overriding through Permissions is enabled.");
			} else {
				return ("Overriding through Permissions is disabled.");
			}
		}
		
		return null;
	}
	
	public int getOptionIndex (String name) {
		if (name.equalsIgnoreCase("ActiveByDefault")
				|| name.equalsIgnoreCase("Default")
				|| name.equalsIgnoreCase("a")) return 0;
		if (name.equalsIgnoreCase("UseAnything")
				|| name.equalsIgnoreCase("Anything")
				|| name.equalsIgnoreCase("u")) return 1;
		if (name.equalsIgnoreCase("MoreDamageToTools")
				|| name.equalsIgnoreCase("Damage")
				|| name.equalsIgnoreCase("m")) return 2;
		if (name.equalsIgnoreCase("InterruptIfToolBreaks")
				|| name.equalsIgnoreCase("Interrupt")
				|| name.equalsIgnoreCase("i")) return 3;
		if (name.equalsIgnoreCase("SupportMcmmoIfAvailable")
				|| name.equalsIgnoreCase("mcMMO")
				|| name.equalsIgnoreCase("s")) return 4;
		if (name.equalsIgnoreCase("TreeFellerNeeded")
				|| name.equalsIgnoreCase("TreeFeller")
				|| name.equalsIgnoreCase("t")) return 5;
		if (name.equalsIgnoreCase("LogsMoveDown")
				|| name.equalsIgnoreCase("Down")
				|| name.equalsIgnoreCase("l")) return 6;
		if (name.equalsIgnoreCase("OnlyTrees")
				|| name.equalsIgnoreCase("trees")
				|| name.equalsIgnoreCase("o")) return 7;
		if (name.equalsIgnoreCase("EnableOverride")
				|| name.equalsIgnoreCase("override")
				|| name.equalsIgnoreCase("e")) return 8;
		return -1;
	}
	
	public void putOption (String option) {
		if (!plugin.options.contains(option)) {
			plugin.options.add(option);
		}
	}
	
	public void listOption (int index, CommandSender sender) {
		if (index == -1) {
			for (int counter = 0; counter <= maxopt; counter++) {
				if (plugin.options.contains(getOptionName(counter))) {
					sender.sendMessage(ChatColor.AQUA + "[ChopTree] " + getOptionReadable(counter, true));
				} else {
					sender.sendMessage(ChatColor.AQUA + "[ChopTree] " + getOptionReadable(counter, false));
				}
			}
		} else {
			if (plugin.options.contains(getOptionName(index))) {
				sender.sendMessage(ChatColor.AQUA + "[ChopTree] " + getOptionReadable(index, true));
			} else {
				sender.sendMessage(ChatColor.DARK_AQUA + "[ChopTree] " + getOptionReadable(index, false));
			}
		}	
	}

	public void toggleOption (String name, String arg) {
		
		name = getOptionName(getOptionIndex(name));
		if (arg.toLowerCase().contains("true")
				|| arg.toLowerCase().contains("on")
				|| arg.toLowerCase().contains("enable")) {
			
			if (!plugin.options.contains(name)) {
				plugin.options.add(name);
			}
			
		} else if (arg.toLowerCase().contains("false")
				|| arg.toLowerCase().contains("off")
				|| arg.toLowerCase().contains("disable")) {
			
			if (plugin.options.contains(name)) {
				plugin.options.remove(name);
			}
			
		}
	}
	
	public boolean getDefault (int index) {
		if (index == 0) {
			//active by default
			return true;
		}
		
		if (index == 1) {
			//use anything
			return true;
		}
		
		if (index == 2) {
			//more damage to tools
			return false;
		}
		
		if (index == 3) {
			//interrupt if tool breaks
			return false;
		}
		
		if (index == 4) {
			//support mcMMO if available
			return true;
		}
		
		if (index == 5) {
			//tree feller needed
			return false;
		}
		
		if (index == 6) {
			//logs move down
			return false;
		}
		
		if (index == 7) {
			//trees only
			return true;
		}
		
		if (index == 8) {
			//enable override
			return false;
		}
		
		return (Boolean) null;
	}
	
	public void putDefault () {
		plugin.options.clear();
		for (int counter = 0; counter <= maxopt; counter++) {
			if (getOptionName(counter) != null) {
				if (getDefault(counter)) plugin.options.add(getOptionName(counter));
			}
		}
	}
	
	public void putCommand (String command, String prefix) {
		command = (prefix + command.toLowerCase());
		if (!plugin.options.contains(command)) {
			plugin.options.add(command);
		}
	}
	
	public void putMaterial (String name) {
		
		Material material = null;
		material = Material.matchMaterial(name);
	
		if (material != null) {
			int ID = material.getId();
			if (!plugin.mats.contains(ID)) plugin.mats.add(ID);
		} else {
			log.warning("[ChopTree] Could not match material \"" + name +"\".");
		}
	}

	public void initChunks () {
		if (chunkfile.exists()) {
			try {
				FileInputStream stream;
				stream = new FileInputStream(chunkfile);
				BufferedReader input = new BufferedReader(new InputStreamReader(stream));
				
				String index = null;
				
				do {
					index = input.readLine();
					if (index != null) {
						plugin.chunks.add(index);
					}
				} while (index != null);
				
				input.close();
				stream.close();
			} catch (Exception e) {
				log.warning("[ChopTree] Error reading chunks file. Chunks are not protected.");
			}
		}
	}

	public void writeChunks () {
		if (plugin.chunks.isEmpty()) return;
		
		File backup = new File (dir, "backup.chunks");
		try {
			if (chunkfile.exists()) chunkfile.renameTo(backup);
			
			FileWriter stream = new FileWriter(chunkfile);
			BufferedWriter writer = new BufferedWriter(stream);
			
			for (int counter = 0; counter < plugin.chunks.size(); counter++) {
				writer.write(plugin.chunks.get(counter));
				writer.newLine();
			}
			
			writer.close();
			stream.close();
			
				if (backup.exists()) backup.delete();
		} catch (Exception e) {
			if (chunkfile.exists()) chunkfile.delete();
			if (backup.exists()) backup.renameTo(chunkfile);
		}

	}
	
	public void chunkFullProtect (Player player, Chunk chunk) {
		String loc = chunk.getX() + ";" + chunk.getZ();
		String other = chunk.getX() + ":" + chunk.getZ();
		if (plugin.chunks.contains(loc)) {
			plugin.chunks.remove(loc);
		} else {
			if (plugin.chunks.contains(other)) plugin.chunks.remove(other);
			plugin.chunks.add(loc);
		}
		chunkMessage(player, chunk);
	}
	
	public void chunkProtect (Player player, Chunk chunk) {
		String loc = chunk.getX() + ":" + chunk.getZ();
		String other = chunk.getX() + ";" + chunk.getZ();
		if (plugin.chunks.contains(loc)) {
			plugin.chunks.remove(loc);
		} else {
			if (plugin.chunks.contains(other)) plugin.chunks.remove(other);
			plugin.chunks.add(loc);
		}
		chunkMessage(player, chunk);
	}

	public void chunkMessage (Player player, Chunk chunk) {
		if (plugin.chunks.contains(chunk.getX() + ";" + chunk.getZ())) {
			player.sendMessage(ChatColor.GREEN + 
					"[ChopTree] Fully protected chunk X " + chunk.getBlock(0, 0, 0).getX() + " to " 
					+ chunk.getBlock(15, 0, 0).getX() + ", Z " + chunk.getBlock(0, 0, 0).getZ() + " to " 
					+ chunk.getBlock(0, 0, 15).getZ() + ".");
			
		} else if (plugin.chunks.contains(chunk.getX() + ":" + chunk.getZ())) {
			player.sendMessage(ChatColor.GREEN + 
					"[ChopTree] Protected chunk X " + chunk.getBlock(0, 0, 0).getX() + " to " + chunk.getBlock(15, 0, 0).getX()
					+ ", Z " + chunk.getBlock(0, 0, 0).getZ() + " to " + chunk.getBlock(0, 0, 15).getZ() + ".");
			
		} else {
			player.sendMessage(ChatColor.RED + 
					"[ChopTree] Unprotected chunk X " + chunk.getBlock(0, 0, 0).getX() + " to " + chunk.getBlock(15, 0, 0).getX()
					+ ", Z " + chunk.getBlock(0, 0, 0).getZ() + " to " + chunk.getBlock(0, 0, 15).getZ() + ".");			
		}
		chunkBlinkGrass(chunk);
	}
	
	public void chunkBlinkGrass (Chunk chunk) {
		int x = 0;
		int y = 127;
		int z = 0;
		Block block = null;
		for (x = 0; x < 16; x++) {
			for (z = 0; z < 16; z++) {
				for (y = 127; y > 0; y--) {
					block = chunk.getBlock(x, y, z);
					if (block.getTypeId() == 2) {
						block.setTypeId(3);
						blink.add(block);
						break;
					}
				}				
			}
		}
		//Thread
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
            public void run() {
            	for (int counter = 0; counter < blink.size(); counter++) {
        			if (blink.get(counter).getTypeId() == 3) {
        				blink.get(counter).setTypeId(2);
        			}
        		}
            	blink.clear();
            }
            
        }, 40L);
	}

}
