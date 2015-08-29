package de.papaharni.amcbungee.util;

public class ChatLogger {
    private final String _name,_server,_msg;
    private final long _time;
    
    public ChatLogger(String name, String server, String msg) {
        _name = name;
        _server = server;
        _msg = msg;
        _time = System.currentTimeMillis();
    }
    
    public String getName() {
        return _name;
    }
    
    public String getServer() {
        return _server;
    }
    
    public String getMsg() {
        return _msg;
    }
    
    public Long getTime() {
        return _time;
    }
}
