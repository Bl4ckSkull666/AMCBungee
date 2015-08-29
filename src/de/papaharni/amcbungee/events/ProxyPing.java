package de.papaharni.amcbungee.events;

import de.papaharni.amcbungee.AMCBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author Pappi
 */
public class ProxyPing implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyPing(ProxyPingEvent e) {
        if(e.getResponse().getPlayers().getMax() == 0)
            return;
        
        e.setResponse(
            new ServerPing(
                e.getResponse().getVersion(),
                e.getResponse().getPlayers(),
                ChatColor.translateAlternateColorCodes('&', AMCBungee.getRandomModt(e.getConnection().getAddress().getAddress())),
                AMCBungee.getRandomFavicon()
            )
        );
    }
}
