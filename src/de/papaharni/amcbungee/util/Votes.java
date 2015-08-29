/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee.util;

import de.papaharni.amcbungee.AMCBungee;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Pappi
 */
public class Votes {
    private final HashMap<Integer, Boolean> _myvotes = new HashMap<>();
    private final String _playername;
    private final String _playerIP;
    public Votes(String p, String ip) {
        _playername = p;
        _playerIP = ip;
    }
    
    public List<Integer> getNotVoted() {
        updateVotes();
        List<Integer> list = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> e : _myvotes.entrySet()) {
            if(!e.getValue())
                list.add(e.getKey());
        }
        return list;
    }
    
    public void setVote(int v, boolean bol) {
        _myvotes.put(v, bol);
    }
    
    public boolean getVoteStatus(int v) {
        return _myvotes.containsKey(v)?_myvotes.get(v):false;
    }
    
    public void updateVotes() {
        clearAllVotes();
        AMCBungee.getInstance().getSQL().updatePlayerVotes(this);
    }
    
    public String getName() {
        return _playername;
    }
    
    public String getIP() {
        return _playerIP;
    }
    
    private void clearAllVotes() {
        for(int i = 0; i < AMCBungee.getInstance().getMyConfig()._maxVotes; i++)
            _myvotes.put(i, false);
    }
}
