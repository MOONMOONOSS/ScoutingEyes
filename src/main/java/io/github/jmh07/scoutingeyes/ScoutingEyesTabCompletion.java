package io.github.jmh07.scoutingeyes;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoutingEyesTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        if(args.length == 1){
            List<String> option= new ArrayList<>();
            option.add("basic");
            option.add("omega");
            return  option;
        }
        if(args.length == 2){
            List<String> number= new ArrayList<>();
            number.add("1");
            number.add("64");
            return number;
        }

        if(args.length == 3){
            List<String> playerList = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
            Bukkit.getServer().getOnlinePlayers().toArray(players);
            for (Player p:players) {
                playerList.add(p.getName());
            }
            return playerList;
        }

        return null;
    }
}
