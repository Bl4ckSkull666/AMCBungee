/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.events;

import de.papaharni.amcbungee.AMCBungee;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni
 */
public class AutoStartServer implements Listener {
    private final ArrayList<String> _serverStarts;
    private final ArrayList<ScheduledTask> _tasks;

    public AutoStartServer() {
        _serverStarts = new ArrayList<>();
        _tasks = new ArrayList<>();
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onServerConnect(ServerConnectEvent e) {
        AMCBungee.getInstance().getLogger().log(Level.INFO, "Check-Point 1 - ");
        if(e.getPlayer().getServer() == null || !isGivenServerIn("autostarts.only-from", e.getPlayer().getServer().getInfo().getName().toLowerCase()))
            return;
        
        AMCBungee.getInstance().getLogger().log(Level.INFO, "Check-Point 2 - " + e.getTarget().getName().toLowerCase());
        if(!isGivenServerIn("autostarts.check-server", e.getTarget().getName().toLowerCase()))
            return;
        
        AMCBungee.getInstance().getLogger().log(Level.INFO, "Check-Point 3");
        if(_serverStarts.contains(e.getTarget().getName().toLowerCase())) {
            //Bitte Warte noch einen Moment bis der Server Start bereit ist.
            e.getPlayer().sendMessage(AMCBungee.convert("§cPlease wait a moment. The server is already in the starting process."));
            e.setCancelled(true);
            return;
        }
        
        AMCBungee.getInstance().getLogger().log(Level.INFO, "Check-Point 4");
        if(AMCBungee.getServerPing().containsKey(e.getTarget().getName().toLowerCase())) {
            if(!AMCBungee.getServerPing().get(e.getTarget().getName().toLowerCase()).isOnline()) {
                //Starte Server
                List<String> temp = AMCBungee.getInstance().getConfig().getStringList("autostarts.check-server." + e.getTarget().getName().toLowerCase());
                try {
                    for(String str_cmd: temp)
                        printLinuxCommand(str_cmd);
                    e.getPlayer().sendMessage(AMCBungee.convert("§cPlease wait a moment. We will be starting now the wished server."));
                    _serverStarts.add(e.getTarget().getName().toLowerCase());
                    _tasks.add(ProxyServer.getInstance().getScheduler().schedule(AMCBungee.getInstance(), new informUsers(e.getTarget().getName().toLowerCase()), AMCBungee.getInstance().getConfig().getLong("autostarts.wait-seconds", 30), TimeUnit.SECONDS));
                } catch (IOException ex) {
                    AMCBungee.getInstance().getLogger().log(Level.INFO, "Error on start " + e.getTarget().getName().toLowerCase() + ".", ex);
                    e.getPlayer().sendMessage(AMCBungee.convert("Happend error on starting " + e.getTarget().getName().toLowerCase() + ". Please try again."));
                } catch (Exception ex) {
                    AMCBungee.getInstance().getLogger().log(Level.INFO, "Error on start " + e.getTarget().getName().toLowerCase() + ".", ex);
                    e.getPlayer().sendMessage(AMCBungee.convert("Happend error on starting " + e.getTarget().getName().toLowerCase() + ". Please try again."));
                }
                e.setCancelled(true);
            }
        }
    }
    
    private boolean isGivenServerIn(String confPath, String servername) {
        if(AMCBungee.getInstance().getConfig().isConfigurationSection(confPath + "." + servername.toLowerCase()))
            return true;
        
        if(AMCBungee.getInstance().getConfig().isList(confPath + "." + servername.toLowerCase()))
            return true;
        
        if(AMCBungee.getInstance().getConfig().isList(confPath)) {
            for(String str: AMCBungee.getInstance().getConfig().getStringList(confPath)) {
                if(str.equalsIgnoreCase(servername))
                    return true;
            }
        }
        
        if(AMCBungee.getInstance().getConfig().isString(confPath)) {
            if(AMCBungee.getInstance().getConfig().getString(confPath).equalsIgnoreCase(servername))
                return true;
        }
        return false;
    }
    
    private void printLinuxCommand(String command) throws Exception { 
        String line;
        Process process = Runtime.getRuntime().exec(command);
        Reader r = new InputStreamReader(process.getInputStream());
        BufferedReader in = new BufferedReader(r);
        while((line = in.readLine()) != null) AMCBungee.getInstance().getLogger().log(Level.INFO, line);
        in.close();
    }
    
    private class informUsers implements Runnable {
        private final String _servername;
        
        public informUsers(String sname) {
            _servername = sname;
        }
        
        @Override
        public void run() {
            if(_tasks.contains(this) && _tasks.get(_tasks.indexOf(this)) != null)
                _tasks.remove(this);
            
            if(AMCBungee.getServerPing().containsKey(_servername)) {
                if(AMCBungee.getServerPing().get(_servername).isOnline()) {
                    _serverStarts.remove(_servername);
                    for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
                        pp.sendMessage(AMCBungee.convert("§aServer §e" + _servername + " §ais now online."));
                    }
                } else {
                    _tasks.add(ProxyServer.getInstance().getScheduler().schedule(AMCBungee.getInstance(), new informUsers(_servername), 5, TimeUnit.SECONDS));
                }
            }
        }
    }
}
