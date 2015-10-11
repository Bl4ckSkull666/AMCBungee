package de.papaharni.amcbungee.events;

import de.papaharni.amcbungee.AMCBungee;
import de.papaharni.amcbungee.AMCBungee.saveChat;
import de.papaharni.amcbungee.util.ChatLogger;
import de.papaharni.amcbungee.util.PlayerAccess;
import de.papaharni.amcbungee.util.Rnd;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
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
    private final HashMap<UUID, String> _lastMessage = new HashMap<>();
    private final HashMap<UUID, Long> _lastMessageTime = new HashMap<>();
    
    public ChatEvents(AMCBungee plugin) {
        _plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onChatHighest(ChatEvent e) {
        if(e.isCancelled() || e.getMessage().isEmpty())
            return;
                
        ProxiedPlayer ps = getPlayer(e.getSender());        
        
        if(ps == null)
            return;
        
        if(_lastMessage.containsKey(ps.getUniqueId()) && _lastMessageTime.containsKey(ps.getUniqueId())) { 
            if(_lastMessage.get(ps.getUniqueId()).equalsIgnoreCase(e.getMessage())) {
                ps.sendMessage(AMCBungee.convert("&cYou can't send two time the same message."));
                e.setCancelled(true);
                return;
            }
            
            if((System.currentTimeMillis()-_lastMessageTime.get(ps.getUniqueId())) < 2000) {
                ps.sendMessage(AMCBungee.convert("&cPlease wait a moment before you send a message again."));
                e.setCancelled(true);
                return;
            }
        }
        
        ChatLogger cl = new ChatLogger(ps.getName(), ps.getServer().getInfo().getName(), e.getMessage());
        ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new saveChat(cl));
        
        //Check is Chat allowed while said Hello
        PlayerAccess pa = AMCBungee.getPlayerAccess(ps.getName());
        if(ProxyServer.getInstance().getPlayers().size() == 1)
            pa.setAllowChat();
        else if(!pa.getChat() && ProxyServer.getInstance().getPlayers().size() < 20 && !ps.hasPermission("amcbungee.bypass.greeting")) {
            if(!e.isCommand()) {
                for(String str: AMCBungee.getInstance().getConfig().getStringList("chat.greeting")) {
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
                if(AMCBungee.getInstance().getConfig().getStringList("commands.hide").contains(cmd[0].substring(1)))
                    return;
                if(AMCBungee.getInstance().getConfig().getStringList("commands.hide").contains(cmd[0].substring(1)) && !ps.hasPermission("amcbungee.bypass.commands")) {
                    for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
                        if(p.hasPermission("amcbungee.team"))
                            AMCBungee.sendMessages(p, new String[] {"§4Player " + ps.getDisplayName() + " §4use " + cmd[0].substring(1) + " on " + ps.getServer().getInfo().getName(),"§cSpieler " + ps.getDisplayName() + " §cbenutzt " + cmd[0].substring(1) + " auf " + AMCBungee.getInstance().getServerName(ps.getServer().getInfo().getName())});
                    }
                    
                    if(Rnd.get(1, 100) <= _plugin.getConfig().getInt("commands.infokickpc", 50)) {
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
        for(String word: _plugin.getConfig().getStringList("chat.wordfilter")) {
            if(e.getMessage().toLowerCase().contains(word.toLowerCase())) {
                if(!_plugin.getPlayersWarn().containsKey(ps.getName()))
                    _plugin.getPlayersWarn().put(ps.getName(), 1);
                else
                    _plugin.getPlayersWarn().put(ps.getName(), _plugin.getPlayersWarn().get(ps.getName())+1);
                
                if(AMCBungee.isDeLanguage(AMCBungee.getPlayerLang(ps.getName())))
                    ps.disconnect(AMCBungee.convert("Bitte achte auf deine Wortwahl wenn du weiterhin auf unserem Server sein möchtest. ( Warnung " + _plugin.getPlayersWarn().get(ps.getName()) + "/" + _plugin.getConfig().getInt("chat.warning", 5) + " )"));
                else
                    ps.disconnect(AMCBungee.convert("Watch your word selection if you continue want to be on the server. ( Warning " + _plugin.getPlayersWarn().get(ps.getName()) + "/" + _plugin.getConfig().getInt("chat.warning", 5) + " )"));
                
                if(_plugin.getPlayersWarn().get(ps.getName()) >= _plugin.getConfig().getInt("chat.warning", 5))
                    _plugin.setPlayersBlocked(ps.getName());
                e.setCancelled(true);
                return;
            }
        }

        _lastMessage.put(ps.getUniqueId(), e.getMessage());
        _lastMessageTime.put(ps.getUniqueId(), System.currentTimeMillis());
        //ProxyServer.getInstance().getScheduler().runAsync(_plugin, new aSyncChat(ps, e.getMessage()));
    }
    
    /*@EventHandler(priority = EventPriority.LOW)
    public void onChatLowest(ChatEvent e) {
        if(e.isCancelled() || e.getMessage().isEmpty())
            return;
                
        ProxiedPlayer ps = getPlayer(e.getSender());        
        if(ps == null)
            return;
        
        if(e.isCommand() || e.getMessage().startsWith("/"))
            return;

        _lastMessage.put(ps.getUniqueId(), e.getMessage());
        _lastMessageTime.put(ps.getUniqueId(), System.currentTimeMillis());
        ProxyServer.getInstance().getScheduler().runAsync(_plugin, new aSyncChat(ps, e.getMessage()));
    }*/
    
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
            String name = "<§e" + ((_p.getDisplayName() != null && !_p.getDisplayName().isEmpty())?_p.getDisplayName():_p.getName());
            if(AMCBungee.getPlayerAge().containsKey(_p.getUniqueId()) && AMCBungee.getPlayerAge().get(_p.getUniqueId()) > 0) {
                String verifyColor = AMCBungee.getInstance().getConfig().getString("chat-verify-color.is" + (AMCBungee.getPlayerVerification().contains(_p.getUniqueId())?"":"not") + "verify", "&f");
                String age = verifyColor + "[";
                if(AMCBungee.getInstance().getConfig().getBoolean("chat-gender-color.use", false)) {
                    if(!AMCBungee.getPlayerGender().containsKey(_p.getUniqueId()))
                        AMCBungee.getPlayerGender().put(_p.getUniqueId(), "none");
                    age += AMCBungee.getInstance().getConfig().getString("chat-gender-color." + AMCBungee.getPlayerGender().get(_p.getUniqueId()), "");
                }
                age += (AMCBungee.getPlayerAge().get(_p.getUniqueId()) > -1)?String.valueOf(AMCBungee.getPlayerAge().get(_p.getUniqueId())):"N/A";
                age += verifyColor + "]";
                name += age;
            }
            name += "§f> §6";
        
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
