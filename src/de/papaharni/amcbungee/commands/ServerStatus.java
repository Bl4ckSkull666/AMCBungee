/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.commands;

import de.papaharni.amcbungee.AMCBungee;
import java.net.ServerSocket;
import java.util.Map;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author PapaHarni
 */
public class ServerStatus extends Command {
    
    public ServerStatus() {
        super("serverstatus", "amcbungee.serverstatus");
    }
    
    @Override
    public void execute(CommandSender s, String[] args) {
        ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new getServerStatus(s));
    }
    
    public class getServerStatus implements Runnable {
        private final CommandSender _cs;
        public getServerStatus(CommandSender cs) {
            _cs = cs;
        }
        
        @Override
        public void run() {
            for(Map.Entry<String, ServerInfo> me: ProxyServer.getInstance().getServers().entrySet()) {
                try {
                    ServerSocket socket=new ServerSocket();
                    socket.bind(me.getValue().getAddress());
                    socket.setSoTimeout(2000);
                    String msg = "";
                    if(socket.isBound())
                        msg = ChatColor.DARK_GREEN + "Server " + me.getKey() + " is Online.";
                    else
                        msg = ChatColor.RED + "Server " + me.getKey() + " is currently Offline.";
                    _cs.sendMessage(AMCBungee.convert(msg));
                } catch (Exception ex) {
                    _cs.sendMessage(AMCBungee.convert(ChatColor.DARK_GRAY + "Cant check Server " + me.getKey() + ". Please try again."));
                    AMCBungee.getInstance().getLogger().log(Level.INFO, ChatColor.DARK_GRAY + "Cant check Server " + me.getKey() + ".", ex.getLocalizedMessage());
                }
            }
        }
    }
}
