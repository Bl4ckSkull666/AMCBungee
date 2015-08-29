/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.papaharni.amcbungee;

import de.papaharni.amcbungee.util.ChatLogger;
import de.papaharni.amcbungee.util.Votes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author Pappi
 */
public class MySQLData {
    private final myConf _config;
    
    public MySQLData(myConf conf) {
        _config = conf;
        
        if(isMySQLDriver()) {
            Connection con = getConnect();
            if(con == null) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Konnte keine Verbindung zur Server Datenbank aufbauen.");
                AMCBungee.getInstance().getMyConfig()._smysql.put("use", "false");
            } else {
                AMCBungee.getInstance().getMyConfig()._smysql.put("use", setupStructure(con)?"true":"false");
            }
            close(con);
            
            con = getHpConnect();
            if(con == null) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Konnte keine Verbindung zur HP Datenbank aufbauen.");
                AMCBungee.getInstance().getMyConfig()._hmysql.put("use", "false");
            } else {
                AMCBungee.getInstance().getMyConfig()._hmysql.put("use", setupHpStructure(con)?"true":"false");
            }
            close(con);
            
            con = getBoConnect();
            if(con == null) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Konnte keine Verbindung zur Foren Datenbank aufbauen.");
                AMCBungee.getInstance().getMyConfig()._fmysql.put("use", "false");
            } else {
                AMCBungee.getInstance().getMyConfig()._fmysql.put("use", setupBoStructure(con)?"true":"false");
            }
            close(con);
        }
    }
    
    private boolean isMySQLDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
        } catch(ClassNotFoundException t) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Konnte den MySQL Treiber nicht finden! Beende Plugin.", t);
            return false;
        }
    }
    
    private Connection getConnect() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + _config._smysql.get("host") + ":" + _config._smysql.get("port") + "/"
                    + _config._smysql.get("data"), _config._smysql.get("user"), _config._smysql.get("pass"));
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Herstellen der Verbindung zum MySQL Server!", e);
        }
        return con;
    }
    
    private Connection getHpConnect() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + _config._hmysql.get("host") + ":" + _config._hmysql.get("port") + "/"
                    + _config._hmysql.get("data"), _config._hmysql.get("user"), _config._hmysql.get("pass"));
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Herstellen der Verbindung zum MySQL Server!", e);
        }
        return con;
    }
    
    public Connection getBoConnect() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + _config._fmysql.get("host") + ":" + _config._fmysql.get("port") + "/"
                    + _config._fmysql.get("data"), _config._fmysql.get("user"), _config._fmysql.get("pass"));
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Herstellen der Verbindung zum MySQL Server!", e);
        }
        return con;
    }
    
    private boolean setupStructure(Connection con) {
        if(setupStructure1(con) && setupStructure2(con) && setupStructure3(con) && setupStructure4(con) && setupStructure5(con))
            return true;
        
        return false;
    }
    
    private boolean setupHpStructure(Connection con) {
        if(setupHpStructure1(con) && setupHpStructure2(con))
            return true;
        
        return false;
    }
    
    private boolean setupBoStructure(Connection con) {
        return true;
    }
    
    private boolean setupStructure1(Connection con) {
        try {
            PreparedStatement statement;
            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `onlinePlayers` (" 
                + " `name` varchar(32) NOT NULL,"
                + " `server` varchar(64) NOT NULL DEFAULT 'none',"
                + " `status` int(11) NOT NULL,"
                + " `date_time` datetime NOT NULL,"
                + " PRIMARY KEY (`name`)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Erstellen der Datenbank Struktur!", e);
            return false;
        }
        return true;
    }
    
    private boolean setupStructure2(Connection con) {
        try {
            PreparedStatement statement;
            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `playerIPHistory` (" 
                + " `name` varchar(32) NOT NULL,"
                + " `ip` varchar(32) NOT NULL,"
                + " `date_time` datetime NOT NULL,"
                + " PRIMARY KEY (`name`,`ip`)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Erstellen der Datenbank Struktur!", e);
            return false;
        }
        return true;
    }
    
    private boolean setupStructure3(Connection con) {
        try {
            PreparedStatement statement;
            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `ipRangeBan` (" 
                + " `ip1` int(11) NOT NULL,"
                + " `ip2` int(11) NOT NULL,"
                + " `msg` varchar(255) NOT NULL,"
                + " `date_time` datetime NOT NULL,"
                + " PRIMARY KEY (`ip1`,`ip2`)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Erstellen der Datenbank Struktur!", e);
            return false;
        }
        return true;
    }
    
    private boolean setupStructure4(Connection con) {
        try {
            PreparedStatement statement;
            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `chatlogger` (" 
                + " `id` int(11) NOT NULL AUTO_INCREMENT,"
                + " `name` varchar(32) NOT NULL,"
                + " `server` varchar(32) NOT NULL,"
                + " `msg` varchar(255) NOT NULL,"
                + " `date_time` datetime NOT NULL,"
                + " PRIMARY KEY (`id`)"
                + ") ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Erstellen der Datenbank Struktur!", e);
            return false;
        }
        return true;
    }
    
    private boolean setupStructure5(Connection con) {
        try {
            PreparedStatement statement;
            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `uuiddatabase` (" 
                + " `uuid` varchar(40) NOT NULL,"
                + " `name` varchar(20) NOT NULL,"
                + " `oldnames` text NOT NULL,"
                + " `lastCheck` datetime NOT NULL,"
                + " `lastSeen` datetime NOT NULL,"
                + " PRIMARY KEY (`uuid`)"                    
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Erstellen der Datenbank Struktur!", e);
            return false;
        }
        return true;
    }
    
    private boolean setupHpStructure1(Connection con) {
        try {
            PreparedStatement statement;
            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `clicked` (" 
                + " `id` int(11) NOT NULL AUTO_INCREMENT,"
                + "  `clickId` int(11) NOT NULL,"
                + "  `ip` varchar(60) NOT NULL,"
                + "  `loggedin` int(11) NOT NULL,"
                + "  `loginId` int(11) NOT NULL,"
                + "  `loginName` varchar(64) NOT NULL,"
                + "  `clicktime` int(11) NOT NULL,"
                + "  PRIMARY KEY (`id`)"
                + ") ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Erstellen der Datenbank Struktur!", e);
            return false;
        }
        return true;
    }
    
    private boolean setupHpStructure2(Connection con) {
        try {
            PreparedStatement statement;
            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `temporaer` (" 
                + " `id` int(11) NOT NULL AUTO_INCREMENT,"
                + "  `field1` varchar(255) NOT NULL DEFAULT 'none',"
                + "  `field2` varchar(255) NOT NULL DEFAULT 'none',"
                + "  `field3` varchar(255) NOT NULL DEFAULT 'none',"
                + "  `field4` varchar(255) NOT NULL DEFAULT 'none',"
                + "  `field5` varchar(255) NOT NULL DEFAULT 'none',"
                + "  PRIMARY KEY (`id`)"
                + ") ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Erstellen der Datenbank Struktur!", e);
            return false;
        }
        return true;
    }
    
    /*public String entranceCheck(PendingConnection c) {
        String returnMsg = "0:It's happend an error, please try again.";
        
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            if(Boolean.parseBoolean(_config._smysql.get("use"))) {
                con = getConnect();

                //Check is Player Nick banned?
                statement = con.prepareStatement("SELECT `ban_reason`,`ban_expires_on` FROM `BanManager_bans` WHERE `banned` = ?");
                statement.setString(1, c.getName());
                rset = statement.executeQuery();
                if(rset.next()) {
                    if(rset.getInt("ban_expires_on") == 0)
                        returnMsg = "1:You are banned while \"" + rset.getString("ban_reason") + "\". Your ban will never end. Please Contact the team to clarify the case. Thank you. (support@amc-server.de) "; 
                    else 
                        returnMsg = "1:You are banned while \"" + rset.getString("ban_reason") + "\". Your ban will end in " + Mixes.getRestTime((rset.getInt("ban_expires_on")-(System.currentTimeMillis()/1000))) + ". Please Contact the team to clarify the case. Thank you. (support@amc-server.de)";
                    
                    rset.close();
                    statement.close();
                    close(con);
                    return returnMsg;
                }
                rset.close();
                statement.close();

                //Check is player ip banned?
                int[] ips = Mixes.getSplitIp(c.getAddress().getAddress().getHostAddress());
                if(ips.length >= 2 && ips[0] > -1 && ips[1] > -1) {
                    statement = con.prepareStatement("SELECT `msg` FROM `ipRangeBan` WHERE `ip1` = ? AND `ip2` = ? LIMIT 0,1");
                    statement.setInt(1, ips[0]);
                    statement.setInt(2, ips[1]);
                    rset = statement.executeQuery();
                    if(rset.next()) {
                        returnMsg  = "2:Your IP-Range is banned while \"" + rset.getString("msg") + "\". Please Contact the team to clarify the case. Thank you. (support@amc-server.de)";
                        rset.close();
                        statement.close();
                        close(con);
                        return returnMsg;
                    }
                    rset.close();
                    statement.close();
                }
                close(con);
            }
                
            if(Boolean.parseBoolean(_config._fmysql.get("use"))) {
                //Check have user a board account?
                con = getBoConnect();
                statement = con.prepareStatement("SELECT * FROM `bb1_users` WHERE `username` = ? LIMIT 0,1");
                statement.setString(1, c.getName());
                rset = statement.executeQuery();
                if(rset.next()) {
                    returnMsg  = "3:Board Account exist.";
                    rset.close();
                    statement.close();
                    close(con);
                    return returnMsg;
                }
                close(con);
            }
                
            return "4:No Board Account exist.";
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Auslesen der Daten von Spieler " + c.getName() + "!", e);
        }
        close(con);
        
        return "0:It's happend an error, please try again.";
    }
    */
    
    public void setUUID(ProxiedPlayer p) {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            Connection con = null;
            try {
                boolean isSameNick = false;
                String nicks = "";
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("SELECT `name`,`oldnames` FROM `uuiddatabase` WHERE `uuid` = ? LIMIT 0,1");
                statement.setString(1, p.getUniqueId().toString());
                ResultSet rs = statement.executeQuery();
                if(rs.next()) {
                    isSameNick = p.getName().equalsIgnoreCase(rs.getString("name"));
                    nicks = (rs.getString("oldnames").isEmpty()?"":rs.getString("oldnames") + ", ") + rs.getString("name");
                }
                rs.close();
                statement.close();
                if(isSameNick) {
                    statement = con.prepareStatement("INSERT INTO `uuiddatabase` (`uuid`,`name`,`lastCheck`,`lastSeen`) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE `lastCheck`=?,`lastSeen`=?");
                    statement.setString(1, p.getUniqueId().toString());
                    statement.setString(2, p.getName());
                    statement.setString(3, getDateFormat());
                    statement.setString(4, getDateFormat());
                    statement.setString(5, getDateFormat());
                    statement.setString(6, getDateFormat());
                } else {
                    statement = con.prepareStatement("INSERT INTO `uuiddatabase` (`uuid`,`name`,`oldnames`,`lastCheck`,`lastSeen`) VALUES (?,?,?,?,?) "
                            + "ON DUPLICATE KEY UPDATE `name`=?,`oldnames`=?,`lastCheck`=?,`lastSeen`=?");
                    statement.setString(1, p.getUniqueId().toString());
                    statement.setString(2, p.getName());
                    statement.setString(3, nicks);
                    statement.setString(4, getDateFormat());
                    statement.setString(5, getDateFormat());
                    statement.setString(6, p.getName());
                    statement.setString(7, nicks);
                    statement.setString(8, getDateFormat());
                    statement.setString(9, getDateFormat());
                }
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim setzen des Servers von Spieler " + p + "!", e);
            }
            close(con);
        }
    }
    
    public void setServerByPlayer(String p, String s) {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            Connection con = null;

            try {
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `onlinePlayers` (`name`,`server`,`status`,`date_time`,`time_total`,`uuid`) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `server`=?");
                statement.setString(1, p);
                statement.setString(2, s);
                statement.setInt(3, 1);
                statement.setString(4, getDateFormat());
                statement.setInt(5, 0);
                statement.setString(6, ProxyServer.getInstance().getPlayer(p).getUniqueId().toString());
                statement.setString(7, s);
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim setzen des Servers von Spieler " + p + "!", e);
            }
            close(con);
        }
    }
    
    public void resetPlayerOnline() {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            Connection con = null;

            try {
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("UPDATE `onlinePlayers` SET `status` = 0");
                statement.execute();
                statement.close();
                
                statement = con.prepareStatement("UPDATE `onlinePlayers` SET `status` = 1 WHERE `name` = ?");
                for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
                    statement.setString(1, p.getName());
                    statement.execute();
                }
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim updaten des Online status", e);
            }
            close(con);
        }
    }
    
    public void setOnlineStatus(String pName, String pUUID, int status, long ontime) {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            Connection con = null;

            try {
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `onlinePlayers` (`name`,`server`,`status`,`date_time`,`time_total`,`uuid`,`premium`) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `status`=?,`date_time`=?,`time_total`=time_total+?");
                statement.setString(1, pName);
                statement.setString(2, "lobby");
                statement.setInt(3, status);
                statement.setString(4, getDateFormat());
                statement.setLong(5, ontime);
                statement.setString(6, pUUID);
                statement.setBoolean(7, true);
                statement.setInt(8, status);
                statement.setString(9, getDateFormat());
                statement.setLong(10, ontime);
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim setzen des Status von Spieler " + pName + "!", e);
            }
            close(con);
        }
    }
    
    public void setUserIp(String p, String ip) {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            Connection con = null;
            
            try {
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `playerIPHistory` (`name`,`ip`,`date_time`) VALUES (?,?,?) ON DUPLICATE KEY UPDATE `date_time`=?");
                statement.setString(1, p);
                statement.setString(2, ip);
                statement.setString(3, getDateFormat());
                statement.setString(4, getDateFormat());
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Speichern der IP von Spieler " + p + " mit der IP " + ip + "!", e);
            }
            close(con);
        }
    }
    
    public String isIPRangeBlocked(int ip1, int ip2) {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            String msg = "";
            Connection con = null;
            
            try {
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("SELECT `msg` FROM `ipRangeBan` WHERE `ip1` = ? AND `ip2` = ? LIMIT 0,1");
                statement.setInt(1, ip1);
                statement.setInt(2, ip2);
                ResultSet rset = statement.executeQuery();
                if(rset.next()) {
                    msg = rset.getString("msg");
                }
                rset.close();
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Auslesen des IP Ranges!", e);
            }
            close(con);
            return msg;
        }
        return "";
    }
    
    public void setIPRangeBlocked(int ip1, int ip2, String msg) {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            Connection con = null;
            
            try {
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `ipRangeBan` (`ip1`,`ip2`,`msg`,`date_time`) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE `msg`=?,`date_time`=?");
                statement.setInt(1, ip1);
                statement.setInt(2, ip2);
                statement.setString(3, msg);
                statement.setString(4, getDateFormat());
                statement.setString(5, msg);
                statement.setString(6, getDateFormat());
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Einlesen des IP Ranges!", e);
            }
            close(con);
        }
    }
    
    public void delIPRangeBlocked(int ip1, int ip2) {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            Connection con = null;
            
            try {
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("DELETE FROM `ipRangeBan` WHERE `ip1` = ? AND`ip2` = ?");
                statement.setInt(1, ip1);
                statement.setInt(2, ip2);
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Löschen des IP Range Bannes!", e);
            }
            close(con);
        }
    }
    
    public String getLastIPByPlayer(String p) {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            String ip = "";
            Connection con = null;
            
            try {
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("SELECT `ip` FROM `playerIPHistory` WHERE `name` = ? ORDER BY `date_time` DESC LIMIT 0,1");
                statement.setString(1, p);
                ResultSet rset = statement.executeQuery();
                if(rset.next()) {
                    ip = rset.getString("ip");
                }
                rset.close();
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Auslesen des IP Ranges!", e);
            }
            close(con);
            return ip;
        }
        return "";
    }
    
    public int getPlayerPoint(String name, String type) {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            int p = -1;
            Connection con = null;
            
            try {
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("SELECT `" + type.toLowerCase() + "` FROM `user_points` WHERE `uuid` = (SELECT `uuid` FROM `uuiddatabase` WHERE `name` = ?) LIMIT 0,1");
                statement.setString(1, name);
                ResultSet rset = statement.executeQuery();
                if(rset.next())
                    p = rset.getInt(type.toLowerCase());
                else
                    p = 0;
                rset.close();
                statement.close();
            } catch(SQLException e) {
                p = -2;
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Auslesen der Points!", e);
            } finally {
                close(con);
                return p;
            }
        }
        return -3;
    }
    
    public boolean setPlayerPoints(String name, int p, String type, boolean positive) {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            Connection con = null;
            
            try {
                con = getConnect();
                PreparedStatement statement;
                if(positive)
                    statement = con.prepareStatement("INSERT INTO `user_points` (`userId`,`username`,`vpoints`,`sppoints`) VALUES (0,?,?,?) ON DUPLICATE KEY UPDATE `vpoints`=vpoints+?, `sppoints`=sppoints+?");
                else
                    statement = con.prepareStatement("INSERT INTO `user_points` (`userId`,`username`,`vpoints`,`sppoints`) VALUES (0,?,?,?) ON DUPLICATE KEY UPDATE `vpoints`=vpoints-?, `sppoints`=sppoints-?");
                statement.setString(1, name);
                statement.setInt(2, (type.equalsIgnoreCase("vpoints")?p:0));
                statement.setInt(3, (type.equalsIgnoreCase("sppoints")?p:0));
                statement.setInt(4, (type.equalsIgnoreCase("vpoints")?p:0));
                statement.setInt(5, (type.equalsIgnoreCase("sppoints")?p:0));
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                close(con);
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim setzen der Points!", e);
                return false;
            } finally {
                close(con);
                return true;
            }
        }
        return false;
    }
    
    public void updatePlayerVotes(Votes v) {
        if(Boolean.parseBoolean(_config._hmysql.get("use"))) {
            Connection con = null;
            try {
                con = getHpConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("SELECT `clickId` FROM `clicked` WHERE `name` = ? AND `clickId` < '50' AND `clicktime` >= ? OR `ip` = ? AND `clickId` < '50' AND `clicktime` >= ?");
                statement.setString(1, v.getName());
                statement.setLong(2, ((System.currentTimeMillis()/1000)-(60*60*6)));
                statement.setString(3, v.getIP());
                statement.setLong(4, ((System.currentTimeMillis()/1000)-(60*60*6)));
                ResultSet rset = statement.executeQuery();
                while(rset.next()) {
                    v.setVote(rset.getInt("clickId"), true);
                }
                rset.close();
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Auslesen der Votes!", e);
            }
            close(con);
        }
    }
    
    public boolean addPlayerVotes(int voteId, String str, String p) {
        if(Boolean.parseBoolean(_config._hmysql.get("use"))) {
            boolean success = true;
            Connection con = null;
            
            try {
                con = getHpConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `temporaer` (`field1`,`field2`,`field3`,`field4`,`field5`) VALUES (?,?,?,?,?)");
                statement.setString(1, String.valueOf(voteId));
                statement.setString(2, str);
                statement.setString(3, String.valueOf((System.currentTimeMillis()/1000)));
                statement.setString(4, p);
                statement.setString(5, "vote");
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                success = false;
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Einlesen der Votes!", e);
            }
            close(con);
            return success;
        }
        return false;
    }
    
    public boolean canSpendButton(String p) {
        if(Boolean.parseBoolean(_config._hmysql.get("use"))) {
            boolean can = true;
            Connection con = null;
            
            try {
                con = getHpConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("SELECT `id` FROM `clicked` WHERE `name` = ? AND `clickId` = '80' AND `clicktime` >= ?");
                statement.setString(1, p);
                statement.setLong(2, ((System.currentTimeMillis()/1000)-(60*60*6)));
                ResultSet rset;
                rset = statement.executeQuery();
                if(rset.next()) {
                    can = false;
                }
                rset.close();
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Auslesen des Spenden Stand!", e);
            }
            close(con);
            return can;
        }
        return true;
    }
    
    public boolean setSpendingKey(String p, String k) {
        if(Boolean.parseBoolean(_config._hmysql.get("use"))) {
            boolean success = true;
            Connection con = null;
            
            try {
                con = getHpConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `temporaer` (`field1`,`field2`,`field3`,`field5`) VALUES (?,?,?,?)");
                statement.setString(1, p);
                statement.setString(2, k);
                statement.setString(3, Long.toString(System.currentTimeMillis()/1000));
                statement.setString(4, "spenden");
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                success = false;
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Speichern des Keys für Spenden!", e);
            }
            close(con);
            return success;
        }
        return false;
    }
    
    public final void close(Connection con) {
        if(con == null)
            return;
        
        try {
            con.close();
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim beenden einer MySQL Verbindung", e);
        }
    }
    
    private String getDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date d = new Date();
        d.setTime(System.currentTimeMillis());
        return sdf.format(d);
    }
    
    public void saveChatLogger(ChatLogger cl) {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            Connection con = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            Date d = new Date();
            try {
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `chatlogger` (`name`,`server`,`date_time`,`msg`) VALUES (?,?,?,?)");
                statement.setString(1, cl.getName());
                statement.setString(2, cl.getServer());
                d.setTime(cl.getTime());
                statement.setString(3, sdf.format(d));
                statement.setString(4, cl.getMsg());
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim ChatLog eintrag!", e);
            }
            close(con);
        }
    }
    
    public void saveChatLogger(List<ChatLogger> clList) {
        if(Boolean.parseBoolean(_config._smysql.get("use"))) {
            Connection con = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            Date d = new Date();
            try {
                con = getConnect();
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `chatlogger` (`name`,`server`,`date_time`,`msg`) VALUES (?,?,?,?)");
                for(ChatLogger cl: clList) {
                    statement.setString(1, cl.getName());
                    statement.setString(2, cl.getServer());
                    d.setTime(cl.getTime());
                    statement.setString(3, sdf.format(d));
                    statement.setString(4, cl.getMsg());
                    statement.execute();
                }
                statement.close();
            } catch(SQLException e) {
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim ChatLog eintrag!", e);
            }
            close(con);
        }
    }
    

    /*public String isPlayerBannedByUUID(ProxiedPlayer p) {
        Connection con = null;
        String br = "";
        try {
            String uuid = p.getUniqueId().toString();
            con = getConnect();
            PreparedStatement statement;
            ResultSet rset;
            
            if(uuid.equals("") || !uuid.equals(getUUID)) {
                uuid = getUUID;
                statement = con.prepareStatement("INSERT INTO `onlinePlayers` (`name`,`server`,`status`,`date_time`,`time_total`,`uuid`) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE uuid=?");
                statement.setString(1, p);
                statement.setString(2, "");
                statement.setInt(3, 1);
                statement.setString(4, getDateFormat());
                statement.setLong(5, 0);
                statement.setString(6, uuid);
                statement.setString(7, uuid);
                statement.execute();
                statement.close();
            }
            statement = con.prepareStatement("SELECT (SELECT `ban_reason` FROM `BanManager_bans` WHERE `banned` = o.name) AS `banReason` FROM `onlinePlayers` AS o WHERE o.uuid = ? AND o.name <> ?");
            statement.setString(1, uuid);
            statement.setString(2, p);
            rset = statement.executeQuery();
            while(rset.next()) {
                try {
                    if(!rset.getString("banReason").equals("")) {
                        br = rset.getString("banReason");
                    }
                } catch(NullPointerException e) { }
            }
            rset.close();
            statement.close(); 
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim UUID Check!", e);
        }
        close(con);
        return br;
    }*/
}
