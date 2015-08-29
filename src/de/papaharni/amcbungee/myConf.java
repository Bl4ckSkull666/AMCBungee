/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.Configuration;
import net.md_5.bungee.api.ProxyServer;

/**
 *
 * @author Pappi
 */
public class myConf {
    
    public final boolean _debug;
    
    //MySQL - Server
    public final HashMap<String, String> _smysql;
    //MySQL - Forum
    public final HashMap<String, String> _fmysql;
    //MySQL - Homepage
    public final HashMap<String, String> _hmysql;
    //Use MySQL Tables
    public final HashMap<String, String> _tmysql;
    
    public final HashMap<String, String> _sNames;
    
    public final int _maxVotes;
    
    public final List<String> _cmd_hide;
    public final List<String> _cmd_info;
    public final int _cmd_info_kick_pc;
        
    public final long _chat_wait;
    public final int _chat_warn;
    public final long _chat_mute;
    public final boolean _useWebReg;
    public final List<String> _chat_wordfilter;
    public final List<String> _chat_greeting;
    
    public final boolean _useGuestAccess;
    public final long _useGuestTime;
    
    public myConf(Configuration config) {
        ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Lade Konfiguration");
        _debug = config.getBoolean("debug", false);
        
        _smysql = getHashMapStr(config, "mysql.server");
        _fmysql = getHashMapStr(config, "mysql.forum");
        _hmysql = getHashMapStr(config, "mysql.homepage");
        _tmysql = getHashMapStr(config, "mysql.tables");
        
        _sNames = getHashMapStr(config, "servernames");
        
        _maxVotes = getConfigInt("maxVotes");
        
        _cmd_hide = config.getStringList("commands.hide");
        _cmd_info = config.getStringList("commands.info");
        _cmd_info_kick_pc = config.getInt("commands.infokickpc", 50);
        
        _chat_wait = config.getLong("chat.waiting", 30000L);
        _chat_warn = config.getInt("chat.warning", 5);
        _chat_mute = config.getLong("chat.mutetime", 600000L);
        
        _chat_wordfilter = config.getStringList("chat.wordfilter");
        
        _chat_greeting = config.getStringList("chat.greeting");
        
        _useWebReg = config.getBoolean("use.WebRegistration", false);
        
        _useGuestAccess = config.getBoolean("GuestAccess.use", false);
        _useGuestTime = config.getLong("GuestAccess.time", 1800000L);
    }
    
    private HashMap<String, String> getHashMapStr(Configuration config, String path) {
        HashMap<String, String> hm = new HashMap<>();
        for(String key : config.getConfigurationSection(path).getKeys(false)) {
            hm.put(key.toLowerCase(), config.getString(path + "." + key));
        }
        return hm;
    }
    
    private String getConfigString(String var) {
        try {
           String msg = "";
           URL url = new URL("http://www.amc-server.de/functions/getBungeeConfig.php?id=" + var);
           Scanner s = new Scanner(url.openStream());
           if(s.hasNextLine()) {
               msg += s.nextLine();
           }
           return msg;
        } catch(IOException e) {
            AMCBungee.getInstance().getLogger().log(Level.WARNING, "Konnte " + var + " Status nicht abrufen", e);
            return "";
        }
    }
    
    private int getConfigInt(String var) {
        try {
            int i = Integer.parseInt(getConfigString(var));
            return i;
        } catch(NumberFormatException e) {
            return -1;
        }
    }
}
