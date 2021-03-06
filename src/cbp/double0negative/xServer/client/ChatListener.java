package cbp.double0negative.xServer.client;

import java.util.HashMap;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

import cbp.double0negative.xServer.XServer;
import cbp.double0negative.xServer.packets.Packet;
import cbp.double0negative.xServer.packets.PacketTypes;
import cbp.double0negative.xServer.util.LogManager;

public class ChatListener implements Listener
{

	Client c;

	public void setClient(Client c)
	{
		this.c = c;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void handleChat(AsyncPlayerChatEvent event)
	{
		if (event.isCancelled())
			return;
		if (XServer.checkPerm(event.getPlayer(), "xserver.message.send"))
		{
			String msg = event.getMessage().replaceAll("(&([a-fk-or0-9]))", "\u00A7$2");
			event.setMessage(msg);
			c.sendMessage(event.getMessage(), event.getPlayer().getDisplayName());
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void handleCommand(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().toLowerCase().startsWith("/all") && event.getPlayer().isOp())
		{
			event.setMessage(event.getMessage().replaceFirst("/all ", ""));
			c.send(new Packet(PacketTypes.PACKET_PLAYER_COMMAND, event.getMessage()));
			LogManager.getInstance().info("/all sent (" + event.getMessage() + ")");
		} else if(event.getMessage().toLowerCase().startsWith("/all"))
		{
			LogManager.getInstance().info("Caught /All, Insufficient permissions");
			event.getPlayer().sendMessage("Unknown command. Type \"/?\" for help.");
			event.isCancelled();
		}
		if (event.getMessage().equalsIgnoreCase("/reload"))
		{
			XServer.restartMode = PacketTypes.DC_TYPE_RELOAD;
		}
		if (event.getMessage().startsWith("/stop"))
		{
			XServer.restartMode = PacketTypes.DC_TYPE_STOP;
		}
	}
    @EventHandler(priority= EventPriority.MONITOR)
	public void onServerCommand(ServerCommandEvent event) {
    	if (event.getCommand().toLowerCase().startsWith("all") && event.getSender() instanceof ConsoleCommandSender)
		{
			event.setCommand(event.getCommand().replaceFirst("all /", ""));
			c.send(new Packet(PacketTypes.PACKET_PLAYER_COMMAND, event.getCommand()));
			LogManager.getInstance().info("/all sent (" + event.getCommand() + ")");
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void handlePlayerJoin(PlayerJoinEvent event)
	{

		HashMap<String, String> f = new HashMap<String, String>();

		f.put("USERNAME", event.getPlayer().getDisplayName());
		f.put("SERVERNAME", XServer.serverName);
		c.send(new Packet(PacketTypes.PACKET_PLAYER_JOIN, f));

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void handlePlayerLeave(PlayerQuitEvent event)
	{

		HashMap<String, String> f = new HashMap<String, String>();

		f.put("USERNAME", event.getPlayer().getDisplayName());
		f.put("SERVERNAME", XServer.serverName);
		c.send(new Packet(PacketTypes.PACKET_PLAYER_LEAVE, f));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void handlePlayerDeath(PlayerDeathEvent event)
	{

		HashMap<String, String> f = new HashMap<String, String>();

		f.put("USERNAME", event.getEntity().getDisplayName());
		f.put("SERVERNAME", XServer.serverName);
		c.send(new Packet(PacketTypes.PACKET_PLAYER_DEATH, f));
	}

	/*
	 * @EventHandler(priority = EventPriority.HIGH) public void
	 * handleCommand(PlayerCommandPreprocessEvent event){
	 * 
	 * if(event.getMessage().equalsIgnoreCase("/reload")){ XServer.restartMode =
	 * PacketTypes.DC_TYPE_RELOAD; } if(event.getMessage().startsWith("/stop")){
	 * XServer.restartMode = PacketTypes.DC_TYPE_STOP; }
	 * 
	 * }
	 */
}
