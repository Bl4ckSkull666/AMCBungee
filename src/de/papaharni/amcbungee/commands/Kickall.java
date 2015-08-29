/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Pappi
 */
public class Kickall extends Command {
    
    public Kickall() {
        super("disconnect", "amcbungee.disconnect");
    }
    
    @Override
    public void execute(CommandSender s, String[] args) {
        if(args.length < 1) {
            s.sendMessages(new String[] {"§cPlease enter a Player or all for all players","§4Bitte gib einen Spieler an oder all für alle"});
            return;
        }
        String str = buildString(args, 1);
        if(str.isEmpty()) {
            s.sendMessages(new String[] {"§cPlease enter a Message","§aBitte gib eine Nachricht an."});
            return;
        }
        
        if(!args[0].equalsIgnoreCase("all")) {
            String[] players = args[0].split("\\,");
            boolean allExist = true;
            for(String player: players) {
                if(ProxyServer.getInstance().getPlayer(player) == null)
                    allExist = false;
            }

            if(!allExist) {
                if(players.length > 1)
                    s.sendMessages(new String[] {"§cOne or more Players don't exist online.","§cMindestes ein Spieler ist nicht online."});
                else
                    s.sendMessages(new String[] {"§cThe player is not online.","§cDer Spieler ist nicht online."});
                return;
            }
            
            for(String player: players) {
                if(ProxyServer.getInstance().getPlayer(player) == null)
                    continue;
                if(ProxyServer.getInstance().getPlayer(player).getName().equalsIgnoreCase(s.getName()))
                    continue;
                
                ProxyServer.getInstance().getPlayer(player).disconnect(str);
            }
        } else {
            for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
                if(p.getName().equalsIgnoreCase(s.getName()))
                    continue;
                p.disconnect(str);
            }
        }
        s.sendMessages(new String[] {"§cThe Players Kicked.","§aDie Spieler wurden erfolgreich gekickt."});
    }
    
    private String buildString(String[] a, int i) {
        if(a.length <= i)
            return "";
        
        if(a.length <= (i+1))
            return a[i];
        
        String str = a[i];
        for(i++ ; i < a.length; i++)
            str += " " + a[i];
        
        return str;
    }
}
