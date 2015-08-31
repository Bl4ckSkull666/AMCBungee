/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.papaharni.amcbungee.AMCBungee;
import java.util.UUID;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni
 */
public class PluginMessage implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPluginMessage(PluginMessageEvent e) {
        if(!e.getTag().equalsIgnoreCase("MyBungee"))
            return;

        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String sub = in.readUTF();
        UUID uuid = UUID.fromString(in.readUTF());
        if(sub.equalsIgnoreCase("age")) {
            int age = in.readInt();
            AMCBungee.getPlayerAge().put(uuid, age);
        } else if(sub.equalsIgnoreCase("gender")) {
            String gender = in.readUTF();
            AMCBungee.getPlayerGender().put(uuid, gender);
        } else if(sub.equalsIgnoreCase("verify")) {
            boolean bol = in.readBoolean();
            if(bol)
                AMCBungee.getPlayerVerification().add(uuid);
            else
                AMCBungee.getPlayerVerification().remove(uuid);
        }
    }
}
