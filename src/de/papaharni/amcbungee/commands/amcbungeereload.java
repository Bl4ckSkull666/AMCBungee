/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee.commands;

import de.papaharni.amcbungee.AMCBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Pappi
 */
public class amcbungeereload extends Command {
    
    public amcbungeereload() {
        super("amcbungeereload", "amcbungee.reload");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer)s;
        if(p == null)
            return;
     
        ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new doReload(p));
    }
    
    public class doReload implements Runnable {
        private ProxiedPlayer _p;
        
        public doReload(ProxiedPlayer p) {
            _p = p;
        }
        
        @Override
        public void run() {
            AMCBungee.getInstance().reloadConfig();
            AMCBungee.getInstance().reloadMyConfig();
            AMCBungee.getInstance().getPlayerModes().save();
            _p.sendMessage(AMCBungee.convert("&f[&4AMCBungee&f]&aDie Konfiguration wurde neugeladen."));
        }
    }
}
