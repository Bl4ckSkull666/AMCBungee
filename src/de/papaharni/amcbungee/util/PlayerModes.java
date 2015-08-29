/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.file.FileConfiguration;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.file.YamlConfiguration;
import net.md_5.bungee.api.plugin.Plugin;

/**
 *
 * @author PapaHarni
 */
public class PlayerModes {
    private HashMap<String, Boolean> _list = new HashMap<>();
    
    private Plugin _pl;
    public PlayerModes(Plugin pl) throws IOException {
        _pl = pl;
        if(!pl.getDataFolder().exists())
            pl.getDataFolder().mkdir();
            
        File f = new File(pl.getDataFolder(), "playermodes.yml");
            
        try {
            if(!f.exists())
                f.createNewFile();
        } catch(IOException ex) {
            return;
        }
            
        FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
        for(String key: fc.getKeys(false))
            _list.put(key, fc.getBoolean(key));
        pl.getLogger().log(Level.INFO, "Loaded {0} playermodes.", _list.size());
    }
    
    public void save() {
        if(_list.isEmpty())
            return;
        
        if(!_pl.getDataFolder().exists())
            _pl.getDataFolder().mkdir();
            
        File f = new File(_pl.getDataFolder(), "playermodes.yml");
            
        try {
            if(!f.exists())
                f.createNewFile();
        } catch(IOException ex) {
            return;
        }
            
        FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
        for(Map.Entry<String, Boolean> me: _list.entrySet())
            fc.set(me.getKey(), me.getValue());
        
        try {
            fc.save(f);
        } catch (IOException ex) {
            _pl.getLogger().log(Level.INFO, "Cant save playermodes", ex);
        }
    }
    
    public boolean seenInOnlineMode(String uuid) {
        if(!_list.containsKey(uuid))
            return false;
        return _list.get(uuid);
    }
    
    public boolean isSeen(String uuid) {
        return _list.containsKey(uuid);
    }
    
    public void add(String uuid, boolean bol) {
        _list.put(uuid, bol);
    }
}
