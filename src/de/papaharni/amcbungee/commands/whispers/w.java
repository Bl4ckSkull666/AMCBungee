package de.papaharni.amcbungee.commands.whispers;

import de.papaharni.amcbungee.AMCBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Pappi
 */
public class w extends Command {
    
    public w() {
        super("w", "amcbungee.msg");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        AMCBungee.sendPrivateMessage(s, args);
    }
}