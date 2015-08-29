/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.util;

import de.papaharni.amcbungee.AMCBungee;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PingTask implements Runnable {

    private final ServerInfo _server;
    private boolean _online = false;

    public PingTask(ServerInfo server) {
        _server = server;
    }

    public boolean isOnline() {
        return _online;
    }

    @Override
    public void run() {
        _server.ping(new Callback<ServerPing>() {
            @Override
            public void done(ServerPing v, Throwable thrwbl) {
                if (thrwbl != null || v == null) {
                    _online = false;
                    return;
                }
                _online = true;
            }

        });
        
        if(AMCBungee.getServerPingLastStatus().get(_server.getName().toLowerCase()) && !_online) {
            for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers())
                pp.sendMessage(AMCBungee.convert("§cServer §e" + _server.getName() + " §cis now offline."));
            AMCBungee.getServerPingLastStatus().put(_server.getName().toLowerCase(), _online);
        }
        
        AMCBungee.getServerPingLastStatus().put(_server.getName().toLowerCase(), _online);
    }
}
