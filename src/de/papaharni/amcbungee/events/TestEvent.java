/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.events;

import de.papaharni.amcbungee.AMCBungee;
import java.util.logging.Level;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni
 */
public class TestEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onAsync(AsyncEvent e) {
        logMe("AsyncEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(ChatEvent e) {
        logMe("ChatEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(LoginEvent e) {
        logMe("LoginEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPermissionCheck(PermissionCheckEvent e) {
        logMe("PermissionCheckEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        logMe("TargetedEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerHandshake(PlayerHandshakeEvent e) {
        logMe("PlayerHandshakeEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPluginMessage(PluginMessageEvent e) {
        logMe("PluginMessageEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPostLogin(PostLoginEvent e) {
        logMe("PostLoginEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPreLogin(PreLoginEvent e) {
        logMe("TargetedEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onProxyPing(ProxyPingEvent e) {
        logMe("ProxyPingEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onProxyReload(ProxyReloadEvent e) {
        logMe("ProxyReloadEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onServerConnect(ServerConnectEvent e) {
        logMe("ServerConnectEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onServerConnected(ServerConnectedEvent e) {
        logMe("ServerConnectedEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onServerDisconnect(ServerDisconnectEvent e) {
        logMe("ServerDisconnectEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onServerKick(ServerKickEvent e) {
       logMe("ServerKickEvent"); 
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onServerSwitch(ServerSwitchEvent e) {
        logMe("ServerSwitchEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onTabComplete(TabCompleteEvent e) {
        logMe("TabCompleteEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onTabCompleteResponse(TabCompleteResponseEvent e) {
        
        logMe("TabCompleteResponseEvent");
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onTargeted(TargetedEvent e) {
        logMe("TargetedEvent");
    }
    
    private void logMe(String str) {
        AMCBungee.getInstance().getLogger().log(Level.INFO, "Running Event : {0}", str);
    }
}