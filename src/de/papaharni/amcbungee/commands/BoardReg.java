package de.papaharni.amcbungee.commands;

import de.papaharni.amcbungee.AMCBungee;
import de.papaharni.amcbungee.myConf;
import de.papaharni.amcbungee.util.Rnd;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.*;
import javax.mail.internet.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Pappi
 */
public class BoardReg extends Command {
    
    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
    
    private final String _mail_pattern = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$^";
    
    public BoardReg() {
        super("register", "amcbungee.boardregister");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer)s;
        if(p == null)
            return;
        
        if(!Boolean.parseBoolean(AMCBungee.getInstance().getMyConfig()._fmysql.get("use"))) {
            p.sendMessages(new String[]{"§2The Registration is not avalible yet. Please try again later.","§aDie Registration ist derzeit nicht verfuegbar. Bitte versuch es spaeter noch einmal."});
            return;
        }
        
        if(args.length < 2) {
            p.sendMessages(new String[]{"§2Use /register {your@email.com} {your@email.com} - You will be get your password in a e-Mail.","§aVerwende /register {deine@email.de} {deine@email.de} - Du bekommst dein Passwort in einer E-Mail."});
            return;
        }
        
        if(!validateMail(args[0])) {
            p.sendMessages(new String[]{"§2Your first mail is not a correctly mail.","§aDeine Erste E-Mail ist keine wahre E-Mail."});
            return;
        }
        
        if(!validateMail(args[1])) {
            p.sendMessages(new String[]{"§2Your second mail is not a correctly mail.","§aDeine Zweite E-Mail ist keine wahre E-Mail."});
            return;
        }
        
        if(!args[0].contains(args[1])) {
            p.sendMessages(new String[]{"§4Your E-Mail are not the same.","§cDeine E-Mail ist nicht die gleiche."});
            return;
        }
        
        ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new createBoardAccount(p, args[0]));
    }
    
    private boolean validateMail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
    
    private String getRandomString(int len) {
        String AB = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(len);
        for(int i = 0;i < len;i++) 
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        
        return sb.toString();
    }
    
    private String createsalt(int strlength,int endlength) {
	String stringer = "";
	String pool = "123456789";
        
	for(int i=1;i<=strlength;i++) {
            int r = Rnd.get(0, pool.length()-1);
            stringer += pool.substring(r, r+1);
	}
	return stringer.substring(0,endlength);
    }

    private String createMyPassword(String passwd) {
        String salt = createsalt(40,16);
        return "$SHA$" + salt + "$" + sha256(sha256(passwd) + salt);
    }
    
    private String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(UnsupportedEncodingException | NoSuchAlgorithmException e){
            AMCBungee.getInstance().getLogger().log(Level.WARNING, "Konnte Sha1 nicht erstellen", e);
            return "";
        }
    }
    
    private boolean isMailAllowed(String domain) {
        try {
           String msg = "";
           URL url = new URL("http://www.mogelmail.de/q/" + domain);
           Scanner s = new Scanner(url.openStream());
           while(s.hasNextLine()) {
               msg += s.nextLine();
           }
           
           try {
               int i = Integer.parseInt(msg);
               if(i != 1)
                   return true;
               else
                   return false;
           } catch(NumberFormatException ex) {
               return false;
           }
        } catch(IOException e) {
            AMCBungee.getInstance().getLogger().log(Level.WARNING, "Konnte " + domain + " nicht Abfrage auf mogelmail.de", e);
            return false;
        }
    }
    
    private String getSha1(String password)
    {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Convertieren des Passwortes zu SHA-1.!", e);
            return "";
        }
        return sha1;
    }

    private String byteToHex(final byte[] hash)
    {
        String result;
        try (Formatter formatter = new Formatter()) {
            for (byte b : hash)
            {
                formatter.format("%02x", b);
            }   result = formatter.toString();
        }
        return result;
    }
    
    public class createBoardAccount implements Runnable {
        private final ProxiedPlayer _p;
        private final String _mail;
        
        public createBoardAccount(ProxiedPlayer p, String mail) {
           _p = p;
           _mail = mail;
        }
        
        @Override
        public void run() {
            String[] dom = _mail.split("@");
            if(dom.length < 2 || dom.length > 2) {
                _p.sendMessages(new String[]{"§4Your e-Mail is not correct.","§cDeine E-Mail ist nicht Korrekt."});
                return;
            }
            
            if(!isMailAllowed(dom[1])) {
                _p.sendMessages(new String[]{"§4Your e-Mail address is infected. Please use a other e-Mail address.","§cDeine E-Mail Adresse ist infiziert. Bitte verwende eine andere E-Mail Adresse."});
                return;
            }
            
            
            int userId = -1;
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch(ClassNotFoundException t) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0021)","§cEin unerwarteter Fehler ist aufgetreten. (0021)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Konnte MySQL Treiber nicht finden.!", t);
                return;
            }
            
            Connection con = AMCBungee.getInstance().getSQL().getBoConnect();
            try {
                myConf conf = AMCBungee.getInstance().getMyConfig();
                con = DriverManager.getConnection("jdbc:mysql://" + conf._fmysql.get("host") + ":" + conf._fmysql.get("port") + "/"
                        + conf._fmysql.get("data"), conf._fmysql.get("user"), conf._fmysql.get("pass"));
            } catch(SQLException e) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0022)","§cEin unerwarteter Fehler ist aufgetreten. (0022)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Herstellen der Verbindung zum MySQL Server!", e);
                return;
            }
            
            //Check is player not registed yet.
            try {
                PreparedStatement statement;
                statement = con.prepareStatement("SELECT `email` FROM `bb1_users` WHERE `username` = ?");
                statement.setString(1, _p.getName());
                ResultSet rset = statement.executeQuery();
                if(rset.next()) {
                    _p.sendMessages(new String[]{"§4You have already a Board Account with the mail " + rset.getString("email") + " registed.","§cDu hast bereits einen Forum Account mit der E-Mail " + rset.getString("email") + " registriert."});
                    rset.close();
                    statement.close();
                    return;
                }
                rset.close();
                statement.close();
            } catch(SQLException e) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0023)","§cEin unerwarteter Fehler ist aufgetreten. (0023)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Konnte nicht ermitteln ob Spieler schon existiert!", e);
                return;
            }
            
            //Check is e-mail already in use.
            try {
                PreparedStatement statement;
                statement = con.prepareStatement("SELECT `username` FROM `bb1_users` WHERE `email` = ?");
                statement.setString(1, _mail);
                ResultSet rset = statement.executeQuery();
                if(rset.next()) {
                    _p.sendMessages(new String[]{"§4Your E-Mail is already in use by " + rset.getString("username") + ".","§cDeine E-Mail wird bereits verwendet von " + rset.getString("username") + "."});
                    rset.close();
                    statement.close();
                    return;
                }
                rset.close();
                statement.close();
            } catch(SQLException e) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0024)","§cEin unerwarteter Fehler ist aufgetreten. (0024)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Konnte nicht ermitteln ob E-Mail schon existiert!", e);
                return;
            }
            
            //Select the last UserId
            try {
                PreparedStatement statement;
                statement = con.prepareStatement("SELECT `userid` FROM `bb1_users` ORDER BY `userid` DESC LIMIT 0,1");
                ResultSet rset = statement.executeQuery();
                if(rset.next()) {
                    userId = rset.getInt("userid");
                } else {
                    _p.sendMessages(new String[]{"§4An unexpected error occurred. (0025)","§cEin unerwarteter Fehler ist aufgetreten. (0025)"});
                    ProxyServer.getInstance().getLogger().log(Level.WARNING, "Konnte die nächste UserId nicht ermitteln!");
                    rset.close();
                    statement.close();
                    return;
                }
                rset.close();
                statement.close();
            } catch(SQLException e) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0026)","§cEin unerwarteter Fehler ist aufgetreten. (0026)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Konnte die nächste UserId nicht ermitteln!", e);
                return;
            }
            
            //Is UserID higher as -1 , set it +1;
            if(userId > -1)
                userId++;
            else {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0027)","§cEin unerwarteter Fehler ist aufgetreten. (0027)"});
                return;
            }
            
            //Get Random Password
            String passwd = getRandomString(12);
            String hash_passwd = createMyPassword(passwd);
            
            //Create Main Board Account
            try {
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `bb1_users` ("
                        + "`userid`,`username`,`password`,`guthaben`,`sparbuch`,"
                        + "`kredit`,`useraktie`,`sha1_password`,`email`,`userposts`,"
                        + "`groupcombinationid`,`rankid`,`title`,`regdate`,`lastvisit`,"
                        + "`lastactivity`,`usertext`,`signature`,`disablesignature`,`icq`,"
                        + "`aim`,`yim`,`msn`,`homepage`,`birthday`,"
                        + "`avatarid`,`gender`,`showemail`,`admincanemail`,`usercanemail`,"
                        + "`invisible`,`usecookies`,`styleid`,`langid`,`activation`,"
                        + "`daysprune`,`timezoneoffset`,`startweek`,`dateformat`,`timeformat`,"
                        + "`emailnotify`,`notificationperpm`,`buddylist`,`ignorelist`,`receivepm`,"
                        + "`emailonpm`,`pmpopup`,`umaxposts`,`showsignatures`,`showavatars`,"
                        + "`showimages`,`ratingcount`,`ratingpoints`,`threadview`,`useuseraccess`,"
                        + "`isgroupleader`,`rankgroupid`,`useronlinegroupid`,`allowsigsmilies`,`allowsightml`,"
                        + "`allowsigbbcode`,`allowsigimages`,`emailonapplication`,`acpmode`,`acppersonalmenu`,"
                        + "`acpmenumarkfirst`,`acpmenuhidelast`,`usewysiwyg`,`pmtotalcount`,`pminboxcount`,"
                        + "`pmnewcount`,`pmunreadcount`,`reg_ipaddress`,`kredit_aktiv`,`kredit_raten`,"
                        + "`kredit_ratenbetrag`,`guthaben_gehalt`,`wgid`,`lbid`,`premium_vip_end`,"
                        + "`payClicks`,`vote_points`,`findus`"
                        + ") VALUES ("
                        + "?,?,?,?,?," //5
                        + "?,?,?,?,?," //10
                        + "?,?,?,?,?," //15
                        + "?,?,?,?,?," //20
                        + "?,?,?,?,?," //25
                        + "?,?,?,?,?," //30
                        + "?,?,?,?,?," //35
                        + "?,?,?,?,?," //40
                        + "?,?,?,?,?," //45
                        + "?,?,?,?,?," //50
                        + "?,?,?,?,?," //55
                        + "?,?,?,?,?," //60
                        + "?,?,?,?,?," //65
                        + "?,?,?,?,?," //70
                        + "?,?,?,?,?," //75
                        + "?,?,?,?,?," //80
                        + "?,?,?)"); //83
                statement.setInt(1, userId); //UserID
                statement.setString(2, _p.getName()); //Username
                statement.setString(3, hash_passwd); //Passwort
                statement.setInt(4, 100); //Guthaben
                statement.setInt(5, 100); //Sparbuch
                statement.setInt(6, 0); //Kredit
                statement.setInt(7, 0); //useraktie
                statement.setString(8, getSha1(passwd));//sha1_password
                statement.setString(9, _mail);//email
                statement.setInt(10, 0); //userposts
                statement.setInt(11, 4); //groupcombinationid
                statement.setInt(12, 4); //rankid
                statement.setString(13, "AMC-Server " + ((_mail.endsWith(".de") || _mail.endsWith(".ch") || _mail.endsWith(".at"))?"Mitglied":"Member")); //title
                statement.setInt(14, (int)(System.currentTimeMillis()/1000)); //regdate
                statement.setInt(15, 0); //lastvisit
                statement.setInt(16, 0); //lastactivity
                statement.setString(17, ""); //usertext
                statement.setString(18, ""); //signature
                statement.setInt(19, 0); //disablesignature
                statement.setString(20, ""); //icq
                statement.setString(21, ""); //aim
                statement.setString(22, ""); //yim
                statement.setString(23, ""); //msn
                statement.setString(24, "http://www.AMC-Server.de"); //homepage
                statement.setString(25, "0000-00-00"); //birthday
                statement.setInt(26, 4); //avatarid
                statement.setInt(27, 0); //gender
                statement.setInt(28, 0); //showemail
                statement.setInt(29, 1); //admincanemail
                statement.setInt(30, 0); //usercanemail
                statement.setInt(31, 0); //invisible
                statement.setInt(32, 1); //usecookies
                statement.setInt(33, 0); //styleid
                statement.setInt(34, 0); //langid
                statement.setInt(35, 1); //activation
                statement.setInt(36, 0); //daysprune
                statement.setInt(37, 1); //timezoneoffset
                statement.setInt(38, 0); //startweek
                statement.setString(39, "d.m.Y"); //dateformat
                statement.setString(40, "H:i"); //timeformat
                statement.setInt(41, 1); //emailnotify
                statement.setInt(42, 0); //notificationperpm
                statement.setString(43, "2 7 8");//buddylist
                statement.setString(44, "");//ignorelist
                statement.setInt(45, 0); //receivepm
                statement.setInt(46, 1); //emailonpm
                statement.setInt(47, 0); //pmpopup
                statement.setInt(48, 0); //umaxposts
                statement.setInt(49, 1); //showsignatures
                statement.setInt(50, 1); //showavatars
                statement.setInt(51, 1); //showimages
                statement.setInt(52, 0); //ratingcount
                statement.setInt(53, 0); //ratingpoints
                statement.setInt(54, 0); //threadview
                statement.setInt(55, 0); //useuseraccess
                statement.setInt(56, 0); //isgroupleader
                statement.setInt(57, 4); //rankgroupid
                statement.setInt(58, 4); //useronlinegroupid
                statement.setInt(59, 1); //allowsigsmilies
                statement.setInt(60, 0); //allowsightml
                statement.setInt(61, 1); //allowsigbbcode
                statement.setInt(62, 1); //allowsigimages
                statement.setInt(63, 0); //emailonapplication
                statement.setInt(64, 0); //acpmode
                statement.setInt(65, 0); //acppersonalmenu
                statement.setInt(66, 0); //acpmenumarkfirst
                statement.setInt(67, 0); //acpmenuhidelast
                statement.setInt(68, 0); //usewysiwyg
                statement.setInt(69, 0); //pmtotalcount
                statement.setInt(70, 0); //pminboxcount
                statement.setInt(71, 0); //pmnewcount
                statement.setInt(72, 0); //pmunreadcount
                statement.setString(73, _p.getPendingConnection().getAddress().getAddress().getHostAddress()); //reg_ipaddress
                statement.setInt(74, 0); //kredit_aktiv
                statement.setInt(75, 0); //kredit_raten
                statement.setInt(76, 0); //kredit_ratenbetrag
                statement.setInt(77, 0); //guthaben_gehalt
                statement.setInt(78, 0); //wgid
                statement.setInt(79, 0); //lbid
                statement.setInt(80, 0); //premium_vip_end
                statement.setInt(81, 0); //payClicks
                statement.setInt(82, 0); //vote_points
                statement.setString(83, ""); //findus
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0028)","§cEin unerwarteter Fehler ist aufgetreten. (0028)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim erstellen des Users in bb1_users!", e);
                onMySQLError(1, con, _p.getName(), userId);
                return;
            }
            
            //Create UserFields
            try {
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `bb1_userfields` (`userid`) VALUES (?)");
                statement.setInt(1, userId);
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0029)","§cEin unerwarteter Fehler ist aufgetreten. (0029)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Erstellen des bb1_userfields Eintrages!", e);
                onMySQLError(2, con, _p.getName(), userId);
                return;
            }
            
            //Create User2Group
            try {
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `bb1_user2groups` (`userid`,`groupid`) VALUES (?,?)");
                statement.setInt(1, userId);
                statement.setInt(2, 4);
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0030)","§cEin unerwarteter Fehler ist aufgetreten. (0030)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Erstellen des bb1_user2groups Eintrag.!", e);
                onMySQLError(3, con, _p.getName(), userId);
                return;
            }
            
            //Update Stats
            try {
                PreparedStatement statement;
                statement = con.prepareStatement("UPDATE `bb1_stats` SET `usercount`=usercount+1, `lastuserid`=?");
                statement.setInt(1, userId);
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0031)","§cEin unerwarteter Fehler ist aufgetreten. (0031)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Updaten der bb1_stats!", e);
                onMySQLError(4, con, _p.getName(), userId);
                return;
            }
            
            //Add Shoutbox entry
            try {
                String wmsg = "Hello and Welcome on AngelZMinecraft <a href=\"profile.php?userid=%userid%\" target=\"_self\">%username%</a>";
                if(_mail.endsWith(".de") || _mail.endsWith(".ch") || _mail.endsWith(".at"))
                    wmsg = "Hallo und Herzlich Willkommen auf AngelZMinecraft <a href=\"profile.php?userid=%userid%\" target=\"_self\">%username%</a>";
                
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO `bb1_aw_shoutbox` (`userid`,`username`,`msg`,`timing`) VALUES (?,?,?,?)");
                statement.setInt(1, 0);
                statement.setString(2, "James");
                statement.setString(3, wmsg.replaceAll("%userid%", String.valueOf(userId)).replaceAll("%username%", _p.getName()));
                statement.setInt(4, (int)(System.currentTimeMillis()/1000));
                statement.execute();
                statement.close();
            } catch(SQLException e) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0032)","§cEin unerwarteter Fehler ist aufgetreten. (0032)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Erstellen des bb1_aw_shoutbox Eintrages!", e);
                onMySQLError(5, con, _p.getName(), userId);
                return;
            }
            
            //Send Mail
            try{
                Properties properties = System.getProperties();
                properties.setProperty("mail.smtp.host", "mail.amc-server.de");
                properties.setProperty("mail.user", "noreply@amc-server.de");
                properties.setProperty("mail.password", "AxCb5pTrN65agX70");
                
                Session session = Session.getDefaultInstance(properties);
                // Create a default MimeMessage object.
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress("noreply@amc-server.de", "AngelZMineCraft Team"));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(_mail, _p.getName()));

                // Set Subject: header field
                message.setSubject(((_mail.endsWith(".de") || _mail.endsWith(".ch") || _mail.endsWith(".at"))?"Registration auf AngelZMineCraft":"Registration on AngelZMineCraft"));

                // Create the message part
                String body;
                //String htmlbody;
                if(_mail.endsWith(".de") || _mail.endsWith(".ch") || _mail.endsWith(".at")) {
                    /* htmlbody = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"de\"  dir=\"ltr\" lang=\"de-DE\">";
                    htmlbody += "<head>";
                    htmlbody += "<style type=\"text/css\">";
                    htmlbody += "a:link{ color:#B18904; text-decoration:none; font-weight: bolder; }";
                    htmlbody += "a:hover{ color:#B18904; text-decoration:none; font-weight: bolder; }";
                    htmlbody += "a:visited{ color:#B18904; text-decoration:none; font-weight: bolder; }";
                    htmlbody += "a:active{ color:#B18904; text-decoration:none; font-weight: bolder; }";
                    htmlbody += "</style>";
                    htmlbody += "</head>";
                    htmlbody += "<body bgcolor=\"#001F21\" text=\"#FFBF00\">";
                    htmlbody += "<center>";
                    htmlbody += "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border: #c0c0c0 solid 2px;padding: 10px;\">";
                    htmlbody += "<tr>";
                    htmlbody += "<td align=\"center\">";
                    htmlbody += "<br />";
                    htmlbody += "<a href=\"http://www.amc-server.de\"><img src=\"http://www.amc-server.de/images/banners/banner.png\" border=\"0\" /></a>";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "<tr>";
                    htmlbody += "<td style=\"width:100%; height:2px; color:#c0c0c0; text-align: center;\">";
                    htmlbody += "---------------------------------------------------------------------------------";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "<tr>";
                    htmlbody += "<td align=\"left\">";
                    htmlbody += "Hallo " + _p.getName() + ",<br />";
                    htmlbody += "vielen Dank für deine Forum Registration auf AngelZMineCraft.<br />";
                    htmlbody += "<br />";
                    htmlbody += "Dein Loginname für unser Forum sowie unsere WebSeite ist " + _p.getName() + "<br />";
                    htmlbody += "Dein vorläufiges Passwort lautet <b>" + passwd + "</b><br />";
                    htmlbody += "<br />";
                    htmlbody += "<span style=\"color: red;\">Bitte ändere dein Passwort bei uns im Forum <a href=\"http://forum.amc-server.de/usercp.php?action=password_change\">HIER</a></span><br />";
                    htmlbody += "Bitte trage innerhalb der nächsten 7 Tage dein Wahres Geburtsdatum und Geschlecht in dein Forum Profil ein.<br />";
                    htmlbody += "<br />";
                    htmlbody += "Mit freundlichem Gruss<br />";
                    htmlbody += "Das AngelZMineCraft Team<br />";
                    htmlbody += "<br />";
                    htmlbody += "<br />";
                    htmlbody += "<br />";
                    htmlbody += "Solltest du Fragen haben so kannst du uns gerne eine E-Mail an support@amc-server.de senden.";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "<tr>";
                    htmlbody += "<td style=\"width:100%; height:2px; color:#c0c0c0; text-align: center;\">";
                    htmlbody += "---------------------------------------------------------------------------------";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "<tr>";
                    htmlbody += "<td align=\"left\">";
                    htmlbody += "WebSeite --> www.amc-server.de<br />";
                    htmlbody += "Forum --> http://forum.amc-server.de<br />";
                    htmlbody += "TeamSpeak 2 --> ts2.amc-server.de<br />";
                    htmlbody += "TeamSpeak 3 --> ts3.amc-server.de<br />";
                    htmlbody += "IRC-Chat --> irc.angelz-world.de , Channel #AngelZMineCraft<br />";
                    htmlbody += "Folge uns auf FaceBook --> https://www.facebook.com/AngelZMineCraftServer<br />";
                    htmlbody += "Folge uns auf Twitter --> https://twitter.com/AMC_Server<br />";
                    htmlbody += "Folge uns auf GooglePlus --> https://plus.google.com/+Amc-serverDe";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "<tr>";
                    htmlbody += "<td style=\"width:100%; height:2px; color:#c0c0c0; text-align: center;\">";
                    htmlbody += "---------------------------------------------------------------------------------";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "<tr>";
                    htmlbody += "<td align=\"center\">";
                    htmlbody += "<a href=\"http://www.amc-server.de\"><img src=\"http://www.amc-server.de/images/banners/banner.png\" border=\"0\" /></a>";
                    htmlbody += "<br />";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "</table>";
                    htmlbody += "</body>";
                    htmlbody += "</html>"; */
                    
                    //Body
                    body = "Hallo " + _p.getName() + ",\r\n";
                    body += "vielen Dank für deine Forum Registration auf AngelZMineCraft.\r\n";
                    body += "\r\n";
                    body += "Dein Loginname für unser Forum sowie unsere WebSeite ist " + _p.getName() + "\r\n";
                    body += "Dein vorläufiges Passwort lautet " + passwd + "\r\n";
                    body += "\r\n";
                    body += "Bitte ändere dein Passwort bei uns im Forum --> http://forum.amc-server.de/usercp.php?action=password_change\r\n";
                    body += "Bitte trage innerhalb der nächsten 7 Tage dein Wahres Geburtsdatum und Geschlecht in dein Forum Profil ein.\r\n";
                    body += "\r\n";
                    body += "Mit freundlichem Gruss\r\n";
                    body += "Das AngelZMineCraft Team\r\n";
                    body += "\r\n";
                    body += "\r\n";
                    body += "\r\n";
                    body += "Solltest du Fragen haben so kannst du uns gerne eine E-Mail an support@amc-server.de senden.\r\n";
                    body += "-----------------------------------------------------------------------------\r\n";
                    body += "WebSeite --> www.amc-server.de\r\n";
                    body += "Forum --> http://forum.amc-server.de\r\n";
                    body += "TeamSpeak 2 --> ts2.amc-server.de\r\n";
                    body += "TeamSpeak 3 --> ts3.amc-server.de\r\n";
                    body += "IRC-Chat --> irc.angelz-world.de , Channel #AngelZMineCraft\r\n";
                    body += "Folge uns auf FaceBook --> https://www.facebook.com/AngelZMineCraftServer\r\n";
                    body += "Folge uns auf Twitter --> https://twitter.com/AMC_Server\r\n";
                    body += "Folge uns auf GooglePlus --> https://plus.google.com/+Amc-serverDe";
                } else {
                    /* htmlbody = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\"  dir=\"ltr\" lang=\"en-EN\">";
                    htmlbody += "<head>";
                    htmlbody += "<style type=\"text/css\">";
                    htmlbody += "a:link{ color:#B18904; text-decoration:none; font-weight: bolder; }";
                    htmlbody += "a:hover{ color:#B18904; text-decoration:none; font-weight: bolder; }";
                    htmlbody += "a:visited{ color:#B18904; text-decoration:none; font-weight: bolder; }";
                    htmlbody += "a:active{ color:#B18904; text-decoration:none; font-weight: bolder; }";
                    htmlbody += "</style>";
                    htmlbody += "</head>";
                    htmlbody += "<body bgcolor=\"#001F21\" text=\"#FFBF00\">";
                    htmlbody += "<center>";
                    htmlbody += "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border: #c0c0c0 solid 2px;padding: 10px;\">";
                    htmlbody += "<tr>";
                    htmlbody += "<td align=\"center\">";
                    htmlbody += "<br />";
                    htmlbody += "<a href=\"http://www.amc-server.de\"><img src=\"http://www.amc-server.de/images/banners/banner.png\" border=\"0\" /></a>";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "<tr>";
                    htmlbody += "<td style=\"width:100%; height:2px; color:#c0c0c0; text-align: center;\">";
                    htmlbody += "---------------------------------------------------------------------------------";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "<tr>";
                    htmlbody += "<td align=\"left\">";
                    htmlbody += "Hello " + _p.getName() + ",<br />";
                    htmlbody += "vielen Dank für deine Forum Registration auf AngelZMineCraft.<br />";
                    htmlbody += "<br />";
                    htmlbody += "Your Loginname for our Board and Website is " + _p.getName() + "<br />";
                    htmlbody += "Your temporary password is <b>" + passwd + "</b><br />";
                    htmlbody += "<br />";
                    htmlbody += "<span style=\"color: red;\">Please change your password at our Forum <a href=\"http://forum.amc-server.de/usercp.php?action=password_change\">HERE</a></span><br />";
                    htmlbody += "Please enter in the next 7 days your date of birth and gender of truth in your forum profile.<br />";
                    htmlbody += "<br />";
                    htmlbody += "Sincerely<br />";
                    htmlbody += "The AngelZMineCraft Team<br />";
                    htmlbody += "<br />";
                    htmlbody += "<br />";
                    htmlbody += "<br />";
                    htmlbody += "If you have any questions you can send us an e-mail to support@amc-server.de<br />";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "<tr>";
                    htmlbody += "<td style=\"width:100%; height:2px; color:#c0c0c0; text-align: center;\">";
                    htmlbody += "---------------------------------------------------------------------------------";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "<tr>";
                    htmlbody += "<td align=\"left\">";
                    htmlbody += "WebSide --> www.amc-server.de<br />";
                    htmlbody += "Forum --> http://forum.amc-server.de<br />";
                    htmlbody += "TeamSpeak 2 --> ts2.amc-server.de<br />";
                    htmlbody += "TeamSpeak 3 --> ts3.amc-server.de<br />";
                    htmlbody += "IRC-Chat --> irc.angelz-world.de , Channel #AngelZMineCraft<br />";
                    htmlbody += "Follow us on FaceBook --> https://www.facebook.com/AngelZMineCraftServer<br />";
                    htmlbody += "Follow us on Twitter --> https://twitter.com/AMC_Server<br />";
                    htmlbody += "Follow us on GooglePlus --> https://plus.google.com/+Amc-serverDe";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "<tr>";
                    htmlbody += "<td style=\"width:100%; height:2px; color:#c0c0c0; text-align: center;\">";
                    htmlbody += "---------------------------------------------------------------------------------";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "<tr>";
                    htmlbody += "<td align=\"center\">";
                    htmlbody += "<a href=\"http://www.amc-server.de\"><img src=\"http://www.amc-server.de/images/banners/banner.png\" border=\"0\" /></a>";
                    htmlbody += "<br />";
                    htmlbody += "</td>";
                    htmlbody += "</tr>";
                    htmlbody += "</table>";
                    htmlbody += "</body>";
                    htmlbody += "</html>"; */
                    
                    //Body
                    body = "Hello " + _p.getName() + ",\r\n";
                    body += "vielen Dank für deine Forum Registration auf AngelZMineCraft.\r\n";
                    body += "\r\n";
                    body += "Your Loginname for our Board and Website is " + _p.getName() + "\r\n";
                    body += "Your temporary password is " + passwd + "\r\n";
                    body += "\r\n";
                    body += "Please change your password at our Forum http://forum.amc-server.de/usercp.php?action=password_change\r\n";
                    body += "Please enter in the next 7 days your date of birth and gender of truth in your forum profile.\r\n";
                    body += "\r\n";
                    body += "Sincerely\r\n";
                    body += "The AngelZMineCraft Team\r\n";
                    body += "\r\n";
                    body += "\r\n";
                    body += "\r\n";
                    body += "If you have any questions you can send us an e-mail to support@amc-server.de\r\n";
                    body += "-----------------------------------------------------------------------------\r\n";
                    body += "WebSide --> www.amc-server.de";
                    body += "Forum --> http://forum.amc-server.de\r\n";
                    body += "TeamSpeak 2 --> ts2.amc-server.de\r\n";
                    body += "TeamSpeak 3 --> ts3.amc-server.de\r\n";
                    body += "IRC-Chat --> irc.angelz-world.de , Channel #AngelZMineCraft\r\n";
                    body += "Follow us on FaceBook --> https://www.facebook.com/AngelZMineCraftServer\r\n";
                    body += "Follow us on Twitter --> https://twitter.com/AMC_Server\r\n";
                    body += "Follow us on GooglePlus --> https://plus.google.com/+Amc-serverDe";
                }
                
                //message.setContent(htmlbody, "text/html");
                message.setText(body);
                Transport.send(message);
                _p.sendMessages(new String[]{"§2Board Account successful created. We have send you an E-Mail.","§aForum Account erfolgreich erstellt. Wir haben dir eine E-Mail geschickt."});
            } catch (AddressException ex) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0035)","§cEin unerwarteter Fehler ist aufgetreten. (0035)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Verarbeiten der E-Mail!", ex);
            } catch (MessagingException ex) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0034)","§cEin unerwarteter Fehler ist aufgetreten. (0034)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Versenden der E-Mail!", ex);
            } catch (UnsupportedEncodingException ex) {
                _p.sendMessages(new String[]{"§4An unexpected error occurred. (0033)","§cEin unerwarteter Fehler ist aufgetreten. (0033)"});
                ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler im E-Mail Prozess!", ex);
            }
        }
    }
    
    private void onMySQLError(int id, Connection con, String name, int userId) {
        try {
            if(id >= 1) {
                PreparedStatement statement;
                statement = con.prepareStatement("DELETE FROM `bb1_users` WHERE `userid` = ?");
                statement.setInt(1, userId);
                statement.execute();
                statement.close();
            }
            
            if(id >= 2) {
                PreparedStatement statement;
                statement = con.prepareStatement("DELETE FROM `bb1_userfields` WHERE `userid` = ?");
                statement.setInt(1, userId);
                statement.execute();
                statement.close();
            }
            
            if(id >= 3) {
                PreparedStatement statement;
                statement = con.prepareStatement("DELETE FROM `bb1_user2groups` WHERE `userid` = ?");
                statement.setInt(1, userId);
                statement.execute();
                statement.close();
            }
            
            if(id >= 4) {
                PreparedStatement statement1;
                statement1 = con.prepareStatement("SELECT `userid` FROM `bb1_users` ORDER BY `userid` DESC LIMIT 0,1");
                try (ResultSet rset = statement1.executeQuery()) {
                    if(rset.next()) {
                        PreparedStatement statement;
                        statement = con.prepareStatement("UPDATE `bb1_stats` SET `usercount`=usercount+1, `lastuserid`=?");
                        statement.setInt(1, rset.getInt("userid"));
                        statement.execute();
                        statement.close();
                    }
                }
                statement1.close();
            }
            
            if(id >= 5) {
                PreparedStatement statement;
                statement = con.prepareStatement("DELETE FROM `bb1_aw_shoutbox` WHERE `msg` LIKE ?");
                statement.setString(1, "%" + name + "%");
                statement.execute();
                statement.close();
            }
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Loeschvorgang einer fehlgelaufenen Account erstellung.", e);
        }
    }
}
