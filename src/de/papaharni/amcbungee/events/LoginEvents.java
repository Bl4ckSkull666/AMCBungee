/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee.events;

import de.papaharni.amcbungee.AMCBungee;
import de.papaharni.amcbungee.util.ChatLogger;
import de.papaharni.amcbungee.util.Mixes;
import de.papaharni.amcbungee.util.PlayerAccess;
import de.papaharni.amcbungee.util.Votes;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni aka Bl4ckSkull666
 */
public class LoginEvents implements Listener {
    private final AMCBungee _plugin;
    
    public LoginEvents(AMCBungee plugin) {
        _plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onServerConnect(ServerConnectEvent e) {
        if(e.isCancelled())
            return;
        
        if(e.getPlayer().getPendingConnection().isOnlineMode()) {
            //AMCBungee.getInstance().getLogger().log(Level.INFO, "Player {0} ( {1} ) is in Online Mode. All ok", new Object[]{e.getPlayer().getName(), e.getPlayer().getUniqueId().toString()});
            AMCBungee.getInstance().getPlayerModes().add(e.getPlayer().getUniqueId().toString(), true);
        } else {
            //AMCBungee.getInstance().getLogger().log(Level.INFO, "ATTENTION - Player {0} ( {1} ) is in Offline Mode.", new Object[]{e.getPlayer().getName(), e.getPlayer().getUniqueId().toString()});
            if(AMCBungee.getInstance().getPlayerModes().isSeen(e.getPlayer().getUniqueId().toString())) {
                if(AMCBungee.getInstance().getPlayerModes().seenInOnlineMode(e.getPlayer().getUniqueId().toString())) {
                    e.getPlayer().disconnect(AMCBungee.convert("We have seen your UUID already as Premium. Please use the Mojang Account to connect to our servers."));
                    e.setCancelled(true);
                    return;
                }
            }

            if(Mixes.isGivenServerIn("only-online-mode", e.getTarget().getName())) {
                String defServer = AMCBungee.getInstance().getConfig().getString("only-online-mode.send-offline-to", "lobby");
                if(AMCBungee.getServerPing().containsKey(defServer)) {
                    if(AMCBungee.getServerPing().get(defServer).isOnline()) {
                        ServerInfo si = ProxyServer.getInstance().getServerInfo(defServer);
                        if(si != null)
                            e.setTarget(si);
                    }
                }
            }
            AMCBungee.getInstance().getPlayerModes().add(e.getPlayer().getUniqueId().toString(), false);
        }
        
        PlayerAccess pa = AMCBungee.getPlayerAccess(e.getPlayer().getName());
        if(ProxyServer.getInstance().getPlayers().size() == 1)
            pa.setAllowChat();
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLoginEvent(PostLoginEvent e) {
        if(AMCBungee.getInstance().getConfig().getBoolean("use-whitelist", false) && !AMCBungee.getWhitelist().isListed(e.getPlayer()))
            return;
        
        String country = Mixes.getCountryName(e.getPlayer().getPendingConnection().getAddress().getAddress());
        String langCode = Mixes.getLangCode(e.getPlayer().getPendingConnection().getAddress().getAddress());
        TextComponent countryCode = new TextComponent(ChatColor.BLUE + langCode);
        countryCode.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, 
                        new ComponentBuilder(ChatColor.GREEN + country).create()
                )
        );
        
        TextComponent message = new TextComponent(ChatColor.WHITE + "[");
        message.addExtra(countryCode);
        message.addExtra(ChatColor.WHITE + "]");
        TextComponent player = new TextComponent(ChatColor.GRAY + e.getPlayer().getName());
        player.setHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, 
                        new ComponentBuilder(ChatColor.YELLOW + e.getPlayer().getUniqueId().toString()).create()
                )
        );
        message.addExtra(player);
        message.addExtra(ChatColor.GOLD + " has joined the Network.");
        for(ProxiedPlayer pr: ProxyServer.getInstance().getPlayers()) {
            if(!pr.getUniqueId().equals(e.getPlayer().getUniqueId())) {
                pr.sendMessage(message);
            }
        }
        
        ChatLogger cl = new ChatLogger("server", "server", ChatColor.GRAY + e.getPlayer().getName() + ChatColor.GOLD + " has joined the Network.");
        ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new AMCBungee.saveChat(cl));
        
        Title myTitle = ProxyServer.getInstance().createTitle();
        TextComponent tMsg = new TextComponent(ChatColor.GOLD + "Welcome on AngelZMineCraft");
        tMsg.setHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, 
                        new ComponentBuilder(ChatColor.YELLOW + "Homepage --> www.AMC-Server.de").create()
                )
        );
        tMsg.setClickEvent(
                new ClickEvent(
                        ClickEvent.Action.OPEN_URL,
                        "http://www.amc-server.de"
                )
        );
        myTitle.title(tMsg);
        myTitle.stay(100);
        myTitle.fadeIn(40);
        myTitle.fadeOut(40);
        TextComponent playerMsg = new TextComponent(ChatColor.DARK_GREEN + e.getPlayer().getName());
        myTitle.subTitle(playerMsg);
        myTitle.send(e.getPlayer());
        tMsg.addExtra(" ");
        tMsg.addExtra(playerMsg);
        e.getPlayer().sendMessage(tMsg);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoginEvent(LoginEvent e) {
        if(e.isCancelled())
            return;
        
        if(e.getCancelReason() != null && !e.getCancelReason().isEmpty())
            return;
        
        if(AMCBungee.getInstance().getConfig().isList("ignore-names")) {
            for(String str: AMCBungee.getInstance().getConfig().getStringList("ignore-names")) {
                if(e.getConnection().getName().toLowerCase().contains(str.toLowerCase())) {
                    e.setCancelReason(ChatColor.RED + "Your name does not match our rules. Found " + ChatColor.YELLOW + str.toLowerCase() + ChatColor.RED + " in your Name as not allowed name part.");
                    e.setCancelled(true);
                    return;
                }
            }
        }
        
        String langCode = Mixes.getLangCode(e.getConnection().getAddress().getAddress());
        if(!_plugin.getConfig().getBoolean("allow-proxy", false)) {
            if("A1".equalsIgnoreCase(langCode)) {
                e.setCancelReason(ChatColor.RED + "This server don't allow Anonymous Proxy using. Please deactivate your Proxy access.");
                e.setCancelled(true);
                return;
            }
        }
        
        if(_plugin.isPlayersBlocked(e.getConnection().getName())) {
            e.setCancelReason(ChatColor.RED + "Your access to this server is temporary blocked. Try again later.");
            e.setCancelled(true);
            return;
        }
        
        AMCBungee.getLastSeenPlayers().put(e.getConnection().getAddress().getAddress(), e.getConnection().getName());
        ScheduledTask st = ProxyServer.getInstance().getScheduler().runAsync(_plugin, new doJobsAfterJoin(e.getConnection()));
        AMCBungee.getJoinTasks().put(e.getConnection().getName(), st);
    }
    
    public class doJobsAfterJoin implements Runnable {
        private final PendingConnection _pc;
        
        public doJobsAfterJoin(PendingConnection pc) {
            _pc = pc;
        }
        
        @Override
        public void run() {
            String langCode = Mixes.getLangCode(_pc.getAddress().getAddress());
            AMCBungee.getPlayersLanguage().put(_pc.getName(), Mixes.getLanguageForLangCode(langCode));
            
            _plugin.getSQL().setUserIp(_pc.getName(), _pc.getAddress().getAddress().getHostAddress());
            _plugin.getSQL().setOnlineStatus(_pc.getName(), _pc.getUniqueId().toString(), 1, 0);
            
            PlayerAccess pa = AMCBungee.getPlayerAccess(_pc.getName());
        
            //Player was longer as 1 Hour offline. Need new Greeting.
            if(pa.getTime() != 0L && (System.currentTimeMillis()-pa.getTime()) <= (60*60*1000))
                pa.setAllowChat();
            
            Votes v = new Votes(_pc.getName(), _pc.getAddress().getAddress().getHostAddress());
            v.updateVotes();
            _plugin.getPlayerVotes().put(_pc.getName(), v);
            
            AMCBungee.getJoinTasks().remove(_pc.getName().toLowerCase());
        }
    }
}
