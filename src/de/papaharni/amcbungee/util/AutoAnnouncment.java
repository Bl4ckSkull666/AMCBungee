/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee.util;

import java.util.ArrayList;

/**
 *
 * @author Pappi
 */
public class AutoAnnouncment {
    private long _minInterval = -1;
    private long _maxInterval = -1;
    private boolean _randomInterval = false;
    private boolean _randomMessage = false;
    private ArrayList<String> _messages = new ArrayList<>();
    private int _lastMessage = 0;
    
    public AutoAnnouncment() {
    
    }
    
    public void setInterval(long min, long max) {
        _minInterval = min;
        _maxInterval = max;
    }
    
    public long getInterval() {
        if(_minInterval >= 0L && _maxInterval >= 0L && _randomInterval) {
            return Rnd.get(_minInterval, _maxInterval);
        } else if(_maxInterval > 0L) {
            return _maxInterval;
        } else if(_minInterval > 0L) {
            return _minInterval;
        }
        return -1;
    }
    
    public String getMessage() {
        if(_messages.size() <= 0)
            return "";
        
        if(_randomMessage)
            return _messages.get(Rnd.get(0, _messages.size()));
        
        _lastMessage++;
        if(_lastMessage == _messages.size())
            _lastMessage = 0;
        return _messages.get(_lastMessage);
    }
    
    public void setRandomMessage(boolean bol) {
        _randomMessage = bol;
    }
    
    public void setMessage(String str) {
        _messages.add(str);
    }
    
    public void delMessage(int id) {
        _messages.remove((id-1));
    }
}
