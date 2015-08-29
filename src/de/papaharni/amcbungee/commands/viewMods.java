/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.commands;

import java.awt.TextComponent;
import java.util.Map;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Pappi
 */
public class viewMods extends Command {
    
    public viewMods() {
        super("viewMods", "amcbungee.cmd.viewmods");
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        if(a.length < 1) {
            s.sendMessage("Please give a online Player name.");
            return;
        }
        
        if(ProxyServer.getInstance().getPlayer(a[0]) == null) {
            s.sendMessage("Can't find given Player");
            return;
        }
        
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(a[0]);
        s.sendMessage("§6Mod List of Player " + p.getDisplayName() + " §6:");
        for(Map.Entry<String, String> e : p.getModList().entrySet()) {
            s.sendMessage("§6" + e.getKey() + " §9: §2" + e.getValue());
        }
    }
    
}
