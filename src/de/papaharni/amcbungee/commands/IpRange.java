package de.papaharni.amcbungee.commands;

import de.papaharni.amcbungee.AMCBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Pappi
 */
public class IpRange extends Command {
    private final AMCBungee _plugin;
    
    public IpRange(AMCBungee plugin) {
        super("iprange", "amcbungee.iprange");
        _plugin = plugin;
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer)s;
        
        if(p == null)
            return;
        
        if(!p.hasPermission("amcbungee.iprange")) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Du hast keine Rechte diesen Befehl zu verwenden."));
            return;
        }
        
        if(args.length < 1) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Bitte verwende :"));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4/iprange {del} {Name/(ip1=0-255) (ip2=0-255)/(ganze ip}"));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4/iprange {add} {Name/(ip1=0-255) (ip2=0-255)/(ganze ip} {Nachricht}."));
            return;
        }
        
        switch(args[0].toLowerCase()) {
            case "add":
                addIPRangeBan(p, args);
                break;
            case "del":
                delIPRangeBan(p, args);
                break;
            default:
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Bitte verwende :"));
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4/iprange {del} {Name/(ip1=0-255) (ip2=0-255)/(ganze ip)}"));
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4/iprange {add} {Name/(ip1=0-255) (ip2=0-255)/(ganze ip)} {Nachricht}."));
                break;
        }
    }
    
    private void delIPRangeBan(ProxiedPlayer p, String[] args) {
        if(args.length < 2) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Bitte verwende : /iprange {del} {Name/(ip1=0-255) (ip2=0-255)/(ganze ip)}"));
            return;
        }
        int ip1 = -1;
        int ip2 = -1;
        
        String[] ippaths = args[1].split("\\.");
        if(ippaths.length == 4) {
            if(isNumeric(ippaths[0]) && isNumeric(ippaths[1])) {
                ip1 = Integer.parseInt(ippaths[0]);
                ip2 = Integer.parseInt(ippaths[1]);
            }
        } else if(args.length >= 3) {
            if(isNumeric(args[1]) && isNumeric(args[2])) {
                ip1 = Integer.parseInt(args[1]);
                ip2 = Integer.parseInt(args[2]);
            }
        } else {
            ProxiedPlayer pb = ProxyServer.getInstance().getPlayer(args[1]);
            if(pb != null) {
                String[] ips = pb.getAddress().getAddress().getHostAddress().split("\\.");
                if(ips.length >= 2) {
                    if(isNumeric(ips[0]) && isNumeric(ips[1])) {
                        ip1 = Integer.parseInt(ips[0]);
                        ip2 = Integer.parseInt(ips[1]);
                    }
                }
            } else {
                String ipstr = _plugin.getSQL().getLastIPByPlayer(args[1]);
                if(ipstr != null && !ipstr.isEmpty())  {
                    String[] ips = ipstr.split("\\.");
                    if(ips.length >= 2) {
                        if(isNumeric(ips[0]) && isNumeric(ips[1])) {
                            ip1 = Integer.parseInt(ips[0]);
                            ip2 = Integer.parseInt(ips[1]);
                        }
                    }
                }
            }
        }
        
        if(ip1 <= -1 && ip2 <= -1) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Konnte keine IP Ermitteln oder es fehlt die Nachricht.."));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Bitte verwende /iprande {del} {Name}."));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4oder /iprange {del} {ip1} {ip2}."));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4oder /iprange {del} {ip}."));
            return;
        }
        _plugin.getSQL().delIPRangeBlocked(ip1, ip2);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Alle IP Adressen im Bereich von " + String.valueOf(ip1) + "." + String.valueOf(ip2) + ".*.* wurden wieder freigegeben."));
    }
    
    private void addIPRangeBan(ProxiedPlayer p, String[] args) {
        if(args.length < 3) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Bitte verwende : /iprange {add} {Name/(ip1=0-255) (ip2=0-255)/(ganze ip)} {Nachricht}."));
            return;
        }
        String msg = "";
        int ip1 = -1;
        int ip2 = -1;
        
        String[] ippaths = args[1].split("\\.");
        if(ippaths.length == 4) {
            if(isNumeric(ippaths[0]) && isNumeric(ippaths[1])) {
                ip1 = Integer.parseInt(ippaths[0]);
                ip2 = Integer.parseInt(ippaths[1]);
                msg = getMessageBy(2, args);
            }
        } else if(isNumeric(args[1]) && isNumeric(args[2])) {
            ip1 = Integer.parseInt(args[1]);
            ip2 = Integer.parseInt(args[2]);
            msg = getMessageBy(3, args);
        } else {
            ProxiedPlayer pb = ProxyServer.getInstance().getPlayer(args[1]);
            if(pb != null) {
                String[] ips = pb.getAddress().getAddress().getHostAddress().split("\\.");
                if(ips.length >= 2) {
                    if(isNumeric(ips[0]) && isNumeric(ips[1])) {
                        ip1 = Integer.parseInt(ips[0]);
                        ip2 = Integer.parseInt(ips[1]);
                    }
                }
            } else {
                String ipstr = _plugin.getSQL().getLastIPByPlayer(args[1]);
                if(ipstr != null && !ipstr.isEmpty())  {
                    String[] ips = ipstr.split("\\.");
                    if(ips.length >= 2) {
                        if(isNumeric(ips[0]) && isNumeric(ips[1])) {
                            ip1 = Integer.parseInt(ips[0]);
                            ip2 = Integer.parseInt(ips[1]);
                        }
                    }
                }
            } 
            msg = getMessageBy(2, args);
        }
        
        if(ip1 <= -1 && ip2 <= -1 && msg.isEmpty()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Konnte keine IP Ermitteln oder es fehlt die Nachricht.."));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Bitte verwende /iprange {add} {Name} {Nachricht}."));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4oder /iprange {add} {ip1} {ip2} {Nachricht}."));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4oder /iprange {add} {ip} {Nachricht}."));
            return;
        }
        _plugin.getSQL().setIPRangeBlocked(ip1, ip2, msg);
        checkOnlinePlayerIps(ip1, ip2, msg);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Alle IP Adressen im Bereich von " + String.valueOf(ip1) + "." + String.valueOf(ip2) + ".*.* wurden gesperrt."));
    }
    
    private ProxiedPlayer getOnlinePlayer(String p) {
        return ProxyServer.getInstance().getPlayer(p);
    }
    
    private String getMessageBy(int a, String[] args) {
        if(args.length <= a)
            return "";
        String msg = args[a];
        for(int i = (a+1);i < args.length; i++) {
            msg += " " + args[i];
        }
        return msg;
    }
    
    private boolean isNumeric(String str) {
        try {
            int a = Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
    
    private void checkOnlinePlayerIps(int ip1, int ip2, String msg) {
        for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
            String strip = p.getPendingConnection().getAddress().getAddress().getHostAddress();
            String[] aip = strip.split("\\.");
            if(aip.length != 4)
                continue;
            if(!isNumeric(aip[0]) || !isNumeric(aip[1]))
                continue;
            int pip1 = Integer.parseInt(aip[0]);
            int pip2 = Integer.parseInt(aip[1]);
            if(pip1 == ip1 && pip2 == ip2)
                p.disconnect(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }
}
