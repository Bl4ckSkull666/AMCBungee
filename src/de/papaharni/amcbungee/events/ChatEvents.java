package de.papaharni.amcbungee.events;

import de.papaharni.amcbungee.AMCBungee;
import de.papaharni.amcbungee.AMCBungee.saveChat;
import de.papaharni.amcbungee.util.ChatLogger;
import de.papaharni.amcbungee.util.PlayerAccess;
import de.papaharni.amcbungee.util.Rnd;
import java.util.Calendar;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author Pappi
 */
public class ChatEvents implements Listener {
    private final AMCBungee _plugin;
    private final String[] _colorCodes = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    private final String[] _formatCodes = {"k", "l", "m", "n", "o", "r"};
    private final String[] _linkCodes = {"http://", "www."};
    
    public ChatEvents(AMCBungee plugin) {
        _plugin = plugin;
    }
    
    /*@EventHandler(priority = EventPriority.HIGHEST)
    public void onChatPlayerAccess(ChatEvent e) {
        ProxiedPlayer ps = getPlayer(e.getSender());
        if(ps == null)
            return;
        
        PlayerAccess pa = AMCBungee.getPlayerAccess(ps.getName());
        if(!pa.getChat() && ProxyServer.getInstance().getPlayers().size() < 20 && !ps.hasPermission("amcbungee.bypass.greeting")) {
            if(!e.isCommand()) {
                for(String str: AMCBungee.getInstance().getMyConfig()._chat_greeting) {
                    if(e.getMessage().toLowerCase().contains(str.toLowerCase())) {
                        pa.setAllowChat();
                        return;
                    }
                }
            }
            
            if(!pa.getChat()) {
                if(AMCBungee.isDeLanguage(AMCBungee.getPlayerLang(ps.getName())))
                    ps.sendMessage("§f[§a§oInfo§r§f] §c§oBitte begrüsse doch zuerst die anderen Spieler auf unserem Server.");
                else
                    ps.sendMessage("§f[§a§oInfo§r§f] §c§oPlease greet first the other peoples on our server.");
                e.setCancelled(true);
            }
        }
    }*/
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatHighest(ChatEvent e) {
        if(e.isCancelled())
            return;
                
        ProxiedPlayer ps = getPlayer(e.getSender());        
        
        if(ps == null)
            return;
        
        //Check is Chat allowed while said Hello
        PlayerAccess pa = AMCBungee.getPlayerAccess(ps.getName());
        if(ProxyServer.getInstance().getPlayers().size() == 1)
            pa.setAllowChat();
        else if(!pa.getChat() && ProxyServer.getInstance().getPlayers().size() < 20 && !ps.hasPermission("amcbungee.bypass.greeting")) {
            if(!e.isCommand()) {
                for(String str: AMCBungee.getInstance().getMyConfig()._chat_greeting) {
                    String msg = e.getMessage().toLowerCase();
                    if(msg.startsWith(str.toLowerCase())) {
                        pa.setAllowChat();
                        break;
                    }
                }
            }
            
            if(!pa.getChat()) {
                if(AMCBungee.isDeLanguage(AMCBungee.getPlayerLang(ps.getName())))
                    ps.sendMessage(AMCBungee.convert("§f[§a§oInfo§r§f] §c§oBitte begrüsse doch zuerst die anderen Spieler auf unserem Server."));
                else
                    ps.sendMessage(AMCBungee.convert("§f[§a§oInfo§r§f] §c§oPlease greet first the other peoples on our server."));
                e.setCancelled(true);
                return;
            }
        }

        //Check is it a command and is it alloewd
        if(e.isCommand() && e.getMessage().startsWith("/")) {
            String[] cmd = e.getMessage().split(" ");
            if(cmd.length >= 1) {
                if(AMCBungee.getInstance().getMyConfig()._cmd_hide.contains(cmd[0].substring(1)))
                    return;
                if(AMCBungee.getInstance().getMyConfig()._cmd_info.contains(cmd[0].substring(1)) && !ps.hasPermission("amcbungee.bypass.commands")) {
                    for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
                        if(p.hasPermission("amcbungee.team"))
                            AMCBungee.sendMessages(p, new String[] {"§4Player " + ps.getDisplayName() + " §4use " + cmd[0].substring(1) + " on " + ps.getServer().getInfo().getName(),"§cSpieler " + ps.getDisplayName() + " §cbenutzt " + cmd[0].substring(1) + " auf " + AMCBungee.getInstance().getServerName(ps.getServer().getInfo().getName())});
                    }
                    
                    if(Rnd.get(1, 100) <= _plugin.getMyConfig()._cmd_info_kick_pc) {
                        if(AMCBungee.isDeLanguage(AMCBungee.getPlayerLang(ps.getName())))
                            ps.disconnect(AMCBungee.convert("Bitte verwende kein /" + cmd[0].substring(1) + " auf unserem Server. TMI und andere mogel Mods sind hier nicht erlaubt. Vielen Dank"));
                        else
                            ps.disconnect(AMCBungee.convert("Please dont use " + cmd[0].substring(1) + " on our Servers. TMI and other cheat Mods are not allowed here. Thank you"));
                    }
                    e.setCancelled(true);
                    return;
                }
            }
        }
        
        if(e.isCommand() || e.getMessage().startsWith("/"))
            return;
        
        //Check Chat word Filter
        for(String word: _plugin.getMyConfig()._chat_wordfilter) {
            if(e.getMessage().toLowerCase().contains(word.toLowerCase())) {
                if(!_plugin.getPlayersWarn().containsKey(ps.getName()))
                    _plugin.getPlayersWarn().put(ps.getName(), 1);
                else
                    _plugin.getPlayersWarn().put(ps.getName(), _plugin.getPlayersWarn().get(ps.getName())+1);
                
                if(AMCBungee.isDeLanguage(AMCBungee.getPlayerLang(ps.getName())))
                    ps.disconnect(AMCBungee.convert("Bitte achte auf deine Wortwahl wenn du weiterhin auf unserem Server sein möchtest. ( Warnung " + _plugin.getPlayersWarn().get(ps.getName()) + "/" + _plugin.getMyConfig()._chat_warn + " )"));
                else
                    ps.disconnect(AMCBungee.convert("Watch your word selection if you continue want to be on the server. ( Warning " + _plugin.getPlayersWarn().get(ps.getName()) + "/" + _plugin.getMyConfig()._chat_warn + " )"));
                
                if(_plugin.getPlayersWarn().get(ps.getName()) >= _plugin.getMyConfig()._chat_warn)
                    _plugin.setPlayersBlocked(ps.getName());
                e.setCancelled(true);
                return;
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatLowest(ChatEvent e) {
        if(e.isCancelled() || e.getMessage().isEmpty())
            return;
                
        ProxiedPlayer ps = getPlayer(e.getSender());        
        if(ps == null)
            return;
        
        if(e.isCommand() || e.getMessage().startsWith("/"))
            return;
        
        ChatLogger cl = new ChatLogger(ps.getName(), ps.getServer().getInfo().getName(), e.getMessage());
        ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new saveChat(cl));
        ProxyServer.getInstance().getScheduler().runAsync(_plugin, new aSyncChat(ps, e.getMessage()));
    }
    
    private ProxiedPlayer getPlayer(Connection c) {
        for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if(p.getPendingConnection().getAddress() == c.getAddress())
                return p;
        }
        return null;
    }
    
    public class aSyncChat implements Runnable {
        private final ProxiedPlayer _p;
        private String _msg;
        
        public aSyncChat(ProxiedPlayer p, String msg) {
           _p = p;
           _msg = msg;
        }
        
        @Override
        public void run() {
            if(_msg.startsWith("!"))
                _msg = _msg.substring(1);
            
            String prefix = "§e<§b" + AMCBungee.getInstance().getServerName(_p.getServer().getInfo().getName()) + "§e>§f";
            String name = "<§e" + ((_p.getDisplayName() != null && !_p.getDisplayName().isEmpty())?_p.getDisplayName():_p.getName()) + "§f> §6";
        
            if(!_p.hasPermission("amcbungee.link")) {
                for(String search: _linkCodes)
                    _msg = _msg.replace(search, "");
            }
            if(!_p.hasPermission("amcbungee.format")) {
                for(String search: _formatCodes) 
                    _msg = _msg.replace("&" + search, "");
            }
            if(!_p.hasPermission("amcbungee.color")) {
                for(String search: _colorCodes) 
                    _msg = _msg.replace("&" + search, "");
            }
            
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            String time = ChatColor.WHITE + "[" + ChatColor.GOLD + (cal.get(Calendar.HOUR_OF_DAY) < 10?"0":"") + cal.get(Calendar.HOUR_OF_DAY) + ":" + (cal.get(Calendar.MINUTE) < 10?"0":"") + cal.get(Calendar.MINUTE) + ":" + (cal.get(Calendar.SECOND) < 10?"0":"") + cal.get(Calendar.SECOND) + ChatColor.WHITE + "]";
        

            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                if(!p.getServer().getInfo().getName().equalsIgnoreCase(_p.getServer().getInfo().getName()))
                    p.sendMessage(AMCBungee.convert(time + prefix + name + _msg));
            }
        }
    }
}
