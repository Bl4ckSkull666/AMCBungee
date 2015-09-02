package de.papaharni.amcbungee;

import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import de.papaharni.amcbungee.commands.*;
import de.papaharni.amcbungee.commands.whispers.*;
import de.papaharni.amcbungee.events.*;
import de.papaharni.amcbungee.util.ChatLogger;
import de.papaharni.amcbungee.util.Mixes;
import de.papaharni.amcbungee.util.PingTask;
import de.papaharni.amcbungee.util.PlayerAccess;
import de.papaharni.amcbungee.util.PlayerModes;
import de.papaharni.amcbungee.util.Rnd;
import de.papaharni.amcbungee.util.Votes;
import de.papaharni.amcbungee.util.multis;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import javax.imageio.ImageIO;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import yamlapi.file.FileConfiguration;

/**
 *
 * @author Pappi
 */
public class AMCBungee extends Plugin {
    private static AMCBungee _instance;
    private MySQLData _sql;
    private final HashMap<String, String> _players = new HashMap<>();
    private final HashMap<String, Long> _playersJoin = new HashMap<>();
    private final HashMap<String, Votes> _playerVotes = new HashMap<>();
    private final HashMap<String, ScheduledTask> _playerTasks = new HashMap<>();
    private final Collection<String> _guestAccess = new ArrayList<>();
    private final List<ChatLogger> _cl = new ArrayList<>();
    private int total_votes = -1;
    private ScheduledTask _chatLogTask = null;
    private ScheduledTask _playerOnlineTask = null;
    private ScheduledTask _voteInfoTask = null;
    private final HashMap<String, Long> _playerChatTime = new HashMap<>();
    private final HashMap<String, Long> _playerWarnings = new HashMap<>();
    private final HashMap<String, Long> _playerLastWarnings = new HashMap<>();
    private final HashMap<String, Long> _playerMuteTime = new HashMap<>();
    private final HashMap<String, String> _multiIPSchutz = new HashMap<>();
    private final HashMap<String, Integer> _playerWarning = new HashMap<>();
    private static final HashMap<String, String> _playerLanguage = new HashMap<>();
    private final Collection<multis> _multis = new ArrayList<>();
    private final Collection<String> _playersBlocked = new ArrayList<>();
    private FileConfiguration _config;
    
    private static final HashMap<String, String> _playersLastReceiver = new HashMap<>();
    private static final ArrayList<Favicon> _favics = new ArrayList<>();
    private static final HashMap<String, ScheduledTask> _joinTasks = new HashMap<>();
    private static final HashMap<InetAddress, String> _lastSeenPlayers = new HashMap<>();
    private static final HashMap<String, PingTask> _serverState = new HashMap<>();
    private static final HashMap<String, ScheduledTask> _serverStateTasks = new HashMap<>();
    private static final HashMap<String, Boolean> _serverStateLastStatus = new HashMap<>();
    private static PlayerModes _playermodes;
    
    @Override
    public void onDisable() {
        _playermodes.save();
        if(_chatLogTask != null)
            _chatLogTask.cancel();
        
        if(_voteInfoTask != null)
            _voteInfoTask.cancel();
        
        _sql.saveChatLogger(_cl);
        _cl.clear();
        if(_playerOnlineTask != null)
            _playerOnlineTask.cancel();
        
        for(Map.Entry<String, ScheduledTask> me: _serverStateTasks.entrySet()) {
            if(me.getValue() != null)
                me.getValue().cancel();
        }
    }

    @Override
    public void onEnable() {
        _instance = this;
        reloadConfig();
        total_votes = getTotalVotes();
        _config.set("max-votes", total_votes);
        
        _sql = new MySQLData();
        
        //Alles was MySQL benötigt
        if(_sql != null) {
            //Befehle
            ProxyServer.getInstance().getPluginManager().registerCommand(this, new IpRange(this));
            ProxyServer.getInstance().getPluginManager().registerCommand(this, new Spenden());
            ProxyServer.getInstance().getPluginManager().registerCommand(this, new BoardReg());
            if(total_votes >= 0)
                ProxyServer.getInstance().getPluginManager().registerCommand(this, new Vote());
            
            //Event
            ProxyServer.getInstance().getPluginManager().registerListener(this, new LoginEvents(this));
            ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerConnectEvents(this));
            ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerDisconnectEvents(this));
        }
        
        //Alles was KEIN MySQL benötigt
        //Befehle
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Team());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Op());
        //Whispers
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new m());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new msg());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new t());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new tell());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new w());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new whisper());
        
        
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Kickall());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new amcbungeereload());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new viewMods());
        //Event
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ChatEvents(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ProxyPing());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new AutoStartServer());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new TabComplete());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMessage());
        
        File f = new File("GeoIp.dat");
        if(!f.exists()) {
            try {
                URL url = new URL("http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz");
                InputStream in = url.openConnection().getInputStream();
                OutputStream out = new FileOutputStream("GeoIp.dat.gz");
                byte[] buffer = new byte[1024];
                int i;
                while ((i = in.read(buffer)) > 0) {
                    out.write(buffer, 0, i);
                }
                in.close();
                out.close();
                
                f = new File("GeoIp.dat.gz");
                if(f.exists()) {
                    GZIPInputStream ingz = new GZIPInputStream(
                            new FileInputStream("GeoIp.dat.gz")
                    );

                    // Open the output file
                    OutputStream outgz = new FileOutputStream("GeoIp.dat");

                    // Transfer bytes from the compressed file to the output file
                    byte[] buf = new byte[1024];
                    while ((i = ingz.read(buf)) > 0) {
                        outgz.write(buf, 0, i);
                    }
                    // Close the file and stream
                    ingz.close();
                    outgz.close();
                }
                f.delete();
            } catch(IOException e) {
                AMCBungee.getInstance().getLogger().log(Level.WARNING, "Fehler beim GeoIp.dat holen", e);
            }
        }
        
        setDeLanguages("at");
        setDeLanguages("ch");
        setDeLanguages("de");
        
        //_chatLogTask = ProxyServer.getInstance().getScheduler().schedule(this, new autoSaveChatLogger(), 5, TimeUnit.MINUTES);
        _playerOnlineTask = ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new playerOnlineTask());
        _voteInfoTask = ProxyServer.getInstance().getScheduler().schedule(this, new voteInformation(), 5, 20, TimeUnit.MINUTES);
        setFavicons();
        for(ServerInfo server : getProxy().getServers().values()) {
            PingTask task = new PingTask(server);
            _serverState.put(server.getName().toLowerCase(), task);
            _serverStateTasks.put(server.getName().toLowerCase(), getProxy().getScheduler().schedule(this, task, 10, 10, TimeUnit.SECONDS));
            _serverStateLastStatus.put(server.getName().toLowerCase(), false);
        }
        
        try {
            _playermodes = new PlayerModes((Plugin)this);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Can't load PlayerModes.", ex);
        }
        
        Mu1ti1ingu41.loadExternalDefaultLanguage(this, "languages");
    }
    
    public FileConfiguration getConfig() {
        return _config;
    }

    public void saveConfig() {
        Mu1ti1ingu41.saveConfig(_config, _instance);
    }
    
    public void reloadConfig() {
        _config = Mu1ti1ingu41.loadConfig(_instance);
    }
    
    public static AMCBungee getInstance() {
        return _instance;
    }
    
    public PlayerModes getPlayerModes() {
        return _playermodes;
    }
    
    private void setFavicons() {
        _favics.clear();
        for(String fav: getConfig().getStringList("favicons")) {
            try {
                File f = new File(fav);
                if(f == null)
                    continue;
                
                BufferedImage bi = ImageIO.read(f);
                if(bi == null)
                    continue;
                
                Favicon favi = Favicon.create(bi);
                if(favi == null)
                    continue;
                
                _favics.add(favi);
            } catch(IOException e) {
                getLogger().log(Level.WARNING, fav + " is no correct path to an image");
            }
        }
    }
    
    /*public void loadConfig() {
        if(!this.getDataFolder().exists())
            this.getDataFolder().mkdir();
        try {
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                Files.copy(getResourceAsStream("config.yml"), file.toPath());
            }
            Configuration conf = (Configuration)ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

            

        } catch(IOException e) {
            
        }
    }*/
    
    public MySQLData getSQL() {
        return _sql;
    }
    
    public String getServerName(String server) {
        return _config.getString("server-names." + server, server);
    }
    
    public HashMap<String, String> getPlayerOnServer() {
        return _players;
    }
    
    public HashMap<String, Long> getOnlineSince() {
        return _playersJoin;
    }
    
    public int getMaxVotes() {
        return total_votes;
    }
    
    public HashMap<String, Votes> getPlayerVotes() {
        return _playerVotes;
    }
    
    public HashMap<String, ScheduledTask> getPlayerTask() {
        return _playerTasks;
    }
    
    public HashMap<String, Long> getPlayerWarnings() {
        return _playerWarnings;
    }
    
    public HashMap<String, Long> getPlayerLastWarning() {
        return _playerLastWarnings;
    }
    
    public HashMap<String, Long> getPlayerTime() {
        return _playerChatTime;
    }
    
    public HashMap<String, Integer> getPlayersWarn() {
        return _playerWarning;
    }
    
    public static HashMap<String, String> getPlayersLanguage() {
        return _playerLanguage;
    }
    
    public static String getPlayerLang(String name) {
        return _playerLanguage.containsKey(name)?_playerLanguage.get(name):"en";
    }
    
    public HashMap<String, String> getMultiIPSchutz() {
        return _multiIPSchutz;
    }
    
    public void setPlayersBlocked(String name) {
        _playersBlocked.add(name.toLowerCase());
    }
    
    public boolean isPlayersBlocked(String name) {
        return _playersBlocked.contains(name.toLowerCase());
    }
    
    public boolean isNamesInMulti(String n1, String n2) {
        for(multis m:_multis) {
            if(m.checkNamesIn(n1, n2))
                return true;
        }
        return false;
    }
    
    public static int getTotalVotes() {
        try {
           String msg = "";
           URL url = new URL("http://vote.amc-server.de/totalvotes.php");
           Scanner s = new Scanner(url.openStream());
           if(s.hasNextLine()) {
               msg = s.nextLine();
           }
           if(isNumeric(msg))
               return Integer.parseInt(msg);
           
        } catch(IOException e) {
            AMCBungee.getInstance().getLogger().log(Level.WARNING, "Konnte den Votes Total stand nicht abrufen", e);
            return -1;
        }
        return -1;
    }
    
    public static String getUserUUID(String p) {
        try {
            String msg = "";
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + p + "?at=" + Math.round((System.currentTimeMillis()/1000)));
            Scanner s = new Scanner(url.openStream());
            while(s.hasNextLine())
                msg += s.nextLine();

            if(!msg.equals("")) {
                String[] searches = {"\"","{","}"};
                for(String search: searches)
                    msg = msg.replace(search,"");
                
                String[] msgs = msg.split(",");
                for(String m: msgs) {
                    String[] k = m.split(":");
                    if(k[0].equalsIgnoreCase("id")) {
                        return k[1];
                    }
                }
            }
        } catch(IOException e) {
            AMCBungee.getInstance().getLogger().log(Level.WARNING, "Konnte keine UUID finden für " + p, e);
            return "";
        }
        return "";
    }
    
    public static int getConfigInt(String var) {
        try {
            int i = Integer.parseInt(getConfigString(var));
            return i;
        } catch(NumberFormatException e) {
            return -1;
        }
    }
    
    public static boolean getConfigBoolean(String var) {
        return Boolean.parseBoolean(getConfigString(var));
    }
    
    public static String getConfigString(String var) {
        try {
           String msg = "";
           URL url = new URL("http://www.amc-server.de/functions/getBungeeConfig.php?id=" + var);
           Scanner s = new Scanner(url.openStream());
           if(s.hasNextLine()) {
               msg += s.nextLine();
           }
           return msg;
        } catch(IOException e) {
            AMCBungee.getInstance().getLogger().log(Level.WARNING, "Konnte " + var + " Status nicht abrufen", e);
            return "";
        }
    }
    
    public static boolean isNumeric(String str) {
        try {
            int a = Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
    
    public Collection<String> getGuestAccessList() {
        return _guestAccess;
    }
    
    /*public void addChatLog(ChatLogger cl) {
        _cl.add(cl);
        if(_cl.size() >= 25) {
            List<ChatLogger> clList = new ArrayList<>();
            clList.addAll(_cl);
            _cl.clear();
            _sql.saveChatLogger(clList);
        }
    }
    
    public class autoSaveChatLogger implements Runnable {
        @Override
        public void run() {
            if(_chatLogTask != null)
                _chatLogTask.cancel();
            
            if(_cl.size() >= 1) {
                List<ChatLogger> clList = new ArrayList<>();
                clList.addAll(_cl);
                _cl.clear();
                _sql.saveChatLogger(clList);
            }
            
            _chatLogTask = ProxyServer.getInstance().getScheduler().schedule(AMCBungee.getInstance(), new autoSaveChatLogger(), 5, TimeUnit.MINUTES);
        }
    }*/
    
    public static class saveChat implements Runnable {
        private final ChatLogger _cl;
        public saveChat(ChatLogger cl) {
            _cl = cl;
        }
        
        @Override
        public void run() {
            AMCBungee.getInstance().getSQL().saveChatLogger(_cl);
        }
    }
    
    public class voteInformation implements Runnable {
        @Override
        public void run() {
            ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new aVoteInformation());
        }
    }
    
    public class aVoteInformation implements Runnable {
        @Override
        public void run() {
            String prefix = getConfig().getString("vote.prefix", "&f[&5Vote&f]");
            for(ProxiedPlayer p: ProxyServer.getInstance().getPlayers()) {
                if(!_playerVotes.containsKey(p.getName()))
                    continue;
                
                if(_playerVotes.get(p.getName()).getNotVoted().isEmpty())
                    continue;
                
                if(!_playerLanguage.containsKey(p.getName()))
                    continue;
                
                String pLang = getPlayerLang(p.getName());
                if(getConfig().isList("vote.msg." + pLang)) {
                    for(String msg: getConfig().getStringList("vote.msg." + pLang))
                        p.sendMessage(convert(prefix + msg));
                } else if(getConfig().isString("vote.msg." + pLang)) {
                    p.sendMessage(convert(prefix + getConfig().getString("vote.msg." + pLang)));
                } else {
                    if(getConfig().isList("vote.msg.default")) {
                        for(String msg: getConfig().getStringList("vote.msg.default"))
                            p.sendMessage(convert(prefix + msg));
                    } else if(getConfig().isString("vote.msg.default")) {
                        p.sendMessage(convert(prefix + getConfig().getString("vote.msg.default")));
                    } else {
                        getLogger().log(Level.WARNING, "Can't find Vote Message");
                    }
                    
                }
            }
        }
    }
    
    public class playerOnlineTask implements Runnable {
        @Override
        public void run() {
            if(_playerOnlineTask != null)
                _playerOnlineTask.cancel();
            _sql.resetPlayerOnline();
            _playerOnlineTask = ProxyServer.getInstance().getScheduler().schedule(AMCBungee.getInstance(), new playerOnlineTask(), 15, TimeUnit.MINUTES);
        }
    }
    
    public static void sendPrivateMessage(CommandSender s, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer)s;
        
        if(p == null)
            return;
        
        if(!p.hasPermission("amcbungee.msg")) {
            if(AMCBungee.isDeLanguage(AMCBungee.getPlayerLang(p.getName())))
                p.sendMessage(convert("&4Du hast keine Rechte diesen Befehl zu verwenden."));
            else
                p.sendMessage(convert("&4You don't have permission to use this command."));
            return;
        }
        
        if(args.length < 2) {
            if(AMCBungee.isDeLanguage(AMCBungee.getPlayerLang(p.getName())))
                p.sendMessage(convert("&4Format fehlt! /m {empfaenger} {nachricht}"));
            else
                p.sendMessage(convert("&4Wrong use. /m {receiver} {message}"));
            return;
        }
        
        int msgStart = 1;
        ProxiedPlayer pr = getPlayer(args[0]);
        if(pr == null && _playersLastReceiver.containsKey(p.getName())) {
            pr = getPlayer(_playersLastReceiver.get(p.getName()));
            msgStart++;
        } else if(pr != null) {
            _playersLastReceiver.put(p.getName(), args[0]);
        }
        
        if(pr == null) {
            if(AMCBungee.isDeLanguage(AMCBungee.getPlayerLang(p.getName())))
                p.sendMessage(convert("&4Konnte den Empfaenger nicht finden."));
            else
                p.sendMessage(convert("&4Can't found the receiver."));
            return;
        }
        
        String msg = args[msgStart];
        for(int i = msgStart+1;i < args.length; i++) {
            msg += " " + args[i];
        }
        
        p.sendMessage(convert("&f<&bich &f-> &e" + ((pr.getDisplayName() != null && !pr.getDisplayName().isEmpty())?pr.getDisplayName():pr.getName()) + "&f> &6" + msg));
        pr.sendMessage(convert("&f<&e" + ((p.getDisplayName() != null && !p.getDisplayName().isEmpty())?p.getDisplayName():p.getName()) + " &f-> &bmir&f> &6" + msg));
    }
    
    public static ProxiedPlayer getPlayer(String str) {
        for(ProxiedPlayer pr: ProxyServer.getInstance().getPlayers()) {
            if(pr.getName().equalsIgnoreCase(str))
                return pr;
        }
        return null;
    }
    
    private static final ArrayList<String> _deLanguages = new ArrayList<>();
    public static void setDeLanguages(String lang) {
        _deLanguages.add(lang);
    }
    
    public static boolean isDeLanguage(String lang) {
        return _deLanguages.contains(lang.toLowerCase());
    }
    
    private static final HashMap<String, PlayerAccess> _playerAccesses = new HashMap<>();
    public static void setPlayerAccess(String name, PlayerAccess pa) {
        _playerAccesses.put(name, pa);
    }
    
    public static PlayerAccess getPlayerAccess(String name) {
        if(!_playerAccesses.containsKey(name))
            _playerAccesses.put(name, new PlayerAccess(name));
        return _playerAccesses.get(name);
    }
    
    public static String getRandomModt(InetAddress ia) {
        String langCode = Mixes.getLangCode(ia);
        String transform = Mixes.getLanguageForLangCode(langCode);
        
        if(AMCBungee.getInstance().getConfig().isList("motd." + transform))
            return AMCBungee.getInstance().getConfig().getStringList("motd." + transform).get(Rnd.get(0, (AMCBungee.getInstance().getConfig().getStringList("motd." + transform).size()-1)));
        else if(AMCBungee.getInstance().getConfig().isList("motd." + langCode))
            return AMCBungee.getInstance().getConfig().getStringList("motd." + langCode).get(Rnd.get(0, (AMCBungee.getInstance().getConfig().getStringList("motd." + langCode).size()-1)));
        else if(AMCBungee.getInstance().getConfig().isList("motd.default"))
            return AMCBungee.getInstance().getConfig().getStringList("motd.default").get(Rnd.get(0, (AMCBungee.getInstance().getConfig().getStringList("motd.default").size()-1)));
        else
            return "";
    }
    
    public static Favicon getRandomFavicon() {
        if(_favics.size() < 1)
            return null;
        
        return _favics.get(Rnd.get(0, (_favics.size()-1)));
    }
    
    public static HashMap<String, ScheduledTask> getJoinTasks() {
        return _joinTasks;
    }
    
    public static HashMap<InetAddress, String> getLastSeenPlayers() {
        return _lastSeenPlayers;
    }
    
    public static BaseComponent[] convert(String message) {
        BaseComponent[] bcs = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message));
        for(BaseComponent bc: bcs) {
            if(isPlayer(bc.toPlainText())) {
                
            }
        }
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message));
    }
    
    private static boolean isPlayer(String name) {
        for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
            if(name.equalsIgnoreCase(ChatColor.stripColor(pp.getName())))
                return true;
            if(!pp.getDisplayName().isEmpty() && name.equalsIgnoreCase(ChatColor.stripColor(pp.getDisplayName())))
                return true;
        }
        return false;
    }
    
    public static void sendMessages(ProxiedPlayer pp, String[] messages) {
        for(String str: messages)
            pp.sendMessage(convert(str));
    }
    
    public static HashMap<String, PingTask> getServerPing() {
        return _serverState;
    }
    
    public static HashMap<String, Boolean> getServerPingLastStatus() {
        return _serverStateLastStatus;
    }
    
    private final static HashMap<UUID, Integer> _playerAge = new HashMap<>();
    private final static HashMap<UUID, String> _playerGender = new HashMap<>();
    private final static ArrayList<UUID> _playerVerification = new ArrayList<>();
    
    public static HashMap<UUID, Integer> getPlayerAge() {
        return _playerAge;
    }
    
    public static HashMap<UUID, String> getPlayerGender() {
        return _playerGender;
    }
    
    public static ArrayList<UUID> getPlayerVerification() {
        return _playerVerification;
    }
}
