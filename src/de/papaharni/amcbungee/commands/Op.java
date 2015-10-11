/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.commands;

import de.papaharni.amcbungee.AMCBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author PapaHarni
 */
public class Op extends Command {
    
    public Op() {
        super("op", "");
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        if(!(s instanceof ProxiedPlayer)) {
            s.sendMessage(AMCBungee.convert("THis Command can be only run by a Player."));
            return;
        }
        ProxiedPlayer spp = (ProxiedPlayer)s;
        ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new InformTheTeam(spp));
    }
    
    private class InformTheTeam implements Runnable {
        private final ProxiedPlayer _spp;
        public InformTheTeam(ProxiedPlayer pp) {
            _spp = pp;
        }
        
        @Override
        public void run() {
            if(_spp == null)
                return;
            
            int i = 0;
            for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
                if(pp.hasPermission("amcserver.team")) {
                    pp.sendMessage();
                    pp.sendMessage(AMCBungee.convert(ChatColor.RED + "Player " + ChatColor.YELLOW + _spp.getName() + ChatColor.RED + " needs help on " + ChatColor.YELLOW + _spp.getServer().getInfo().getName() + ChatColor.RED + ". Please contact him." ));
                    i++;
                }
            }
            
            if(i == 0) {
                _spp.sendMessage(AMCBungee.convert(ChatColor.RED + "We are sorry but we can't find yet a Team Member to help you. Please try again later."));
            } else {
                _spp.sendMessage(AMCBungee.convert(ChatColor.DARK_GREEN + "The Team is informated that you need help."));
                _spp.sendMessage(AMCBungee.convert(ChatColor.GREEN + "Please wait a moment."));
                _spp.sendMessage(AMCBungee.convert(ChatColor.DARK_GREEN + "The team will report as fast as possible be delivered to you."));
            }
        }
    }
}
