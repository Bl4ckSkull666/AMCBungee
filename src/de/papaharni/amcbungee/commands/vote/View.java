/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.commands.vote;

import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import de.papaharni.amcbungee.AMCBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author PapaHarni
 */
public final class View implements Runnable {
    private final CommandSender _s;
    private final ProxiedPlayer _p;
    private final String[] _a;
    
    public View(CommandSender s, ProxiedPlayer p, String[] a) {
        _s = s;
        _p = p;
        _a = a;
    }
    
    @Override
    public void run() {
        BaseComponent[] prefix = Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.prefix", "&f[&cVote&f]");
        String name = _p.getName();
        if(_a.length >= 2 && _p.hasPermission("amcbungee.vote.view.other"))
            name = _a[1];
                        
        int vp = AMCBungee.getInstance().getSQL().getPlayerPoint(name, "vpoints");
        int sp = AMCBungee.getInstance().getSQL().getPlayerPoint(name, "sppoints");
        if(vp >= 0 && sp >= 0) {
            if(_p.getName().equalsIgnoreCase(_s.getName()))
                _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.view.you", "&2You have :")));
            else
                _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.view.other", "&2%name% has :", new String[] {"%name%"}, new String[] {name})));
                            
            _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.view.vote", "&9%vote% &2Vote-Points.", new String[] {"%vote%"}, new String[] {String.valueOf(vp)})));
            _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.view.spend", "&9%sp% &2Spend-Points.", new String[] {"%spend%"}, new String[] {String.valueOf(sp)})));
        } else if(vp == -1 || sp == -1) {
            //Unbekannt
            _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.error.0023", "&cUnknown Error. &f(&e0023&f)")));
        } else if(vp == -2 || sp == -2) {
            //Auslese Fehler
            _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.error.0024", "&cError on Reading Database Informations. &f(&e0024&f)")));
        } else if(vp == -3 || sp == -3) {
             //Allgemeiner DB fehler
            _p.sendMessage(Mu1ti1ingu41.merge(prefix, Language.getMessage(AMCBungee.getInstance(), _p.getUniqueId(), "command.vote.error.0025", "&cError on Reading Database Informations. &f(&e0025&f)")));
        }
    }
}
