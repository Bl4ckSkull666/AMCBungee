package de.papaharni.amcbungee.commands.whispers;

import de.papaharni.amcbungee.AMCBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Pappi
 */
public class tell extends Command {
    
    public tell() {
        super("tell", "amcbungee.msg");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        AMCBungee.sendPrivateMessage(s, args);
    }
}
