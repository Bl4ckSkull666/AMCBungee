/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Pappi
 */
public class BColor extends Command {
    
    private String[] codes = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","k","l","m","n"};
    
    public BColor() {
        super("bcolor", "amcbungee.bcolor");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer)s;
        if(p == null)
            return;
        
        if(args.length >= 1) {
            String cCode = "";
            for(String arg: args) {
                if(arg.startsWith("&") && arg.length() == 2 && codes.equals(arg.substring(1))) {
                    cCode += arg;
                }
            }
            cCode += p.getName();
            cCode = ChatColor.translateAlternateColorCodes('&', cCode);
            if(cCode.length() > 16)
                cCode = cCode.substring(0, 15);
            p.setDisplayName(cCode);
            p.sendMessage("Deine neue BungeeCord DisplayNamen Farbe ist " + p.getDisplayName());
        } else {
            p.sendMessage("Deine BungeeCord DisplayName Farbe ist " + p.getDisplayName());
        }
    }
}
