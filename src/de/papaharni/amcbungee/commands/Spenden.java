/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee.commands;

import de.papaharni.amcbungee.AMCBungee;
import java.util.Random;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Pappi
 */
public class Spenden extends Command {
    
    public Spenden() {
        super("Spenden", "amcbungee.spenden");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer)s;
        if(p == null)
            return;
        
        if(AMCBungee.getInstance().getSQL().canSpendButton(p.getName())) {
            String k = getRandomString();
            if(!AMCBungee.getInstance().getSQL().setSpendingKey(p.getName(), k)) {
                p.sendMessage("Es ist ein Fehler aufgetreten. Bitte versuche es noch einmal.");
                return;
            }
            
            p.sendMessage(ChatColor.GOLD + "~~~~~~~~~~ " + ChatColor.DARK_PURPLE + "SP-Punkte" + ChatColor.GOLD + " ~~~~~~~~~~");
            p.sendMessage(ChatColor.GREEN + "Bitte klicke auf den nachfolgenden Link,");
            p.sendMessage(ChatColor.GREEN + "danach auf das Bild.");
            p.sendMessage(ChatColor.YELLOW + "http://spend.amc-server.de/?sp=" + k);
            p.sendMessage(ChatColor.RED + "Dieser Link ist nur 2 Minuten gültig. Bitte verwende diesen vor Ablauf der Zeit.");
            p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "AdBlocker deaktivieren");
            p.sendMessage(ChatColor.GOLD + "~~~~~~~~~~ " + ChatColor.DARK_PURPLE + "SP-Punkte" + ChatColor.GOLD + " ~~~~~~~~~~");
        }
    }
    
    private String getRandomString() {
        String str = "";
        String[] stra = {"q","w","e","r","t","z","u","i","o","p","a","s","d","f","g","h","j","k","l","y","x","c","v","b","n","m","Q","W","E","R","T","Z","U","I","O","P","A","S","D","F","G","H","J","K","L","Y","X","C","V","B","N","M","0","1","2","3","4","5","6","7","8","9"};
        for(int i = 0; i < 16; i++) {
            str += stra[new Random().nextInt(stra.length)];
        }
        return str;
    }
}
