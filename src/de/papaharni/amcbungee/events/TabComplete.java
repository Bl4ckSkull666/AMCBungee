/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.events;

import de.papaharni.amcbungee.AMCBungee;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni
 */
public class TabComplete implements Listener {
    //private final HashMap<UUID, String> _cursors = new HashMap<>();
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onTabComplete(TabCompleteEvent e) {
        if(e.getReceiver() != null) {
            ProxiedPlayer rp = getPlayer(e.getReceiver().getAddress());
            if(rp == null)
                return;
            
            String names = "";
            String begin = e.getCursor();
            for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
                if(pp.getName().toLowerCase().startsWith(begin)) {
                    e.getSuggestions().add(pp.getName());
                    if(pp.getDisplayName().isEmpty())
                        names += (names.isEmpty()?"":ChatColor.GOLD + ", ") + ChatColor.YELLOW + pp.getName();
                    else
                        names += (names.isEmpty()?"":ChatColor.GOLD + ", ") + pp.getDisplayName();
                }
            }
            rp.sendMessage(AMCBungee.convert(names));
        }
    }
    
    /*@EventHandler(priority = EventPriority.HIGH)
    public void onTabCompleteResponse(TabCompleteResponseEvent e) {
        if(e.getReceiver() == null)
            return;
        
        ProxiedPlayer rp = getPlayer(e.getReceiver().getAddress());
        if(rp == null)
            return;
        
        if(!_cursors.containsKey(rp.getUniqueId()))
            return;
        
        e.getSuggestions().clear();
        String names = "";
        String begin = _cursors.get(rp.getUniqueId()).toLowerCase();
        for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
            if(pp.getName().toLowerCase().startsWith(begin)) {
                e.getSuggestions().add(pp.getName());
                if(pp.getDisplayName().isEmpty())
                    names += (names.isEmpty()?"":ChatColor.GOLD + ", ") + ChatColor.YELLOW + pp.getName();
                else
                    names += (names.isEmpty()?"":ChatColor.GOLD + ", ") + pp.getDisplayName();
            }
        }
        rp.sendMessage(AMCBungee.convert(names));
        _cursors.remove(rp.getUniqueId());
    }*/
    
    private ProxiedPlayer getPlayer(InetSocketAddress con) {
        for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
            if(pp.getPendingConnection().getAddress().equals(con))
                return pp;
        }
        return null;
    }
}
