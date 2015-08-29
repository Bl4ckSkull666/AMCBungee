/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee.commands;

import java.util.Map;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class plist extends Command {
    
    public plist() {
        super("plist", "plist.use");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer)s;
        
        if(p == null)
            return;
        
        if(!p.hasPermission("plist.use"))
            return;
    
        for (Map.Entry<String, ServerInfo> e : ProxyServer.getInstance().getServers().entrySet()) {
            String sname = e.getKey();
            ServerInfo sinfo = e.getValue();
            String list = "";
            int i = 0;
            p.sendMessage("Auf dem Server " + sname + " sind derzeit " + sinfo.getPlayers().size() + " Spieler Online :");
            for(ProxiedPlayer pl: sinfo.getPlayers()) {
                list += ((list.length() <= 1)?"":", ") + pl.getDisplayName();
                if(i % 5 == 0) {
                    p.sendMessage(list);
                    list = "";
                }
            }
            if(list.length() > 1)
                p.sendMessage(list);
        }
    }
}
