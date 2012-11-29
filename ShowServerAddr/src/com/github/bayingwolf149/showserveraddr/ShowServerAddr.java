package com.github.bayingwolf149.showserveraddr;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilderFactory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//Plugin Created By: BayingWolf149 aka ILoveWolves14

public class ShowServerAddr extends JavaPlugin implements Listener {
	
	private String webaddress;
	
	public void onEnable() {
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}
	    webaddress = getConfig().getString("WEBADDRESS");
	    if (webaddress == null) {
	      webaddress = "http://www.example.com/";
	    }
	    getServer().getPluginManager().registerEvents(this, this);
	    getServer().getScheduler().scheduleAsyncDelayedTask(this, new Update(getDescription().getVersion()));
	    getLogger().info("ShowServerAddr Successfully Enabled!");
	}
	
	private String defwebaddress = "http://www.example.com/";
	
	private void setAddr(String wadd) {
	    webaddress = (wadd == null ? defwebaddress : wadd);
	    getConfig().set("WEBADDRESS", webaddress);
	    saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ((args.length > 0) && (args[0].equalsIgnoreCase("resetadd"))) {
			if (!sender.hasPermission("showserveraddr.resetadd")) {
		        sender.sendMessage("You do not have permission to perform this command!");
		        return true;
		    }
			setAddr(null);
		    sender.sendMessage(ChatColor.GREEN + "Web address successfully set back to default: " + ChatColor.RESET + ColorUtils.format(webaddress));
		    return true;
		}
	    if ((args.length > 0) && (args[0].equalsIgnoreCase("setadd"))) {
	    	if (!sender.hasPermission("showserveraddr.setadd")) {
	    		sender.sendMessage("You do not have permission to perform this command");
	        return true;
	        }
	    	if (args.length < 2) {
	        sender.sendMessage(ChatColor.RED + "You must add a web address to set!");
	        return true;
	        }
	    	StringBuilder stringb = new StringBuilder();
	    	for (int i = 1; i < args.length; i++) {
	    		stringb.append(args[i]);
	    		stringb.append(" ");
	    	}
	    	stringb.setLength(stringb.length() - 1);
	    	String newwebaddress = stringb.toString();
	    	setAddr(newwebaddress);
	    	sender.sendMessage(ChatColor.GREEN + "Web address successfully changed to: " + ChatColor.RESET + ColorUtils.format(newwebaddress));
	    	return true;
	    }
	    if (sender.hasPermission("showserveraddr.viewadd")) {
	    	sender.sendMessage(ChatColor.GREEN + "Server Address: " + ChatColor.RESET + ColorUtils.format(webaddress));
	    return true;
	    } else {
	    sender.sendMessage("You do not have permission to perform this command");
	    return true;
	    }
	}
	
	public void debug(Exception error) {
		getLogger().log(Level.SEVERE, "Critical Error Occured!", error);
	}
	
	public void log(String msg) {
		getLogger().info(msg);
	}
	
	public static enum ColorUtils {
	    BLACK("&0", ChatColor.BLACK.toString()), 
	    DARK_BLUE("&1", ChatColor.DARK_BLUE.toString()), 
	    DARK_GREEN("&2", ChatColor.DARK_GREEN.toString()), 
	    DARK_AQUA("&3", ChatColor.DARK_AQUA.toString()), 
	    DARK_RED("&4", ChatColor.DARK_RED.toString()), 
	    DARK_PURPLE("&5", ChatColor.DARK_PURPLE.toString()), 
	    GOLD("&6", ChatColor.GOLD.toString()), 
	    GRAY("&7", ChatColor.GRAY.toString()), 
	    DARK_GRAY("&8", ChatColor.DARK_GRAY.toString()), 
	    BLUE("&9", ChatColor.BLUE.toString()), 
	    GREEN("&a", ChatColor.GREEN.toString()), 
	    AQUA("&b", ChatColor.AQUA.toString()), 
	    RED("&c", ChatColor.RED.toString()), 
	    LIGHT_PURPLE("&d", ChatColor.LIGHT_PURPLE.toString()), 
	    YELLOW("&e", ChatColor.YELLOW.toString()), 
	    WHITE("&f", ChatColor.WHITE.toString()), 
	    MAGIC("&k", ChatColor.MAGIC.toString()), 
	    BOLD("&l", ChatColor.BOLD.toString()), 
	    STRIKETHROUGH("&m", ChatColor.STRIKETHROUGH.toString()), 
	    UNDERLINE("&n", ChatColor.UNDERLINE.toString()), 
	    ITALIC("&o", ChatColor.ITALIC.toString()), 
	    RESET("&r", ChatColor.RESET.toString());

	    private final String input;
	    private final String MinecraftColor;

	    private ColorUtils(String input, String MinecraftColor)
	    {
	      this.input = input;
	      this.MinecraftColor = MinecraftColor;
	    }

	    public String getMinecraftColor()
	    {
	      return MinecraftColor;
	    }

	    public String getInput()
	    {
	      return input;
	    }

	    public static String format(String message)
	    {
	      String msg = message;
	      for (ColorUtils c : values()) {
	        msg = msg.replace(c.getInput(), c.getMinecraftColor());
	      }
	      return msg;
	    }
	  }
	
	private class Update implements Runnable {
		
		private String version;
		
		public Update(String version) {
			this.version = version;
		}
		
		public void run() {
			try {
				URL url = new URL("http://dev.bukkit.org/server-mods/showserveraddr/files.rss");
				Document rss = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
				rss.getDocumentElement().normalize();
				NodeList nodes = rss.getElementsByTagName("item");
				Node node = nodes.item(0);
				if (node.getNodeType() == 1) {
					Element element = (Element)node;
					NodeList etl = element.getElementsByTagName("title");
					Element name = (Element)etl.item(0);
					NodeList fstnodes = name.getChildNodes();
					String curvur = fstnodes.item(0).getNodeValue();
					if (curvur.contains(version)) {
						log("You are running the latest version of ShowServerAddr!");
					} else {
						log(curvur + " has been released!");
						log("Visit http://dev.bukkit.org/server-mods/showserveraddr/ for the most recent version!");
					}
				}
			} catch (Exception e) {
				debug(e);
			}
		}
	}
}
