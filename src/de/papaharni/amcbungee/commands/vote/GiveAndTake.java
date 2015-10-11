/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.commands.vote;

import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import de.papaharni.amcbungee.AMCBungee;
import de.papaharni.amcbungee.util.Mixes;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author PapaHarni
 */
public final class GiveAndTake implements Runnable {
    private final ProxiedPlayer _p;
    private final String[] _a;
    private final String _t;
    
    public GiveAndTake(ProxiedPlayer p, String[] a, String t) {
        _p = p;
        _a = a;
        _t = t;
    }
    
    @Override
    public void run() {
        BaseComponent[] prefix = Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.prefix", "&f[&cVote&f]");
        if(!_p.hasPermission("amcbungee.vote.give")) {
            _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote." + _t + ".permission", "&cYou don't have permission to give points.")));
            return;
        }
        
        if(_a.length < 4) {
            _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote." + _t + ".example", "&cPlease use /vote give {vpoints/sppoints} {playername} {amount}")));
            return;
        }

        if(!_a[1].equalsIgnoreCase("vpoints") && !_a[1].equalsIgnoreCase("sppoints")) {
            _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote." + _t + ".type", "&cUnknown type. Need vpoints or sppoints.")));
            return;
        }

        if(!Mixes.isNumeric(_a[3])) {
            _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote." + _t + ".type", "&cNeed a Amount as fourth argument.")));
            return;
        }
        
        boolean add = true;
        String type = Language.getMsg(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote." + _t + "." + _a[1], _a[1]);
        if(_t.equalsIgnoreCase("take")) {
            int has = AMCBungee.getInstance().getSQL().getPlayerPoint(_a[2], _a[1].toLowerCase());
            if(has < Integer.parseInt(_a[3])) {
                _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote." + _t + ".need-more", "&aPlayer has only %amount% of %type%.", new String[] {"%amount%","%type%","%player%"}, new String[] {String.valueOf(has), type})));
                return;
            }
            add = false;
        }
            
        if(AMCBungee.getInstance().getSQL().setPlayerPoints(_a[2], Integer.parseInt(_a[3]), _a[1].toLowerCase(), add)) {
            _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote." + _t + ".success", "&aAdded/Taked %amount% of %type% to/of %player%.", new String[] {"%amount%","%type%","%player%"}, new String[] {_a[3], type, _a[2]})));
            if(ProxyServer.getInstance().getPlayer(_a[2]) != null) {
                ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(_a[2]);
                type = Language.getMsg(AMCBungee.getInstance(), pp.getUniqueId(), "command.vote." + _t + "." + _a[1], _a[1]);
                pp.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), pp.getUniqueId(), "command.vote." + _t + ".added", "&aYou have been get/deducted %amount% of %type%.", new String[] {"%amount%","%type%"}, new String[] {_a[3], type})));
            }
        } else 
           _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote." + _t + ".error", "&cError on set Points.")));
    }
}
