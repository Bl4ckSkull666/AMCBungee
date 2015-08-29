/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.papaharni.amcbungee.commands;

import de.papaharni.amcbungee.AMCBungee;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.logging.Level;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author PapaHarni
 */
public class RunProgram extends Command {
    
    public RunProgram() {
        super("runprocess", "amcbungee.runprocess");
    }
    
    @Override
    public void execute(CommandSender s, String[] a) {
        ProxyServer.getInstance().getScheduler().runAsync(AMCBungee.getInstance(), new getRunProgram(s, a));
    }
    
    public class getRunProgram implements Runnable {
        private final CommandSender _s;
        private final String[] _a;
        public getRunProgram(CommandSender cs, String[] args) {
            _s = cs;
            _a = args;
        }
        
        @Override
        public void run() {
            if(_a.length < 1) {
                _s.sendMessage(AMCBungee.convert("Please give me a name."));
                return;
            }
            
            if(!AMCBungee.getInstance().getConfig().isList("run-process." + _a[0].toLowerCase())) {
                _s.sendMessage(AMCBungee.convert("Can't find the given name in the configuration."));
                return;
            }
            
            List<String> temp = AMCBungee.getInstance().getConfig().getStringList("run-process." + _a[0].toLowerCase());
            try {
                for(String str_cmd: temp)
                    printLinuxCommand(str_cmd);
                _s.sendMessage(AMCBungee.convert("Hope it will be running now."));
            } catch (IOException ex) {
                _s.sendMessage(AMCBungee.convert("Happend error on run process. More infos please look in the log file."));
                AMCBungee.getInstance().getLogger().log(Level.INFO, "Error on start " + _a[0] + ".", ex);
            } catch (Exception ex) {
                _s.sendMessage(AMCBungee.convert("Happend error on run process. More infos please look in the log file."));
                AMCBungee.getInstance().getLogger().log(Level.INFO, "Error on start " + _a[0] + ".", ex);
            }
        }
    }
    
    private static void printLinuxCommand(String command) throws Exception { 
        AMCBungee.getInstance().getLogger().log(Level.INFO,"Linux command: " + command);
        String line;
        Process process = Runtime.getRuntime().exec(command);
        Reader r = new InputStreamReader(process.getInputStream());
        BufferedReader in = new BufferedReader(r);
        while((line = in.readLine()) != null) AMCBungee.getInstance().getLogger().log(Level.INFO, line);
        in.close();
    }
}
