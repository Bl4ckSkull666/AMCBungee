/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Pappi
 */
public class multis {
    private final Collection<String> _players = new ArrayList<>();
    public multis(String name, String name2) {
        _players.add(name);
        _players.add(name2);
    }
    
    public void addName(String name) {
        _players.add(name);
    }
    
    public boolean isName(String name) {
        for(String n: _players) {
            if(n.equalsIgnoreCase(name))
                return true;
        }
        return false;
    }
    
    public boolean checkNamesIn(String name1, String name2) {
        for(String n1: _players) {
            if(n1.equalsIgnoreCase(name1)) {
                for(String n2: _players) {
                    if(n2.equalsIgnoreCase(name2))
                        return true;
                }
            }
        }
        return false;
    } 
}
