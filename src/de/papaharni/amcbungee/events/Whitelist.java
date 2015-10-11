/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.events;

import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.papaharni.amcbungee.AMCBungee;
import de.papaharni.amcbungee.util.Mixes;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni
 */
public class Whitelist extends Command implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerConnect(ServerConnectEvent e) {
        if(!AMCBungee.getInstance().getConfig().getBoolean("use-whitelist", false))
            return;
        
        ProxiedPlayer pp = e.getPlayer();
        if(!AMCBungee.getWhitelist().isListed(pp)) {
            pp.disconnect(Language.getMessage(AMCBungee.getInstance(), pp.getUniqueId(), "whitelist.denied", "You're not on the Server Whitelist. Please ask the Team to add you to the Whitelist."));
            e.setCancelled(true);
        }
    }
    
    public Whitelist() {
        super("whitelist", "amcbungee.whitelist");
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof ProxiedPlayer)
            uuid = ((ProxiedPlayer)s).getUniqueId();
        
        if(a.length < 1) {
            s.sendMessage(Language.getMessage(AMCBungee.getInstance(), uuid, "whitelist.command.arguments-one", "Need one or more arguments."));
            return;
        }
        
        ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new doForWhitelist(s,a));
    }
    
    private class doForWhitelist implements Runnable {
        private final CommandSender _s;
        private final String[] _a;
        
        public doForWhitelist(CommandSender s, String[] a) {
            _s = s;
            _a = a;
        }
        
        @Override
        public void run() {
            UUID suuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
            if(_s instanceof ProxiedPlayer)
                suuid = ((ProxiedPlayer)_s).getUniqueId();
            switch(_a[0].toLowerCase()) {
                case "on":
                    if(!_s.hasPermission("amcbungee.whitelist.status")) {
                        _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.permission.status", "You dont have permission to enable/disable the whitelist."));
                        return;
                    }
                    
                    AMCBungee.getWhitelist().load();
                    AMCBungee.startWhitelistTask();
                    
                    AMCBungee.getInstance().getConfig().set("use-whitelist", true);
                    AMCBungee.getInstance().saveConfig();
                    
                    _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.command.on", "Whitelist is now activated. Only whitelisted players can be join now."));
                    return;
                case "off":
                    if(!_s.hasPermission("amcbungee.whitelist.status")) {
                        _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.permission.status", "You dont have permission to enable/disable the whitelist."));
                        return;
                    }
                    
                    AMCBungee.cancelWhitelistTask();
                    
                    AMCBungee.getInstance().getConfig().set("use-whitelist", false);
                    AMCBungee.getInstance().saveConfig();
                    
                    _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.command.off", "Whitelist is deactivated. Everyone can now join the Server."));
                    return;
                case "add":
                    if(_a.length < 2) {
                        _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.command.arguments-more", "Need two or more arguments."));
                        return;
                    }
                    for(int i = 1; i < _a.length; i++) {
                        if(_a[i].startsWith("+")) {
                            String name = _a[i].substring(1);
                            if(AMCBungee.getWhitelist().isListed(name)) {
                                _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.command.add.islisted", "%name% is already listed.", new String[] {"%name%"}, new String[] {name}));
                                continue;
                            }
                            AMCBungee.getInstance().getSQL().addWhiteList(name, null);
                            _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.command.add.name", "Added %name% successful to Whitelist.", new String[] {"%name%"}, new String[] {name}));
                            AMCBungee.getWhitelist().add(name.toLowerCase());
                        } else {
                            UUID uuid = getUUIDFromMojang(_a[i]);
                            if(uuid == null) {
                                _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.command.add.no-uuid", "Cant find UUID for Name %name%, if you wish to add this name to the whitelist, use a + directly before the Name", new String[] {"%name%"}, new String[] {_a[i]}));
                            } else {
                                if(AMCBungee.getWhitelist().isListed(uuid)) {
                                _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.command.add.islisted", "%name% is already listed.", new String[] {"%name%"}, new String[] {_a[i]}));
                                continue;
                            }
                                AMCBungee.getInstance().getSQL().addWhiteList("", uuid);
                                _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.command.add.uuid", "Added UUID of %name% successful to Whitelist.", new String[] {"%name%"}, new String[] {_a[i]}));
                                AMCBungee.getWhitelist().add(uuid);
                            }
                        }
                    }
                    return;
                case "remove":
                    for(int i = 1; i < _a.length; i++) {
                        ProxiedPlayer pp = null;
                        UUID uuid = getUUIDFromMojang(_a[i]);
                        if(uuid != null) {
                            if(ProxyServer.getInstance().getPlayer(uuid) != null)
                                pp = ProxyServer.getInstance().getPlayer(uuid);
                            AMCBungee.getInstance().getSQL().removeWhiteList("", uuid);
                            AMCBungee.getWhitelist().remove(uuid);
                        }
                        if(pp == null && ProxyServer.getInstance().getPlayer(_a[i]) != null)
                            pp = ProxyServer.getInstance().getPlayer(_a[i]);
                        AMCBungee.getInstance().getSQL().removeWhiteList(_a[i], null);
                        AMCBungee.getWhitelist().remove(_a[i].toLowerCase());
                        _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.command.remove.successful", "Removed %name% successful from Whitelist.", new String[] {"%name%"}, new String[] {_a[i]}));
                        if(pp != null)
                            pp.disconnect(Language.getMessage(AMCBungee.getInstance(), pp.getUniqueId(), "whitelist.command.kicked", "You are no longer Whitelisted on this Server."));
                    }
                    return;
                case "list":
                    int page = 1;
                    if(_a.length == 2) {
                        if(Mixes.isNumeric(_a[1]))
                            page = Integer.parseInt(_a[1]);
                    }
                    HashMap<String, String> list = AMCBungee.getInstance().getSQL().listWhiteList((page*10-10));
                    if(list.isEmpty()) {
                        _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.command.list.empty", "The whitelist page %page% is currently empty.", new String[] {"%page%"}, new String[] {String.valueOf(page)}));
                        return;
                    }
                    
                    int i = (page*10)-9;
                    for(Map.Entry<String, String> me: list.entrySet()) {
                        _s.sendMessage(Language.convertString("" + ChatColor.BLUE + i + ". " + ChatColor.GOLD + me.getKey() + ChatColor.WHITE + " (" + ChatColor.YELLOW + me.getValue() + ChatColor.WHITE + ")"));
                    }
                    _s.sendMessage(Language.getMessage(AMCBungee.getInstance(), suuid, "whitelist.command.list.pages", "&eFor more Type /whitelist list (page number)"));          }
        }
    }
    
    private UUID getUUIDFromMojang(String name) {
        try {
           String msg = "";
           URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name + "?at=" + (int)(System.currentTimeMillis()/1000));
           Scanner s = new Scanner(url.openStream());
           if(s.hasNextLine())
               msg += s.nextLine();
           
           if(msg.isEmpty())
               return null;

           msg = msg.replace("{", "").replace("}", "").replace("\"", "");
           String[] t1 = msg.split(",");
           if(t1.length != 2)
               return null;
           
           String[] t2 = t1[0].split(":");
           if(t2.length != 2)
               return null;
           
           if(!t2[0].equalsIgnoreCase("id") || t2[1].length() != 32)
               return null;
           
           if(!Mixes.isUUID(t2[1]))
               return null;
           
           return Mixes.getUUID(t2[1]);
        } catch(IOException e) {
            AMCBungee.getInstance().getLogger().log(Level.WARNING, "Error on get UUID of " + name + " from Mojang.", e);
            return null;
        }
    }
}
