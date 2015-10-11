/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee;

import java.util.ArrayList;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author PapaHarni
 */
public final class Whitelister {
    private final ArrayList<UUID> _uuidList;
    private final ArrayList<String> _nameList;
    
    public Whitelister() {
        _nameList = new ArrayList<>();
        _uuidList = new ArrayList<>();
        load();
    }
    
    public void load() {
        _nameList.clear();
        _uuidList.clear();
        AMCBungee.getInstance().getSQL().loadWhiteList(this);
    }

    public void add(UUID uuid) {
        _uuidList.add(uuid);
    }
    
    public void add(String name) {
        _nameList.add(name);
    }
    
    public void remove(UUID uuid) {
        _uuidList.remove(uuid);
    }
    
    public void remove(String name) {
        _nameList.remove(name);
    }
    
    public boolean isListed(UUID uuid) {
        if(_uuidList.contains(uuid))
            return true;
        for(UUID id: _uuidList) {
            if(id.toString().equalsIgnoreCase(uuid.toString()) || id.toString().replace("-", "").equalsIgnoreCase(uuid.toString()))
                return true;
        }
        return false;
    }
    
    public boolean isListed(String name) {
        if(_nameList.contains(name))
            return true;
        for(String na: _nameList) {
            if(na.equalsIgnoreCase(name))
                return true;
        }
        return false;
    }
    
    public boolean isListed(ProxiedPlayer pp) {
        if(isListed(pp.getUniqueId()))
            return true;
        else if(isListed(pp.getName().toLowerCase())) {
            _nameList.remove(pp.getName().toLowerCase());
            _uuidList.add(pp.getUniqueId());
            AMCBungee.getInstance().getSQL().replaceWhiteList(pp.getName().toLowerCase(), pp.getUniqueId());
            return true;
        }
        return false;
    }
}
