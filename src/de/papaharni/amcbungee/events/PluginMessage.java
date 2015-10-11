/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.papaharni.amcbungee.AMCBungee;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni
 */
public class PluginMessage implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onPluginMessage(PluginMessageEvent e) {
        if(!e.getTag().equalsIgnoreCase("BungeeCord"))
            return;

        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String sub = in.readUTF();
        if(!sub.equalsIgnoreCase("MyBungee"))
            return;
        
        String cat = in.readUTF();
        UUID uuid = UUID.fromString(in.readUTF());
        if(cat.equalsIgnoreCase("age")) {
            int age = in.readInt();
            AMCBungee.getInstance().getLogger().log(Level.INFO, "Found {0} with UUID {1} and Age of {2}", new Object[] {cat, uuid.toString(), age});
            AMCBungee.getPlayerAge().put(uuid, age);
        } else if(cat.equalsIgnoreCase("gender")) {
            String gender = in.readUTF();
            AMCBungee.getInstance().getLogger().log(Level.INFO, "Found {0} with UUID {1} and Gender {2}", new Object[] {cat, uuid.toString(), gender});
            AMCBungee.getPlayerGender().put(uuid, gender);
        } else if(cat.equalsIgnoreCase("verify")) {
            boolean bol = in.readBoolean();
            if(bol)
                AMCBungee.getPlayerVerification().add(uuid);
            else
                AMCBungee.getPlayerVerification().remove(uuid);
            AMCBungee.getInstance().getLogger().log(Level.INFO, "Found {0} with UUID {1} is Verify? {2}", new Object[] {cat, uuid.toString(), bol});
        } else if(cat.equalsIgnoreCase("chat")) {
            String ignore = in.readUTF();
            String msg = in.readUTF().replace("__", " ");
            for(Map.Entry<String, ServerInfo> me: ProxyServer.getInstance().getServers().entrySet()) {
                if(me.getKey().equalsIgnoreCase(ignore) || me.getValue().getName().equalsIgnoreCase(ignore))
                    continue;
                for(ProxiedPlayer pp: me.getValue().getPlayers())
                    pp.sendMessage(Language.convertString(ChatColor.WHITE + "[" + ChatColor.BLUE + Language.getMsg(AMCBungee.getInstance(), pp.getUniqueId(), "server." + ignore, ignore) + ChatColor.WHITE + "]" + msg));
            }
        }
    }
}
