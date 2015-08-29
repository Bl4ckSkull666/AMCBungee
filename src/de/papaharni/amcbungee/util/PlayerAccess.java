package de.papaharni.amcbungee.util;

public class PlayerAccess {
    private final String _name;
    private long _time = 0;
    private boolean _allowChat = false;
    
    public PlayerAccess(String name) {
        _name = name;
    }
    
    public void setTime() {
        _time = System.currentTimeMillis();
    }
    
    public long getTime() {
        return _time;
    }
    
    public void setAllowChat() {
        _allowChat = true;
    }
    
    public void setDenyChat() {
        _allowChat = false;
    }
    
    public boolean getChat() {
        return _allowChat;
    }
}
