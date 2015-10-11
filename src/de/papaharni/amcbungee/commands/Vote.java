package de.papaharni.amcbungee.commands;

import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import de.papaharni.amcbungee.AMCBungee;
import de.papaharni.amcbungee.commands.vote.GiveAndTake;
import de.papaharni.amcbungee.commands.vote.View;
import de.papaharni.amcbungee.util.Rnd;
import de.papaharni.amcbungee.util.Votes;
import java.util.List;
import java.util.Random;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
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
        
        BaseComponent[] prefix = Language.getMessage(AMCBungee.getInstance(), p.getUniqueId(), "command.vote.prefix", "&f[&cVote&f]");
        if(args.length >= 1) {
            if(!p.hasPermission("amcbungee.vote." + args[0].toLowerCase())) {
                p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), p.getUniqueId(), "command.vote.no-permission", "&cYou don't have permission to use this Command.")));
                return;
            }
                
            switch(args[0].toLowerCase()) {
                case "view":
                    ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new View(s, p, args));
                    return;
                case "give":
                    ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new GiveAndTake(p, args, "give"));
                    return;
                case "take":
                    ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new GiveAndTake(p, args, "take"));
                    return;
                case "all":
                    ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new useVote(p, false));
                    return;
            }
        }
        ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new useVote(p, true));
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
        private final BaseComponent[] _prefix;
        private final boolean _c;
        
        public useVote(ProxiedPlayer p, boolean c) {
           _p = p;
           _c = c;
           _prefix = Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.prefix", "&f[&cVote&f]");
        }
        
        @Override
        public void run() {
            if(!AMCBungee.getInstance().getPlayerVotes().containsKey(_p.getName())) {
                _p.sendMessage(Mu1ti1ingu41.merge(_prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.wait", "&cPlease wait a moment.")));
                return;
            }
            
            Votes v = AMCBungee.getInstance().getPlayerVotes().get(_p.getName());
            v.updateVotes();
            List<Integer> li = v.getNotVoted();
            if(li.size() < 1) {
                _p.sendMessage(Mu1ti1ingu41.merge(_prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.no-more-open", "&cYou have no more votes open yet. Please try again later.")));
                return;
            }
            
            _p.sendMessage(Mu1ti1ingu41.merge(_prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.header", "&6~~~~~~~~~~ &5Vote made easy &6~~~~~~~~~~")));
            if(_c) {
                int voteId = li.get(new Random().nextInt(li.size()));
                String k = getRandomString();
                if(!AMCBungee.getInstance().getSQL().addPlayerVotes(voteId, k, _p.getName())) {
                    _p.sendMessage(Mu1ti1ingu41.merge(_prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.error", "&cIt's happend an error. Please try again.")));
                    return;
                }
                _p.sendMessage(Mu1ti1ingu41.merge(_prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.1", "&aPlease Click here to open the Vote page.", new String[] {"%url%"}, new String[] {"http://vote.amc-server.de/?v=" + k})));
            } else {
                while(!li.isEmpty()) {
                    int rnd = Rnd.get(0, li.size()-1);
                    int voteId = li.get(rnd);
                    String k = getRandomString();
                    if(AMCBungee.getInstance().getSQL().addPlayerVotes(voteId, k, _p.getName())) {
                        _p.sendMessage(Mu1ti1ingu41.merge(_prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.1", "&aPlease Click here to open the Vote page.", new String[] {"%url%"}, new String[] {"http://vote.amc-server.de/?v=" + k})));
                        li.remove(rnd);
                    }
                }
            }
            _p.sendMessage(Mu1ti1ingu41.merge(_prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.2", "It's may be that you must click on pictures or do captcha's on the opening page.")));
            _p.sendMessage(Mu1ti1ingu41.merge(_prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.3", "The link will be expires in 2 minutes.")));
            if(_c)
                _p.sendMessage(Mu1ti1ingu41.merge(_prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.footer", "&6~~~~~~~~~~ &5%vote% &fof &5%total% &6~~~~~~~~~~", new String[] {"%vote%","%total%"}, new String[] {String.valueOf((AMCBungee.getInstance().getConfig().getInt("max-votes", 0)-li.size()+1)),String.valueOf(AMCBungee.getInstance().getConfig().getInt("max-votes", 0))})));
            else
                _p.sendMessage(Mu1ti1ingu41.merge(_prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.header", "&6~~~~~~~~~~ &5Vote made easy &6~~~~~~~~~~")));
        }
    }
}
