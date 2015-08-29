package de.papaharni.amcbungee.commands;

import de.papaharni.amcbungee.AMCBungee;
import de.papaharni.amcbungee.util.Mixes;
import de.papaharni.amcbungee.util.Votes;
import java.util.List;
import java.util.Random;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author Pappi
 */
public class Vote extends Command {
    
    public Vote() {
        super("vote", "amcbungee.vote");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer)s;
        if(p == null)
            return;
        
        String prefix = ChatColor.translateAlternateColorCodes('&', AMCBungee.getInstance().getConfig().getString("vote.prefix", "&f[&5Vote&f]"));
        String lang = AMCBungee.getPlayerLang(p.getName());
        if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("view") || args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take")) {
                if(!p.hasPermission("amcbungee.vote." + args[0].toLowerCase())) {
                    if(AMCBungee.isDeLanguage(lang))
                        p.sendMessage(prefix + "§4Du hast keine Rechte diesen Befehl zu verwenden.");
                    else
                        p.sendMessage(prefix + "§cYou don't have permission to use this Command.");
                    return;
                }
                
                switch(args[0].toLowerCase()) {
                    case "view":
                        String name = p.getName();
                        if(args.length >= 2 && p.hasPermission("amcbungee.vote.view.other")) {
                            name = args[1];
                        }
                        
                        int vp = AMCBungee.getInstance().getSQL().getPlayerPoint(name, "vpoints");
                        int sp = AMCBungee.getInstance().getSQL().getPlayerPoint(name, "sppoints");
                        if(vp >= 0 && sp >= 0) {
                            if(AMCBungee.isDeLanguage(lang))
                                p.sendMessages(new String[] {prefix + (name.equalsIgnoreCase(p.getName())?"§2Du hast":"§2" + name + " hat"),prefix + "§9" + vp + " §2Vote-Punkte",prefix + "§9" + sp + " §2Spenden-Punkte"});
                            else
                                p.sendMessages(new String[] {prefix + (name.equalsIgnoreCase(p.getName())?"§2You have":"§2" + name + " has"),prefix + "§9" + vp + " §2Vote-Points",prefix + "§9" + sp + " §2Spend-Points"});
                        } else if(vp == -1 || sp == -1) {
                            //Unbekannt
                            if(AMCBungee.isDeLanguage(lang))
                                p.sendMessage(prefix + "§cUnbekannter Fehler.");
                            else
                                p.sendMessage(prefix + "§cUnnamend Error.");
                        } else if(vp == -2 || sp == -2) {
                            //Auslese Fehler
                            if(AMCBungee.isDeLanguage(lang))
                                p.sendMessage(prefix + "§cFehler beim Lesen der Informationen aus der Datenbank.");
                            else
                                p.sendMessage(prefix + "§cError on Reading Database Informations.");
                        } else if(vp == -3 || sp == -3) {
                            //Allgemeiner DB fehler
                            if(AMCBungee.isDeLanguage(lang))
                                p.sendMessage(prefix + "§cFehler beim LAden der Datenbank Konfiguration beim Server Start.");
                            else
                                p.sendMessage(prefix + "§cError on load Database configuration on server startup.");
                        }
                        return;
                    case "give":
                        if(args.length < 4) {
                            if(AMCBungee.isDeLanguage(lang))
                                p.sendMessage(prefix + "$4Bitte verwende /vote " + args[0] + " {type} {player} {summe}");
                            else
                                p.sendMessage(prefix + "§cPlease use /vote " + args[0] + " {type} {player} {amount}");
                            return;
                        }

                        if(!args[1].equalsIgnoreCase("vpoints") && !args[1].equalsIgnoreCase("sppoints")) {
                            if(AMCBungee.isDeLanguage(lang))
                                p.sendMessage(prefix + "$4Unbekannter Type.");
                            else
                                p.sendMessage(prefix + "$cInvalide Type.");
                            return;
                        }

                        if(!Mixes.isNumeric(args[3])) {
                            if(AMCBungee.isDeLanguage(lang))
                                p.sendMessage(prefix + "$4Summe ist keine Nummer.");
                            else
                                p.sendMessage(prefix + "§cAmount is not a number.");
                            return;
                        }
                        
                        AMCBungee.getInstance().getSQL().setPlayerPoints(args[2], Integer.parseInt(args[3]), args[1].toLowerCase(), true);
                        if(AMCBungee.isDeLanguage(lang))
                            p.sendMessage(prefix + "§2Erfolgreich gegeben.");
                        else
                            p.sendMessage(prefix + "§aSuccessful given.");
                        if(ProxyServer.getInstance().getPlayer(args[2]) != null) {
                            if(AMCBungee.isDeLanguage(AMCBungee.getPlayerLang(args[2]))) {
                                String t = (args[1].equalsIgnoreCase("sppoints")?"Spenden-Punkte":"Vote-Punkte");
                                ProxyServer.getInstance().getPlayer(args[2]).sendMessage(prefix + "§2Dir wurden §9" + args[3] + " " + t + " §2gutgeschrieben.");
                            } else {
                                String t = (args[1].equalsIgnoreCase("sppoints")?"Spend-Points":"Vote-Points");
                                ProxyServer.getInstance().getPlayer(args[2]).sendMessage(prefix + "§aYou have been credited §9" + args[3] + " " + t + "§a.");
                            }
                        }
                        return;
                    case "take":
                        if(args.length < 4) {
                            if(AMCBungee.isDeLanguage(lang))
                                p.sendMessage(prefix + "$4Bitte verwende /vote " + args[0] + " {type} {player} {summe}");
                            else
                                p.sendMessage(prefix + "§cPlease use /vote " + args[0] + " {type} {player} {amount}");
                            return;
                        }

                        if(!args[1].equalsIgnoreCase("vpoints") && !args[1].equalsIgnoreCase("sppoints")) {
                            if(AMCBungee.isDeLanguage(lang))
                                p.sendMessage(prefix + "$4Unbekannter Type.");
                            else
                                p.sendMessage(prefix + "$cInvalide Type.");
                            return;
                        }

                        if(!Mixes.isNumeric(args[3])) {
                            if(AMCBungee.isDeLanguage(lang))
                                p.sendMessage(prefix + "$4Summe ist keine Nummer.");
                            else
                                p.sendMessage(prefix + "§cAmount is not a number.");
                            return;
                        }
                        
                        int has = AMCBungee.getInstance().getSQL().getPlayerPoint(args[2], args[1].toLowerCase());
                        if(has < Integer.parseInt(args[3])) {
                            if(AMCBungee.isDeLanguage(lang)) {
                                String t = (args[1].equalsIgnoreCase("sppoints")?"Spenden-Punkte":"Vote-Punkte");
                                p.sendMessage(prefix + "§4Spieler §9" + args[2] + " §4hat nicht genug von §9" + t);
                            } else {
                                String t = (args[1].equalsIgnoreCase("sppoints")?"Spend-Points":"Vote-Points");
                                p.sendMessage(prefix + "§cPlayer §9" + args[2] + " §chas not enought of §9" + t);
                            }
                            return;
                        }
                        
                        AMCBungee.getInstance().getSQL().setPlayerPoints(args[2], Integer.parseInt(args[3]), args[1].toLowerCase(), false);
                        if(AMCBungee.isDeLanguage(lang))
                            p.sendMessage(prefix + "§2Erfolgreich abgezogen.");
                        else
                            p.sendMessage(prefix + "§aSuccessful deducted.");
                        if(ProxyServer.getInstance().getPlayer(args[2]) != null) {
                            if(AMCBungee.isDeLanguage(AMCBungee.getPlayerLang(args[2]))) {
                                String t = (args[1].equalsIgnoreCase("sppoints")?"Spenden-Punkte":"Vote-Punkte");
                                ProxyServer.getInstance().getPlayer(args[2]).sendMessage(prefix + "§2Dir wurden §9" + args[3] + " " + t + " §2abgezogen.");
                            } else {
                                String t = (args[1].equalsIgnoreCase("sppoints")?"Spend-Points":"Vote-Points");
                                ProxyServer.getInstance().getPlayer(args[2]).sendMessage(prefix + "§aYou were withdrawn §9" + args[3] + " " + t + "§a.");
                            }
                        }
                        return;
                    default:
                        p.sendMessage(prefix + "§cYou have do something wrong.");
                        return;                  
                        
                }
            }
        }
        ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new useVote(p, lang));
    }
    
    private String getRandomString() {
        String str = "";
        String[] stra = {"q","w","e","r","t","z","u","i","o","p","a","s","d","f","g","h","j","k","l","y","x","c","v","b","n","m","Q","W","E","R","T","Z","U","I","O","P","A","S","D","F","G","H","J","K","L","Y","X","C","V","B","N","M","0","1","2","3","4","5","6","7","8","9"};
        for(int i = 0; i < 16; i++) {
            str += stra[new Random().nextInt(stra.length)];
        }
        return str;
    }
    
    public class useVote implements Runnable {
        private final ProxiedPlayer _p;
        private final String _l;
        private final String _prefix;
        
        public useVote(ProxiedPlayer p, String l) {
           _p = p;
           _l = l;
           _prefix = ChatColor.translateAlternateColorCodes('&', AMCBungee.getInstance().getConfig().getString("vote.prefix", "&f[&5Vote&f]"));
        }
        
        @Override
        public void run() {
            if(AMCBungee.getInstance().getPlayerVotes().containsKey(_p.getName())) {
                Votes v = AMCBungee.getInstance().getPlayerVotes().get(_p.getName());
                v.updateVotes();
                List<Integer> li = v.getNotVoted();
                if(li.size() < 1) {
                    if(AMCBungee.isDeLanguage(_l))
                        _p.sendMessage(_prefix + "§4Du hast keine Votes mehr offen derzeit. Bitte versuch es später nochmal.");
                    else
                        _p.sendMessage(_prefix + "§cYou have no votes currently open. Please check later again.");
                    return;
                }

                int voteId = li.get(new Random().nextInt(li.size()));
                String k = getRandomString();
                if(!AMCBungee.getInstance().getSQL().addPlayerVotes(voteId, k, _p.getName())) {
                    if(AMCBungee.isDeLanguage(_l))
                        _p.sendMessage(_prefix + "§4Es ist ein Fehler aufgetreten. Bitte versuche es noch einmal.");
                    else
                        _p.sendMessage(_prefix + "§cIt's happend an error. Please try again.");
                    return;
                }
                if(AMCBungee.isDeLanguage(_l)) {
                    _p.sendMessage(_prefix + ChatColor.GOLD + "~~~~~~~~~~ " + ChatColor.DARK_PURPLE + "Voten leicht gemacht" + ChatColor.GOLD + " ~~~~~~~~~~");
                    _p.sendMessage(_prefix + ChatColor.GREEN + "Bitte klicke auf den nachfolgenden Link,");
                    _p.sendMessage(_prefix + ChatColor.GREEN + "es kann sein das du danach noch auf Bilder klicken musst");
                    _p.sendMessage(_prefix + ChatColor.GREEN + "oder Captcha's oder aehnliches.");
                    _p.sendMessage(_prefix + ChatColor.YELLOW + "http://vote.amc-server.de/?v=" + k);
                    _p.sendMessage(_prefix + ChatColor.RED + "Dieser Link ist nur 2 Minuten gültig. Bitte verwende diesen vor Ablauf der Zeit.");
                    _p.sendMessage(_prefix + ChatColor.GOLD + "~~~~~~~~~~ " + ChatColor.DARK_PURPLE + (AMCBungee.getInstance().getMyConfig()._maxVotes-li.size()+1) + ChatColor.WHITE + " / " + ChatColor.DARK_PURPLE + AMCBungee.getInstance().getMyConfig()._maxVotes + ChatColor.GOLD + " ~~~~~~~~~~");
                } else {
                    _p.sendMessage(_prefix + ChatColor.GOLD + "~~~~~~~~~~ " + ChatColor.DARK_PURPLE + "Vote made easy" + ChatColor.GOLD + " ~~~~~~~~~~");
                    _p.sendMessage(_prefix + ChatColor.GREEN + "Please click on the following link,");
                    _p.sendMessage(_prefix + ChatColor.GREEN + "it may be that you then have to click on pictures must");
                    _p.sendMessage(_prefix + ChatColor.GREEN + "or Captcha's or similar.");
                    _p.sendMessage(_prefix + ChatColor.YELLOW + "http://vote.amc-server.de/?v=" + k);
                    _p.sendMessage(_prefix + ChatColor.RED + "This link is valid for only 2 minutes. Please use this before the time expires.");
                    _p.sendMessage(_prefix + ChatColor.GOLD + "~~~~~~~~~~ " + ChatColor.DARK_PURPLE + (AMCBungee.getInstance().getMyConfig()._maxVotes-li.size()+1) + ChatColor.WHITE + " / " + ChatColor.DARK_PURPLE + AMCBungee.getInstance().getMyConfig()._maxVotes + ChatColor.GOLD + " ~~~~~~~~~~");
                }
            } else {
                if(AMCBungee.isDeLanguage(_l)) 
                    _p.sendMessage(_prefix + "§4Es ist ein Fehler aufgetreten. Bitte Verbinde dich mit dem Server neu.");
                else
                    _p.sendMessage(_prefix + "§cIt's been an error. Please reConnect to the server.");
            }
        }
    }
}
