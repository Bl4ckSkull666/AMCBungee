/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee.util;

import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.Region;
import de.papaharni.amcbungee.AMCBungee;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author Pappi
 */
public final class Mixes {
    public static String getRestTime(long t) {
        int d = (int)Math.floor(t / 86400);
        t = (int)t - (d * 86400);
        int h = (int)Math.floor(t / 3600);
        t = (int)t - (h * 3600);
        int m = (int)Math.floor(t / 60);
        t = (int)t - (m * 60);
        return ((d > 0)?d + " Day" + ((d != 1)?"s, ":", "):"") + ((h > 0)?h + " Hour" + ((h != 1)?"s, ":", "):"") + ((m > 0)?m + " Minute" + ((m != 1)?"s, ":", "):"") + ((t > 0)?t + " Second" + ((t != 1)?"s":""):"");
    }
    
    public static int[] getSplitIp(String ip) {
        String[] ippath = ip.split("\\.");
        if(ippath.length < 4)
            return new int[] {-1, -1};
        
        if(!isNumeric(ippath[0]) || !isNumeric(ippath[1]))
            return new int[] {-1, -1};
        
        int ip1 = Integer.parseInt(ippath[0]);
        int ip2 = Integer.parseInt(ippath[1]);
        
        if(ip1 < 0 || ip2 < 0 || ip1 > 255 || ip2 > 255)
            return new int[] {-1,-1};
        
        return new int[] {ip1, ip2};
    }
    
    public static boolean isNumeric(String str) {
        try {
            int n = Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
            
    public static void setDisplayColor(ProxiedPlayer p) {
        String dspname = "";
        if(p.hasPermission("amcbungee.nickcolor.0"))
            dspname = "&0";
        if(p.hasPermission("amcbungee.nickcolor.1"))
            dspname = "&1";
        if(p.hasPermission("amcbungee.nickcolor.2"))
            dspname = "&2";
        if(p.hasPermission("amcbungee.nickcolor.3"))
            dspname = "&3";
        if(p.hasPermission("amcbungee.nickcolor.4"))
            dspname = "&4";
        if(p.hasPermission("amcbungee.nickcolor.5"))
            dspname = "&5";
        if(p.hasPermission("amcbungee.nickcolor.6"))
            dspname = "&6";
        if(p.hasPermission("amcbungee.nickcolor.7"))
            dspname = "&7";
        if(p.hasPermission("amcbungee.nickcolor.8"))
            dspname = "&8";
        if(p.hasPermission("amcbungee.nickcolor.9"))
            dspname = "&9";
        if(p.hasPermission("amcbungee.nickcolor.a"))
            dspname = "&a";
        if(p.hasPermission("amcbungee.nickcolor.b"))
            dspname = "&b";
        if(p.hasPermission("amcbungee.nickcolor.c"))
            dspname = "&c";
        if(p.hasPermission("amcbungee.nickcolor.d"))
            dspname = "&d";
        if(p.hasPermission("amcbungee.nickcolor.e"))
            dspname = "&e";
        if(p.hasPermission("amcbungee.nickcolor.f"))
            dspname = "&f";
        if(p.hasPermission("amcbungee.nickcolor.k"))
            dspname = "&k";
        if(p.hasPermission("amcbungee.nickcolor.l"))
            dspname = "&l";
        if(p.hasPermission("amcbungee.nickcolor.m"))
            dspname = "&m";
        if(p.hasPermission("amcbungee.nickcolor.n"))
            dspname = "&n";
        if(p.hasPermission("amcbungee.nickcolor.o"))
            dspname = "&o";
        
        if(AMCBungee.getInstance().getGuestAccessList().contains(p.getName())) {
            dspname = "&e";
            AMCBungee.getInstance().getGuestAccessList().remove(p.getName());
        }
        
        if(dspname.isEmpty())
            dspname = "&2";
        
        dspname = ChatColor.translateAlternateColorCodes('&', (dspname + p.getName()));
        
        if(dspname.length() > 16)
            dspname = dspname.substring(0, 16);
        
        p.setDisplayName(dspname);
    }
    
    public static String getLangCode(InetAddress ip) {
        File f = new File("GeoIp.dat");
        if(!f.exists())
            return "";
        try {
            LookupService cl = new LookupService("GeoIp.dat",LookupService.GEOIP_MEMORY_CACHE);
            String code = cl.getCountry(ip).getCode();
            cl.close();
            return code;
        } catch(IOException e) {
            
        }
        return "";
    }
    
    public static String getCountryName(InetAddress ip) {
        File f = new File("GeoIp.dat");
        if(!f.exists())
            return "";
        try {
            LookupService cl = new LookupService("GeoIp.dat",LookupService.GEOIP_MEMORY_CACHE);
            String code = cl.getCountry(ip).getName();
            cl.close();
            return code;
        } catch(IOException e) {
            
        }
        return "";
    }
    
    public static String getRegionName(InetAddress ip) {
        File f = new File("GeoIp.dat");
        if(!f.exists())
            return "";
        try {
            String code = "";
            LookupService cl = new LookupService("GeoIp.dat",LookupService.GEOIP_MEMORY_CACHE);
            Region r = cl.getRegion(bytesToLong(ip.getAddress()));
            if(r != null)
                code = r.region;
            cl.close();
            return code;
        } catch(IOException e) {
            
        }
        return "";
    }
    
    private static long bytesToLong(byte [] address) {
        long ipnum = 0;
        for (int i = 0; i < 4; ++i) {
            long y = address[i];
            if (y < 0) {
                y+= 256;
            }
            ipnum += y << ((3-i)*8);
        }
        return ipnum;
    }
    
    public static String getLanguageForLangCode(String langcode) {
        return AMCBungee.getInstance().getConfig().getString("combi-languages." + langcode.toLowerCase(), langcode.toLowerCase());
    }
    
    public static boolean isGivenServerIn(String confPath, String servername) {
        if(AMCBungee.getInstance().getConfig().isConfigurationSection(confPath + "." + servername.toLowerCase()))
            return true;
        
        if(AMCBungee.getInstance().getConfig().isList(confPath + "." + servername.toLowerCase()))
            return true;
        
        if(AMCBungee.getInstance().getConfig().isList(confPath)) {
            for(String str: AMCBungee.getInstance().getConfig().getStringList(confPath)) {
                if(str.equalsIgnoreCase(servername))
                    return true;
            }
        }
        
        if(AMCBungee.getInstance().getConfig().isString(confPath)) {
            if(AMCBungee.getInstance().getConfig().getString(confPath).equalsIgnoreCase(servername))
                return true;
        }
        return false;
    }
}
