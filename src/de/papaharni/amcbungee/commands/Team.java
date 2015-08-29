package de.papaharni.amcbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Pappi
 */
public class Team extends Command {
    
    public Team() {
        super("team", "amcbungee.team");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer)s;
        
        if(p == null)
            return;
        
        if(!p.hasPermission("amcbungee.team"))
            return;
        
        if(args.length < 1) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Du hast vergessen einen Text einzugeben."));
            return;
        }
        
        String msg = args[0];
        for(int i = 1;i < args.length; i++) {
            msg += " " + args[i];
        }
        
        for(ProxiedPlayer pr: ProxyServer.getInstance().getPlayers()) {
            if(pr.hasPermission("amcbungee.team"))
                pr.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&6Team&f][&e" + p.getServer().getInfo().getName() + "&f]<&9" + p.getName() + "&f>&2" + msg));
        }
    }
}
