/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee.events;

import de.papaharni.amcbungee.AMCBungee;
import de.papaharni.amcbungee.util.Mixes;
import de.papaharni.amcbungee.util.PingTask;
import java.util.HashMap;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.tab.GlobalPing;

/**
 *
 * @author Pappi
 */
public class ServerConnectEvents implements Listener {
    private final AMCBungee _plugin;
    private long _lastMotdTime;
    
    public ServerConnectEvents(AMCBungee plugin) {
        _plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onServerConnect(ServerConnectEvent e) {
        ProxiedPlayer p = e.getPlayer();
        
        if(p == null)
            return;
        
        ProxyServer.getInstance().getScheduler().runAsync(_plugin, new getServerConnecting(e.getPlayer()));
        if(!_plugin.getOnlineSince().containsKey(p.getName())) {
            _plugin.getOnlineSince().put(p.getName(), System.currentTimeMillis());
            Mixes.setDisplayColor(p);
        }
    }
    
    
    private static final HashMap<String, Boolean> _sStatus = new HashMap<>();
    
    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        ServerInfo si = e.getPlayer().getServer().getInfo();
        if(!_sStatus.containsKey(si.getName().toLowerCase()))
            _sStatus.put(si.getName().toLowerCase(), false);
        
        if(AMCBungee.getServerPing().containsKey(si.getName().toLowerCase())) {
            PingTask pt = AMCBungee.getServerPing().get(si.getName().toLowerCase());
            if(!pt.isOnline() && _sStatus.get(si.getName().toLowerCase())) {
                
            }
        }
        ProxyServer.getInstance().getScheduler().runAsync(_plugin, new getServerSwitch(e.getPlayer(), e.getPlayer().getServer().getInfo().getName()));
    }
    
    public class getServerConnecting implements Runnable {
        private final ProxiedPlayer _p;
        public getServerConnecting(ProxiedPlayer p) {
            _p = p;
        }
        
        @Override
        public void run() {
            _plugin.getSQL().setUUID(_p);
        }
    }
    
    public class getServerSwitch implements Runnable {
        private final ProxiedPlayer _p;
        private final String _server;
        
        public getServerSwitch(ProxiedPlayer p, String server) {
           _p = p;
           _server = server;
        }
        
        @Override
        public void run() {
            _plugin.getSQL().setServerByPlayer(_p.getName(), _server);
        }
    }
}