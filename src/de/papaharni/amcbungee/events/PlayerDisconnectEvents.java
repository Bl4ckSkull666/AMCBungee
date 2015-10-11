package de.papaharni.amcbungee.events;

import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import de.papaharni.amcbungee.AMCBungee;
import de.papaharni.amcbungee.util.ChatLogger;
import de.papaharni.amcbungee.util.PlayerAccess;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Pappi
 */
public class PlayerDisconnectEvents implements Listener {
    private final AMCBungee _plugin;
    private final List<String> _pList = new ArrayList<>();
    
    public PlayerDisconnectEvents(AMCBungee plugin) {
        _plugin = plugin;
    }
    
    @EventHandler
    public void onServerKick(ServerKickEvent e) {
        if(AMCBungee.getInstance().getConfig().getBoolean("use-whitelist", false) && !AMCBungee.getWhitelist().isListed(e.getPlayer()))
            return;
        
        ProxiedPlayer p = e.getPlayer();
        if(p == null)
            return;
        
        p.resetTabHeader();
        PlayerAccess pa = AMCBungee.getPlayerAccess(p.getName());
        pa.setTime();
        
        _pList.add(p.getName());
        ProxyServer.getInstance().getScheduler().runAsync(_plugin, new getPlayerDisconnect(p, true, e.getKickReason()));
    }
    
    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        if(AMCBungee.getInstance().getConfig().getBoolean("use-whitelist", false) && !AMCBungee.getWhitelist().isListed(e.getPlayer()))
            return;
        
        ProxiedPlayer p = e.getPlayer();
        if(p == null)
            return;
        
        p.resetTabHeader();
        PlayerAccess pa = AMCBungee.getPlayerAccess(p.getName());
        pa.setTime();
        
        if(_pList.contains(p.getName()))
            _pList.remove(p.getName());
        else
            ProxyServer.getInstance().getScheduler().runAsync(_plugin, new getPlayerDisconnect(p, false, ""));
    }
    
    public class getPlayerDisconnect implements Runnable {
        
        private final ProxiedPlayer _p;
        private final boolean _isKicked;
        private final String _kickReason;
        
        public getPlayerDisconnect(ProxiedPlayer p, boolean kicked, String kickReason) {
           _p = p;
           _isKicked = kicked;
           _kickReason = kickReason;
        }
        
        @Override
        public void run() {
            AMCBungee.getPlayersLanguage().remove(_p.getName());

            long onTime = 0;
            if(_plugin.getOnlineSince().containsKey(_p.getName())) {
                onTime = (System.currentTimeMillis()-_plugin.getOnlineSince().get(_p.getName()))/1000;
                _plugin.getOnlineSince().remove(_p.getName());
            }

            _plugin.getSQL().setOnlineStatus(_p.getName(), _p.getUniqueId().toString(), 0, onTime);
            _plugin.getPlayerOnServer().remove(_p.getName());
            
            if(!_kickReason.isEmpty())
                _p.disconnect(Mu1ti1ingu41.castMessage(_kickReason));
            
            if(AMCBungee.getJoinTasks().containsKey(_p.getName().toLowerCase())) {
                AMCBungee.getJoinTasks().get(_p.getName().toLowerCase()).cancel();
                AMCBungee.getJoinTasks().remove(_p.getName().toLowerCase());
                return;
            }
            
            String leftMessage = ChatColor.GRAY + _p.getName() + ChatColor.GOLD + " has left the Network. " + ChatColor.YELLOW + "Good Bye.";
            if(_isKicked)
                leftMessage = ChatColor.GOLD + "Oh no, " + ChatColor.GRAY + _p.getName() + ChatColor.GOLD + " has learned to fly out of the Network. " + ChatColor.YELLOW + "Good Bye.";
            
            ChatLogger cl = new ChatLogger("server", "server", leftMessage);
            ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new AMCBungee.saveChat(cl));
            
            for(ProxiedPlayer pr: ProxyServer.getInstance().getPlayers()) {
                if(pr != _p && _p != null && pr != null)
                    pr.sendMessage(Mu1ti1ingu41.castMessage(leftMessage));
            }
        }
    }
}
