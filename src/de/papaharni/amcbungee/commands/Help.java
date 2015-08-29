/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.commands;

import net.craftminecraft.bungee.bungeeyaml.bukkitapi.file.FileConfiguration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author PapaHarni
 */
public class Help extends Command {
    
    public Help() {
        super("help", "");
        loadHelp();
    }
    
    private static FileConfiguration _fc;
    private static long _lastLoad;
    
    @Override
    public void execute(CommandSender s, String[] a) {/*
        if(_lastLoad <= (System.currentTimeMillis()-(1000*60*60))) {
            _fc = LoadAndSave.loadHelp();
            _lastLoad = System.currentTimeMillis();
        }
        
        if(_fc == null) {
            s.sendMessage(Language.getMessage(pd.getLanguage(), "command.help.error", "Error on load help. Please Inform the Team."));
            return;
        }
        
        String language = "default";
        if(_fc.isConfigurationSection(pd.getLanguage()))
            language = pd.getLanguage();
        
        
        String section = "index";
        int page = 1;
        if(a.length > 0) {
            for(String str: a) {
                if(Rnd.isNumeric(str) && Integer.parseInt(str) >= 1) {
                    page = Integer.parseInt(str);
                    continue;
                }
                if(_fc.isConfigurationSection(language + "." + str.toLowerCase()))
                    section = str.toLowerCase();
            }
        }
        
        if(!_fc.isList(language + "." + section + "." + page)) {
            p.sendMessage(Language.getMessage(pd.getLanguage(), "command.help.notexist", "The wished Page can't be found."));
            return true;
        }
        
        p.sendMessage(Language.getMessage(pd.getLanguage(), "command.help.header", "&e=============== &6Help Menu &e==============="));
        for(String msg: _fc.getStringList(language + "." + section + "." + page))
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        
        int max = 1;
        for(String k: _fc.getConfigurationSection(language + "." + section).getKeys(false)) {
            if(Rnd.isNumeric(k))
                max = Integer.parseInt(k);
        }
        
        if(max > 1)
            p.sendMessage(Language.getMessage(pd.getLanguage(), "command.help.page", "Page %cur% of %max% pages.", new String[] {"%cur%", "%max%"}, new String[] {String.valueOf(page), String.valueOf(max)}));
        p.sendMessage(Language.getMessage(pd.getLanguage(), "command.help.footer", "&e=============== &6Help Menu &e==============="));
        */
    }

    public static void loadHelp() {
        /*_fc = LoadAndSave.loadHelp();
        _lastLoad = System.currentTimeMillis();*/
    }
}
